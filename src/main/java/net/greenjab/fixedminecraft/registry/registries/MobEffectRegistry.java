package net.greenjab.fixedminecraft.registry.registries;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.effect.CustomEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class MobEffectRegistry {

    public static Holder<MobEffect> AWKWARD =  register("awkward", new CustomEffect(MobEffectCategory.NEUTRAL,0xA72BEC));
    public static Holder<MobEffect> REACH = register("reach", new CustomEffect(MobEffectCategory.NEUTRAL,0x98D982));
    public static Holder<MobEffect> INSOMNIA = register("insomnia", new CustomEffect(MobEffectCategory.BENEFICIAL,0x98D982));

    private static Holder<MobEffect> register(String name, MobEffect statusEffect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, FixedMinecraft.id(name), statusEffect);
    }

    public static void registerMobEffects() {
        System.out.println("register MobEffects");
    }
}
