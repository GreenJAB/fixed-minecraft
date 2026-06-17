package net.greenjab.fixedminecraft.registry.other;

import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.EquipmentDispenseItemBehavior;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

import static net.minecraft.world.level.block.DispenserBlock.DISPENSER_REGISTRY;

public class DispencerMinecartEntity extends AbstractMinecartContainer {
    private static final DefaultDispenseItemBehavior DEFAULT_BEHAVIOR = new DefaultDispenseItemBehavior();
    private static final EntityDataAccessor<Boolean> POWERED = SynchedEntityData.defineId(DispencerMinecartEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FACING_RIGHT = SynchedEntityData.defineId(DispencerMinecartEntity.class, EntityDataSerializers.BOOLEAN);

    private int cooldown = 0;
    public DispencerMinecartEntity(EntityType<? extends DispencerMinecartEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(POWERED, false);
        builder.define(FACING_RIGHT, false);
    }

    protected boolean isPowered() {
        return this.entityData.get(POWERED);
    }
    protected void setPowered(boolean powered) {
        this.entityData.set(POWERED, powered);
    }
    protected boolean isFacingRight() {
        return this.entityData.get(FACING_RIGHT);
    }
    protected void setFacingRight(boolean facingRight) {
        this.entityData.set(FACING_RIGHT, facingRight);
    }

    @Override
    public @NonNull BlockState getDefaultDisplayBlockState() {
        return Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, isFacingRight()?Direction.WEST:Direction.EAST).setValue(DispenserBlock.TRIGGERED, isPowered());
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    public void activateMinecart(@NonNull ServerLevel serverWorld, int x, int y, int z, boolean bl) {
        if (cooldown==0) {
            if (bl) {
                BlockPos pos = new BlockPos(x, y, z);
                if (serverWorld.getBlockState(pos).is(Blocks.ACTIVATOR_RAIL)) {
                    this.cooldown = 8;
                    float rotation = this.getYRot();
                    if (this.getBehavior() instanceof NewMinecartBehavior behavior) {
                        if (behavior.cartHasPosRotLerp()) {
                            rotation = behavior.getCartLerpYRot(0);
                        }
                    } else if (this.getBehavior() instanceof OldMinecartBehavior behavior) {
                        Vec3 pos2 = behavior.getPos(x, y, z);
                        if (pos2 != null) {
                            Vec3 p0 = behavior.getPosOffs(x, y, z, 0.3F);
                            Vec3 p1 = behavior.getPosOffs(x, y, z, -0.3F);
                            p0 = Objects.requireNonNullElse(p0, pos2);
                            p1 = Objects.requireNonNullElse(p1, pos2);
                            Vec3 direction = p1.add(-p0.x, -p0.y, -p0.z);
                            rotation = (float)(Math.atan2(direction.z, direction.x) * 180.0 / Math.PI);
                        }
                    }
                    Direction dir = Direction.fromYRot(rotation);
                    if (dir.getAxis() == Direction.Axis.Z) dir = dir.getOpposite();
                    if (isFacingRight()) dir = dir.getOpposite();
                    dispense(serverWorld, Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, dir), pos);
                }
            }
        }
    }

    @Override
    public @NonNull InteractionResult interact(Player player, @NonNull InteractionHand hand, final @NonNull Vec3 location) {
        if (player.isSecondaryUseActive()){
            setFacingRight(!isFacingRight());
            return InteractionResult.SUCCESS;
        }
        return super.interact(player, hand, location);
    }


    protected void dispense(ServerLevel world, BlockState state, BlockPos pos) {
       DispenserBlockEntity dispenserBlockEntity = new DispenserBlockEntity(pos, state);
        NonNullList<ItemStack> inv = this.getItemStacks();
        NonNullList<ItemStack> inv2 = NonNullList.withSize(9, ItemStack.EMPTY);
        for (int slot = 0;slot<9;slot++) {
            inv2.set(slot, inv.get(slot));
        }
        dispenserBlockEntity.setItems(inv2);

        BlockSource blockPointer = new BlockSource(world, pos, state, dispenserBlockEntity);
        int i = dispenserBlockEntity.getRandomSlot(world.getRandom());
        if (i < 0) {
            world.levelEvent(LevelEvent.SOUND_DISPENSER_FAIL, pos, 0);
            world.gameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Context.of(dispenserBlockEntity.getBlockState()));
        } else {
            ItemStack itemStack = dispenserBlockEntity.getItem(i);
            DispenseItemBehavior dispenserBehavior = this.getBehaviorForItem(world, itemStack);
            if (dispenserBehavior != DispenseItemBehavior.NOOP) {
                dispenserBlockEntity.setItem(i, dispenserBehavior.dispense(blockPointer, itemStack));
            }
        }

        for (int slot = 0;slot<9;slot++) {
            this.setChestVehicleItem(slot, inv2.get(slot));
        }
    }
    protected DispenseItemBehavior getBehaviorForItem(Level world, ItemStack stack) {
        if (!stack.isItemEnabled(world.enabledFeatures())) {
            return DEFAULT_BEHAVIOR;
        } else {
            DispenseItemBehavior dispenserBehavior = DISPENSER_REGISTRY.get(stack.getItem());
            return dispenserBehavior != null ? dispenserBehavior : getBehaviorForItem(stack);
        }
    }

    private static DispenseItemBehavior getBehaviorForItem(ItemStack stack) {
        return stack.has(DataComponents.EQUIPPABLE) ? EquipmentDispenseItemBehavior.INSTANCE : DEFAULT_BEHAVIOR;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (this.cooldown > 0) {
                this.cooldown--;
            }
            this.setPowered(cooldown > 0);
        }
    }

    @Override
    protected @NonNull Item getDropItem() {
        return ItemRegistry.DISPENSER_MINECART;
    }

    @Override
    public @NonNull ItemStack getPickResult() {
        return new ItemStack(ItemRegistry.DISPENSER_MINECART);
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput view) {
        super.addAdditionalSaveData(view);
        view.putShort("Cooldown", (short)this.cooldown);
        view.putBoolean("FacingRight", isFacingRight());
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput view) {
        super.readAdditionalSaveData(view);
        this.cooldown = view.getShortOr("Cooldown", (short)0);
        setFacingRight(view.getBooleanOr("FacingRight", false));
    }

    @Override
    public @NonNull AbstractContainerMenu createMenu(int syncId, @NonNull Inventory playerInventory) {
        return new DispenserMenu(syncId, playerInventory, this);
    }
}
