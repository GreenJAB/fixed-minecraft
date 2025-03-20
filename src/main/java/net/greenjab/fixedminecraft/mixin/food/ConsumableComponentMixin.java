package net.greenjab.fixedminecraft.mixin.food;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ConsumableComponent.class)
public class ConsumableComponentMixin {

    @Inject(method = "finishConsumption", at = @At("HEAD"))
    private void eatingPausesStaminaRegen(World world, LivingEntity user, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            serverPlayerEntity.addExhaustion(0.01F);
        }
    }

    @Inject(method = "shouldSpawnParticlesAndPlaySounds", at = @At("HEAD"), cancellable = true)
    private void playSoundForLongFoods(int remainingUseTicks, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(remainingUseTicks % 4==0);
        cir.cancel();
    }
}
