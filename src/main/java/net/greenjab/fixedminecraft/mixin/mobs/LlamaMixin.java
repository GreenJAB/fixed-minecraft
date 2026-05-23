package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Llama.class)
public abstract class LlamaMixin extends AbstractChestedHorse {
    public LlamaMixin(EntityType<? extends LlamaMixin> entityType, Level world) {
        super(entityType, world);
    }

    @ModifyArg(method = "getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/animal/equine/Llama;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/equine/Llama;setStrength(I)V"), index = 0)
    private int injected(int x, @Local(ordinal = 1)Llama otherLlama) {
        Llama llamaEntity = (Llama)(Object)this;
        int i = (llamaEntity.getStrength() + otherLlama.getStrength()) / 2;
        float f = this.random.nextFloat();
        if (f < 0.5F) i--;
        if (f > 0.8F) i++;
        return i;
    }
}
