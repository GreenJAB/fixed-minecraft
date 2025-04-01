package net.greenjab.fixedminecraft.registry.other;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FixedFurnaceMinecartEntity extends FurnaceMinecartEntity {
    private final ArrayList<AbstractMinecartEntity> train = new ArrayList<>();
    private final ArrayList<UUID> uuids = new ArrayList<>();
    private int fuel;
    public int powerRailSetLit = 0;

    private final float dist = 1.5f;

    public FixedFurnaceMinecartEntity(EntityType<? extends FurnaceMinecartEntity> entityType, World world) { super(entityType, world);}

    public ArrayList<AbstractMinecartEntity> getTrain() { return train; }

    @Override
    public void tick() {
        if (!this.getWorld().isClient()) {
            if (!uuids.isEmpty()) {
                train.clear();
                train.add(this);
                for (UUID uuid : uuids) {
                    Entity entity = ((ServerWorld) this.getWorld()).getEntity(uuid);
                    if (entity instanceof AbstractMinecartEntity minecart) {
                        BlockPos var11 = minecart.getRailOrMinecartPos();
                        BlockState blockState = this.getWorld().getBlockState(var11);
                        boolean bl = AbstractRailBlock.isRail(blockState);
                        minecart.setOnRail(bl);
                        minecart.addCommandTag("train");
                        minecart.addCommandTag("trainMove");
                        minecart.age=0;
                        train.add(minecart);
                    }
                }
                uuids.clear();
            }
        }
        super.tick();
        if (!this.getWorld().isClient()) {
            ServerWorld world = (ServerWorld) this.getWorld();
            AbstractMinecartEntity fakeMinecart = new ChestMinecartEntity(EntityType.CHEST_MINECART, world);
            fakeMinecart.noClip = true;
            fakeMinecart.addCommandTag("train");
            if (train.isEmpty()) train.add(this);

            if (train.size()>1 && fuel<100) {
                DefaultedList<ItemStack> inv = null;
                if (train.get(1) instanceof ChestMinecartEntity chestMinecartEntity) inv = chestMinecartEntity.getInventory();
                else if (train.get(1) instanceof HopperMinecartEntity hopperMinecartEntity) inv = hopperMinecartEntity.getInventory();
                if (inv != null) {
                    for (int i = 0; i < inv.size();i++) {
                        ItemStack itemStack = inv.get(i);
                        if (this.getWorld().getFuelRegistry().isFuel(itemStack)) {
                            int itemFuel = this.getWorld().getFuelRegistry().getFuelTicks(itemStack);
                            if (itemStack.isOf(Items.LAVA_BUCKET)) {
                                inv.set(i, Items.BUCKET.getDefaultStack());
                            } else {
                                itemStack.decrement(1);
                            }
                            fuel += itemFuel;
                            break;
                        }
                    }
                }
            }
            if (powerRailSetLit!=0) {
                if (fuel > 0) this.setLit(powerRailSetLit==1);
                powerRailSetLit=0;
            }
            if (fuel > 0 && this.isLit()) fuel--;
            if (fuel <= 0)  this.setLit(false);

            disconnectBadMinecarts(world);

            setFakeMinecart(fakeMinecart, this);
            fakeMinecart.setVelocity(new Vec3d(-dist, 0, 0).rotateY((float) (fakeMinecart.getYaw()*Math.PI/180f)));
            boolean rail = true;
            boolean cont = true;
            for (int i = 1; i< train.size(); i++) {
                AbstractMinecartEntity minecart = train.get(i);
                AbstractMinecartEntity prevMinecart = train.get(i-1);
                minecart.removeCommandTag("trainMove");
                if (cont) {
                    BlockPos var11 = minecart.getRailOrMinecartPos();
                    BlockState blockState = this.getWorld().getBlockState(var11);
                    boolean bl = AbstractRailBlock.isRail(blockState);
                    minecart.setOnRail(bl);
                    if (!bl) rail = false;
                    int age = minecart.age;
                    minecart.age = 0;
                    if (rail) {
                        minecart.addCommandTag("trainMove");
                        minecart.getController().moveOnRail(world);
                        if (this.isOnRail()) {
                            fakeMinecart.getController().moveOnRail(world);
                            if (minecart.getPos().squaredDistanceTo(fakeMinecart.getPos()) < 4) {

                                minecart.setPosition(fakeMinecart.getPos());
                                minecart.setPitch(fakeMinecart.getPitch());
                                minecart.setYaw((fakeMinecart.getYaw() + 360) % 360);
                                Vec3d vel = fakeMinecart.getVelocity()
                                        .getHorizontal()
                                        .normalize()
                                        .multiply(-this.getVelocity().horizontalLength());
                                minecart.setVelocity(vel.x, minecart.getVelocity().y, vel.z);
                            } else {
                                cont = false;
                                minecart.age=age+10;
                            }
                        } else {
                            Vec3d vel = new Vec3d(1, 0, 0).rotateY((float) (minecart.getYaw() * Math.PI / 180f))
                                    .getHorizontal().normalize().multiply(this.getVelocity().horizontalLength());
                            minecart.setVelocity(vel.x, minecart.getVelocity().y, vel.z);
                        }
                    }else {
                        minecart.tick();
                        Vec3d vel = new Vec3d(1, 0, 0).rotateY((float) (minecart.getYaw() * Math.PI / 180f))
                                .getHorizontal().normalize().multiply(this.getVelocity().horizontalLength());
                        minecart.setVelocity(vel.x, minecart.getVelocity().y, vel.z);
                        minecart.addCommandTag("trainMove");
                    }
                }
                if (minecart.getPos().squaredDistanceTo(prevMinecart.getPos())>9) {
                    minecart.age+=10;
                }
            }
            if (this.getPortalCooldown()<6) addGoodMinecarts(world, fakeMinecart);
            fakeMinecart.remove(Entity.RemovalReason.DISCARDED);

        }
        if (this.isLit() && this.random.nextInt(4) == 0) {
            this.getWorld().addParticleClient(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.8, this.getZ(), 0.0, 0.0, 0.0);
        }
    }

    private void setFakeMinecart(AbstractMinecartEntity fakeMinecart, AbstractMinecartEntity minecart) {
        fakeMinecart.setPosition(minecart.getPos());
        fakeMinecart.setOnRail(true);
        fakeMinecart.setPitch(minecart.getPitch());
        fakeMinecart.setYaw((minecart.getYaw()+360)%360);
        fakeMinecart.setVelocity(minecart.getVelocity());
    }

    private void addGoodMinecarts(ServerWorld world, AbstractMinecartEntity fakeMinecart) {
        int i = train.size()-1;
        while (i< train.size()&& train.size()<8) {
            AbstractMinecartEntity lastMinecart = train.get(i);
            if (lastMinecart.isOnRail()) {
                List<AbstractMinecartEntity> list = world.getEntitiesByClass(
                        AbstractMinecartEntity.class,
                        lastMinecart.getBoundingBox().contract(0.2),
                        entity -> entity != null && !(entity instanceof FurnaceMinecartEntity) && !entity.getCommandTags().contains("train")
                );
                if (list.isEmpty()) {
                    setFakeMinecart(fakeMinecart, lastMinecart);
                    fakeMinecart.setVelocity(new Vec3d(-dist, 0, 0).rotateY((float) (fakeMinecart.getYaw()*Math.PI/180f)));
                    fakeMinecart.getController().moveOnRail(world);

                    list = world.getEntitiesByClass(
                            AbstractMinecartEntity.class,
                            fakeMinecart.getBoundingBox().contract(0.2),
                            entity -> entity != null && !(entity instanceof FurnaceMinecartEntity) &&
                                      !entity.getCommandTags().contains("train")
                    );
                    if (!list.isEmpty()) {
                        BlockPos var5 = list.get(0).getRailOrMinecartPos();
                        BlockState blockState = this.getWorld().getBlockState(var5);
                        if (AbstractRailBlock.isRail(blockState)) {
                            addMinecart(list.get(0), fakeMinecart);
                        }
                    }
                } else {
                    for (AbstractMinecartEntity minecart : list) {
                        if (train.size()<8) {
                            BlockPos var5 = minecart.getRailOrMinecartPos();
                            BlockState blockState = this.getWorld().getBlockState(var5);
                            if (AbstractRailBlock.isRail(blockState)) {
                                addMinecart(minecart, lastMinecart);
                            }
                        }
                    }
                }
            }
            i++;
        }
    }

    private void addMinecart(AbstractMinecartEntity minecart, AbstractMinecartEntity minecart2) {
        minecart.setOnRail(true);
        minecart.addCommandTag("train");
        minecart.addCommandTag("trainMove");
        minecart.setVelocity(train.get(train.size()-1).getVelocity().add(0, 0.1, 0));
        minecart.setPosition(minecart2.getPos());
        minecart.setPitch(minecart2.getPitch());
        minecart.setYaw((minecart2.getYaw() + 360) % 360);
        minecart.age = 0;
        train.add(minecart);
        minecart.getWorld()
                .playSound(minecart, minecart.getBlockPos(), SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    private void disconnectBadMinecarts(ServerWorld world) {
        for (int i = 1; i< train.size(); i++) {
            if (train.get(i) == null || train.get(i).isRemoved()  || (train.get(i).isOnGround()&&train.get(i).getVelocity().horizontalLength()<0.01) || !train.get(i).getCommandTags().contains("train")) {
                while (train.size()>i) {
                    train.get(i).removeCommandTag("train");
                    train.get(i).removeCommandTag("trainMove");
                    train.get(i).age=-50;
                    world.playSound(train.get(i), train.get(i).getBlockPos(), SoundEvents.BLOCK_BAMBOO_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    train.remove(i);
                }
            }
        }
    }

    @Override
    protected Vec3d applySlowdown(Vec3d velocity) {
        Vec3d vec3d;
        if (this.isLit()) {
            Vec3d push = new Vec3d(1, 0, 0).rotateY((float) (((this.getYaw()+360)%360)*Math.PI/180f));
            vec3d = this.getVelocity().add(push.getX()/40.0f, 0.0, push.getZ()/40.0f);
        } else {
            vec3d = velocity.multiply(0.75, 0.0, 0.75);
        }
        return vec3d;
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putShort("Fuel", (short)fuel);
        nbt.putBoolean("Lit", this.isLit());
        nbt.putShort("TrainLength", (short)train.size());
        for (int i = 1;i<train.size();i++) {
            nbt.putString("Train"+i, String.valueOf(train.get(i).getUuid()));
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        fuel = nbt.getShort("Fuel", (short)0);
        this.setLit(nbt.getBoolean("Lit", false));
        int len = nbt.getShort("TrainLength", (short)0);
        for (int i = 1;i<len;i++) {
            String uu = nbt.getString("Train" + i, "");
            if (!uu.isEmpty()) {
                UUID uuid = UUID.fromString(uu);
                uuids.add(uuid);
            }

        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (fuel>0) this.setLit(true);
        if (this.getWorld().getFuelRegistry().isFuel(itemStack)) {
            int itemFuel = this.getWorld().getFuelRegistry().getFuelTicks(itemStack);
            if (fuel + itemFuel <= 32000) {
                fuel += itemFuel;
                this.setLit(true);
                if (itemStack.isOf(Items.LAVA_BUCKET)) {
                    if (!player.isInCreativeMode()) {
                        ItemStack itemStack2 = ItemUsage.exchangeStack(itemStack, player, Items.BUCKET.getDefaultStack());
                        player.setStackInHand(hand, itemStack2);
                    }
                } else {
                    itemStack.decrementUnlessCreative(1, player);
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        for (AbstractMinecartEntity minecart : train) {
            if (minecart!=null) {
                minecart.removeCommandTag("train");
                minecart.removeCommandTag("trainMove");
            }
        }
        super.remove(reason);
    }
    @Override
    public Entity teleportTo(TeleportTarget teleportTarget) {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.resetIdleTimeout();
            serverWorld.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(this.getBlockPos()), 3);
        }
        for (AbstractMinecartEntity minecart : train) {
            if (minecart!=null) {
                minecart.removeCommandTag("train");
                minecart.removeCommandTag("trainMove");
                minecart.addCommandTag("trainTP");
            }
        }
        train.clear();
        return super.teleportTo(teleportTarget);
    }

    @Override
    protected double getMaxSpeed(ServerWorld world) {
        return super.getMaxSpeed(world) * (1-0.05*train.size());
    }
}
