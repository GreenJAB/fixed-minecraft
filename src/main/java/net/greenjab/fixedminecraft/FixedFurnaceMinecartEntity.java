package net.greenjab.fixedminecraft;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FixedFurnaceMinecartEntity extends FurnaceMinecartEntity {
    private ArrayList<AbstractMinecartEntity> train = new ArrayList<>();

    public FixedFurnaceMinecartEntity(EntityType<? extends FurnaceMinecartEntity> entityType, World world) {
        super(entityType, world);
    }

    public ArrayList<AbstractMinecartEntity> getTrain() {
        return train;
    }

    @Override
    public void tick() {
        super.tick();
        if (train.isEmpty()) train.add(this);
        if (!this.getWorld().isClient()) {
            for (int i = 1; i< train.size(); i++) {
                if (train.get(i) == null || train.get(i).isRemoved() || train.get(i).getWorld()!=this.getWorld() || train.get(i).isOnGround()) {
                    while (train.size()>i) {
                        train.get(i).removeCommandTag("train");
                        train.remove(i);
                    }
                }
            }

            if (train.size()<8 &&  train.getLast().isOnRail()) {
                List<AbstractMinecartEntity> list = this.getWorld().getEntitiesByClass(
                        AbstractMinecartEntity.class,
                        train.getLast().getBoundingBox().expand(0.75).offset(new Vec3d(-0.8, 0, 0).rotateY(this.getYaw())),
                        entity -> entity != null && !(entity instanceof FurnaceMinecartEntity) && !entity.getCommandTags().contains("train")
                );
                if (!list.isEmpty()) {
                    list.getFirst().addCommandTag("train");
                    train.add(list.getFirst());
                }
            }

            for (int i = 1; i< train.size(); i++) {
                AbstractMinecartEntity minecart = train.get(i);
                AbstractMinecartEntity prevMinecart = train.get(i-1);
                minecart.removeCommandTag("trainMove");
                if (prevMinecart.isOnRail()) {

                    AbstractMinecartEntity fakeMinecart = new ChestMinecartEntity(EntityType.CHEST_MINECART, this.getWorld());
                    fakeMinecart.noClip = true;

                    fakeMinecart.setPosition(prevMinecart.getPos());
                    fakeMinecart.setOnRail(true);
                    fakeMinecart.setVelocity(new Vec3d(-1.5, 0, 0).rotateY(prevMinecart.getYaw()));
                    if (prevMinecart.getYaw()>300) {
                        fakeMinecart.setVelocity(fakeMinecart.getVelocity().multiply(-1));
                    }
                    fakeMinecart.setPitch(prevMinecart.getPitch());
                    fakeMinecart.setYaw(prevMinecart.getYaw());

                    fakeMinecart.getController().moveOnRail((ServerWorld) this.getWorld());
                    if (fakeMinecart.isOnRail()) {
                        minecart.addCommandTag("trainMove");
                        Vec3d pos = minecart.getPos();
                        minecart.setPosition(fakeMinecart.getPos());

                        //
                        minecart.setOnRail(fakeMinecart.isOnRail());
                        minecart.setPitch(fakeMinecart.getPitch());
                        minecart.setYaw(fakeMinecart.getYaw());
                        minecart.setVelocity(fakeMinecart.getPos().subtract(pos).multiply(1));
                        //System.out.println(prevMinecart.getVelocity() + ", " + minecart.getVelocity());
                        //minecart.noClip = true;
                    } else {
                        Vec3d v = prevMinecart.getVelocity();
                        minecart.setVelocity(v.x, minecart.getVelocity().y, v.z);
                    }
                    fakeMinecart.remove(Entity.RemovalReason.DISCARDED);
                } else {
                    Vec3d pv = prevMinecart.getVelocity();
                    Vec3d pp = prevMinecart.getPos();
                    Vec3d nv = new Vec3d(-1.5, 0, 0).rotateY(prevMinecart.getYaw());
                    if (minecart.getPos().squaredDistanceTo(pp)<10 + pp.horizontalLengthSquared()) {
                        //minecart.setPosition(pp.x-nv.x, minecart.getPos().y, pp.z-nv.z);
                        minecart.setVelocity(pv.x, minecart.getVelocity().y, pv.z);
                    }
                }
            }




            /*    if (train.get(0)==null) {
                    train.set(0, list.get(0));
                    train.get(0).addCommandTag("train");
                } else {
                    if (train.get(1)==null) {
                        train.set(1, list.get(0));
                        train.get(1).addCommandTag("train");
                    }
                }
            }
            if (train.get(0)!=null && this.isOnRail()) {
                //System.out.println(train.get(0));
                if (train.get(0).isRemoved()) {
                    train.set(0, null);
                } else {
                    AbstractMinecartEntity minecart = train.get(0);

                    AbstractMinecartEntity fakeMinecart = new ChestMinecartEntity(EntityType.CHEST_MINECART, this.getWorld());
                    fakeMinecart.noClip = true;
                    Vec3d pos = this.getPos();
                    fakeMinecart.setPosition(pos);
                    fakeMinecart.setVelocity(new Vec3d(-1.5, 0, 0).rotateY(this.getYaw()));
                    fakeMinecart.setPitch(this.getPitch());
                    fakeMinecart.setYaw(this.getYaw());

                    fakeMinecart.getController().moveOnRail((ServerWorld) this.getWorld());

                    minecart.setPosition(fakeMinecart.getPos());
                    minecart.setVelocity(fakeMinecart.getPos().subtract(pos).multiply(1/20.0));
                    minecart.setPitch(fakeMinecart.getPitch());
                    minecart.setYaw(fakeMinecart.getYaw());
                    fakeMinecart.remove(Entity.RemovalReason.DISCARDED);

                    if (train.get(1)!=null && minecart.isOnRail()) {
                        //System.out.println(train.get(0));
                        if (train.get(1).isRemoved()) {
                            train.set(1, null);
                        } else {
                            AbstractMinecartEntity minecart2 = train.get(1);

                            AbstractMinecartEntity fakeMinecart2 = new ChestMinecartEntity(EntityType.CHEST_MINECART, this.getWorld());
                            fakeMinecart2.noClip = true;
                            fakeMinecart2.setPosition(minecart.getPos());
                            fakeMinecart2.setVelocity(new Vec3d(-1, 0, 0).rotateY(minecart.getYaw()).multiply(1.5f));
                            fakeMinecart2.setPitch(minecart.getPitch());
                            fakeMinecart2.setYaw(minecart.getYaw());

                            fakeMinecart2.getController().moveOnRail((ServerWorld) this.getWorld());

                            minecart2.setPosition(fakeMinecart2.getPos());
                            minecart2.setVelocity(minecart.getVelocity());
                            minecart2.setPitch(fakeMinecart2.getPitch());
                            minecart2.setYaw(fakeMinecart2.getYaw());
                            fakeMinecart2.remove(Entity.RemovalReason.DISCARDED);
                        }
                    }
                }*/
            //}
        }
    }

    @Override
    protected Vec3d applySlowdown(Vec3d velocity) {
        //double d = this.pushVec.getX() * this.pushVec.getX() + this.pushVec.getZ() * this.pushVec.getZ();
        Vec3d vec3d;
        //if (d > 1.0E-7) {
        if (this.pushVec.lengthSquared() > 1.0E-7) {
            //d = Math.sqrt(d);
            //this.pushVec.multiply(1/d);
            //this.pushVec.normalize();
            //this.pushVec = this.method_64276(velocity);
            this.pushVec = new Vec3d(1, 0, 0).rotateY(this.getYaw());
            //float f = (float) (1.0f/(1.0f+(1.0f*this.getVelocity().horizontalLength())));
            //this.pushVec.multiply(f);
            vec3d = this.getVelocity().add(this.pushVec.getX()/40.0f, 0.0, this.pushVec.getZ()/40.0f);
            //System.out.println("vec3d: " + vec3d);
            if (this.isTouchingWater()) {
                vec3d = vec3d.multiply(0.1);
            }
        } else {
            vec3d = velocity.multiply(0.75, 0.0, 0.75);
        }
        //double dd = this.getController().getSpeedRetention();
        //System.out.println("dd: " + dd);
        //vec3d = vec3d.multiply(dd, 0.0, dd);
        return vec3d;
        //return super.applySlowdown(vec3d);
    }//*/
    /*@Override
    protected Vec3d applySlowdown(Vec3d velocity) {
        Vec3d vec3d;
        if (this.pushVec.lengthSquared() > 1.0E-7) {
            this.pushVec = this.method_64276(velocity);
            vec3d = velocity.multiply(0.2, 0.0, 0.2).add(this.pushVec);
            if (this.isTouchingWater()) {
                vec3d = vec3d.multiply(0.1);
            }
        } else {
            vec3d = velocity.multiply(0.98, 0.0, 0.98);
        }
        double dd = this.getController().getSpeedRetention();
        vec3d = vec3d.multiply(dd, 0.0, dd);
        return vec3d;
    }//*/

    private Vec3d method_64276(Vec3d velocity) {
        double d = 1.0E-4;
        double e = 0.001;
        return this.pushVec.horizontalLengthSquared() > 1.0E-4 && velocity.horizontalLengthSquared() > 0.001
                ? this.pushVec.projectOnto(velocity).normalize().multiply(this.pushVec.length())
                : this.pushVec;
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
    }

    @Override
    public void kill(ServerWorld world) {
        System.out.println("kill");
        for (AbstractMinecartEntity minecart : train) {
            if (minecart!=null) {
                minecart.removeCommandTag("train");
            }
        }
        super.kill(world);
    }

    @Override
    protected double getMaxSpeed(ServerWorld world) {
        return super.getMaxSpeed(world) * (1-0.05*train.size());
    }
}
