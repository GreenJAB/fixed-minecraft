package net.greenjab.fixedminecraft.mixin;

import net.greenjab.fixedminecraft.blocks.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {

    /*@ModifyConstant(method = "updateLevel", constant = @Constant(intValue = 4))
    private static int largerBeaconArea(int value) {
        return 10;
    }*/


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

    @Inject(method = "applyPlayerEffects", at = @At("HEAD"), cancellable = true)
    private static void ModifyBeaconEffects(World world, BlockPos pos, int beaconLevel, StatusEffect primaryEffect,
                                            StatusEffect secondaryEffect, CallbackInfo ci) {
        int statusLevel = beaconLevel >= 3?1:0;
        switch (world.getBlockState(pos.down()).getBlock().toString()) {
            case "Block{minecraft:gold_block}":
                primaryEffect = StatusEffects.HASTE;
                break;
            case "Block{minecraft:emerald_block}":
                primaryEffect = StatusEffects.JUMP_BOOST;
                break;
            case "Block{minecraft:iron_block}":
                primaryEffect = StatusEffects.STRENGTH;
                break;
            case "Block{minecraft:diamond_block}":
                primaryEffect = StatusEffects.REGENERATION;
                break;
            case "Block{minecraft:ancient_debris}":
                primaryEffect = StatusEffects.RESISTANCE;
                break;
            case "Block{minecraft:netherite_block}":
                statusLevel+=2;
                primaryEffect = StatusEffects.RESISTANCE;
                break;
            case "Block{minecraft:waxed_copper_block}":
                primaryEffect = StatusEffects.SPEED;
                break;
            case "Block{minecraft:coal_block}":
                primaryEffect = StatusEffects.NIGHT_VISION;
                break;
            case "Block{minecraft:redstone_block}":
                primaryEffect = StatusEffects.HEALTH_BOOST;
                break;
            case "Block{minecraft:lapis_block}":
                primaryEffect = StatusEffects.SATURATION;
                break;
        }

        if (!world.isClient && primaryEffect != null) {
            double d = (double)(beaconLevel * 20 + 10);
            int i = 0;
            if (beaconLevel >= 4 && primaryEffect == secondaryEffect) {
                i = 1;
            }

            int j = (9 + beaconLevel * 2) * 20;
            Box box = (new Box(pos)).expand(d).stretch(0.0, (double)world.getHeight(), 0.0);
            List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, box);
            Iterator var11 = list.iterator();

            PlayerEntity playerEntity;
            while(var11.hasNext()) {
                playerEntity = (PlayerEntity)var11.next();
                playerEntity.addStatusEffect(new StatusEffectInstance(primaryEffect, j, statusLevel, true, false));
            }
        }
    }
}
