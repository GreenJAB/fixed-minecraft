package net.greenjab.fixedminecraft.render;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.greenjab.fixedminecraft.mixin.enchanting.ChiseledBookshelfBlockInvoker;
import net.greenjab.fixedminecraft.registry.ItemRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public abstract class PlayerLookHelper {
    /**
     * Gets the book and itemstack that the player is looking at using {@link net.minecraft.block.ChiseledBookshelfBlock#getHitPos(BlockHitResult, Direction) ChiseledBookshelfBlock#getHitPos(BlockHitResult, Direction)} and {@link net.minecraft.block.ChiseledBookshelfBlock#getSlotForHitPos(Vec2f) ChiseledBookshelfBlock#getSlotForHitPos(Vec2f)}
     *
     * @param blockEntity the chiseled bookshelf block entity, fetched from world if null
     * @return the book and itemstack that the player is looking at
     */
    @SuppressWarnings("JavadocReference")
    public static ItemStack getLookingAtBook(ChiseledBookshelfBlockEntity blockEntity) {
         ItemStack book = ItemStack.EMPTY;
        MinecraftClient client = MinecraftClient.getInstance();
        if (!(client.crosshairTarget instanceof BlockHitResult hit)) return book;
        // Get block entity from world if null
        assert client.world != null;
        if (blockEntity == null) {
            Optional<ChiseledBookshelfBlockEntity> blockEntityOptional = client.world.getBlockEntity(hit.getBlockPos(), BlockEntityType.CHISELED_BOOKSHELF);
            if (blockEntityOptional.isEmpty()) return book;
            blockEntity = blockEntityOptional.get();
        } else if (!hit.getBlockPos().equals(blockEntity.getPos())) return book;
        // Get hit position on the block and the slot
        Optional<Vec2f> hitPos = ChiseledBookshelfBlockInvoker.getHitPos(hit, blockEntity.getCachedState().get(HorizontalFacingBlock.FACING));
        if (hitPos.isEmpty()) return book;
        //int slot = ChiseledBookshelfBlockInvoker.getSlotForHitPos(hitPos.get());
        OptionalInt slot = getSlotForHitPos(hit, client.world.getBlockState(hit.getBlockPos()));
        return blockEntity.getStack(slot.getAsInt());
    }

    private static OptionalInt getSlotForHitPos(BlockHitResult hit, BlockState state) {
        return (OptionalInt)getHitPos(hit, state.get(HorizontalFacingBlock.FACING)).map(/* method_55772 */ hitPos -> {
            int i = hitPos.y >= 0.5F ? 0 : 1;
            int j = getColumn(hitPos.x);
            return OptionalInt.of(j + i * 3);
        }).orElseGet(OptionalInt::empty);
    }

    private static Optional<Vec2f> getHitPos(BlockHitResult hit, Direction facing) {
        Direction direction = hit.getSide();
        if (facing != direction) {
            return Optional.empty();
        } else {
            BlockPos blockPos = hit.getBlockPos().offset(direction);
            Vec3d vec3d = hit.getPos().subtract((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
            double d = vec3d.getX();
            double e = vec3d.getY();
            double f = vec3d.getZ();

            return switch (direction) {
                case NORTH -> Optional.of(new Vec2f((float)(1.0 - d), (float)e));
                case SOUTH -> Optional.of(new Vec2f((float)d, (float)e));
                case WEST -> Optional.of(new Vec2f((float)f, (float)e));
                case EAST -> Optional.of(new Vec2f((float)(1.0 - f), (float)e));
                case DOWN, UP -> Optional.empty();
            };
        }
    }

    private static int getColumn(float x) {
        float f = 0.0625F;
        float g = 0.375F;
        if (x < 0.375F) {
            return 0;
        } else {
            float h = 0.6875F;
            return x < 0.6875F ? 1 : 2;
        }
    }



    public static List<Text> getBookText(ItemStack book) {
        List<Text> displayText = new ArrayList<>();
        displayText.add(book.getItem().getName());
        if (book.contains(DataComponentTypes.CUSTOM_NAME) || book.isOf(Items.WRITTEN_BOOK)) {
            Style s = book.getName().getStyle().withItalic(true);
            //s.withItalic(true);
            displayText.add(book.getName().getWithStyle(s).get(0));
        }
        if (book.isOf(ItemRegistry.MAP_BOOK)) {
            if (book.getComponents().contains(ItemRegistry.MAP_BOOK_ADDITIONS)) {
                int n = book.getComponents().get(ItemRegistry.MAP_BOOK_ADDITIONS).additions().size();
                Text t = Text.of("ID: " + (n+1));
                displayText.add(t);
            }

        }
        if (book.getItem() == Items.ENCHANTED_BOOK) {

            if (EnchantmentHelper.hasEnchantments(book)) {
                ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(book);
                for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
                    RegistryEntry<Enchantment> registryEntry = entry.getKey();
                    displayText.add(Enchantment.getName(registryEntry, entry.getIntValue()));
                }


                /*for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : this.enchantments.object2IntEntrySet()) {
                    RegistryEntry<Enchantment> registryEntry2 = (RegistryEntry<Enchantment>)entry.getKey();
                    //if (!registryEntryList.contains(registryEntry2)) {
                        tooltip.accept(Enchantment.getName((RegistryEntry<Enchantment>)entry.getKey(), entry.getIntValue()));
                    //}
                }

                Map<Enchantment, Integer> enchantments = EnchantmentHelper.fromNbt(tag.getList("StoredEnchantments", 10));
                for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    displayText.add(entry.getKey().getName(entry.getValue()));
                }*/
            }


            /*NbtCompound tag = book.getNbt();
            if (tag != null && tag.contains("StoredEnchantments")) {
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.fromNbt(tag.getList("StoredEnchantments", 10));
                for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    displayText.add(entry.getKey().getName(entry.getValue()));
                }
            }*/
        }
        return displayText;
    }


}

