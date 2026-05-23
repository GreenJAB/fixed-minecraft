package net.greenjab.fixedminecraft.mixin.effects;

import net.greenjab.fixedminecraft.registry.registries.MobEffectRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin {

    @Inject(method = "updateBase", at = @At("HEAD"), cancellable = true)
    private static void ModifyBeaconPyramid(Level level, int x, int y, int z,
                                            CallbackInfoReturnable<Integer> cir) {
        int i = 0;
        Block base = level.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
        for(int j = 1; j <= 10; i = j++) {
            int k = y - j;
            if (k < level.getMinY()) {
                break;
            }

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

            if (!bl) {
                break;
            }
        }

        cir.setReturnValue(i);

    }

    @Unique
    private static Map<BlockState, Holder<MobEffect>> vanillaEffects = Map.of(
            Blocks.GOLD_BLOCK.defaultBlockState(), MobEffects.HASTE,
            Blocks.EMERALD_BLOCK.defaultBlockState(), MobEffects.JUMP_BOOST,
            Blocks.IRON_BLOCK.defaultBlockState(), MobEffects.STRENGTH,
            Blocks.DIAMOND_BLOCK.defaultBlockState(), MobEffects.REGENERATION,
            Blocks.ANCIENT_DEBRIS.defaultBlockState(), MobEffects.RESISTANCE,
            Blocks.NETHERITE_BLOCK.defaultBlockState(), MobEffects.RESISTANCE);

    @Unique
    private static Map<BlockState, Holder<MobEffect>> newEffects = Map.of(
            Blocks.COAL_BLOCK.defaultBlockState(), MobEffects.NIGHT_VISION,
            Blocks.REDSTONE_BLOCK.defaultBlockState(), MobEffectRegistry.REACH,
            Blocks.LAPIS_BLOCK.defaultBlockState(), MobEffects.SATURATION,
            Blocks.QUARTZ_BLOCK.defaultBlockState(), MobEffects.INVISIBILITY);

    @Inject(method = "applyEffects", at = @At("HEAD"), cancellable = true)
    private static void ModifyBeaconEffects(Level level, BlockPos worldPosition, int levels, @Nullable Holder<MobEffect> primaryPower,
                                            @Nullable Holder<MobEffect> secondaryPower, CallbackInfo ci) {
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
            double d = (levels * 20 + 10);

            int j = (9 + levels * 2) * 20;
            AABB box = (new AABB(worldPosition)).inflate(d).expandTowards(0.0, level.getHeight(), 0.0);
            List<Player> list = level.getEntitiesOfClass(Player.class, box);
            Iterator<Player> var11 = list.iterator();
            Player playerEntity;
            while(var11.hasNext()) {
                playerEntity = var11.next();
                playerEntity.addEffect(new MobEffectInstance(primaryPower, j, statusLevel, true, false, true));

               if (statusLevel==1 && blockState == Blocks.DIAMOND_BLOCK.defaultBlockState()) {
                   if (playerEntity instanceof ServerPlayer SPE)
                       CriteriaTriggers.CONSUME_ITEM.trigger(SPE, Items.BEACON.getDefaultInstance());
               }
            }


            List<AbstractHorse> listHorse = level.getEntitiesOfClass(AbstractHorse.class, box);
            Iterator<AbstractHorse> var11Horse = listHorse.iterator();
            AbstractHorse horse;
            while(var11Horse.hasNext()) {
                horse = var11Horse.next();
                if (horse.isTamed()) {
                    horse.addEffect(new MobEffectInstance(primaryPower, j, statusLevel, true, false));
                }
            }

            List<TamableAnimal> listPet = level.getEntitiesOfClass(TamableAnimal.class, box);
            Iterator<TamableAnimal> var11Pet = listPet.iterator();
            TamableAnimal pet;
            while(var11Pet.hasNext()) {
                pet = var11Pet.next();
                if (pet.isTame()) {
                    pet.addEffect(new MobEffectInstance(primaryPower, j, statusLevel, true, false));
                }
            }
        }
        ci.cancel();
    }
}
