package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.greenjab.fixedminecraft.mobs.ArmorTrimmer;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initEquipment", at = @At(value = "HEAD"),cancellable = true)
    private void Armor(Random random, LocalDifficulty localDifficulty, CallbackInfo ci) {
        int y= this.getBlockPos().getY();
        float f = this.getWorld().getDifficulty() == Difficulty.HARD ? 0.20F : 0.125F;
        if (y < this.getWorld().getSeaLevel()) f += (this.getWorld().getSeaLevel() - y) / (128 * 10f);
        EquipmentSlot[] var6 = EquipmentSlot.values();
        for (EquipmentSlot equipmentSlot : var6) {
            if (random.nextFloat() < f * localDifficulty.getClampedLocalDifficulty()) {
                int i = 0;
                if (random.nextFloat() < f) i++;
                if (random.nextFloat() < f) i++;
                if (random.nextFloat() < f) i++;
                if (random.nextFloat() < f) i++;

                if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                    ItemStack itemStack = this.getEquippedStack(equipmentSlot);
                    if (itemStack.isEmpty()) {
                        ItemStack item = new ItemStack(Objects.requireNonNull(MobEntity.getEquipmentForSlot(equipmentSlot, i)));
                        if (i==0) {
                            DyeItem dye = DyeItem.byColor(DyeColor.byId(this.getWorld().random.nextInt(16)));
                            List<DyeItem> colour = List.of(dye);
                            item = DyedColorComponent.setColor(item, colour);
                        }
                        this.equipStack(equipmentSlot, ArmorTrimmer.trimAtChanceIfTrimable(item, this.random, this.getWorld().getRegistryManager()));
                    }
                }
            }
        }
        ci.cancel();
    }

    @ModifyArg(method = "enchantEquipment(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/util/math/random/Random;FLnet/minecraft/world/LocalDifficulty;)V", at = @At(value = "INVOKE",
                                                                                                                                                                                                                target = "Lnet/minecraft/entity/mob/MobEntity;equipStack(Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/ItemStack;)V"), index = 1)
    private ItemStack applySuperEnchantArmor(
            ItemStack stack) {
        return FixedMinecraftEnchantmentHelper.applySuperEnchants(stack, random);
    }

    @Inject(method = "initialize", at=@At(value = "HEAD"))
    private void addStuff(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData,
                          CallbackInfoReturnable<EntityData> cir){
        MobEntity LE = (MobEntity)(Object)this;
        Random random = LE.getWorld().getRandom();
        int y= LE.getBlockPos().getY();
        if (LE instanceof HostileEntity && world.getDimension().hasSkyLight()) {
            addEffect(world, difficulty, LE, y);
            addModifiers(world, random, LE);

        }
    }

    @Unique
    private void addModifiers(ServerWorldAccess world, Random random, MobEntity LE) {
        int i = 0;
        if (world.getDifficulty() == Difficulty.NORMAL) i = 1;
        if (world.getDifficulty() == Difficulty.HARD) i = 2;
        float h = i*3*gaussian(random);
        increaseHealth(LE, h);
        increaseSpeed(random, LE, i);
    }

    @Unique
    private static void increaseSpeed(Random random, MobEntity LE, int i) {
        if (LE.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)!=null) {
            if (!LE.isBaby()) LE.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(
                    LE.getAttributeBaseValue(EntityAttributes.MOVEMENT_SPEED) * (1 + (i * 0.15f * gaussian(random))));
        }
    }

    @Unique
    private static void increaseHealth(MobEntity LE, float h) {
        if (LE.getAttributeInstance(EntityAttributes.MAX_HEALTH)!=null) {
            LE.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(
                    LE.getAttributeBaseValue(EntityAttributes.MAX_HEALTH) + h);
            LE.setHealth(LE.getHealth() + h);
        }
    }

    @Unique
    private void addEffect(ServerWorldAccess world, LocalDifficulty localDifficulty, MobEntity LE, int y){
        if (random.nextFloat() < 0.2f * localDifficulty.getClampedLocalDifficulty()) {
            if (world.getLightLevel(LightType.SKY, LE.getBlockPos()) < 7 && !(LE instanceof SpiderEntity)) {
                if (random.nextFloat() < (LE.getWorld().getSeaLevel() - y) / (128 * 3.0f)) {
                    StatusEffectInstance effect = getEffect(random, LE);
                    LE.addStatusEffect(effect);
                }
            }
        }
    }


    @Unique
    private static float gaussian(Random random){
        return (float)(random.nextGaussian()/4.0f)+0.5f;
    }

    @Unique
    public StatusEffectInstance getEffect(Random random, LivingEntity LE) {

        int i = random.nextInt(6);
        if (LE instanceof CreeperEntity) {
            if (random.nextFloat()<0.5f) i+=5;
        }
        if (i == 0) {
            return new StatusEffectInstance(StatusEffects.SPEED, -1, 0);
        } else if (i == 1) {
            return new StatusEffectInstance(StatusEffects.STRENGTH, -1, 0);
        } else if (i == 2) {
            return new StatusEffectInstance(StatusEffects.JUMP_BOOST, -1, 1);
        } else if (i == 3) {
            return new StatusEffectInstance(StatusEffects.SLOW_FALLING, -1, 0);
        } else if (i == 4) {
            return new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, -1, 0);
        } else if (i == 5) {
            return new StatusEffectInstance(StatusEffects.LUCK, -1, 0);
        } else if (i == 6) {
            return new StatusEffectInstance(StatusEffects.MINING_FATIGUE, -1, 0);
        } else if (i == 7) {
            return new StatusEffectInstance(StatusEffects.BLINDNESS, -1, 0);
        } else if (i == 8) {
            return new StatusEffectInstance(StatusEffects.REGENERATION, -1, 0);
        } else {
            return new StatusEffectInstance(StatusEffects.ABSORPTION, -1, 0);
        }
    }


}
