package net.greenjab.fixedminecraft.render;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.greenjab.fixedminecraft.mixin.enchanting.ChiseledBookshelfBlockInvoker;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/** Credit: Bawnorton */
public abstract class PlayerLookHelper {
    /**
     * Gets the book and itemstack that the player is looking at using {@link net.minecraft.block.ChiseledBookshelfBlock#getHitPos(BlockHitResult, Direction) ChiseledBookshelfBlock#getHitPos(BlockHitResult, Direction)} and {@link net.minecraft.block.ChiseledBookshelfBlock#getSlotForHitPos(Vec2f) ChiseledBookshelfBlock#getSlotForHitPos(Vec2f)}
     *
     * @param blockEntity the chiseled bookshelf block entity, fetched from world if null
     * @return the book and itemstack that the player is looking at
     */
    @SuppressWarnings("JavadocReference")
    public static ItemStack getLookingAtBook(ChiseledBookShelfBlockEntity blockEntity) {

        ItemStack book = ItemStack.EMPTY;
        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.hitResult instanceof BlockHitResult hit)) return book;
        // Get block entity from world if null
        assert minecraft.level != null;
        if (blockEntity == null) {
            Optional<ChiseledBookShelfBlockEntity> blockEntityOptional = minecraft.level.getBlockEntity(hit.getBlockPos(), BlockEntityType.CHISELED_BOOKSHELF);
            if (blockEntityOptional.isEmpty()) return book;
            blockEntity = blockEntityOptional.get();
        } else if (!hit.getBlockPos().equals(blockEntity.getBlockPos())) return book;
        // Get hit position on the block and the slot
        Optional<Vec2> hitPos = ChiseledBookshelfBlockInvoker.getHitPosOnFront(hit, blockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING));
        if (hitPos.isEmpty()) return book;
        OptionalInt slot = getSlotForHitPos(hit, minecraft.level.getBlockState(hit.getBlockPos()));
        return blockEntity.getItem(slot.getAsInt());
    }

    private static OptionalInt getSlotForHitPos(BlockHitResult hit, BlockState state) {
        return getHitPos(hit, state.getValue(HorizontalDirectionalBlock.FACING)).map(hitPos -> {
            int i = hitPos.y >= 0.5F ? 0 : 1;
            int j = getColumn(hitPos.x);
            return OptionalInt.of(j + i * 3);
        }).orElseGet(OptionalInt::empty);
    }

    private static Optional<Vec2> getHitPos(BlockHitResult hit, Direction facing) {
        Direction direction = hit.getDirection();
        if (facing != direction) {
            return Optional.empty();
        } else {
            BlockPos blockPos = hit.getBlockPos().offset(direction.getUnitVec3i());
            Vec3 vec3d = hit.getLocation().subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            double d = vec3d.x();
            double e = vec3d.y();
            double f = vec3d.z();

            return switch (direction) {
                case NORTH -> Optional.of(new Vec2((float)(1.0 - d), (float)e));
                case SOUTH -> Optional.of(new Vec2((float)d, (float)e));
                case WEST -> Optional.of(new Vec2((float)f, (float)e));
                case EAST -> Optional.of(new Vec2((float)(1.0 - f), (float)e));
                case DOWN, UP -> Optional.empty();
            };
        }
    }

    private static int getColumn(float x) {
        if (x < 0.375F) {
            return 0;
        } else {
            return x < 0.6875F ? 1 : 2;
        }
    }

    static int getMapBookId(ItemStack stack) {
        MapId mapIdComponent = stack.getOrDefault(DataComponents.MAP_ID, new MapId(-1));
        return mapIdComponent.id();
    }

    public static List<Component> getBookText(ItemStack book) {
        List<Component> displayText = new ArrayList<>();
        displayText.add(book.getItemName());
        if (book.has(DataComponents.CUSTOM_NAME) || book.is(Items.WRITTEN_BOOK)) {
            Style s = book.getHoverName().getStyle().withItalic(true).withColor(-1);
            displayText.add(book.getHoverName().toFlatList(s).getFirst());
        }
        if (book.is(ItemRegistry.MAP_BOOK)) {
            int id = getMapBookId(book);
            if (id!=-1) {
                Component t = Component.translatable("ID: " + (id+1));
                displayText.add(t);
            }

        }
        if (book.getItem() == Items.ENCHANTED_BOOK) {
            if (EnchantmentHelper.hasAnyEnchantments(book)) {
                ItemEnchantments itemEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(book);
                for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemEnchantments.entrySet()) {
                    Holder<Enchantment> registryEntry = entry.getKey();
                    displayText.add(Enchantment.getFullname(registryEntry, entry.getIntValue()));
                }
            }
        }
        return displayText;
    }


}

