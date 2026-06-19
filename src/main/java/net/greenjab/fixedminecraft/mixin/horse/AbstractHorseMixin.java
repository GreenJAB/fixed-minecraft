package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Map;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal {
    @Unique
    private static final Map<Item, Float> rageChance = new Object2FloatArrayMap<>();

    @Unique
    private static final Map<
            Holder<MobEffect>,
            String> effectModififers = new Object2ObjectArrayMap<>();

    static {
        rageChance.put(Items.NETHERITE_HORSE_ARMOR, 1F);
        rageChance.put(Items.DIAMOND_HORSE_ARMOR, 0.9F);
        rageChance.put(Items.IRON_HORSE_ARMOR, 0.75F);
        rageChance.put(Items.GOLDEN_HORSE_ARMOR, 0.6F);
        rageChance.put(ItemRegistry.CHAINMAIL_HORSE_ARMOR, 0.5F);
        rageChance.put(Items.COPPER_HORSE_ARMOR, 0.45F);
        rageChance.put(Items.LEATHER_HORSE_ARMOR, 0.3F);

        effectModififers.put(MobEffects.SPEED, "movement_speed");
        effectModififers.put(MobEffects.JUMP_BOOST, "jump_strength");
        effectModififers.put(MobEffects.REGENERATION, "max_health");
    }

    protected AbstractHorseMixin(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    @Inject(method = "standIfPossible", at = @At("HEAD"), cancellable = true)
    private void rejectAngryWhenDrip(CallbackInfo ci) {
        ItemStack armor = equipment.get(EquipmentSlot.BODY);
        float chance = rageChance.getOrDefault(armor.getItem(), 0F);
        if (Math.random() <= chance) ci.cancel();
    }

    @ModifyArg(method = "setOffspringAttribute", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/equine/AbstractHorse;createOffspringAttribute(DDDDLnet/minecraft/util/RandomSource;)D"
    ), index = 0)
    private double modifyBaseAttributeParent1(double original,
                                              @Local(argsOnly = true) Holder<Attribute> attribute) {
        AgeableMob PE = this;
        return modifyAttribute(original, attribute.value(), PE.getActiveEffects());
    }

    @ModifyArg(method = "setOffspringAttribute", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/equine/AbstractHorse;createOffspringAttribute(DDDDLnet/minecraft/util/RandomSource;)D"
    ), index = 1)
    private double modifyBaseAttributeParent2(double original,
                                              @Local(argsOnly = true) Holder<Attribute> attribute,
                                              @Local(argsOnly = true)
                                              AgeableMob partner) {
        return modifyAttribute(original, attribute.value(), partner.getActiveEffects());
    }

    @Unique
    private double modifyAttribute(double original, Attribute attribute, Collection<MobEffectInstance> effects) {
        MobEffectInstance chosenEffect = getStatusEffectInstance(effects);
        if (chosenEffect != null) {
            if (attribute.getDescriptionId().contains(effectModififers.get(chosenEffect.getEffect()))) {
                double d = 0;
                if (attribute.getDescriptionId().contains("max_health")) { d = 2; }
                if (attribute.getDescriptionId().contains("jump_strength")) { d = 0.08; }
                if (attribute.getDescriptionId().contains("movement_speed")) { d = 0.03; }
                d*=(chosenEffect.getAmplifier()+1 + (chosenEffect.isAmbient()?0:1));
                return original + d;
            }
        }

        return original;
    }

    @Unique
    @org.jetbrains.annotations.Nullable
    private static MobEffectInstance getStatusEffectInstance(Collection<MobEffectInstance> effects) {
        MobEffectInstance chosenEffect = null;
        int longestDuration = -1;
        int highestLevel = -1;

        for (MobEffectInstance effect : effects) {
            if (effectModififers.containsKey(effect.getEffect())) {
                int lvl = effect.getAmplifier() + (effect.isAmbient()?0:1);
                int dur = effect.getDuration();
                if (lvl > highestLevel) {
                    highestLevel = lvl;
                    longestDuration = dur;
                    chosenEffect = effect;
                } else {
                    if (effect.isInfiniteDuration()) {
                        longestDuration = 999999999;
                        chosenEffect = effect;
                    } else if (dur > longestDuration) {
                        longestDuration = dur;
                        chosenEffect = effect;
                    }
                }
            }
        }
        return chosenEffect;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/equine/AbstractHorse;isStanding()Z"))
    private void sprintCheck(CallbackInfo ci) {
        AbstractHorse AHE = (AbstractHorse) (Object)this;
        if (AHE.hasControllingPassenger() && AHE.onGround()) {
            if (!AHE.getControllingPassenger().isSprinting()) {
                Vec3 v= AHE.getDeltaMovement();
                double d = v.horizontalDistance();
                if (d > 0.01) {
                    double s = Math.min(0.1, d);
                    AHE.setDeltaMovement(s * v.x / d, v.y, s * v.z / d);
                }
            }
        }
        AHE.refreshDimensions();

        if (AHE.entityTags().contains("locate") && AHE.tickCount>20 * 60 * 5) {
            AHE.getAttributes().getInstance(Attributes.WAYPOINT_TRANSMIT_RANGE).setBaseValue(0);
            AHE.removeTag("locate");
        }
    }

    @Inject(method = "getDismountLocationForPassenger", at = @At("HEAD"))
    private void addLocaterBarIcon(LivingEntity passenger, CallbackInfoReturnable<Vec3> cir) {
        if (passenger instanceof ServerPlayer) {
            AbstractHorse AHE = (AbstractHorse) (Object) this;
            if (!AHE.isTamed())return;
            AHE.tickCount = 0;
            AHE.addTag("locate");
            AHE.getAttributes().getInstance(Attributes.WAYPOINT_TRANSMIT_RANGE).setBaseValue(100);
            String s = "/waypoint modify " + AHE.getStringUUID() + " style set horse";
            AHE.level().getServer().getCommands().performPrefixedCommand(createCommandSource((ServerLevel) AHE.level(), AHE.blockPosition()), s);
        }
    }

    @Inject(method = "doPlayerRide", at = @At("HEAD"))
    private void removeLocaterBarIcon(Player player, CallbackInfo ci) {
        AbstractHorse AHE = (AbstractHorse) (Object) this;
        AHE.getAttributes().getInstance(Attributes.WAYPOINT_TRANSMIT_RANGE).setBaseValue(0);
        AHE.removeTag("locate");
    }

    @Unique
    private static CommandSourceStack createCommandSource(ServerLevel world, BlockPos pos) {
        String string = "Sign";
        Component text = Component.literal("Sign");
        return new CommandSourceStack(
                CommandSource.NULL, Vec3.atCenterOf(pos), Vec2.ZERO, world, LevelBasedPermissionSet.GAMEMASTER, string, text, world.getServer(), null
        );
    }
}
