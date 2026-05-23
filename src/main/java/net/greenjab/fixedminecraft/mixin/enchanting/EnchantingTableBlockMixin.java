package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.other.NewEnchantmentMenu;
import net.greenjab.fixedminecraft.registry.registries.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantingTableBlock.class)
public abstract class EnchantingTableBlockMixin {
    @Inject(method = "getMenuProvider", at = @At(value = "HEAD"), cancellable = true)
    private void newEnchantingMenu(BlockState state, Level level, BlockPos pos, CallbackInfoReturnable<MenuProvider> cir) {
        if (level.getBlockEntity(pos) instanceof EnchantingTableBlockEntity enchantingTable) {
            Component title = enchantingTable.getDisplayName();
            cir.setReturnValue(new SimpleMenuProvider(
                    (containerId, inventory, _) -> new NewEnchantmentMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)),
                    title));
        } else cir.setReturnValue( null);
    }

    @Inject(method = "animateTick", at = @At(value = "INVOKE",
                                             target = "Lnet/minecraft/world/level/block/EnchantingTableBlock;isValidBookShelf(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Z"
    ))
    private void rainbowParticle(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci, @Local(ordinal = 1) BlockPos offset) {
        if (random.nextInt(1) == 0 && isValidChiseledBookShelf(level, pos, offset)) {
            level.addParticle(
                    ParticleRegistry.CHISELED_ENCHANT,
                    pos.getX() + 0.5,
                    pos.getY() + 2.0,
                    pos.getZ() + 0.5,
                    offset.getX() + random.nextFloat() - 0.5,
                    offset.getY() - random.nextFloat() - 1.0F,
                    offset.getZ() + random.nextFloat() - 0.5
            );
        }
    }

    @Unique
    private static boolean isValidChiseledBookShelf(final Level level, final BlockPos pos, final BlockPos offset) {
        if (level.getBlockState(pos.offset(offset)).is(Blocks.CHISELED_BOOKSHELF)
               && level.getBlockState(pos.offset(offset.getX() / 2, offset.getY(), offset.getZ() / 2)).is(BlockTags.ENCHANTMENT_POWER_TRANSMITTER)){
            if (level.getBlockEntity(pos.offset(offset)) instanceof ChiseledBookShelfBlockEntity chiseledBookShelfBlockEntity) {
                for (ItemStack stack : chiseledBookShelfBlockEntity.getItems()) {
                    if (stack.is(Items.ENCHANTED_BOOK)) return true;
                }
            }
        }
        return false;
    }
}
