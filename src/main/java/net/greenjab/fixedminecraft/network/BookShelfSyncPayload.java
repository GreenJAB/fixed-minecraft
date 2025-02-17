package net.greenjab.fixedminecraft.network;


import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookState;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record BookShelfSyncPayload(/*ItemStack item, */BlockPos pos) implements CustomPayload {
    public static final Id<BookShelfSyncPayload> PACKET_ID = new Id<>(Identifier.of("fixedminecraft", "book_shelf_sync"));

    public static final PacketCodec<RegistryByteBuf, BookShelfSyncPayload> PACKET_CODEC = PacketCodec.tuple(
            //ItemStack.PACKET_CODEC,
            //BookShelfSyncPayload::item,
            BlockPos.PACKET_CODEC,
            BookShelfSyncPayload::pos,
            BookShelfSyncPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(PACKET_ID, PACKET_CODEC);
    }

    public static @NotNull BookShelfSyncPayload of(ServerPlayerEntity player, BlockPos pos) {
        return new BookShelfSyncPayload(pos);
    }




    /*public static final PacketCodec<RegistryByteBuf, BookShelfSyncPayload> CODEC = Packet.createCodec(
            BookShelfSyncPayload::write, BookShelfSyncPayload::new
    );

    public BookShelfSyncPayload( ItemStack item) {
        this.item = item;
    }

    private BookShelfSyncPayload(RegistryByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.item = Lists.<Pair<EquipmentSlot, ItemStack>>newArrayList();

        int i;
        do {
            i = buf.readByte();
            EquipmentSlot equipmentSlot = (EquipmentSlot)EquipmentSlot.VALUES.get(i & 127);
            ItemStack itemStack = ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);
            this.equipmentList.add(Pair.of(equipmentSlot, itemStack));
        } while ((i & -128) != 0);
    }

    private void write(RegistryByteBuf buf) {
        ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, item);
        /*buf.writeVarInt(this.entityId);
        int i = this.equipmentList.size();

        for (int j = 0; j < 1; j++) {
            Pair<EquipmentSlot, ItemStack> pair = (Pair<EquipmentSlot, ItemStack>)this.equipmentList.get(j);
            EquipmentSlot equipmentSlot = pair.getFirst();
            boolean bl = j != i - 1;
            int k = equipmentSlot.ordinal();
            buf.writeByte(bl ? k | -128 : k);
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, pair.getSecond());
        }*
    }

    @Override
    public PacketType<EntityEquipmentUpdateS2CPacket> getPacketType() {
        return PlayPackets.;
    }

    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntityEquipmentUpdate(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public List<Pair<EquipmentSlot, ItemStack>> getEquipmentList() {
        return this.equipmentList;
    }*/
}
