package net.greenjab.fixedminecraft.registry.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class CustomEffect extends StatusEffect {
    public CustomEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }
}
