package net.greenjab.fixedminecraft.registry.other;

import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.EquippableDispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

import static net.minecraft.block.DispenserBlock.BEHAVIORS;

public class DispencerMinecartEntity extends StorageMinecartEntity {
    private static final ItemDispenserBehavior DEFAULT_BEHAVIOR = new ItemDispenserBehavior();
    private static final TrackedData<Boolean> POWERED = DataTracker.registerData(DispencerMinecartEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> FLIPPED = DataTracker.registerData(DispencerMinecartEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int cooldown = 0;
    public DispencerMinecartEntity(EntityType<? extends DispencerMinecartEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(POWERED, false);
        builder.add(FLIPPED, false);
    }

    protected boolean isPowered() {
        return this.dataTracker.get(POWERED);
    }
    protected void setPowered(boolean powered) {
        this.dataTracker.set(POWERED, powered);
    }
    protected boolean isFlipped() {
        return this.dataTracker.get(FLIPPED);
    }
    protected void setFlipped(boolean flipped) {
        this.dataTracker.set(FLIPPED, flipped);
    }

    @Override
    public BlockState getDefaultContainedBlock() {
        return Blocks.DISPENSER.getDefaultState().with(DispenserBlock.FACING, isFlipped()?Direction.WEST:Direction.EAST).with(DispenserBlock.TRIGGERED, isPowered());
    }

    @Override
    public int size() {
        return 9;
    }

    @Override
    public void onActivatorRail(ServerWorld serverWorld, int x, int y, int z, boolean bl) {
        if (cooldown==0) {
            if (bl) {
                BlockPos pos = new BlockPos(x, y, z);
                if (serverWorld.getBlockState(pos).isOf(Blocks.ACTIVATOR_RAIL)) {
                    this.cooldown = 8;
                    Direction dir = Direction.fromHorizontalDegrees(this.getYaw());
                    if (dir.getAxis() == Direction.Axis.Z) dir = dir.getOpposite();
                    if (isFlipped()) dir = dir.getOpposite();
                    dispense(serverWorld, Blocks.DISPENSER.getDefaultState().with(DispenserBlock.FACING, dir), pos);
                }
            } else {
                setFlipped(!isFlipped());
                this.cooldown = 8;
            }
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.shouldCancelInteraction()){
            setFlipped(!isFlipped());
            return ActionResult.SUCCESS;
        }
        return super.interact(player, hand);
    }


    protected void dispense(ServerWorld world, BlockState state, BlockPos pos) {
       DispenserBlockEntity dispenserBlockEntity = new DispenserBlockEntity(pos, state);
        DefaultedList<ItemStack> inv = this.getInventory();
        DefaultedList<ItemStack> inv2 = DefaultedList.ofSize(9, ItemStack.EMPTY);
        for (int slot = 0;slot<9;slot++) {
            inv2.set(slot, inv.get(slot));
        }
        dispenserBlockEntity.setHeldStacks(inv2);

        BlockPointer blockPointer = new BlockPointer(world, pos, state, dispenserBlockEntity);
        int i = dispenserBlockEntity.chooseNonEmptySlot(world.random);
        if (i < 0) {
            world.syncWorldEvent(WorldEvents.DISPENSER_FAILS, pos, 0);
            world.emitGameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Emitter.of(dispenserBlockEntity.getCachedState()));
        } else {
            ItemStack itemStack = dispenserBlockEntity.getStack(i);
            DispenserBehavior dispenserBehavior = this.getBehaviorForItem(world, itemStack);
            if (dispenserBehavior != DispenserBehavior.NOOP) {
                dispenserBlockEntity.setStack(i, dispenserBehavior.dispense(blockPointer, itemStack));
            }
        }

        for (int slot = 0;slot<9;slot++) {
            this.setInventoryStack(slot, inv2.get(slot));
        }
    }
    protected DispenserBehavior getBehaviorForItem(World world, ItemStack stack) {
        if (!stack.isItemEnabled(world.getEnabledFeatures())) {
            return DEFAULT_BEHAVIOR;
        } else {
            DispenserBehavior dispenserBehavior = BEHAVIORS.get(stack.getItem());
            return dispenserBehavior != null ? dispenserBehavior : getBehaviorForItem(stack);
        }
    }

    private static DispenserBehavior getBehaviorForItem(ItemStack stack) {
        return stack.contains(DataComponentTypes.EQUIPPABLE) ? EquippableDispenserBehavior.INSTANCE : DEFAULT_BEHAVIOR;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getEntityWorld().isClient()) {
            if (this.cooldown > 0) {
                this.cooldown--;
            }
            this.setPowered(cooldown > 0);
        }
    }

    @Override
    protected Item asItem() {
        return ItemRegistry.DISPENSER_MINECART;
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(ItemRegistry.DISPENSER_MINECART);
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putShort("Cooldown", (short)this.cooldown);
        view.putBoolean("FLipped", isFlipped());
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.cooldown = view.getShort("Cooldown", (short)0);
        setFlipped(view.getBoolean("FLipped", false));
    }

    @Override
    public ScreenHandler getScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new Generic3x3ContainerScreenHandler(syncId, playerInventory, this);
    }
}
