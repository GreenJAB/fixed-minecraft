package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.greenjab.fixedminecraft.mobs.ArmorTrimmer;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.gamerules.GameRule;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(Mob.class)
public abstract class MobMixin<T extends Mob> extends LivingEntity {

    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "populateDefaultEquipmentSlots", at = @At(value = "HEAD"),cancellable = true)
    private void Armor(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci) {
        int y= this.blockPosition().getY();
        boolean pale = this.level().getBiome(this.blockPosition()).is(Biomes.PALE_GARDEN);
        float f = this.level().getDifficulty() == Difficulty.HARD ? 0.175F : 0.075F;
        if (pale) {
            f*=2.25f;
            this.addTag("pale");
        }
        if (y < this.level().getSeaLevel()) f += (this.level().getSeaLevel() - y) / (128 * 10f);
        EquipmentSlot[] var6 = EquipmentSlot.values();
        for (EquipmentSlot equipmentSlot : var6) {
            if (random.nextFloat() < f * difficulty.getSpecialMultiplier()) {
                int i = 2;
                if (random.nextFloat() < f) i++;
                if (random.nextFloat() < f) i++;
                if (random.nextFloat() < f) i++;
                if (i ==2) i = 0;

                Mob ME = (Mob) (Object) this;
                if (ME instanceof Husk) i = 2;

                if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                    ItemStack itemStack = this.getItemBySlot(equipmentSlot);
                    if (itemStack.isEmpty()) {
                        ItemStack item = new ItemStack(Objects.requireNonNull(Mob.getEquipmentForSlot(equipmentSlot, i)));
                        if (i==0) {
                            List<DyeColor> colour = List.of(DyeColor.byId(this.level().getRandom().nextInt(16)));
                            item = DyedItemColor.applyDyes(item, colour);
                        }
                        this.setItemSlot(equipmentSlot, ArmorTrimmer.trimAtChanceIfTrimable(item, this.random, this.level().registryAccess(), pale));
                    }
                }
            }
        }
        ci.cancel();
    }

    @Redirect(method = "dropCustomDeathLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isDamageableItem()Z"))
    private boolean copperDurability(ItemStack instance) {
        if (instance.is(ModTags.COPPER_ARMOR)) {
            return false;
        }
        return instance.isDamageableItem();
    }

    @ModifyVariable(
            method = "enchantSpawnedEquipment(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/util/RandomSource;FLnet/minecraft/world/DifficultyInstance;)V",
            at = @At(value = "HEAD", ordinal = 0),
            argsOnly = true
    )
    private float applySuperEnchantArmor(
            float chance) {
        return chance * (this.level().getBiome(this.blockPosition()).is(Biomes.PALE_GARDEN)?1.5f:1);
    }

    @ModifyArg(method = "enchantSpawnedEquipment(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/util/RandomSource;FLnet/minecraft/world/DifficultyInstance;)V", at = @At(value = "INVOKE",
                                                                                                                                                                                                                target = "Lnet/minecraft/world/entity/Mob;setItemSlot(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V"), index = 1)
    private ItemStack applySuperEnchantArmor(
            ItemStack stack) {
        return FixedMinecraftEnchantmentHelper.applySuperEnchants(stack, random, this.level().getBiome(this.blockPosition()).is(Biomes.PALE_GARDEN));
    }

    @Inject(method = "finalizeSpawn", at=@At(value = "HEAD"))
    private void addStuff(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, SpawnGroupData groupData,
                          CallbackInfoReturnable<SpawnGroupData> cir){
        Mob LE = (Mob)(Object)this;
        int y= LE.blockPosition().getY();
        if (LE instanceof Monster && level.dimensionType().hasSkyLight()) {
            addEffect(level, difficulty, LE, y);
            addModifiers(level, LE);

        }
    }

    @Unique
    private void addModifiers(ServerLevelAccessor world, Mob LE) {
        int i = 0;
        if (world.getDifficulty() == Difficulty.NORMAL) i = 1;
        if (world.getDifficulty() == Difficulty.HARD) i = 2;
        if (this.level().getBiome(this.blockPosition()).is(Biomes.PALE_GARDEN)) i = 3;
        float h = i*3*gaussian();
        increaseHealth(LE, h);
        increaseSpeed(LE, i);
    }

    @Unique
    private static void increaseSpeed(Mob LE, int i) {
        if (LE.getAttribute(Attributes.MOVEMENT_SPEED)!=null) {
            if (!LE.isBaby()) LE.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(
                    LE.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * (1 + (i * 0.15f * gaussian())));
        }
    }

    @Unique
    private static void increaseHealth(Mob LE, float h) {
        if (LE.getAttribute(Attributes.MAX_HEALTH)!=null) {
            LE.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
                    LE.getAttributeBaseValue(Attributes.MAX_HEALTH) + h);
            LE.setHealth(LE.getHealth() + h);
        }
    }
    @Unique
    private void addEffect(ServerLevelAccessor world, DifficultyInstance localDifficulty, Mob LE, int y){
        if (random.nextFloat() < 0.2f * localDifficulty.getSpecialMultiplier()) {
            boolean pale = this.level().getBiome(this.blockPosition()).is(Biomes.PALE_GARDEN);
            if ((world.getBrightness(LightLayer.SKY, LE.blockPosition()) < 7 ||pale)  && !(LE instanceof Spider)) {
                if ((random.nextFloat() < (LE.level().getSeaLevel() - y) / 128f ||pale)) {
                    MobEffectInstance effect = getEffect(random, LE);
                    LE.addEffect(effect);
                }
            }
        }
    }

    @Unique
    private static float gaussian(){
        return (float)(Math.tan(0.87433408*Math.PI*(Math.random()-0.5f))/10.0f)+0.5f;
    }

    @Unique
    public MobEffectInstance getEffect(RandomSource random, LivingEntity LE) {
        int l = 6;
        if (LE instanceof Creeper) l+=5 ;
        if (LE instanceof Skeleton) l+=3;
        int i = random.nextInt(l);

        if (i == 0) {
            return new MobEffectInstance(MobEffects.SPEED, -1, 0);
        } else if (i == 1) {
            return new MobEffectInstance(MobEffects.STRENGTH, -1, 0);
        } else if (i == 2) {
            return new MobEffectInstance(MobEffects.JUMP_BOOST, -1, 1);
        } else if (i == 3) {
            return new MobEffectInstance(MobEffects.SLOW_FALLING, -1, 0);
        } else if (i == 4) {
            return new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 0);
        } else if (i == 5) {
            return new MobEffectInstance(MobEffects.ABSORPTION, -1, 0);
        } else if (i == 6) {
            return new MobEffectInstance(MobEffects.NAUSEA, -1, 0);
        } else if (i == 7) {
            return new MobEffectInstance(MobEffects.MINING_FATIGUE, -1, 0);
        } else if (i == 8) {
            return new MobEffectInstance(MobEffects.WEAKNESS, -1, 0);
        } else if (i == 9) {
            return new MobEffectInstance(MobEffects.REGENERATION, -1, 0);
        } else if (i == 10) {
            return new MobEffectInstance(MobEffects.LUCK, -1, 0);
        }
        return new MobEffectInstance(MobEffects.ABSORPTION, -1, 0);
    }

    @Inject(method = "convertTo(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/ConversionParams;Lnet/minecraft/world/entity/EntitySpawnReason;Lnet/minecraft/world/entity/ConversionParams$AfterConversion;)Lnet/minecraft/world/entity/Mob;", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ConversionType;convert(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/world/entity/Mob;Lnet/minecraft/world/entity/ConversionParams;)V"
    ))
    private void removeIronGolemTagOnConversion(EntityType<T> entityType, ConversionParams params, EntitySpawnReason spawnReason,
                                                ConversionParams.AfterConversion<T> afterConversion, CallbackInfoReturnable<T> cir){
        Mob ME = (Mob)(Object)this;
        ME.removeTag("iron_golem");
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/level/gamerules/GameRules;MOB_GRIEFING:Lnet/minecraft/world/level/gamerules/GameRule;",
            opcode = Opcodes.GETSTATIC
    ))
    public GameRule<Boolean> passiveMobGriefing(GameRule<Boolean> original) {
        Mob mob = (Mob)(Object)this;
        if (mob.getType().isAllowedInPeaceful())
            return GameRuleRegistry.PEACEFUL_MOB_GRIEFING;
        return original;
    }

}
