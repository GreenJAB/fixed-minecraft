package net.greenjab.fixedminecraft.mixin.enchanting;

import com.mojang.serialization.Codec;
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
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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

    /*@Unique
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        //NbtCompound nbt = Inventories.writeNbt(new NbtCompound(), inventory, registries);
        NbtCompound nbt = super.toInitialChunkDataNbt(registries);
        nbt.putNullable("hit_direction", ItemStack.MAP_CODEC.codec(), inventory.get(0));
        nbt.putInt("last_interacted_slot", lastInteractedSlot);
        Networking.sendUpdatePacket(pos);
        return nbt;
    }*/


    /*@Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbtCompound = new NbtCompound();
        for (int i = 0; i < 6;i++) {
            if (!inventory.get(i).isEmpty()) {
                RegistryOps<NbtElement> registryOps = registries.getOps(NbtOps.INSTANCE);
                nbtCompound.put("item" + i, ItemStack.CODEC, registryOps, inventory.get(i));
            }
        }
        nbtCompound.putNullable("last_interacted_slot", Codec.INT, lastInteractedSlot);
        Networking.sendUpdatePacket(pos);
        return nbtCompound;
    }

    /*@Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        //Inventories.writeData(view, this.inventory, true);
        for (int i = 0; i < 6;i++) {
            if (!inventory.get(i).isEmpty()) {
                RegistryOps<NbtElement> registryOps = registries.getOps(NbtOps.INSTANCE);
                view.put("item" + i, ItemStack.CODEC, registryOps, inventory.get(i));
            }
        }
        view.putInt("last_interacted_slot", this.lastInteractedSlot);
        Networking.sendUpdatePacket(pos);
    }*/

    /*@Override
    public void readData(ReadView view) {
        System.out.println("read AAAAAAAAAAAAA");
        for (int i = 0; i < 6;i++) {
            inventory.set(i, view.read("item"+i, ItemStack.CODEC).orElse(ItemStack.EMPTY));
            System.out.println(i +", "+ inventory.get(i));
        }
        lastInteractedSlot = view.read("last_interacted_slot", Codec.INT).orElse(null);
    }*/
    /*@Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        Inventories.writeData(view, this.inventory, true);
        view.putInt("last_interacted_slot", this.lastInteractedSlot);
        Networking.sendUpdatePacket(pos);
    }*/

    /*@Inject(method = "writeData", at = @At("TAIL"))
    private void sendPacket(WriteView view, CallbackInfo ci) {
        Networking.sendUpdatePacket(pos);
    }*/


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
