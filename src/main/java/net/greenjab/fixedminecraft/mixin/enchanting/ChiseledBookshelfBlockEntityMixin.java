package net.greenjab.fixedminecraft.mixin.enchanting;

import net.greenjab.fixedminecraft.enchanting.Networking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChiseledBookshelfBlockEntity.class)
public abstract class ChiseledBookshelfBlockEntityMixin extends BlockEntity {
    @Shadow private int lastInteractedSlot;
    @Shadow @Final
    private DefaultedList<ItemStack> inventory;

    public ChiseledBookshelfBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Unique
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = Inventories.writeNbt(new NbtCompound(), inventory, registries);
        nbt.putInt("last_interacted_slot", lastInteractedSlot);
        return nbt;
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
