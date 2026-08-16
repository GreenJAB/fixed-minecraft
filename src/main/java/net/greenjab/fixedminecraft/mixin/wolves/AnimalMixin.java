package net.greenjab.fixedminecraft.mixin.wolves;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalMixin extends AgeableMob {
    protected AnimalMixin(EntityType<? extends AgeableMob> type, Level level) {
        super(type, level);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Animal;getAge()I"))
    public void passiveHeal(CallbackInfo ci) {
        if ((Animal)(Object)this instanceof TamableAnimal tamableAnimal && tamableAnimal.isTame()) {
            if (!this.level().isClientSide() && this.isAlive() && this.tickCount % 100 == 0) {
                this.heal(1.0F);
            }
        }
    }
}
