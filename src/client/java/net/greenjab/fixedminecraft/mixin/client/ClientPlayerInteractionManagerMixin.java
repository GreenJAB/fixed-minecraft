package net.greenjab.fixedminecraft.mixin.client;

import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;


@SuppressWarnings("unchecked")
@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Redirect(method = "getReachDistance", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getReachDistance(Z)F"))
    private float hasLongerReach(boolean creative) {
        if (this.client.player.hasStatusEffect(StatusRegistry.INSTANCE.getREACH())) {
            int i = this.client.player.getStatusEffect(StatusRegistry.INSTANCE.getREACH()).getAmplifier();
            return (creative ? 5.0F : 4.5F) + (i+1)*0.5f;
        }  else {
            return creative ? 5.0F : 4.5F;
        }
    }

    @ModifyArg(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;attack(Lnet/minecraft/entity/Entity;)V"), index = 0)
    private Entity check(Entity target) {
        if (target instanceof InteractionEntity IE) {
            if (IE.getCommandTags().contains("dragon")) {
                List<Entity> entities = target.getWorld().getOtherEntities(IE, IE.getBoundingBox().expand(3));
                EnderDragonEntity dragon = null;
                for (Entity e : entities) {
                    if (e instanceof EnderDragonEntity dragonEntity) {
                        dragon = dragonEntity;
                        return dragon.head;
                    }
                }
            }
        }
        return target;
    }

}
