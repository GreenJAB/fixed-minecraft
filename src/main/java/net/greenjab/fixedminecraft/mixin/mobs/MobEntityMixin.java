package net.greenjab.fixedminecraft.mixin.mobs;

import net.greenjab.fixedminecraft.mobs.ArmorTrimmer;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initEquipment", at = @At(value = "HEAD"),cancellable = true)
    private void Armor(Random random, LocalDifficulty localDifficulty, CallbackInfo ci) {
        int y= this.getBlockPos().getY();
        float f = this.getWorld().getDifficulty() == Difficulty.HARD ? 0.1F : 0.25F;

        EquipmentSlot[] var6 = EquipmentSlot.values();
        int var7 = var6.length;
        for(int var8 = 0; var8 < var7; ++var8) {
            float x = 0.15F;
            if (y < this.getWorld().getSeaLevel()) x+= (this.getWorld().getSeaLevel()-y)/(128*10);
            if (random.nextFloat() < x * localDifficulty.getClampedLocalDifficulty()) {
                int i = random.nextInt(2);
                if (random.nextFloat() < x)  i++;
                if (random.nextFloat() < x)  i++;
                if (random.nextFloat() < x)  i++;

                EquipmentSlot equipmentSlot = var6[var8];
                if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
                    ItemStack itemStack = this.getEquippedStack(equipmentSlot);

                    if (itemStack.isEmpty()) {
                        Item item = MobEntity.getEquipmentForSlot(equipmentSlot, i);
                        if (item != null) {
                            this.equipStack(equipmentSlot, ArmorTrimmer.trimAtChanceIfTrimable(new ItemStack(item), this.random, this.getWorld().getRegistryManager()));
                        }
                    }
                }
            }
        }
        ci.cancel();
    }

    @Inject(method = "initialize", at=@At(value = "HEAD"))
    private void addEffect(ServerWorldAccess world, LocalDifficulty localDifficulty, SpawnReason spawnReason, EntityData entityData,
                             NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir){
        LivingEntity LE = (LivingEntity)(Object)this;
        Random random = LE.getWorld().getRandom();
        int y= LE.getBlockPos().getY();
        if (LE instanceof HostileEntity) {
            if (random.nextFloat() < 0.1f * localDifficulty.getClampedLocalDifficulty()) {
                if (world.getLightLevel(LightType.SKY, LE.getBlockPos()) < 7 && !(LE instanceof SpiderEntity)) {
                    if (random.nextFloat() < (LE.getWorld().getSeaLevel() - y) / (128 * 5.0f)) {
                        StatusEffectInstance effect = getEffect(random, LE);
                        LE.addStatusEffect(effect);
                    }
                }
            }
            int i = 0;
            if (world.getDifficulty() == Difficulty.NORMAL) i = 1;
            if (world.getDifficulty() == Difficulty.HARD) i = 2;
            LE.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(LE.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH)+i*2);
            LE.setHealth(LE.getHealth() + i*2);
            if (!LE.isBaby()) LE.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(LE.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)*(1+i/10f));


        }
    }

    public StatusEffectInstance getEffect(Random random, LivingEntity LE) {

        int i = random.nextInt(6);
        if (LE instanceof CreeperEntity) {
            if (random.nextFloat()<0.5f) i+=5;
        }
        if (i == 1) {
            return new StatusEffectInstance(StatusEffects.SPEED, -1, 0);
        } else if (i == 2) {
            return new StatusEffectInstance(StatusEffects.STRENGTH, -1, 0);
        } else if (i == 3) {
            return new StatusEffectInstance(StatusEffects.JUMP_BOOST, -1, 1);
        } else if (i == 4) {
            return new StatusEffectInstance(StatusEffects.SLOW_FALLING, -1, 0);
        } else if (i == 5) {
            return new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, -1, 0);
        } else if (i == 6) {
            return new StatusEffectInstance(StatusEffects.LUCK, -1, 0);
        } else if (i == 7) {
            return new StatusEffectInstance(StatusEffects.MINING_FATIGUE, -1, 0);
        } else if (i == 8) {
            return new StatusEffectInstance(StatusEffects.BLINDNESS, -1, 0);
        } else if (i == 9) {
            return new StatusEffectInstance(StatusEffects.REGENERATION, -1, 0);
        } else {
            return new StatusEffectInstance(StatusEffects.GLOWING, -1, 0);
        }
    }


}
