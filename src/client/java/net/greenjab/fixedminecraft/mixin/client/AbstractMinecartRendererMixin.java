package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.UUID;

@Mixin(AbstractMinecartRenderer.class)
public abstract class AbstractMinecartRendererMixin<T extends AbstractMinecart, S extends MinecartRenderState> {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;getBehavior()Lnet/minecraft/world/entity/vehicle/minecart/MinecartBehavior;"))
    private void addMinecartLinks(T entity, S state, float partialTicks, CallbackInfo ci) {
        if (entity instanceof AbstractMinecart minecart) {
            if (!minecart.entityTags().isEmpty()) {
                String s = minecart.entityTags().toString();
                s = s.substring(1,s.length()-1);
                if (s.length() == 36) {
                    UUID uuid = UUID.fromString(s);
                    Entity entity2 = minecart.level().getEntity(uuid);
                    if (entity2 instanceof AbstractMinecart minecart2) {
                        float Yrot = minecart.getYRot();
                        float Xrot = minecart.getXRot();
                        float Yrot2 = minecart2.getYRot();
                        float Xrot2 = minecart2.getXRot();
                        if (minecart.getBehavior() instanceof NewMinecartBehavior behavior && behavior.cartHasPosRotLerp()){
                            Yrot = behavior.getCartLerpYRot(partialTicks);
                            Xrot = behavior.getCartLerpXRot(partialTicks);
                        }
                        if (minecart2.getBehavior() instanceof NewMinecartBehavior behavior && behavior.cartHasPosRotLerp()) {
                            Yrot2 = behavior.getCartLerpYRot(partialTicks);
                            Xrot2 = behavior.getCartLerpXRot(partialTicks);
                        }
                        float r =  (float) (Math.PI / 180.0);
                        Vec3 link = new Vec3(-0.6, 0.5, 0).zRot(Xrot * r).yRot(Yrot * r);
                        Vec3 link2 = new Vec3(0.6, 0.5, 0).zRot(Xrot2 * r).yRot(Yrot2 * r);
                        EntityRenderState.LeashState leashData = new EntityRenderState.LeashState();
                        leashData.offset = link;
                        leashData.start = minecart.getPosition(partialTicks).add(link);
                        leashData.end = minecart2.getPosition(partialTicks).add(link2);
                        leashData.startBlockLight = 0;
                        leashData.endBlockLight = 0;
                        leashData.startSkyLight = 0;
                        leashData.endSkyLight = 0;
                        state.leashStates = new ArrayList<>(1);
                        state.leashStates.add(leashData);
                    }
                }
            }
        }
    }
}
