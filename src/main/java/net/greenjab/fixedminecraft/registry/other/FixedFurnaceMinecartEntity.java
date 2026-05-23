package net.greenjab.fixedminecraft.registry.other;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.greenjab.fixedminecraft.network.TrainPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartChest;
import net.minecraft.world.entity.vehicle.minecart.MinecartFurnace;
import net.minecraft.world.entity.vehicle.minecart.MinecartHopper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FixedFurnaceMinecartEntity extends MinecartFurnace {
    private final ArrayList<AbstractMinecart> train = new ArrayList<>();
    private final ArrayList<UUID> uuids = new ArrayList<>();
    private int fuel;
    public int powerRailSetLit = 0;

    private final float dist = 1.5f;

    public FixedFurnaceMinecartEntity(EntityType<? extends MinecartFurnace> entityType, Level world) { super(entityType, world);}

    public ArrayList<AbstractMinecart> getTrain() { return train; }
    public void setTrain(ArrayList<UUID> setTrain) {
        if (!setTrain.isEmpty()) {
            train.clear();
            int i = 1;
            for (UUID uuid : setTrain) {
                Entity entity = this.level().getEntity(uuid);
                if (entity instanceof AbstractMinecart minecart) {
                    minecart.tickCount = 0;
                    minecart.entityTags().clear();
                    if (i<setTrain.size()) minecart.entityTags().add(setTrain.get(i).toString());
                    train.add(minecart);
                }
                i++;
            }
        }
    }

    public static void sendToAround(PlayerList playerManager, @Nullable Player player, double x, double y, double z, double distance, ResourceKey<Level> worldKey, CustomPacketPayload payload) {
        for (int i = 0; i < playerManager.getPlayers().size(); i++) {
            ServerPlayer serverPlayerEntity = playerManager.getPlayers().get(i);
            if (serverPlayerEntity != player && serverPlayerEntity.level().dimension() == worldKey) {
                double d = x - serverPlayerEntity.getX();
                double e = y - serverPlayerEntity.getY();
                double f = z - serverPlayerEntity.getZ();
                if (d * d + e * e + f * f < distance * distance) {
                    ServerPlayNetworking.send(serverPlayerEntity, payload);
                }
            }
        }
    }

    @Override
    public void tick() {
        if (this.level() instanceof ServerLevel serverWorld) {
            if (!uuids.isEmpty()) {
                train.clear();
                train.add(this);
                for (UUID uuid : uuids) {
                    Entity entity = this.level().getEntity(uuid);
                    if (entity instanceof AbstractMinecart minecart) {
                        BlockPos var11 = minecart.getCurrentBlockPosOrRailBelow();
                        BlockState blockState = this.level().getBlockState(var11);
                        boolean bl = BaseRailBlock.isRail(blockState);
                        minecart.setOnRails(bl);
                        minecart.addTag("train");
                        minecart.addTag("trainMove");
                        minecart.tickCount=0;
                        train.add(minecart);
                    }
                }
                uuids.clear();
                sendToClient(serverWorld);
            }
            if (level().getGameTime()%20==0) sendToClient(serverWorld);
        }
        super.tick();
        if (!this.level().isClientSide()) {
            ServerLevel world = (ServerLevel) this.level();
            AbstractMinecart fakeMinecart = new MinecartChest(EntityType.CHEST_MINECART, world);
            fakeMinecart.noPhysics = true;
            fakeMinecart.addTag("train");
            if (train.isEmpty()) train.add(this);

            if (train.size()>1 && fuel<100) {
                NonNullList<ItemStack> inv = null;
                if (train.get(1) instanceof MinecartChest chestMinecartEntity) inv = chestMinecartEntity.getItemStacks();
                else if (train.get(1) instanceof MinecartHopper hopperMinecartEntity) inv = hopperMinecartEntity.getItemStacks();
                if (inv != null) {
                    for (int i = 0; i < inv.size();i++) {
                        ItemStack itemStack = inv.get(i);
                        if (this.level().fuelValues().isFuel(itemStack)) {
                            int itemFuel = this.level().fuelValues().burnDuration(itemStack);
                            if (itemStack.is(Items.LAVA_BUCKET)) {
                                inv.set(i, Items.BUCKET.getDefaultInstance());
                            } else {
                                itemStack.shrink(1);
                            }
                            fuel += itemFuel;
                            break;
                        }
                    }
                }
            }
            if (powerRailSetLit!=0) {
                if (fuel > 0) this.setHasFuel(powerRailSetLit==1);
                powerRailSetLit=0;
            }
            if (fuel > 0 && this.hasFuel()) fuel--;
            if (fuel <= 0)  this.setHasFuel(false);

            disconnectBadMinecarts(world);

            setFakeMinecart(fakeMinecart, this);
            fakeMinecart.setDeltaMovement(new Vec3(-dist, 0, 0).yRot((float) (fakeMinecart.getYRot()*Math.PI/180f)));
            boolean rail = true;
            boolean cont = true;
            for (int i = 1; i< train.size(); i++) {
                AbstractMinecart minecart = train.get(i);
                AbstractMinecart prevMinecart = train.get(i-1);
                minecart.removeTag("trainMove");
                if (cont) {
                    BlockPos var11 = minecart.getCurrentBlockPosOrRailBelow();
                    BlockState blockState = this.level().getBlockState(var11);
                    boolean bl = BaseRailBlock.isRail(blockState);
                    minecart.setOnRails(bl);
                    if (!bl) rail = false;
                    int age = minecart.tickCount;
                    minecart.tickCount = 0;
                    if (rail) {
                        minecart.addTag("trainMove");
                        minecart.getBehavior().moveAlongTrack(world);
                        if (this.isOnRails()) {
                            fakeMinecart.getBehavior().moveAlongTrack(world);
                            if (minecart.position().distanceToSqr(fakeMinecart.position()) < 4) {

                                minecart.setPos(fakeMinecart.position());
                                minecart.setXRot(fakeMinecart.getXRot());
                                minecart.setYRot((fakeMinecart.getYRot() + 360) % 360);
                                Vec3 vel = fakeMinecart.getDeltaMovement()
                                        .horizontal()
                                        .normalize()
                                        .scale(-this.getDeltaMovement().horizontalDistance());
                                minecart.setDeltaMovement(vel.x, minecart.getDeltaMovement().y, vel.z);
                            } else {
                                cont = false;
                                minecart.tickCount=age+10;
                            }
                        } else {
                            Vec3 vel = new Vec3(1, 0, 0).yRot((float) (minecart.getYRot() * Math.PI / 180f))
                                    .horizontal().normalize().scale(this.getDeltaMovement().horizontalDistance());
                            minecart.setDeltaMovement(vel.x, minecart.getDeltaMovement().y, vel.z);
                        }
                    }else {
                        minecart.tick();
                        Vec3 vel = new Vec3(1, 0, 0).yRot((float) (minecart.getYRot() * Math.PI / 180f))
                                .horizontal().normalize().scale(this.getDeltaMovement().horizontalDistance());
                        minecart.setDeltaMovement(vel.x, minecart.getDeltaMovement().y, vel.z);
                        minecart.addTag("trainMove");
                    }
                }
                if (minecart.position().distanceToSqr(prevMinecart.position())>9) {
                    minecart.tickCount+=10;
                }
            }
            if (this.getPortalCooldown()<6) addGoodMinecarts(world, fakeMinecart);
            fakeMinecart.remove(Entity.RemovalReason.DISCARDED);

        }
        if (this.hasFuel() && this.random.nextInt(4) == 0) {
            this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.8, this.getZ(), 0.0, 0.0, 0.0);
        }
    }

    private void sendToClient(ServerLevel serverWorld) {
        ArrayList<UUID> trainUuids = new ArrayList<>();
        for (AbstractMinecart entity : train) trainUuids.add(entity.getUUID());
        TrainPayload payload = new TrainPayload(trainUuids);
        sendToAround(serverWorld.getServer()
                        .getPlayerList(),
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                100,
                serverWorld.dimension(),
                payload
        );
    }

    private void setFakeMinecart(AbstractMinecart fakeMinecart, AbstractMinecart minecart) {
        fakeMinecart.setPos(minecart.position());
        fakeMinecart.setOnRails(true);
        fakeMinecart.setXRot(minecart.getXRot());
        fakeMinecart.setYRot((minecart.getYRot()+360)%360);
        fakeMinecart.setDeltaMovement(minecart.getDeltaMovement());
    }

    private void addGoodMinecarts(ServerLevel world, AbstractMinecart fakeMinecart) {
        int i = train.size()-1;
        while (i< train.size()&& train.size()<8) {
            AbstractMinecart lastMinecart = train.get(i);
            if (lastMinecart.isOnRails()) {
                List<AbstractMinecart> list = world.getEntitiesOfClass(
                        AbstractMinecart.class,
                        lastMinecart.getBoundingBox().deflate(0.2),
                        entity -> !(entity instanceof MinecartFurnace) && !entity.entityTags().contains("train")
                );
                if (list.isEmpty()) {
                    setFakeMinecart(fakeMinecart, lastMinecart);
                    fakeMinecart.setDeltaMovement(new Vec3(-dist, 0, 0).yRot((float) (fakeMinecart.getYRot()*Math.PI/180f)));
                    fakeMinecart.getBehavior().moveAlongTrack(world);

                    list = world.getEntitiesOfClass(
                            AbstractMinecart.class,
                            fakeMinecart.getBoundingBox().deflate(0.2),
                            entity -> !(entity instanceof MinecartFurnace) && !entity.entityTags().contains("train")
                    );
                    if (!list.isEmpty()) {
                        BlockPos var5 = list.getFirst().getCurrentBlockPosOrRailBelow();
                        BlockState blockState = this.level().getBlockState(var5);
                        if (BaseRailBlock.isRail(blockState)) {
                            addMinecart(list.getFirst(), fakeMinecart);
                        }
                    }
                } else {
                    for (AbstractMinecart minecart : list) {
                        if (train.size()<8) {
                            BlockPos var5 = minecart.getCurrentBlockPosOrRailBelow();
                            BlockState blockState = this.level().getBlockState(var5);
                            if (BaseRailBlock.isRail(blockState)) {
                                addMinecart(minecart, lastMinecart);
                            }
                        }
                    }
                }
            }
            i++;
        }
    }

    private void addMinecart(AbstractMinecart minecart, AbstractMinecart minecart2) {
        minecart.setOnRails(true);
        minecart.addTag("train");
        minecart.addTag("trainMove");
        minecart.setDeltaMovement(train.getLast().getDeltaMovement().add(0, 0.1, 0));
        minecart.setPos(minecart2.position());
        minecart.setXRot(minecart2.getXRot());
        if (minecart instanceof DispencerMinecartEntity dispencerMinecartEntity) {
            float dif = minecart.getYRot()-minecart2.getYRot();
            if (Math.acos(Math.cos(dif))>Math.PI/2)dispencerMinecartEntity.setFlipped(!dispencerMinecartEntity.isFlipped());
        }
        minecart.setYRot((minecart2.getYRot() + 360) % 360);
        minecart.tickCount = 0;
        train.add(minecart);
        minecart.level()
                .playSound(minecart, minecart.blockPosition(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private void disconnectBadMinecarts(ServerLevel world) {
        for (int i = 1; i< train.size(); i++) {
            if (train.get(i) == null || train.get(i).isRemoved()  || (train.get(i).onGround()&&train.get(i).getDeltaMovement().horizontalDistance()<0.01) || !train.get(i).entityTags().contains("train")) {
                while (train.size()>i) {
                    train.get(i).removeTag("train");
                    train.get(i).removeTag("trainMove");
                    train.get(i).tickCount=-50;
                    world.playSound(train.get(i), train.get(i).blockPosition(), SoundEvents.BAMBOO_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                    train.remove(i);
                }
            }
        }
    }

    @Override
    protected @NonNull Vec3 applyNaturalSlowdown(@NonNull Vec3 velocity) {
        Vec3 vec3d;
        if (this.hasFuel()) {
            Vec3 push = new Vec3(1, 0, 0).yRot((float) (((this.getYRot()+360)%360)*Math.PI/180f));
            vec3d = this.getDeltaMovement().add(push.x()/40.0f, 0.0, push.z()/40.0f);
        } else {
            vec3d = velocity.multiply(0.75, 0.0, 0.75);
        }
        return vec3d;
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput view) {
        super.addAdditionalSaveData(view);
        view.putShort("Fuel", (short)this.fuel);
        view.putShort("TrainLength", (short)train.size());
        for (int i = 1;i<train.size();i++) {
            view.putString("Train"+i, String.valueOf(train.get(i).getUUID()));
        }
        view.putBoolean("Lit", hasFuel());
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput view) {
        super.readAdditionalSaveData(view);
        this.fuel = view.getShortOr("Fuel", (short)0);
        int len = view.getShortOr("TrainLength", (short)0);
        for (int i = 1;i<len;i++) {
            String uu = view.getStringOr("Train" + i, "");
            if (!uu.isEmpty()) {
                UUID uuid = UUID.fromString(uu);
                uuids.add(uuid);
            }
        }
        setHasFuel(view.getBooleanOr("Lit", false));
    }


    @Override
    public @NonNull InteractionResult interact(Player player, @NonNull InteractionHand hand, @NonNull Vec3 location) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (fuel>0) this.setHasFuel(true);
        if (this.level().fuelValues().isFuel(itemStack)) {
            int itemFuel = this.level().fuelValues().burnDuration(itemStack);
            if (fuel + itemFuel <= 32000) {
                fuel += itemFuel;
                this.setHasFuel(true);
                if (itemStack.is(Items.LAVA_BUCKET)) {
                    if (!player.hasInfiniteMaterials()) {
                        ItemStack itemStack2 = ItemUtils.createFilledResult(itemStack, player, Items.BUCKET.getDefaultInstance());
                        player.setItemInHand(hand, itemStack2);
                    }
                } else {
                    itemStack.consume(1, player);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void remove(Entity.@NonNull RemovalReason reason) {
        for (AbstractMinecart minecart : train) {
            if (minecart!=null) {
                minecart.removeTag("train");
                minecart.removeTag("trainMove");
            }
        }
        super.remove(reason);
    }
    @Override
    public Entity teleport(@NonNull TeleportTransition teleportTarget) {
        if (this.level() instanceof ServerLevel serverWorld) {
            serverWorld.resetEmptyTime();
            serverWorld.getChunkSource().addTicketWithRadius(TicketType.PORTAL, new ChunkPos(this.blockPosition().getX(), this.blockPosition().getZ()), 3);
        }
        for (AbstractMinecart minecart : train) {
            if (minecart!=null) {
                minecart.removeTag("train");
                minecart.removeTag("trainMove");
                minecart.addTag("trainTP");
            }
        }
        train.clear();
        return super.teleport(teleportTarget);
    }

    @Override
    protected double getMaxSpeed(@NonNull ServerLevel world) {
        return super.getMaxSpeed(world) * (1-0.05*train.size());
    }
}
