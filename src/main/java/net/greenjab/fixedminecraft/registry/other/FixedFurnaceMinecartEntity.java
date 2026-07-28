package net.greenjab.fixedminecraft.registry.other;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.greenjab.fixedminecraft.network.TrainPayload;
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

    public FixedFurnaceMinecartEntity(EntityType<? extends MinecartFurnace> entityType, Level world) { super(entityType, world);}

    public ArrayList<AbstractMinecart> getTrain() { return train; }

    @Override
    public void tick() {
        if (this.level() instanceof ServerLevel level && !uuids.isEmpty()) loadTrain(level);
        boolean wasOnRail = this.isOnRails();
        super.tick();
        if (this.level() instanceof ServerLevel level) {
            if (this.isOnRails() && !wasOnRail) {
                Vec3 v = this.getDeltaMovement();
                this.setDeltaMovement(v.normalize().scale(0.1));
                this.getBehavior().moveAlongTrack(level);
                this.setDeltaMovement(v);
            }
            AbstractMinecart fakeMinecart = new MinecartChest(EntityType.CHEST_MINECART, level);
            fakeMinecart.noPhysics = true;
            fakeMinecart.addTag("train");
            if (train.isEmpty()) train.add(this);
            updateFuel();
            disconnectBadMinecarts(level);
            setFakeMinecart(fakeMinecart, this);
            for (int i = 1; i< train.size(); i++) {
                AbstractMinecart minecart = train.get(i);
                AbstractMinecart prevMinecart = train.get(i - 1);
                minecart.removeTag("trainMove");
                minecart.setOnRails(BaseRailBlock.isRail(this.level().getBlockState(minecart.getCurrentBlockPosOrRailBelow())));
                Vec3 velocity = new Vec3(1, 0, 0).yRot((float) (minecart.getYRot() * Math.PI / 180f))
                        .horizontal().normalize().scale(this.getDeltaMovement().horizontalDistance());
                minecart.setDeltaMovement(velocity.x, minecart.getDeltaMovement().y, velocity.z);
                minecart.tick();
                tryMoveToFakeMinecart(level, prevMinecart, minecart, fakeMinecart);
                minecart.addTag("trainMove");
            }
            if (this.getPortalCooldown()<6) addGoodMinecarts(level, fakeMinecart);
            fakeMinecart.remove(Entity.RemovalReason.DISCARDED);
            if (level().getGameTime()%20==0) sendToClient(level);
        } else if (this.hasFuel() && this.random.nextInt(4) == 0) this.level().addParticle(
                ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.8, this.getZ(), 0.0, 0.0, 0.0);
    }

    private void updateFuel() {
        if (train.size()>1 && fuel<100) {
            NonNullList<ItemStack> inv = null;
            if (train.get(1) instanceof MinecartChest chestMinecartEntity) inv = chestMinecartEntity.getItemStacks();
            else if (train.get(1) instanceof MinecartHopper hopperMinecartEntity) inv = hopperMinecartEntity.getItemStacks();
            if (inv != null) {
                for (int i = 0; i < inv.size();i++) {
                    ItemStack itemStack = inv.get(i);
                    if (this.level().fuelValues().isFuel(itemStack)) {
                        int itemFuel = this.level().fuelValues().burnDuration(itemStack);
                        boolean lava = itemStack.is(Items.LAVA_BUCKET);
                        itemStack.shrink(1);
                        if (lava && itemStack.isEmpty()) inv.set(i, Items.BUCKET.getDefaultInstance());
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
        if (fuel <= 0) this.setHasFuel(false);
    }

    private void setFakeMinecart(AbstractMinecart fakeMinecart, AbstractMinecart minecart) {
        fakeMinecart.setPos(minecart.position());
        fakeMinecart.setOnRails(true);
        fakeMinecart.setXRot(minecart.getXRot());
        fakeMinecart.setYRot((minecart.getYRot()+360)%360);
        fakeMinecart.setDeltaMovement(new Vec3(-1.5f, 0, 0).yRot((float) (fakeMinecart.getYRot()*Math.PI/180f)));
    }

    private void addGoodMinecarts(ServerLevel world, AbstractMinecart fakeMinecart) {
        if (train.size()<100) {
            AbstractMinecart lastMinecart = train.getLast();
            if (lastMinecart.isOnRails()) {
                List<AbstractMinecart> list = world.getEntitiesOfClass(AbstractMinecart.class, lastMinecart.getBoundingBox().deflate(0.2),
                        entity -> !(entity instanceof MinecartFurnace) && !entity.entityTags().contains("train"));
                if (list.isEmpty()) {
                    setFakeMinecart(fakeMinecart, lastMinecart);
                    fakeMinecart.getBehavior().moveAlongTrack(world);
                    list = world.getEntitiesOfClass(AbstractMinecart.class, fakeMinecart.getBoundingBox().deflate(0.2),
                            entity -> !(entity instanceof MinecartFurnace) && !entity.entityTags().contains("train"));
                    if (!list.isEmpty() && BaseRailBlock.isRail(this.level().getBlockState(list.getFirst().getCurrentBlockPosOrRailBelow()))) addMinecart(list.getFirst(), fakeMinecart);
                } else {
                    for (AbstractMinecart minecart : list) {
                        if (train.size()<100 && BaseRailBlock.isRail(this.level().getBlockState(minecart.getCurrentBlockPosOrRailBelow()))) addMinecart(minecart, lastMinecart);
                    }
                }
            }
        }
    }

    private void addMinecart(AbstractMinecart minecart, AbstractMinecart minecart2) {
        if (train.contains(minecart))return;
        minecart.setOnRails(true);
        minecart.addTag("train");
        minecart.addTag("trainMove");
        minecart.setDeltaMovement(train.getLast().getDeltaMovement().add(0, 0.1, 0));
        minecart.setPos(minecart2.position());
        minecart.setXRot(minecart2.getXRot());
        minecart.setYRot((minecart2.getYRot() + 360) % 360);
        minecart.tickCount = 0;
        train.add(minecart);
        minecart.level().playSound(minecart, minecart.blockPosition(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private void disconnectBadMinecarts(ServerLevel world) {
        if (this.tickCount<50)return;
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

    private void tryMoveToFakeMinecart(ServerLevel level, AbstractMinecart prevMinecart, AbstractMinecart minecart, AbstractMinecart fakeMinecart) {
        if (prevMinecart.isOnRails() && minecart.isOnRails()) {
            setFakeMinecart(fakeMinecart, prevMinecart);
            fakeMinecart.getBehavior().moveAlongTrack(level);
            if (fakeMinecart.isOnRails() && minecart.position().horizontal().distanceToSqr(fakeMinecart.position().horizontal()) < 4) {
                minecart.setPos(fakeMinecart.position());
                minecart.setXRot(fakeMinecart.getXRot());
                minecart.setYRot((fakeMinecart.getYRot() + 360) % 360);
                minecart.tickCount = 0;
            } else minecart.tickCount += 10;
        } else {
            if (minecart.position().distanceToSqr(prevMinecart.position()) < (minecart.onGround()?9:25)) minecart.tickCount= 0;
            else minecart.tickCount+=10;
        }
    }

    @Override
    protected @NonNull Vec3 applyNaturalSlowdown(@NonNull Vec3 velocity) {
        Vec3 vec3d;
        if (this.hasFuel()) {
            Vec3 push = new Vec3(1, 0, 0).yRot((float) (((this.getYRot()+360)%360)*Math.PI/180f));
            vec3d = this.getDeltaMovement().add(push.x()/40.0f, 0.0, push.z()/40.0f);
            double deacc = 1-0.0015*train.size();
            vec3d = vec3d.multiply(deacc, 0.0, deacc);
        } else vec3d = velocity.multiply(0.75, 0.0, 0.75);
        return vec3d;
    }

    private void loadTrain(ServerLevel serverWorld) {
        train.clear();
        train.add(this);
        for (UUID uuid : uuids) {
            Entity entity = this.level().getEntity(uuid);
            if (entity instanceof AbstractMinecart minecart) {
                minecart.setOnRails(BaseRailBlock.isRail(this.level().getBlockState(minecart.getCurrentBlockPosOrRailBelow())));
                minecart.addTag("train");
                minecart.addTag("trainMove");
                minecart.tickCount=0;
                train.add(minecart);
            }
        }
        uuids.clear();
        sendToClient(serverWorld);
    }

    public void setTrainClient(ArrayList<UUID> setTrain) {
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

    private void sendToClient(ServerLevel serverWorld) {
        ArrayList<UUID> trainUuids = new ArrayList<>();
        for (AbstractMinecart entity : train) trainUuids.add(entity.getUUID());
        TrainPayload payload = new TrainPayload(trainUuids);
        sendToAround(serverWorld.getServer().getPlayerList(), null, this.getX(), this.getY(), this.getZ(), 100, serverWorld.dimension(), payload);
    }

    public static void sendToAround(PlayerList playerManager, @Nullable Player player, double x, double y, double z, double distance, ResourceKey<Level> worldKey, CustomPacketPayload payload) {
        for (int i = 0; i < playerManager.getPlayers().size(); i++) {
            ServerPlayer serverPlayerEntity = playerManager.getPlayers().get(i);
            if (serverPlayerEntity != player && serverPlayerEntity.level().dimension() == worldKey) {
                double d = x - serverPlayerEntity.getX();
                double e = y - serverPlayerEntity.getY();
                double f = z - serverPlayerEntity.getZ();
                if (d * d + e * e + f * f < distance * distance) ServerPlayNetworking.send(serverPlayerEntity, payload);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput view) {
        super.addAdditionalSaveData(view);
        view.putShort("Fuel", (short)this.fuel);
        view.putShort("TrainLength", (short)train.size());
        for (int i = 1;i<train.size();i++) view.putString("Train"+i, String.valueOf(train.get(i).getUUID()));
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
                } else itemStack.consume(1, player);
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

    public boolean isPowered() {
        return this.hasFuel();
    }
}
