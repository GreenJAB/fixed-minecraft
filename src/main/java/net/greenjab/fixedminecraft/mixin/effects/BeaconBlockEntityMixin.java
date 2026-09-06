package net.greenjab.fixedminecraft.mixin.effects;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.registries.MobEffectRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ARGB;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBeamOwner;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin {

    @Inject(method = "updateBase", at = @At("HEAD"), cancellable = true)
    private static void PyramidNeedsToBeSameMaterial(Level level, int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
        if (!FixedMinecraft.gameRules.modified_beacon) return;
        int i = 0;
        Block base = level.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
        for(int j = 1; j <= 10; i = j++) {
            int k = y - j;
            if (k < level.getMinY()) break;
            boolean bl = true;
            for(int l = x - j; l <= x + j && bl; ++l) {
                for(int m = z - j; m <= z + j; ++m) {
                    if (!level.getBlockState(new BlockPos(l, k, m)).is(BlockTags.BEACON_BASE_BLOCKS)) {
                        bl = false;
                        break;
                    }
                    if (base.defaultBlockState().is(BlockTags.COPPER)) {
                        if (!level.getBlockState(new BlockPos(l, k, m)).is(BlockTags.COPPER)) {
                            bl = false;
                            break;
                        }
                    } else if (!level.getBlockState(new BlockPos(l, k, m)).is(base)) {
                        bl = false;
                        break;
                    }
                }
            }
            if (!bl) break;
        }
        cir.setReturnValue(i);
    }

    @Unique private static final Map<BlockState, Holder<MobEffect>> vanillaEffects = Map.of(
            Blocks.GOLD_BLOCK.defaultBlockState(), MobEffects.HASTE,
            Blocks.EMERALD_BLOCK.defaultBlockState(), MobEffects.JUMP_BOOST,
            Blocks.IRON_BLOCK.defaultBlockState(), MobEffects.STRENGTH,
            Blocks.DIAMOND_BLOCK.defaultBlockState(), MobEffects.REGENERATION,
            Blocks.ANCIENT_DEBRIS.defaultBlockState(), MobEffects.RESISTANCE,
            Blocks.NETHERITE_BLOCK.defaultBlockState(), MobEffects.RESISTANCE);

    @Unique private static final Map<BlockState, Holder<MobEffect>> newEffects = Map.of(
            Blocks.COAL_BLOCK.defaultBlockState(), MobEffects.NIGHT_VISION,
            Blocks.REDSTONE_BLOCK.defaultBlockState(), MobEffectRegistry.REACH,
            Blocks.LAPIS_BLOCK.defaultBlockState(), MobEffects.SATURATION,
            Blocks.QUARTZ_BLOCK.defaultBlockState(), MobEffects.INVISIBILITY,
            Blocks.GLOWSTONE.defaultBlockState(), MobEffects.GLOWING,
            Blocks.OBSIDIAN.defaultBlockState(), MobEffects.FIRE_RESISTANCE);

    @Inject(method = "applyEffects", at = @At("HEAD"), cancellable = true)
    private static void ModifyBeaconEffects(Level level, BlockPos worldPosition, int levels, @Nullable Holder<MobEffect> primaryPower,
                                            @Nullable Holder<MobEffect> secondaryPower, CallbackInfo ci) {
        if (!FixedMinecraft.gameRules.modified_beacon) return;
        BlockState blockState = level.getBlockState(worldPosition.below());
        primaryPower = vanillaEffects.get(blockState);
        if (primaryPower == null) {
            primaryPower = newEffects.get(blockState);
        }
        if (blockState.is(BlockTags.COPPER)){
            primaryPower = MobEffects.SPEED;
        }
        int statusLevel = levels >= 3?1:0;
        if (blockState == Blocks.NETHERITE_BLOCK.defaultBlockState()) statusLevel+=2;

        if (!level.isClientSide() && primaryPower != null) {
            @Nullable Holder<MobEffect> finalPrimaryPower = primaryPower;
            int finalStatusLevel = statusLevel;

            double d = (levels * 20 + 10);
            int j = (9 + levels * 2) * 20;

            int y = worldPosition.getY();
            for (; y < level.getHeight(); y++) {
                if (level.getBlockState(new BlockPos(worldPosition.getX(), y, worldPosition.getZ())).is(Blocks.TINTED_GLASS)) break;
            }
            AABB box = (new AABB(worldPosition)).expandTowards(0.0, y, 0.0).inflate(d);

            level.getEntitiesOfClass(Player.class, box).forEach(player -> {
                player.addEffect(new MobEffectInstance(finalPrimaryPower, j, finalStatusLevel, true, false, true));
                if (finalStatusLevel==1 && finalPrimaryPower.is(MobEffects.REGENERATION)) {
                    if (player instanceof ServerPlayer SPE)
                        CriteriaTriggers.CONSUME_ITEM.trigger(SPE, Items.BEACON.getDefaultInstance());
                }});

            level.getEntitiesOfClass(AbstractHorse.class, box).forEach(horse -> {
                if (horse.isTamed()) {
                    if (!finalPrimaryPower.is(MobEffects.INVISIBILITY) || horse.hasEffect(MobEffects.GLOWING))
                        horse.addEffect(new MobEffectInstance(finalPrimaryPower, j, finalStatusLevel, true, false));
                }});

            level.getEntitiesOfClass(TamableAnimal.class, box).forEach(pet -> {
                if (pet.isTame()) {
                    if (!finalPrimaryPower.is(MobEffects.INVISIBILITY) || pet.hasEffect(MobEffects.GLOWING))
                        pet.addEffect(new MobEffectInstance(finalPrimaryPower, j, finalStatusLevel, true, false));
                }});
        }
        ci.cancel();
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"))
    private static Block noBeam(BlockState instance, Operation<Block> original) {
        if (instance.is(Blocks.TINTED_GLASS)) return Blocks.WHITE_STAINED_GLASS;
        return original.call(instance);
    }
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/DyeColor;getTextureDiffuseColor()I"))
    private static int noBeam2(DyeColor instance, Operation<Integer> original, @Local(ordinal = 1) BlockState state) {
        if (state.is(Blocks.TINTED_GLASS)) return ARGB.color(0, 0, 0, 0);
        return original.call(instance);
    }
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/List;clear()V"))
    private static void noBeam3(List<BeaconBeamOwner.Section> instance, Operation<Void> original, @Local BeaconBeamOwner.Section lastBeamSection) {
        if (ARGB.alpha(lastBeamSection.getColor())==255) original.call(instance);
    }
}
