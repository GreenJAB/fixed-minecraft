package net.greenjab.fixedminecraft.mixin.enchanting;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.item.ItemStack.CODEC;

@Mixin(ChiseledBookshelfBlockEntity.class)
public abstract class ChiseledBookshelfBlockEntityMixin extends BlockEntity {
    @Shadow private int lastInteractedSlot;
    @Shadow
    @Final
    private DefaultedList<ItemStack> heldStacks;

    public ChiseledBookshelfBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbtCompound = writeNbt(new NbtCompound(), heldStacks, registries);
        nbtCompound.putNullable("last_interacted_slot", Codec.INT, lastInteractedSlot);
        return nbtCompound;
    }

    @Unique
    private static NbtCompound writeNbt(NbtCompound nbt, DefaultedList<ItemStack> stacks, RegistryWrapper.WrapperLookup registries) {
        NbtList nbtList = new NbtList();

        for (int i = 0; i < stacks.size(); i++) {
            ItemStack itemStack = stacks.get(i);
            if (!itemStack.isEmpty()) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putByte("Slot", (byte)i);
                nbtList.add(toNbt(itemStack, registries, nbtCompound));
            }
        }
        nbt.put("Items", nbtList);

        return nbt;
    }

    @Unique
    private static NbtElement toNbt(ItemStack itemStack, RegistryWrapper.WrapperLookup registries, NbtElement prefix) {
        if (itemStack.isEmpty()) {
            throw new IllegalStateException("Cannot encode empty ItemStack");
        } else {
            return CODEC.encode(itemStack, registries.getOps(NbtOps.INSTANCE), prefix).getOrThrow();
        }
    }

    @Inject(method = "removeStack(II)Lnet/minecraft/item/ItemStack;", at = @At(
            value = "RETURN"
    ), cancellable = true
    )
    private void addNoEnchantTag(int slot, int amount, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack book = cir.getReturnValue();
        if (book.isOf(Items.ENCHANTED_BOOK)) {
            book.set(DataComponentTypes.REPAIR_COST, 2);
        }
        cir.setReturnValue(book);
    }

}
