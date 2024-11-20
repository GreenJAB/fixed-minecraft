package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LlamaEntity.class)
public abstract class LlamaEntityMixin extends AbstractDonkeyEntity {
    public LlamaEntityMixin(EntityType<? extends LlamaEntityMixin> entityType, World world) {
        super(entityType, world);
    }

    @ModifyArg(method = "createChild*", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/LlamaEntity;setStrength(I)V"), index = 0)
    private int injected(int x, @Local(ordinal = 1)LlamaEntity llamaEntity2) {
        LlamaEntity llamaEntity = (LlamaEntity)(Object)this;
        int i = (llamaEntity.getStrength()+ llamaEntity2.getStrength())/2;
        float f = this.random.nextFloat();
        if (f < 0.5F) i--;
        if (f > 0.8F) i++;
        return i;
    }
}
