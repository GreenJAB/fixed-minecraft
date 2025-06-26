package net.greenjab.fixedminecraft.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.tick.TickManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/** Incase villager armour breaks again */
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin{

    /*@Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    protected abstract void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta,
                                         MatrixStack matrices, VertexConsumerProvider vertexConsumers);

    @Inject(method = "renderEntities", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;renderEntity(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"
    ))
    public void addVillagerArmorLayer(MatrixStack matrices, VertexConsumerProvider.Immediate immediate, Camera camera,
                                      RenderTickCounter tickCounter, List<Entity> entities, CallbackInfo ci, @Local Entity entity) {
        if (entity instanceof VillagerEntity) {
            ArmorStandEntity armorStandEntity= new ArmorStandEntity(entity.getWorld(), entity.getX(), entity.getY(), entity.getZ());
            Vec3d vec3d = camera.getPos();
            double d = vec3d.getX();
            double e = vec3d.getY();
            double f = vec3d.getZ();
            assert this.client.world != null;
            TickManager tickManager = this.client.world.getTickManager();

            if (armorStandEntity.age == 0) {
                armorStandEntity.lastRenderX = entity.lastRenderX;
                armorStandEntity.lastRenderY = entity.lastRenderY;
                armorStandEntity.lastRenderZ = entity.lastRenderZ;
            }

            armorStandEntity.setYaw(( entity).getYaw());
            armorStandEntity.setPitch(( entity).getPitch());
            armorStandEntity.bodyYaw = ((VillagerEntity) entity).bodyYaw;
            armorStandEntity.headYaw = ((VillagerEntity) entity).headYaw;
            armorStandEntity.prevYaw = (entity).prevYaw;
            armorStandEntity.prevPitch = (entity).prevPitch;
            armorStandEntity.prevBodyYaw = ((VillagerEntity) entity).prevBodyYaw;
            armorStandEntity.prevHeadYaw = ((VillagerEntity) entity).prevHeadYaw;
            armorStandEntity.setHeadRotation(new EulerAngle(( entity).getPitch(), ((VillagerEntity) entity).headYaw-((VillagerEntity) entity).bodyYaw, 0));//((VillagerEntity) entity).getLookControl().getLookY());

            armorStandEntity.setLeftArmRotation(new EulerAngle(-40.0F, 0.0F, 0.0F));
            armorStandEntity.setRightArmRotation(new EulerAngle(-40.0F, 0.0F, 0.0F));
            armorStandEntity.setLeftLegRotation(new EulerAngle(0.0F, 0.0F, 0.0F));
            armorStandEntity.setRightLegRotation(new EulerAngle(0.0F, 0.0F, 0.0F));

            Iterable<ItemStack> armor = ((VillagerEntity) entity).getArmorItems();
            for (ItemStack ii : armor) {
                EquipmentSlot ES = getPreferredEquipmentSlot(ii);
                armorStandEntity.equipStack(ES, ii);
            }
            armorStandEntity.equipStack(EquipmentSlot.MAINHAND, Items.AIR.getDefaultStack());


            armorStandEntity.getDataTracker().set(ArmorStandEntity.ARMOR_STAND_FLAGS, this.setBitField(armorStandEntity.getDataTracker().get(ArmorStandEntity.ARMOR_STAND_FLAGS)));
            armorStandEntity.setInvisible(true);
            armorStandEntity.setHideBasePlate(true);


            float g = tickCounter.getTickDelta(!tickManager.shouldSkipTick(armorStandEntity));
            this.renderEntity(armorStandEntity, d, e, f, g, matrices, immediate);
        }
    }
    @Unique
    public final EquipmentSlot getPreferredEquipmentSlot(ItemStack stack) {
        EquippableComponent equippableComponent = stack.get(DataComponentTypes.EQUIPPABLE);
        return equippableComponent != null ? equippableComponent.slot() : EquipmentSlot.MAINHAND;
    }
    @Unique
    private byte setBitField(byte value) {
        value = (byte)(value | 16);
        return value;
    }*/
}
