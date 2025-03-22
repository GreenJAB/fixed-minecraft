package net.greenjab.fixedminecraft.registry.item.map_book;

import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.greenjab.fixedminecraft.network.MapBookOpenPayload;
import net.greenjab.fixedminecraft.network.MapBookSyncPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapBookItem extends Item {
    public MapBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());

        if (blockState.isIn(BlockTags.BANNERS)) {
            if (!context.getWorld().isClient) {
                MapStateData mapStateData = this.getNearestMap(context.getStack(), context.getWorld(), context.getBlockPos().toCenterPos());
                MapState mapState = mapStateData.mapState;
                if (mapState != null && !mapState.addBanner(context.getWorld(), context.getBlockPos())) {
                    return ActionResult.FAIL;
                }
            }

            return ActionResult.SUCCESS;
        } else {
            return super.useOnBlock(context);
        }
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world != null && !world.isClient()) {
            ServerPlayerEntity player = (ServerPlayerEntity) user;
            ItemStack item = user.getStackInHand(hand);
            ItemStack otherHand = hand == Hand.MAIN_HAND ? player.getOffHandStack() : player.getMainHandStack();

            var openMap = true;
            if (getNearestMap(item, world, player.getPos())==null) {
                if (addNewMapAtPos(item, (ServerWorld)world, player.getPos(),0)) {
                    player.getWorld().playSoundFromEntity(
                            null,
                            player,
                            SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                            player.getSoundCategory(),
                            1.0f,
                            1.0f
                    );
                    openMap = false;
                }
            } else if (otherHand.isOf(Items.PAPER)) {
                if (addNewMapAtPos(item, (ServerWorld)world, player.getPos(), 0)) {
                    if (!player.getAbilities().creativeMode) {
                        otherHand.decrement(1);
                    }
                    player.getWorld().playSoundFromEntity(
                            null,
                            player,
                            SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                            player.getSoundCategory(),
                            1.0f,
                            1.0f
                    );
                    openMap = false;
                }
            } else if (otherHand.isOf(Items.FILLED_MAP)) {
                if (addNewMapID(item, otherHand, (ServerWorld)world)) {
                    if (!player.getAbilities().creativeMode) {
                        otherHand.decrement(1);
                    }
                    player.getWorld().playSoundFromEntity(
                            null,
                            player,
                            SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                            player.getSoundCategory(),
                            1.0f,
                            1.0f
                    );
                    openMap = false;
                }
            } else {
                ItemStack hasPaper = getPaper(user);
                if (hasPaper.isOf(Items.PAPER)) {
                    if (addNewMapAtPos(item, (ServerWorld)world, player.getPos(), 0)) {
                        if (!player.getAbilities().creativeMode) {
                            hasPaper.decrement(1);
                        }
                        player.getWorld().playSoundFromEntity(
                                null,
                                player,
                                SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                                player.getSoundCategory(),
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
            }
        }
        return ActionResult.SUCCESS;
    }

    private ItemStack getPaper(PlayerEntity playerEntity) {
        for (int i = 0;i < playerEntity.getInventory().size(); i++) {
            ItemStack item = playerEntity.getInventory().getStack(i);
            if (item.isOf(Items.PAPER)) return item;
        }
        return ItemStack.EMPTY;
    }

    private void sendMapUpdates(ServerPlayerEntity player, ItemStack item) {
        for (MapStateData mapStateData : getMapStates(item, player.getWorld())) {
            mapStateData.mapState.getPlayerSyncData(player);
            Packet<?> packet  = mapStateData.mapState.getPlayerMarkerPacket(mapStateData.id, player);
            if (packet != null) {
                player.networkHandler.sendPacket(packet);
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world != null && !world.isClient()) {
            if (stack != null && entity instanceof ServerPlayerEntity player) {
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
                if (selected || ((PlayerEntity) entity).getOffHandStack() == stack) {
                    for (MapStateData mapStateData : getMapStates(stack, entity.getWorld())) {
                        mapStateData.mapState.update(player, stack);
                        if (!mapStateData.mapState.locked) {
                            if (this.getDistanceToEdgeOfMap(mapStateData.mapState, entity.getPos()) < 128.0) {
                                ((FilledMapItem)Items.FILLED_MAP ).updateColors(world, entity, mapStateData.mapState);
                            }
                        }
                    }
                    this.sendMapUpdates(player, stack);
                }
            }
        }
    }

    public static ArrayList<MapStateData> getMapStates(ItemStack stack, World world) {
        ArrayList<MapStateData> list = new ArrayList<>();
        MapBookState mapBookState = getMapBookState(stack, world);
        if (mapBookState != null) {
            for (int i : mapBookState.mapIDs) {
                MapState mapState = world.getMapState(new MapIdComponent(i));
                if (mapState != null) {
                    if (world.getDimensionEntry().getIdAsString().contains(mapState.dimension.getValue().toString())) {
                        list.add(new MapStateData(new MapIdComponent(i), mapState));
                    }
                }
            }
        }
        return list;
    }

    private static MapBookState getMapBookState(ItemStack stack, World world) {
        int id = getMapBookId(stack);
        if (id == -1) return null;
        if (world.isClient) {
            return MapBookStateManager.INSTANCE.getClientMapBookState(id);
        } else if (world.getServer() != null) {
            return MapBookStateManager.INSTANCE.getMapBookState(world.getServer(), id);
        }
        return null;
    }

    public MapStateData getNearestMap(ItemStack stack, World world, Vec3d pos) {
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

    private double getDistanceToEdgeOfMap(MapState mapState, Vec3d pos) {
        return Math.max(
                Math.abs(pos.x - mapState.centerX),
                Math.abs(pos.z - mapState.centerZ)
        ) - (64 * Math.pow(2, mapState.scale));
    }

    private boolean hasMapBookId(ItemStack stack) {
        return stack.contains(DataComponentTypes.MAP_ID);
    }

    public static int getMapBookId(ItemStack stack) {
        MapIdComponent mapIdComponent = stack.getOrDefault(DataComponentTypes.MAP_ID, null);
        if (mapIdComponent!=null) return mapIdComponent.id();
        return -1;
    }

    private int allocateMapBookId(MinecraftServer server) {
        MapBookIdCountsState counts = server.getOverworld().getPersistentStateManager().getOrCreate(
                MapBookIdCountsState.persistentStateType,
                MapBookIdCountsState.IDCOUNTS_KEY
        );
        int i = counts.get();
        MapBookStateManager.INSTANCE.putMapBookState(server, i, new MapBookState());
        return i;
    }

    private void setMapBookId(ItemStack stack, int id) {
        stack.set(DataComponentTypes.MAP_ID, new MapIdComponent(id));
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

    private boolean addNewMapAtPos(ItemStack item, ServerWorld world, Vec3d pos, int scale) {
        MapBookState state = this.getOrCreateMapBookState(item, world.getServer());
        MapStateData nearestState = this.getNearestMap(item, world, pos);
        if (nearestState != null && nearestState.mapState.scale <= scale
            && !(this.getDistanceToEdgeOfMap(nearestState.mapState, pos) > 0.0)) {
            return false;
        } else {
            ItemStack newMap = FilledMapItem.createMap(
                    world,
                    (int)Math.floor(pos.x), (int)Math.floor(pos.z), (byte)scale, true, false
            );
            state.addMapID(newMap.get(DataComponentTypes.MAP_ID).id());
            return true;
        }
    }

    private boolean addNewMapID(ItemStack item, ItemStack filledmap, ServerWorld world) {
        MapIdComponent mapId = filledmap.get(DataComponentTypes.MAP_ID); //?: return false;
        MapBookState state = this.getOrCreateMapBookState(item, world.getServer());
        if (state != null) {
            if (!state.mapIDs.contains(mapId.id())) {
                state.addMapID(mapId.id());
                return true;
            }
        }
        return false;
    }

    @Override
    public Text getName(ItemStack stack) {
        if (stack != null && !this.hasMapBookId(stack)) {
            if (stack.contains(ItemRegistry.MAP_BOOK_ADDITIONS)) {
                return Text.translatable("item.fixedminecraft.map_book_new");
            } else {
                return Text.translatable("item.fixedminecraft.map_book_empty");
            }
        } else {
            return super.getName(stack);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var mapsCount =
                stack.getOrDefault(ItemRegistry.MAP_BOOK_ADDITIONS, MapBookAdditionsComponent.DEFAULT).additions().size();
        int id = getMapBookId(stack);
        if (id != -1) {
            // append tooltip is client-based, so its safe to get the client MapBookState
            MapBookState mapBookState = MapBookStateManager.INSTANCE.getClientMapBookState(id);

            if (mapBookState != null) {
                mapsCount += mapBookState.mapIDs.size();
            }

            tooltip.add(Text.translatable("item.fixedminecraft.map_book_id", (id + 1)).formatted(Formatting.GRAY));
        }

        if (mapsCount > 0) {
            tooltip.add(Text.translatable("item.fixedminecraft.map_book_maps", mapsCount).formatted(Formatting.GRAY));
        }
    }

    static void setAdditions(ItemStack stack, List<Integer> additions) {
        stack.set(
                ItemRegistry.MAP_BOOK_ADDITIONS,
                new MapBookAdditionsComponent(additions)
        );
    }

    private void applyAdditions(ItemStack stack, ServerWorld world) {
        MapBookAdditionsComponent additionsComponent = stack.getOrDefault(ItemRegistry.MAP_BOOK_ADDITIONS, null) ;//?: return
        if (additionsComponent == null) return;
        stack.remove(ItemRegistry.MAP_BOOK_ADDITIONS);

        List<Integer> additions = additionsComponent.additions();
        if (!additions.isEmpty()) {
            MapBookState state = this.getOrCreateMapBookState(stack, world.getServer());

            for (int id : additions) {
                if (id != -1) {
                    MapState newState = world.getMapState(new MapIdComponent(id));

                    state.mapIDs.removeIf( existingID -> {
                        MapState existingState = world.getMapState(new MapIdComponent(existingID));
                        if (existingState == null) return true;
                        assert newState != null;
                        return (mapsAreSameLocation(newState, existingState));
                    });
                    state.addMapID(id);
                }
            }
        }
    }

    public static boolean hasInvalidAdditions(ItemStack stack, World world, List<Integer> additions) {
        MapBookState mapBookState = getMapBookState(stack, world);

        for (int i = 0; i < additions.size();i++){// in additions.indices) {
            int additionA = additions.get(i);
            if (mapBookState != null && mapBookState.mapIDs.contains(additionA)) {
                return true;
            }

            MapState mapA = world.getMapState(new MapIdComponent(additionA)); //?: return true;

            for (int j = i + 1; j < additions.size();j++) {
                int additionB = additions.get(j);
                if (additionA == additionB) {
                    return true;
                }

                MapState mapB = world.getMapState(new MapIdComponent(additionB));
                if (mapB == null || mapsAreSameLocation(mapA, mapB)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean mapsAreSameLocation(MapState mapA, MapState mapB) {
        return mapA.scale == mapB.scale && mapA.centerX == mapB.centerX && mapA.centerZ == mapB.centerZ;
    }

    @Override
    public void onCraftByPlayer(ItemStack stack, World world, PlayerEntity player) {
        super.onCraftByPlayer(stack, world, player);
        if (!world.isClient) {
            mapBookSync((ServerPlayerEntity)player, stack);
        }
    }

    @Override
    public void onCraft(ItemStack stack, World world) {
        if (!world.isClient) {
            applyAdditions(stack, (ServerWorld)world);
        }
    }

    private void mapBookOpen(ServerPlayerEntity player, ItemStack itemStack) {
        ServerPlayNetworking.send(player, new MapBookOpenPayload(itemStack));
    }

    private void mapBookSync(ServerPlayerEntity player, ItemStack itemStack) {
        MapBookSyncPayload payload = MapBookSyncPayload.of(player, itemStack);
        if (payload != null) {
            ServerPlayNetworking.send(player, payload);
        }
    }
}
