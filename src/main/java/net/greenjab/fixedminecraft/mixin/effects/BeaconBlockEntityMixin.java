package net.greenjab.fixedminecraft.mixin.effects;

import net.greenjab.fixedminecraft.registry.registries.StatusRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {

    @Inject(method = "updateLevel", at = @At("HEAD"), cancellable = true)
    private static void ModifyBeaconPyramid(World world, int x, int y, int z,
                                     CallbackInfoReturnable<Integer> cir) {

        int i = 0;
        Block base = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
        for(int j = 1; j <= 10; i = j++) {
            int k = y - j;
            if (k < world.getBottomY()) {
                break;
            }

            boolean bl = true;

            for(int l = x - j; l <= x + j && bl; ++l) {
                for(int m = z - j; m <= z + j; ++m) {
                    if (!world.getBlockState(new BlockPos(l, k, m)).isIn(BlockTags.BEACON_BASE_BLOCKS)) {
                        bl = false;
                        break;
                    }
                    if (!world.getBlockState(new BlockPos(l, k, m)).isOf(base)) {
                        bl = false;
                        break;
                    }
                }
            }

            if (!bl) {
                break;
            }
        }

        cir.setReturnValue(i);

    }

    @Unique
    private static Map<BlockState, RegistryEntry<StatusEffect>> vanillaEffects = Map.of(
            Blocks.GOLD_BLOCK.getDefaultState(), StatusEffects.HASTE,
            Blocks.EMERALD_BLOCK.getDefaultState(), StatusEffects.JUMP_BOOST,
            Blocks.IRON_BLOCK.getDefaultState(), StatusEffects.STRENGTH,
            Blocks.DIAMOND_BLOCK.getDefaultState(), StatusEffects.REGENERATION,
            Blocks.ANCIENT_DEBRIS.getDefaultState(), StatusEffects.RESISTANCE,
            Blocks.NETHERITE_BLOCK.getDefaultState(), StatusEffects.RESISTANCE,
            Blocks.WAXED_COPPER_BLOCK.getDefaultState(), StatusEffects.SPEED);

    @Unique
    private static Map<BlockState, RegistryEntry<StatusEffect>> newEffects = Map.of(
            Blocks.COAL_BLOCK.getDefaultState(), StatusEffects.NIGHT_VISION,
            Blocks.REDSTONE_BLOCK.getDefaultState(), StatusRegistry.REACH,
            Blocks.LAPIS_BLOCK.getDefaultState(), StatusEffects.SATURATION,
            Blocks.QUARTZ_BLOCK.getDefaultState(), StatusEffects.INVISIBILITY);

    @Inject(method = "applyPlayerEffects", at = @At("HEAD"), cancellable = true)
    private static void ModifyBeaconEffects(World world, BlockPos pos, int beaconLevel, @Nullable RegistryEntry<StatusEffect> primaryEffect,
                                            @Nullable RegistryEntry<StatusEffect> secondaryEffect, CallbackInfo ci) {

        primaryEffect = vanillaEffects.get(world.getBlockState(pos.down()));
        if (primaryEffect == null) {
            primaryEffect = newEffects.get(world.getBlockState(pos.down()));
        }
        int statusLevel = beaconLevel >= 3?1:0;
        if (world.getBlockState(pos.down()) == Blocks.NETHERITE_BLOCK.getDefaultState()) statusLevel+=2;

        if (!world.isClient && primaryEffect != null) {
            double d = (beaconLevel * 20 + 10);

            int j = (9 + beaconLevel * 2) * 20;
            Box box = (new Box(pos)).expand(d).stretch(0.0, world.getHeight(), 0.0);
            List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, box);
            Iterator<PlayerEntity> var11 = list.iterator();
            PlayerEntity playerEntity;
            while(var11.hasNext()) {
                playerEntity = var11.next();
                playerEntity.addStatusEffect(new StatusEffectInstance(primaryEffect, j, statusLevel, true, false, true));
            }


            List<AbstractHorseEntity> listHorse = world.getNonSpectatingEntities(AbstractHorseEntity.class, box);
            Iterator<AbstractHorseEntity> var11Horse = listHorse.iterator();
            AbstractHorseEntity horse;
            while(var11Horse.hasNext()) {
                horse = var11Horse.next();
                if (horse.isTame()) {
                    horse.addStatusEffect(new StatusEffectInstance(primaryEffect, j, statusLevel, true, false));
                }
            }

            List<TameableEntity> listPet = world.getNonSpectatingEntities(TameableEntity.class, box);
            Iterator<TameableEntity> var11Pet = listPet.iterator();
            TameableEntity pet;
            while(var11Pet.hasNext()) {
                pet = var11Pet.next();
                if (pet.isTamed()) {
                    pet.addStatusEffect(new StatusEffectInstance(primaryEffect, j, statusLevel, true, false));
                }
            }
        }
        ci.cancel();
    }
}
