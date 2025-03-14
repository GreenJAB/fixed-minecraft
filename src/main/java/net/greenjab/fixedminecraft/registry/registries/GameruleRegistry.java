package net.greenjab.fixedminecraft.registry.registries;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class GameruleRegistry {
    public static GameRules.Key<GameRules.BooleanRule> Ice_Melt_In_Nether;
    public static GameRules.Key<GameRules.BooleanRule> Insomnia_Sleep_Requirement;
    public static GameRules.Key<GameRules.BooleanRule> Require_Totem_Use;
    public static GameRules.Key<GameRules.IntRule> Stamina_Drain_Speed;

    public static void register() {
        Ice_Melt_In_Nether  =
                GameRuleRegistry.register("iceMeltInNether", GameRules.Category.UPDATES, GameRuleFactory.createBooleanRule(true));
        Insomnia_Sleep_Requirement  =
                GameRuleRegistry.register("insomniaSleepRequirement", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));
        Require_Totem_Use  =
                GameRuleRegistry.register("requireTotemUse", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));
        Stamina_Drain_Speed  =
                GameRuleRegistry.register("staminaDrainSpeed", GameRules.Category.PLAYER, GameRuleFactory.createIntRule(100));
    }
}
