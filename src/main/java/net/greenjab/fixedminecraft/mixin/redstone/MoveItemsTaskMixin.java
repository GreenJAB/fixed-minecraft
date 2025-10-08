package net.greenjab.fixedminecraft.mixin.redstone;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.registries.StatusRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MoveItemsTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MoveItemsTask.class)
public abstract class MoveItemsTaskMixin {

    @Shadow
    @Nullable
    private MoveItemsTask.@Nullable Storage targetStorage;

    @Shadow
    protected abstract void transitionToTravelling(PathAwareEntity entity);

    @Shadow
    protected abstract void markVisited(PathAwareEntity entity, World world, BlockPos pos);

    @Inject(method = "placeStack", at = @At(value = "HEAD"))
    private void rememberItem(PathAwareEntity entity, Inventory inventory, CallbackInfo ci) {
        entity.getBrain().remember(StatusRegistry.LAST_ITEM_TYPE, entity.getMainHandStack().getItem(), 6000L);
    }

    @Inject(method = "placeStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/task/MoveItemsTask;resetVisitedPositions(Lnet/minecraft/entity/mob/PathAwareEntity;)V"))
    private void rememberLastLocation(PathAwareEntity entity, Inventory inventory, CallbackInfo ci) {
        entity.getBrain().remember(MemoryModuleType.NEAREST_BED, targetStorage.pos(), 6000L);
    }

    @WrapOperation(method = "takeStack", at = @At(value = "INVOKE",
                                                  target = "Lnet/minecraft/entity/ai/brain/task/MoveItemsTask;extractStack(Lnet/minecraft/inventory/Inventory;)Lnet/minecraft/item/ItemStack;"
    ))
    private ItemStack searchForLastItemFirst(Inventory inventory, Operation<ItemStack> original, @Local(argsOnly = true) PathAwareEntity entity) {
        if (entity.getBrain().hasMemoryModule(StatusRegistry.LAST_ITEM_TYPE)) {
            Item lastItem = entity.getBrain().getOptionalMemory(StatusRegistry.LAST_ITEM_TYPE).orElse(Items.AIR);

            int i = 0;
            for (ItemStack itemStack : inventory) {
                if (!itemStack.isEmpty() && itemStack.isOf(lastItem)) {
                    int j = Math.min(itemStack.getCount(), 16);
                    return inventory.removeStack(i, j);
                }
                i++;
            }
        }
        return original.call(inventory);
    }


    @Inject(method = "takeStack", at = @At(value = "TAIL"))
    private void revisitLastChestIfSameItem(PathAwareEntity entity, Inventory inventory, CallbackInfo ci) {
        if (entity.getMainHandStack().getItem() == entity.getBrain().getOptionalMemory(StatusRegistry.LAST_ITEM_TYPE).orElse(Items.AIR)) {
            World world = entity.getEntityWorld();
            if (entity.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_BED)) {
                BlockEntity blockEntity = world.getBlockEntity(entity.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_BED).get());
                if (blockEntity != null) {
                    MoveItemsTask.Storage storage = MoveItemsTask.Storage.forContainer(blockEntity, world);
                    if (storage != null) {
                        targetStorage = storage;
                        transitionToTravelling(entity);
                        markVisited(entity, world, this.targetStorage.pos());
                    }
                }
            }
        }
    }

    @WrapOperation(method = "hasExistingStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;areItemsEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
    private static boolean shulkerBoxTest(ItemStack chest, ItemStack golem, Operation<Boolean> original) {
        if (golem.getComponents().contains(DataComponentTypes.CONTAINER)) {
            ContainerComponent container = golem.getComponents().get(DataComponentTypes.CONTAINER);
            ItemStack testItem = container.copyFirstStack();
            if (testItem != ItemStack.EMPTY) {
                return ItemStack.areItemsAndComponentsEqual(chest, testItem);
            }
        } else if (golem.getComponents().contains(DataComponentTypes.BUNDLE_CONTENTS)) {
            BundleContentsComponent container = golem.getComponents().get(DataComponentTypes.BUNDLE_CONTENTS);
            ItemStack testItem = container.get(0);
            if (testItem != ItemStack.EMPTY) {
                return ItemStack.areItemsAndComponentsEqual(chest, testItem);
            }
        }
        return original.call(chest, golem);
    }


}
