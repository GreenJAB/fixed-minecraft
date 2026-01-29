package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
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
    protected abstract int getBlockLight(T entity, BlockPos pos);

    @Inject(method = "updateRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;doesRenderOnFire()Z"))
    private void addMinecartLinks(T entity, S state, float tickProgress, CallbackInfo ci) {
       if (entity instanceof AbstractMinecartEntity minecart) {
           if (!minecart.getCommandTags().isEmpty()) {
               String s = minecart.getCommandTags().toString();
               s = s.substring(1,s.length()-1);
               if (s.length() == 36) {
                   UUID uuid = UUID.fromString(s);
                   Entity entity2 = entity.getEntityWorld().getEntity(uuid);
                   if (entity2 instanceof AbstractMinecartEntity minecart2) {

                       World world = entity.getEntityWorld();
                       float gx = entity.lerpYaw(tickProgress) * (float) (Math.PI / 180.0);
                       Vec3d vec3d = new Vec3d(0,0.12,0);
                       BlockPos blockPos = BlockPos.ofFloored(entity.getCameraPosVec(tickProgress));
                       BlockPos blockPos2 = BlockPos.ofFloored(minecart2.getCameraPosVec(tickProgress));
                       int i = getBlockLight(entity, blockPos);
                       int k = world.getLightLevel(LightType.SKY, blockPos);
                       int l = world.getLightLevel(LightType.SKY, blockPos2);
                       state.leashDatas = new ArrayList<>(1);
                       state.leashDatas.add(new EntityRenderState.LeashData());

                           Vec3d vec3d3 = vec3d.rotateY(-gx);
                           EntityRenderState.LeashData leashData2 = state.leashDatas.get(0);
                           leashData2.offset = vec3d3;
                           leashData2.startPos = entity.getLerpedPos(tickProgress).add(vec3d3);
                           leashData2.endPos = minecart2.getLeashPos(tickProgress).add(0, -0.3,0);
                           leashData2.leashedEntityBlockLight = i;
                           leashData2.leashHolderBlockLight = i;
                           leashData2.leashedEntitySkyLight = k;
                           leashData2.leashHolderSkyLight = l;

                   }
               }

           }
       }
    }
}
