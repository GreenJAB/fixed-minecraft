package net.greenjab.fixedminecraft.mixin.enchanting;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.item.ItemStack.CODEC;

@Mixin(ChiseledBookShelfBlockEntity.class)
public abstract class ChiseledBookshelfBlockEntityMixin extends BlockEntity {
    @Shadow private int lastInteractedSlot;
    @Shadow
    @Final
    private NonNullList<ItemStack> items;

    public ChiseledBookshelfBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NonNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registries) {
        CompoundTag nbtCompound = writeNbt(new CompoundTag(), items, registries);
        nbtCompound.storeNullable("last_interacted_slot", Codec.INT, lastInteractedSlot);
        return nbtCompound;
    }

    @Unique
    private static CompoundTag writeNbt(CompoundTag nbt, NonNullList<ItemStack> stacks, HolderLookup.Provider registries) {
        ListTag nbtList = new ListTag();

        for (int i = 0; i < stacks.size(); i++) {
            ItemStack itemStack = stacks.get(i);
            if (!itemStack.isEmpty()) {
                CompoundTag nbtCompound = new CompoundTag();
                nbtCompound.putByte("Slot", (byte)i);
                nbtList.add(toNbt(itemStack, registries, nbtCompound));
            }
        }
        nbt.put("Items", nbtList);

        return nbt;
    }

    @Unique
    private static Tag toNbt(ItemStack itemStack, HolderLookup.Provider registries, Tag prefix) {
        if (itemStack.isEmpty()) {
            throw new IllegalStateException("Cannot encode empty ItemStack");
        } else {
            return CODEC.encode(itemStack, registries.createSerializationContext(NbtOps.INSTANCE), prefix).getOrThrow();
        }
    }

    @Inject(method = "removeItem(II)Lnet/minecraft/world/item/ItemStack;", at = @At(
            value = "RETURN"
    ), cancellable = true
    )
    private void addNoEnchantTag(int slot, int count, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack book = cir.getReturnValue();
        if (book.is(Items.ENCHANTED_BOOK)) {
            book.set(DataComponents.REPAIR_COST, 2);
        }
        cir.setReturnValue(book);
    }

}
