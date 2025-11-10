package net.greenjab.fixedminecraft.registry.registries;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.SharedConstants;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRuleCategory;
import net.minecraft.world.rule.GameRuleType;
import net.minecraft.world.rule.GameRuleVisitor;
import net.minecraft.world.rule.GameRules;

import java.util.function.ToIntFunction;

public class GameruleRegistry {
    public static GameRule<Boolean> Ice_Melt_In_Nether;
    public static GameRule<Boolean> Insomnia_Sleep_Requirement;
    public static GameRule<Boolean> Require_Totem_Use;
    public static GameRule<Integer> Stamina_Drain_Speed;

    public static void register() {

        Ice_Melt_In_Nether = registerBooleanRule("ice_melt_in_nether", GameRuleCategory.UPDATES, true);
        Insomnia_Sleep_Requirement =registerBooleanRule("insomnia_sleep_requirement", GameRuleCategory.PLAYER, false);
        Require_Totem_Use = registerBooleanRule("require_totem_use", GameRuleCategory.PLAYER, false);
        Stamina_Drain_Speed = registerIntRule("stamina_drain_speed", GameRuleCategory.PLAYER, 100, 1);
    }

    private static GameRule<Boolean> registerBooleanRule(String name, GameRuleCategory category, boolean defaultValue) {
        return register(
                name,
                category,
                GameRuleType.BOOL,
                BoolArgumentType.bool(),
                Codec.BOOL,
                defaultValue,
                FeatureSet.empty(),
                GameRuleVisitor::visitBoolean,
                /* method_76193 */ value -> value ? 1 : 0
        );
    }

    private static GameRule<Integer> registerIntRule(String name, GameRuleCategory category, int defaultValue, int minValue) {
        return registerIntRule(name, category, defaultValue, minValue, Integer.MAX_VALUE, FeatureSet.empty());
    }

    private static GameRule<Integer> registerIntRule(
            String name, GameRuleCategory category, int defaultValue, int minValue, int maxValue, FeatureSet requiredFeatures
    ) {
        return register(
                name,
                category,
                GameRuleType.INT,
                IntegerArgumentType.integer(minValue, maxValue),
                Codec.intRange(minValue, maxValue),
                defaultValue,
                requiredFeatures,
                GameRuleVisitor::visitInt,
                /* method_76194 */ value -> value
        );
    }

    private static <T> GameRule<T> register(
            String name,
            GameRuleCategory category,
            GameRuleType type,
            ArgumentType<T> argumentType,
            Codec<T> codec,
            T defaultValue,
            FeatureSet requiredFeatures,
            GameRules.Acceptor<T> acceptor,
            ToIntFunction<T> commandResultSupplier
    ) {
        return Registry.register(
                Registries.GAME_RULE, FixedMinecraft.id(name), new GameRule<>(category, type, argumentType, acceptor, codec, commandResultSupplier, defaultValue, requiredFeatures)
        );
    }

}
