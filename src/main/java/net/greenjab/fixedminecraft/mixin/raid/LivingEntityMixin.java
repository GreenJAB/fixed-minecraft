package net.greenjab.fixedminecraft.mixin.raid;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.registries.GameruleRegistry;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(LivingEntity.class)
public class LivingEntityMixin  {

    //Need to "use" totem
    @ModifyExpressionValue(method = "tryUseDeathProtector", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack requireUsingTotem(ItemStack original) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (!((ServerWorld)LE.getEntityWorld()).getGameRules().getValue(GameruleRegistry.Require_Totem_Use) || LE.isUsingItem()) {
            return original;
        } else {
            return new ItemStack(Items.AIR);
        }
    }

    @Inject(method = "tryUseDeathProtector", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V", shift = At.Shift.AFTER))
    private void brokenTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir, @Local Hand hand) {
        ItemStack broken = new ItemStack(ItemRegistry.BROKEN_TOTEM);
        if ((LivingEntity)(Object)this instanceof PlayerEntity) {
            PlayerEntity user = (PlayerEntity) (Object) this;
            ItemStack i = user.getStackInHand(hand);
            if (i.isEmpty()) {
                user.setStackInHand(hand, broken);
            } else {
                if (!user.getInventory().insertStack(broken)) {
                    user.dropItem(broken, true);
                }
            }
        }
    }

    @Inject(method = "tryUseDeathProtector", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getEntityWorld()Lnet/minecraft/world/World;", shift = At.Shift.AFTER))
    private void echoTeleport(DamageSource source, CallbackInfoReturnable<Boolean> cir, @Local ItemStack itemStack) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE instanceof ServerPlayerEntity SPE) {
            if (itemStack.isOf(ItemRegistry.ECHO_TOTEM)) {
                goToSpawn(SPE);
            }
        }
    }
    @Unique
    private void goToSpawn(ServerPlayerEntity player) {
        TeleportTarget teleportTarget = player.getRespawnTarget(true, TeleportTarget.NO_OP);
        ServerWorld serverWorld = teleportTarget.world();
        Vec3d pos = teleportTarget.position();
        if (player.teleport(serverWorld, pos.x, pos.y, pos.z, Set.of(), player.getYaw(), player.getPitch(), true)) {
            serverWorld.emitGameEvent(GameEvent.TELEPORT, pos, GameEvent.Emitter.of(player));
            SoundEvent soundEvent = SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
            SoundCategory soundCategory = SoundCategory.PLAYERS;

            serverWorld.playSound(player, pos.getX(), pos.getY(), pos.getZ(), soundEvent, soundCategory);
            player.onLanding();

        }
    }
}
