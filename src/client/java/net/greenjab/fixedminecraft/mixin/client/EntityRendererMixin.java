package net.greenjab.fixedminecraft.mixin.client;


import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.UUID;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin <T extends Entity, S extends EntityRenderState> {

    @Shadow
    protected abstract int getBlockLightLevel(T entity, BlockPos blockPos);

    @Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;displayFireAnimation()Z"))
    private void addMinecartLinks(T entity, S state, float partialTicks, CallbackInfo ci) {
       if (entity instanceof AbstractMinecart minecart) {
           if (!minecart.entityTags().isEmpty()) {
               String s = minecart.entityTags().toString();
               s = s.substring(1,s.length()-1);
               if (s.length() == 36) {
                   UUID uuid = UUID.fromString(s);
                   Entity entity2 = entity.level().getEntity(uuid);
                   if (entity2 instanceof AbstractMinecart minecart2) {

                       Level world = entity.level();
                       float gx = entity.getPreciseBodyRotation(partialTicks) * (float) (Math.PI / 180.0);
                       Vec3 vec3 = new Vec3(0,0.12,0);
                       BlockPos blockPos = BlockPos.containing(entity.getEyePosition(partialTicks));
                       BlockPos blockPos2 = BlockPos.containing(minecart2.getEyePosition(partialTicks));
                       int i = getBlockLightLevel(entity, blockPos);
                       int k = world.getBrightness(LightLayer.SKY, blockPos);
                       int l = world.getBrightness(LightLayer.SKY, blockPos2);
                       state.leashStates = new ArrayList<>(1);
                       state.leashStates.add(new EntityRenderState.LeashState());

                           Vec3 vec3d3 = vec3.yRot(-gx);
                           EntityRenderState.LeashState leashData2 = state.leashStates.getFirst();
                           leashData2.offset = vec3d3;
                           leashData2.start = entity.getPosition(partialTicks).add(vec3d3);
                           leashData2.end = minecart2.getRopeHoldPosition(partialTicks).add(0, -0.3,0);
                           leashData2.startBlockLight = i;
                           leashData2.endBlockLight = i;
                           leashData2.startSkyLight = k;
                           leashData2.endSkyLight = l;

                   }
               }

           }
       }
    }
}
