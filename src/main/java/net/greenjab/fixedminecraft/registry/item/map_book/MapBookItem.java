package net.greenjab.fixedminecraft.registry.item.map_book;

import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.greenjab.fixedminecraft.network.MapBookOpenPayload;
import net.greenjab.fixedminecraft.network.MapBookSyncPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class MapBookItem extends Item {
    public MapBookItem(Properties settings) {
        super(settings);
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());

        if (blockState.is(BlockTags.BANNERS)) {
            if (!context.getLevel().isClientSide()) {
                MapStateData mapStateData = this.getNearestMap(context.getItemInHand(), context.getLevel(), context.getClickedPos().getCenter());
                MapItemSavedData mapState = mapStateData.mapState;
                if (mapState != null && !mapState.toggleBanner(context.getLevel(), context.getClickedPos())) {
                    return InteractionResult.FAIL;
                }
            }

            return InteractionResult.SUCCESS;
        } else {
            return super.useOn(context);
        }
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level world, @NonNull Player user, @NonNull InteractionHand hand) {
        if (!world.isClientSide()) {
            ServerPlayer player = (ServerPlayer) user;
            ItemStack item = user.getItemInHand(hand);
            ItemStack otherHand = hand == InteractionHand.MAIN_HAND ? player.getOffhandItem() : player.getMainHandItem();

            var openMap = true;
            if (getNearestMap(item, world, player.position())==null || otherHand.is(Items.MAP)) {
                if (addNewMapAtPos(item, (ServerLevel)world, player.position(),0)) {
                    if (otherHand.is(Items.MAP) && !player.hasInfiniteMaterials()) {
                        otherHand.shrink(1);
                    }
                    player.level().playSound(
                            null,
                            player,
                            SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                            player.getSoundSource(),
                            1.0f,
                            1.0f
                    );
                    openMap = false;
                }
            } else if (otherHand.is(Items.SHEARS)) {
                if (removeMapAtPos(item, (ServerLevel)world, player.position(), player)) {
                    otherHand.hurtWithoutBreaking(1, player);
                    player.level().playSound(
                            null,
                            player,
                            SoundEvents.SHEEP_SHEAR,
                            player.getSoundSource(),
                            1.0f,
                            1.0f
                    );
                    openMap = false;
                }
            } else if (otherHand.is(Items.FILLED_MAP)) {
                if (addNewMapID(item, otherHand, (ServerLevel)world)) {
                    if (!player.hasInfiniteMaterials()) {
                        otherHand.shrink(1);
                    }
                    player.level().playSound(
                            null,
                            player,
                            SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                            player.getSoundSource(),
                            1.0f,
                            1.0f
                    );
                    openMap = false;
                }
            } else {
                ItemStack hasEmtpyMap = getEmptyMap(user);
                if (hasEmtpyMap.is(Items.MAP)) {
                    boolean hotbar = isHotbar(user, hasEmtpyMap);
                    if (addNewMapAtPos(item, (ServerLevel)world, player.position(), hotbar?2:4)) {
                        if (!player.hasInfiniteMaterials()) {
                            hasEmtpyMap.shrink(1);
                        }
                        player.level().playSound(
                                null,
                                player,
                                SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                                player.getSoundSource(),
                                1.0f,
                                1.0f
                        );
                        openMap = false;
                    }
                }
            }

            this.sendMapUpdates(player, item);
            mapBookSync(player, item);
            if (openMap && this.hasMapBookId(item)) {
                getMapBookState(item, world).update();
                mapBookOpen(player, item);
                player.level().playSound(
                        null,
                        player,
                        SoundEvents.BOOK_PAGE_TURN,
                        player.getSoundSource(),
                        1.0f,
                        1.0f
                );
            }
        }
        return InteractionResult.SUCCESS;
    }

    private ItemStack getEmptyMap(Player playerEntity) {
        for (int i = 0;i < playerEntity.getInventory().getContainerSize(); i++) {
            ItemStack item = playerEntity.getInventory().getItem(i);
            if (item.is(Items.MAP)) return item;
        }
        return ItemStack.EMPTY;
    }
    private boolean isHotbar(Player playerEntity, ItemStack stack) {
        for (int i = 0;i < 9; i++) {
            ItemStack item = playerEntity.getInventory().getItem(i);
            if (item == stack) return true;
        }
        return false;
    }

    private void sendMapUpdates(ServerPlayer player, ItemStack item) {
        for (MapStateData mapStateData : getMapStates(item, player.level())) {
            mapStateData.mapState.getHoldingPlayer(player);
            Packet<?> packet  = mapStateData.mapState.getUpdatePacket(mapStateData.id, player);
            if (packet != null) {
                player.connection.send(packet);
            }
        }
    }

    @Override
    public void inventoryTick(@NonNull ItemStack stack, @NonNull ServerLevel world, @NonNull Entity entity, @Nullable EquipmentSlot slot) {
        if (!world.isClientSide()) {
            if (entity instanceof ServerPlayer player) {
                stack.set(DataComponents.REPAIR_COST, 3);
                int id = getMapBookId(stack);
                if (id != -1) {
                    MapBookState mapBookState = MapBookStateManager.INSTANCE.getMapBookState(world.getServer(), id);
                    if (mapBookState!=null) {
                        mapBookState.addPlayer(player);
                    }
                }

                if (!MapBookStateManager.INSTANCE.currentBooks.contains(id)) {
                    MapBookStateManager.INSTANCE.currentBooks.add(id);
                }
                if ((slot==EquipmentSlot.MAINHAND||slot==EquipmentSlot.OFFHAND) || ((Player) entity).getOffhandItem() == stack) {
                    for (MapStateData mapStateData : getMapStates(stack, entity.level())) {
                        mapStateData.mapState.tickCarriedBy(player, stack, null);
                        if (!mapStateData.mapState.locked) {
                            if (this.getDistanceToEdgeOfMap(mapStateData.mapState, entity.position()) < 128.0) {
                                ((MapItem)Items.FILLED_MAP ).update(world, entity, mapStateData.mapState);
                            }
                        }
                    }
                    this.sendMapUpdates(player, stack);
                }
            }
        }
    }

    public static ArrayList<MapStateData> getMapStates(ItemStack stack, Level world) {
        ArrayList<MapStateData> list = new ArrayList<>();
        MapBookState mapBookState = getMapBookState(stack, world);
        if (mapBookState != null) {
            for (int i : mapBookState.mapIDs) {
                MapItemSavedData mapState = world.getMapData(new MapId(i));
                if (mapState != null) {
                    if (world.dimension().identifier().toString().contains(mapState.dimension.identifier().toString())) {
                        list.add(new MapStateData(new MapId(i), mapState));
                    }
                }
            }
        }
        return list;
    }

    private static MapBookState getMapBookState(ItemStack stack, Level world) {
        int id = getMapBookId(stack);
        if (id == -1) return null;
        if (world.isClientSide()) {
            return MapBookStateManager.INSTANCE.getClientMapBookState(id);
        } else if (world.getServer() != null) {
            return MapBookStateManager.INSTANCE.getMapBookState(world.getServer(), id);
        }
        return null;
    }

    public MapStateData getNearestMap(ItemStack stack, Level world, Vec3 pos) {
        double nearestDistance = Double.MAX_VALUE;
        byte nearestScale = 127;
        MapStateData nearestMap = null;
        Iterator<MapStateData> mapStates = getMapStates(stack, world).iterator();
        while (true) {
            MapStateData mapStateData;
            double distance;
            boolean roughlyEqual;
            do {
                do {
                    if (!mapStates.hasNext()) {
                        return nearestMap;
                    }

                    mapStateData = mapStates.next();
                    distance = this.getDistanceToEdgeOfMap(mapStateData.mapState, pos);
                    if (distance < 0.0) {
                        distance = -1.0;
                    }

                    roughlyEqual = Math.abs(nearestDistance - distance) < 1.0;
                } while (!(distance < nearestDistance) && !roughlyEqual);
            } while (roughlyEqual && (!(distance < 0.0) || mapStateData.mapState.scale >= nearestScale) && (!(distance > 0.0) || mapStateData.mapState.scale <= nearestScale));

            nearestDistance = distance;
            nearestScale = mapStateData.mapState.scale;
            nearestMap = mapStateData;
        }
    }

    private double getDistanceToEdgeOfMap(MapItemSavedData mapState, Vec3 pos) {
        return Math.max(
                Math.abs(pos.x - mapState.centerX),
                Math.abs(pos.z - mapState.centerZ)
        ) - (64 * Math.pow(2, mapState.scale));
    }

    private boolean hasMapBookId(ItemStack stack) {
        return stack.has(DataComponents.MAP_ID);
    }

    public static int getMapBookId(ItemStack stack) {
        MapId mapIdComponent = stack.getOrDefault(DataComponents.MAP_ID, new MapId(-1));
        return mapIdComponent.id();
    }

    private int allocateMapBookId(MinecraftServer server) {

        MapBookIdCountsState counts = server.getDataStorage().computeIfAbsent(
                MapBookIdCountsState.persistentStateType
        );
        int i = counts.get();
        MapBookStateManager.INSTANCE.putMapBookState(server, i, new MapBookState());
        return i;
    }

    private void setMapBookId(ItemStack stack, int id) {
        stack.set(DataComponents.MAP_ID, new MapId(id));
    }

    private int createMapBookState(ItemStack stack, MinecraftServer server) {
        int i = this.allocateMapBookId(server);
        this.setMapBookId(stack, i);
        return i;
    }

    private MapBookState getOrCreateMapBookState(ItemStack stack, MinecraftServer server) {
        int id = getMapBookId(stack);
        MapBookState state = id == -1 ? null : MapBookStateManager.INSTANCE.getMapBookState(server, id);
        if (state != null) {
            return state;
        } else {
            int i = this.createMapBookState(stack, server);
            return MapBookStateManager.INSTANCE.getMapBookState(server, i);
        }
    }

    private boolean addNewMapAtPos(ItemStack item, ServerLevel world, Vec3 pos, int scale) {
        MapBookState state = this.getOrCreateMapBookState(item, world.getServer());
        MapStateData nearestState = this.getNearestMap(item, world, pos);
        if (nearestState != null && nearestState.mapState.scale <= scale
            && !(this.getDistanceToEdgeOfMap(nearestState.mapState, pos) > 0.0)) {
            return false;
        } else {
            ItemStack newMap = MapItem.create(
                    world,
                    (int)Math.floor(pos.x), (int)Math.floor(pos.z), (byte)scale, true, false
            );
            state.addMapID(newMap.get(DataComponents.MAP_ID).id());
            return true;
        }
    }

    private boolean removeMapAtPos(ItemStack item, ServerLevel world, Vec3 pos, ServerPlayer player) {
        MapBookState state = this.getOrCreateMapBookState(item, world.getServer());
        if (state.mapIDs.isEmpty()) return false;
        if (getMapStates(item, world).size()<2) return false;
        MapStateData nearestState = this.getNearestMap(item, world, pos);
        if (nearestState == null) return false;
        if (this.getDistanceToEdgeOfMap(nearestState.mapState, pos) > 0.0) return false;
        if (state.removeMapID(nearestState.id.id())) {
            ItemStack itemStack = new ItemStack(Items.FILLED_MAP);
            itemStack.set(DataComponents.MAP_ID, nearestState.id);
            if (!player.getInventory().add(itemStack)) {
                player.drop(itemStack, true);
            }
        }
        return true;
    }

    private boolean addNewMapID(ItemStack item, ItemStack filledmap, ServerLevel world) {
        MapId mapId = filledmap.get(DataComponents.MAP_ID);
        MapItemSavedData mapState = world.getMapData(mapId);
        MapBookState state = this.getOrCreateMapBookState(item, world.getServer());
        if (state != null && mapState != null && !mapState.locked) {
            if (!state.mapIDs.contains(mapId.id())) {
                state.addMapID(mapId.id());
                return true;
            }
        }
        return false;
    }

    @Override
    public @NonNull Component getName(@NonNull ItemStack stack) {
        if (!this.hasMapBookId(stack)) {
            if (stack.has(ItemRegistry.MAP_BOOK_ADDITIONS)) {
                return Component.translatable("item.fixedminecraft.map_book_new");
            } else {
                return Component.translatable("item.fixedminecraft.map_book_empty");
            }
        } else {
            return super.getName(stack);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.@NonNull TooltipContext context, @NonNull TooltipDisplay displayComponent, @NonNull Consumer<Component> textConsumer, @NonNull TooltipFlag type) {
        var mapsCount =
                stack.getOrDefault(ItemRegistry.MAP_BOOK_ADDITIONS, MapBookAdditionsComponent.DEFAULT).additions().size();
        int id = getMapBookId(stack);
        if (id != -1) {
            // append tooltip is client-based, so its safe to get the client MapBookState
            MapBookState mapBookState = MapBookStateManager.INSTANCE.getClientMapBookState(id);

            if (mapBookState != null) {
                mapsCount += mapBookState.mapIDs.size();
            }

            textConsumer.accept(Component.translatable("item.fixedminecraft.map_book_id", (id + 1)).withStyle(ChatFormatting.GRAY));
        }

        if (mapsCount > 0) {
            textConsumer.accept(Component.translatable("item.fixedminecraft.map_book_maps", mapsCount).withStyle(ChatFormatting.GRAY));
        }
    }

    private void applyAdditions(ItemStack stack, ServerLevel world) {
        MapBookAdditionsComponent additionsComponent = stack.getOrDefault(ItemRegistry.MAP_BOOK_ADDITIONS, MapBookAdditionsComponent.DEFAULT) ;
        stack.remove(ItemRegistry.MAP_BOOK_ADDITIONS);

        List<Integer> additions = additionsComponent.additions();
        if (!additions.isEmpty()) {
            MapBookState state = this.getOrCreateMapBookState(stack, world.getServer());

            for (int id : additions) {
                if (id != -1) {
                    MapItemSavedData newState = world.getMapData(new MapId(id));

                    state.mapIDs.removeIf( existingID -> {
                        MapItemSavedData existingState = world.getMapData(new MapId(existingID));
                        if (existingState == null) return true;
                        assert newState != null;
                        return (mapsAreSameLocation(newState, existingState));
                    });
                    state.addMapID(id);
                }
            }
        }
    }

    private static boolean mapsAreSameLocation(MapItemSavedData mapA, MapItemSavedData mapB) {
        return mapA.scale == mapB.scale && mapA.centerX == mapB.centerX && mapA.centerZ == mapB.centerZ;
    }

    @Override
    public void onCraftedBy(@NonNull ItemStack stack, @NonNull Player player) {
        super.onCraftedBy(stack, player);
        if (player instanceof ServerPlayer serverPlayerEntity)
            mapBookSync(serverPlayerEntity, stack);
    }

    @Override
    public void onCraftedPostProcess(@NonNull ItemStack stack, Level world) {
        if (!world.isClientSide()) {
            applyAdditions(stack, (ServerLevel)world);
        }
    }

    private void mapBookOpen(ServerPlayer player, ItemStack itemStack) {
        ServerPlayNetworking.send(player, new MapBookOpenPayload(itemStack));
    }

    private void mapBookSync(ServerPlayer player, ItemStack itemStack) {
        MapBookSyncPayload payload = MapBookSyncPayload.of(player, itemStack);
        if (payload != null) {
            ServerPlayNetworking.send(player, payload);
        }
    }
}
