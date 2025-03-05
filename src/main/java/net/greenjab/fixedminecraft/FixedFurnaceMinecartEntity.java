package net.greenjab.fixedminecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FixedFurnaceMinecartEntity extends FurnaceMinecartEntity {
    private final ArrayList<AbstractMinecartEntity> train = new ArrayList<>();

    public FixedFurnaceMinecartEntity(EntityType<? extends FurnaceMinecartEntity> entityType, World world) {
        super(entityType, world);
    }

    public ArrayList<AbstractMinecartEntity> getTrain() {
        return train;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            System.out.println("train " + train.size());
            ServerWorld world = (ServerWorld) this.getWorld();
            if (train.isEmpty()) train.add(this);
            AbstractMinecartEntity fakeMinecart = new ChestMinecartEntity(EntityType.CHEST_MINECART, world);
            fakeMinecart.noClip = true;
            fakeMinecart.addCommandTag("train");

            disconnectBadMinecarts(world);
            addGoodMinecarts(world, fakeMinecart);

            for (int i = 1; i< train.size(); i++) {
                AbstractMinecartEntity minecart = train.get(i);
                AbstractMinecartEntity prevMinecart = train.get(i-1);
                minecart.removeCommandTag("trainMove");
                if (prevMinecart.isOnRail() && minecart.isOnRail()) {

                    moveFakeMinecart(world, fakeMinecart, minecart, true);
                    Vec3d pos = fakeMinecart.getPos();
                    moveFakeMinecart(world, fakeMinecart, prevMinecart, false);

                    if (fakeMinecart.isOnRail()) {
                        if (fakeMinecart.getPos().squaredDistanceTo(pos)>4) {
                            //disconnect cause too far away, prevents teleporting minecarts
                            if (minecart.getCommandTags().contains("trainDisconnect")) {
                                minecart.removeCommandTag("train");
                            } else {
                                minecart.addCommandTag("trainDisconnect");
                            }
                        } else {
                            //move minecart in train
                            minecart.removeCommandTag("trainDisconnect");
                            minecart.addCommandTag("trainMove");
                            minecart.setVelocity(fakeMinecart.getPos().subtract(minecart.getPos()));
                            minecart.setPosition(fakeMinecart.getPos());
                            minecart.setPitch(fakeMinecart.getPitch());
                            minecart.setYaw((fakeMinecart.getYaw()+360)%360);
                            ((ExperimentalMinecartController)minecart.getController()).pickUpEntities(minecart.getBoundingBox().expand(0.2, 0.0, 0.2));
                        }
                    }
                } else {
                    //flying through the air
                    Vec3d pv = prevMinecart.getVelocity();
                    minecart.setVelocity(pv.x, minecart.getVelocity().y, pv.z);
                }
            }
            fakeMinecart.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    private void moveFakeMinecart(ServerWorld world, AbstractMinecartEntity fakeMinecart, AbstractMinecartEntity minecart, boolean forward) {
        fakeMinecart.setPosition(minecart.getPos());
        fakeMinecart.setOnRail(true);
        fakeMinecart.setPitch(minecart.getPitch());
        float yaw = (minecart.getYaw()+360)%360;
        fakeMinecart.setYaw(yaw);
        Vec3d velocity = forward?minecart.getVelocity():new Vec3d(-1.5, 0, 0).rotateY((float) (yaw*Math.PI/180f));
        fakeMinecart.setVelocity(velocity);
        fakeMinecart.getController().moveOnRail(world);
    }

    private void addGoodMinecarts(ServerWorld world, AbstractMinecartEntity fakeMinecart) {
        AbstractMinecartEntity lastMinecart = train.getLast();
        if (train.size()<8 &&  lastMinecart.isOnRail()) {
            List<AbstractMinecartEntity> list = world.getEntitiesByClass(
                    AbstractMinecartEntity.class,
                    lastMinecart.getBoundingBox().contract(0.2),
                    entity -> entity != null && !(entity instanceof FurnaceMinecartEntity) && !entity.getCommandTags().contains("train")
            );
            if (list.isEmpty()) {
                moveFakeMinecart(world, fakeMinecart, lastMinecart, false);
                list = world.getEntitiesByClass(
                        AbstractMinecartEntity.class,
                        fakeMinecart.getBoundingBox().contract(0.2),
                        entity -> entity != null && !(entity instanceof FurnaceMinecartEntity) &&
                                  !entity.getCommandTags().contains("train")
                );
                if (!list.isEmpty()) {
                    AbstractMinecartEntity minecart = list.getFirst();
                    if (minecart.isOnRail()) {
                        minecart.addCommandTag("train");
                        minecart.addCommandTag("trainMove");
                        minecart.setVelocity(lastMinecart.getVelocity());
                        minecart.setPosition(fakeMinecart.getPos());
                        minecart.setPitch(fakeMinecart.getPitch());
                        minecart.setYaw((fakeMinecart.getYaw() + 360) % 360);
                        train.add(minecart);
                        world.playSound(minecart, minecart.getBlockPos(), SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }
                }
            } else {
                AbstractMinecartEntity minecart = list.getFirst();
                if (minecart.isOnRail()) {
                    minecart.addCommandTag("train");
                    minecart.addCommandTag("trainMove");
                    minecart.setVelocity(lastMinecart.getVelocity());
                    minecart.setPosition(lastMinecart.getPos());
                    minecart.setPitch(lastMinecart.getPitch());
                    minecart.setYaw((lastMinecart.getYaw() + 360) % 360);
                    train.add(minecart);
                    world.playSound(minecart, minecart.getBlockPos(), SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            }
        }
    }

    private void disconnectBadMinecarts(ServerWorld world) {
        for (int i = 1; i< train.size(); i++) {
            if (train.get(i) == null || train.get(i).isRemoved() || train.get(i).getWorld().getDimensionEntry() != world.getDimensionEntry() || train.get(i).isOnGround() || !train.get(i).getCommandTags().contains("train")) {
                while (train.size()>i) {
                    train.get(i).removeCommandTag("train");
                    train.get(i).removeCommandTag("trainMove");
                    world.playSound(train.get(i), train.get(i).getBlockPos(), SoundEvents.BLOCK_BAMBOO_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    train.remove(i);

                }
            }
        }
    }

    @Override
    protected Vec3d applySlowdown(Vec3d velocity) {
        Vec3d vec3d;
        if (this.pushVec.lengthSquared() > 1.0E-7) {

            this.pushVec = new Vec3d(1, 0, 0).rotateY((float) (((this.getYaw()+360)%360)*Math.PI/180f));
            vec3d = this.getVelocity().add(this.pushVec.getX()/40.0f, 0.0, this.pushVec.getZ()/40.0f);
            if (this.isTouchingWater()) {
                vec3d = vec3d.multiply(0.1);
            }
        } else {
            vec3d = velocity.multiply(0.75, 0.0, 0.75);
        }
        return vec3d;
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        /*nbt.putShort("TrainLength", (short)this.train.size());
        for (int i = 0; i< this.train.size(); i++) {
            nbt.putUuid("Train" + i, this.train.get(i).getUuid());
        }*/
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        /*int len = nbt.getShort("TrainLength");
        this.train.clear();
        for (int i = 0; i< len; i++) {
            UUID uuid = nbt.getUuid("Train" + i);
            if (uuid != null) {
                Entity minecart = ((ServerWorld)this.getWorld()).getEntity(uuid);
                if (minecart!=null) {
                    this.train.add((AbstractMinecartEntity) minecart);
                }
            }
        }*/
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
        System.out.println("teleport");
        for (AbstractMinecartEntity minecart : train) {
            if (minecart!=null) {
                System.out.println("minecart1: " + minecart.getCommandTags().contains("train"));
                minecart.removeCommandTag("train");
                minecart.removeCommandTag("trainMove");
                System.out.println("minecart2: " + minecart.getCommandTags().contains("train"));
            }
        }
        return super.teleportTo(teleportTarget);
    }

    @Override
    protected double getMaxSpeed(ServerWorld world) {
        return super.getMaxSpeed(world) * (1-0.05*train.size());
    }
}
