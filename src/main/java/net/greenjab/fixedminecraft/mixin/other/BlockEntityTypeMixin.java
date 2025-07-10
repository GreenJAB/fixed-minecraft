package net.greenjab.fixedminecraft.mixin.other;

import com.mojang.datafixers.types.Type;
import net.greenjab.fixedminecraft.registry.registries.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HangingSignBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;


@Mixin(BlockEntityType.class)
public abstract class BlockEntityTypeMixin {


    @Shadow
    private static <T extends BlockEntity> BlockEntityType<T> create(String id, BlockEntityType.Builder<T> builder) {
        return null;
    }

    @Redirect(method="<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntityType;create(Ljava/lang/String;Lnet/minecraft/block/entity/BlockEntityType$Builder;)Lnet/minecraft/block/entity/BlockEntityType;", ordinal = 0), slice = @Slice( from = @At(value = "FIELD",
           target = "Lnet/minecraft/block/entity/BlockEntityType;DROPPER:Lnet/minecraft/block/entity/BlockEntityType;")))
    private static <T extends SignBlockEntity> BlockEntityType<SignBlockEntity> sign(String id, BlockEntityType.Builder<T> builder) {
        return create("sign", BlockEntityType.Builder.create(SignBlockEntity::new,
                Blocks.OAK_SIGN,Blocks.SPRUCE_SIGN,Blocks.BIRCH_SIGN,Blocks.ACACIA_SIGN,Blocks.CHERRY_SIGN,
                Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN,Blocks.OAK_WALL_SIGN,
                Blocks.SPRUCE_WALL_SIGN,Blocks.BIRCH_WALL_SIGN,Blocks.ACACIA_WALL_SIGN,Blocks.CHERRY_WALL_SIGN,
                Blocks.JUNGLE_WALL_SIGN,Blocks.DARK_OAK_WALL_SIGN,Blocks.CRIMSON_SIGN,
                Blocks.CRIMSON_WALL_SIGN,Blocks.WARPED_SIGN,Blocks.WARPED_WALL_SIGN,Blocks.MANGROVE_SIGN,
                Blocks.MANGROVE_WALL_SIGN,Blocks.BAMBOO_SIGN,Blocks.BAMBOO_WALL_SIGN,
                BlockRegistry.AZALEA_SIGN, BlockRegistry.AZALEA_WALL_SIGN
                ));
    }

    @Redirect(method="<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntityType;create(Ljava/lang/String;Lnet/minecraft/block/entity/BlockEntityType$Builder;)Lnet/minecraft/block/entity/BlockEntityType;", ordinal = 0), slice = @Slice( from = @At(value = "FIELD",
              target = "Lnet/minecraft/block/entity/BlockEntityType;SIGN:Lnet/minecraft/block/entity/BlockEntityType;")))
    private static <T extends SignBlockEntity> BlockEntityType<SignBlockEntity> hanging_sign(String id, BlockEntityType.Builder<T> builder) {
        return create("hanging_sign",BlockEntityType.Builder.create(HangingSignBlockEntity::new,
                Blocks.OAK_HANGING_SIGN,Blocks.SPRUCE_HANGING_SIGN,Blocks.BIRCH_HANGING_SIGN,
                Blocks.ACACIA_HANGING_SIGN, Blocks.CHERRY_HANGING_SIGN,Blocks.JUNGLE_HANGING_SIGN,
                Blocks.DARK_OAK_HANGING_SIGN,Blocks.CRIMSON_HANGING_SIGN,
                Blocks.WARPED_HANGING_SIGN,Blocks.MANGROVE_HANGING_SIGN,Blocks.BAMBOO_HANGING_SIGN,
                Blocks.OAK_WALL_HANGING_SIGN,Blocks.SPRUCE_WALL_HANGING_SIGN,Blocks.BIRCH_WALL_HANGING_SIGN,
                Blocks.ACACIA_WALL_HANGING_SIGN,Blocks.CHERRY_WALL_HANGING_SIGN,Blocks.JUNGLE_WALL_HANGING_SIGN,
                Blocks.DARK_OAK_WALL_HANGING_SIGN,Blocks.CRIMSON_WALL_HANGING_SIGN,
                Blocks.WARPED_WALL_HANGING_SIGN,Blocks.MANGROVE_WALL_HANGING_SIGN,Blocks.BAMBOO_WALL_HANGING_SIGN,
                BlockRegistry.AZALEA_HANGING_SIGN, BlockRegistry.AZALEA_WALL_HANGING_SIGN
        ));
    }


}
