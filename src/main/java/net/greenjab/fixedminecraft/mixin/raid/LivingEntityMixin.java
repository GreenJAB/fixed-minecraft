package net.greenjab.fixedminecraft.mixin.raid;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin  {

    //Need to "use" totem
    @ModifyExpressionValue(method = "checkTotemDeathProtection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack requireUsingTotem(ItemStack original) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (!((ServerLevel)LE.level()).getGameRules().get(GameRuleRegistry.REQUIRE_TOTEM_USE) || LE.isUsingItem()) {
            return original;
        } else {
            return new ItemStack(Items.AIR);
        }
    }

    @Inject(method = "checkTotemDeathProtection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", shift = At.Shift.AFTER))
    private void brokenTotem(DamageSource killingDamage, CallbackInfoReturnable<Boolean> cir, @Local InteractionHand hand) {
        ItemStack broken = new ItemStack(ItemRegistry.BROKEN_TOTEM);
        if ((LivingEntity)(Object)this instanceof Player) {
            Player user = (Player) (Object) this;
            ItemStack i = user.getItemInHand(hand);
            if (i.isEmpty()) {
                user.setItemInHand(hand, broken);
            } else {
                if (!user.getInventory().add(broken)) {
                    user.drop(broken, true);
                }
            }
        }
    }

    @Inject(method = "checkTotemDeathProtection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;level()Lnet/minecraft/world/level/Level;", shift = At.Shift.AFTER))
    private void echoTeleport(DamageSource killingDamage, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) ItemStack protectionItem) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE instanceof ServerPlayer SPE) {
            if (protectionItem.is(ItemRegistry.ECHO_TOTEM)) {
                goToSpawn(SPE);
            }
        }
    }
    @Unique
    private void goToSpawn(ServerPlayer player) {
        TeleportTransition teleportTarget = player.findRespawnPositionAndUseSpawnBlock(true, TeleportTransition.DO_NOTHING);
        ServerLevel serverWorld = teleportTarget.newLevel();
        Vec3 pos = teleportTarget.position();
        if (player.teleportTo(serverWorld, pos.x, pos.y, pos.z, Set.of(), player.getYRot(), player.getXRot(), true)) {
            serverWorld.gameEvent(GameEvent.TELEPORT, pos, GameEvent.Context.of(player));
            SoundEvent soundEvent = SoundEvents.CHORUS_FRUIT_TELEPORT;
            SoundSource soundCategory = SoundSource.PLAYERS;

            serverWorld.playSound(player, pos.x(), pos.y(), pos.z(), soundEvent, soundCategory);
            player.resetFallDistance();

        }
    }
}
