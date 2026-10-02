package com.example.memeheroes.command;

import com.example.memeheroes.config.MemeDamageConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Map;

/**
 * /changeMemeDamage —— 管理员调整梗技能伤害。
 *
 * 用法：
 *   /changeMemeDamage list                      查看所有技能当前伤害与默认值
 *   /changeMemeDamage get <key>                 查看某技能伤害
 *   /changeMemeDamage set <key> <value>         设置某技能伤害（立即生效并持久化）
 *   /changeMemeDamage reset <key>               重置某技能为默认伤害
 *   /changeMemeDamage resetAll                  重置全部技能为默认伤害
 *
 * 权限：需要 OP 等级 2（与 /give 同级）。
 * key 命名：{梗英文名}.{技能英文名}，例如 paoye.tnt、huaqiang.big_watermelon。
 */
public final class ChangeMemeDamageCommand {

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_KEYS =
            (ctx, builder) -> SharedSuggestionProvider.suggest(MemeDamageConfig.getAllDamages().keySet(), builder);

    private ChangeMemeDamageCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("changeMemeDamage")
                .requires(src -> src.hasPermission(2))

                .then(Commands.literal("list")
                        .executes(ChangeMemeDamageCommand::listAll))

                .then(Commands.literal("get")
                        .then(Commands.argument("key", StringArgumentType.word())
                                .suggests(SUGGEST_KEYS)
                                .executes(ChangeMemeDamageCommand::get)))

                .then(Commands.literal("set")
                        .then(Commands.argument("key", StringArgumentType.word())
                                .suggests(SUGGEST_KEYS)
                                .then(Commands.argument("value", FloatArgumentType.floatArg(0.0F))
                                        .executes(ChangeMemeDamageCommand::set))))

                .then(Commands.literal("reset")
                        .then(Commands.argument("key", StringArgumentType.word())
                                .suggests(SUGGEST_KEYS)
                                .executes(ChangeMemeDamageCommand::reset)))

                .then(Commands.literal("resetAll")
                        .executes(ChangeMemeDamageCommand::resetAll))
        );
    }

    private static int listAll(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> Component.literal("§6===== 梗技能伤害配置 ====="), false);
        Map<String, Float> damages = MemeDamageConfig.getAllDamages();
        Map<String, Float> defaults = MemeDamageConfig.getAllDefaults();
        for (Map.Entry<String, Float> e : damages.entrySet()) {
            float cur = e.getValue();
            float def = defaults.getOrDefault(e.getKey(), 0.0F);
            String line = cur == def
                    ? String.format("§e%s §7= §f%.1f", e.getKey(), cur)
                    : String.format("§e%s §7= §c%.1f §7(默认 %.1f)", e.getKey(), cur, def);
            src.sendSuccess(() -> Component.literal(line), false);
        }
        return 1;
    }

    private static int get(CommandContext<CommandSourceStack> ctx) {
        String key = StringArgumentType.getString(ctx, "key");
        if (!MemeDamageConfig.isValidKey(key)) {
            ctx.getSource().sendFailure(Component.literal("§c未知的技能 key: " + key));
            return 0;
        }
        float cur = MemeDamageConfig.getDamage(key);
        float def = MemeDamageConfig.getAllDefaults().get(key);
        ctx.getSource().sendSuccess(() -> Component.literal(
                String.format("§e%s §7当前= §f%.1f §7默认= §f%.1f", key, cur, def)), false);
        return 1;
    }

    private static int set(CommandContext<CommandSourceStack> ctx) {
        String key = StringArgumentType.getString(ctx, "key");
        float value = FloatArgumentType.getFloat(ctx, "value");
        if (!MemeDamageConfig.isValidKey(key)) {
            ctx.getSource().sendFailure(Component.literal("§c未知的技能 key: " + key));
            return 0;
        }
        MemeDamageConfig.setDamage(key, value);
        MemeDamageConfig.save();
        ctx.getSource().sendSuccess(() -> Component.literal(
                String.format("§a已将 §e%s §a的伤害设置为 §c%.1f", key, value)), true);
        return 1;
    }

    private static int reset(CommandContext<CommandSourceStack> ctx) {
        String key = StringArgumentType.getString(ctx, "key");
        if (!MemeDamageConfig.isValidKey(key)) {
            ctx.getSource().sendFailure(Component.literal("§c未知的技能 key: " + key));
            return 0;
        }
        MemeDamageConfig.resetDamage(key);
        MemeDamageConfig.save();
        float def = MemeDamageConfig.getDamage(key);
        ctx.getSource().sendSuccess(() -> Component.literal(
                String.format("§a已将 §e%s §a重置为默认伤害 §f%.1f", key, def)), true);
        return 1;
    }

    private static int resetAll(CommandContext<CommandSourceStack> ctx) {
        MemeDamageConfig.resetAll();
        MemeDamageConfig.save();
        ctx.getSource().sendSuccess(() -> Component.literal("§a已重置全部梗技能伤害为默认值"), true);
        return 1;
    }
}
