package net.greenjab.fixedminecraft;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
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
    private int fuel;
    public Vec3d pushVec = Vec3d.ZERO;
    private ArrayList<AbstractMinecartEntity> train = new ArrayList<>(7);

    public FixedFurnaceMinecartEntity(EntityType<? extends FurnaceMinecartEntity> entityType, World world) {
        super(entityType, world);
        for (int i=0;i<7;i++) {
            train.add(null);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            List<AbstractMinecartEntity> list = this.getWorld().getEntitiesByClass(
                    AbstractMinecartEntity.class,
                    this.getBoundingBox().expand(2.0),
                    entity -> entity != null && !(entity instanceof FurnaceMinecartEntity)
            );
            if (!list.isEmpty()) {
                if (train.get(0)==null) {
                    train.set(0, list.get(0));
                }
            }
            if (train.get(0)!=null) {
                System.out.println(train.get(0));
                if (train.get(0).isRemoved()) {
                    train.set(0, null);
                }
            }
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
            this.pushVec.normalize();
            //float f = (float) (1.0f/(1.0f+(1.0f*this.getVelocity().horizontalLength())));
            //this.pushVec.multiply(f);
            vec3d = this.getVelocity().add(this.pushVec.getX()/80.0f, 0.0, this.pushVec.getZ()/80.0f);
            //System.out.println("vec3d: " + vec3d);
            if (this.isTouchingWater()) {
                vec3d = vec3d.multiply(0.1);
            }
        } else {
            vec3d = velocity.multiply(0.75, 0.0, 0.75);
        }
        double dd = this.getController().getSpeedRetention();
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
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isIn(ItemTags.FURNACE_MINECART_FUEL) && this.fuel + 3600 <= 32000) {
            itemStack.decrementUnlessCreative(1, player);
            this.fuel += 3600;
        }

        if (this.fuel > 0) {
            this.pushVec = this.getPos().subtract(player.getPos()).getHorizontal();
        }

        return ActionResult.SUCCESS;
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
    }

}
