package net.greenjab.fixedminecraft.render;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.mixin.enchanting.ChiseledBookshelfBlockInvoker;
import net.greenjab.fixedminecraft.registry.ItemRegistry;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        int slot = ChiseledBookshelfBlockInvoker.getSlotForHitPos(hitPos.get());
        return blockEntity.getStack(slot);
    }

    public static List<Text> getBookText(ItemStack book) {
        List<Text> displayText = new ArrayList<>();
        displayText.add(book.getItem().getName());
        if (book.hasCustomName()||book.isOf(Items.WRITTEN_BOOK)) {
            Style s = book.getName().getStyle().withItalic(true);
            //s.withItalic(true);
            displayText.add(book.getName().getWithStyle(s).get(0));
        }
        if (book.isOf(ItemRegistry.INSTANCE.getMAP_BOOK())) {
            NbtCompound tag = book.getNbt();
            if (tag != null && tag.contains("fixedminecraft:map_book")) {
                int n = tag.getInt("fixedminecraft:map_book");
                Text t = Text.of("ID: " + (n+1));
                displayText.add(t);
            }

        }
        if (book.getItem() == Items.ENCHANTED_BOOK) {
            NbtCompound tag = book.getNbt();
            if (tag != null && tag.contains("StoredEnchantments")) {
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.fromNbt(tag.getList("StoredEnchantments", 10));
                for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    displayText.add(entry.getKey().getName(entry.getValue()));
                }
            }
        }
        return displayText;
    }
}
