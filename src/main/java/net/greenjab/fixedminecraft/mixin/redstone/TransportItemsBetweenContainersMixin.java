package net.greenjab.fixedminecraft.mixin.redstone;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.registries.MemoryRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.TransportItemsBetweenContainers;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

@Mixin(TransportItemsBetweenContainers.class)
public abstract class TransportItemsBetweenContainersMixin {

    @Shadow
    @Nullable
    private TransportItemsBetweenContainers.@Nullable TransportItemTarget target;

    @Shadow
    protected abstract void onStartTravelling(PathfinderMob body);

    @Shadow
    protected abstract void setVisitedBlockPos(PathfinderMob body, Level level, BlockPos target);

    @Inject(method = "putDownItem", at = @At(value = "HEAD"))
    private void rememberItem(PathfinderMob body, Container container, CallbackInfo ci) {
        body.getBrain().setMemoryWithExpiry(MemoryRegistry.LAST_ITEM_TYPE, body.getMainHandItem().getItem(), 6000L);
    }

    @Inject(method = "putDownItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/TransportItemsBetweenContainers;clearMemoriesAfterMatchingTargetFound(Lnet/minecraft/world/entity/PathfinderMob;)V"))
    private void rememberLastLocation(PathfinderMob body, Container container, CallbackInfo ci) {
        body.getBrain().setMemoryWithExpiry(MemoryModuleType.NEAREST_BED, target.pos(), 6000L);
    }

    @WrapOperation(method = "pickUpItems", at = @At(value = "INVOKE",
                                                  target = "Lnet/minecraft/world/entity/ai/behavior/TransportItemsBetweenContainers;pickupItemFromContainer(Lnet/minecraft/world/Container;)Lnet/minecraft/world/item/ItemStack;"
    ))
    private ItemStack searchForLastItemFirst(Container container, Operation<ItemStack> original, @Local(argsOnly = true) PathfinderMob body) {
        if (body.getBrain().hasMemoryValue(MemoryRegistry.LAST_ITEM_TYPE)) {
            Item lastItem = body.getBrain().getMemoryInternal(MemoryRegistry.LAST_ITEM_TYPE).orElse(Items.AIR);

            int i = 0;
            for (ItemStack itemStack : container) {
                if (!itemStack.isEmpty() && itemStack.is(lastItem)) {
                    int j = Math.min(itemStack.getCount(), 16);
                    return container.removeItem(i, j);
                }
                i++;
            }
        }
        return original.call(container);
    }


    @Inject(method = "pickUpItems", at = @At(value = "TAIL"))
    private void revisitLastChestIfSameItem(PathfinderMob body, Container container, CallbackInfo ci) {
        Item last = body.getBrain().getMemoryInternal(MemoryRegistry.LAST_ITEM_TYPE).orElse(Items.AIR);
        if (body.getMainHandItem().getItem() == last) {
            Level world = body.level();
            if (body.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_BED)) {
                BlockEntity blockEntity = world.getBlockEntity(body.getBrain().getMemoryInternal(MemoryModuleType.NEAREST_BED).get());
                if (blockEntity != null) {
                    TransportItemsBetweenContainers.TransportItemTarget storage = TransportItemsBetweenContainers.TransportItemTarget.tryCreatePossibleTarget(blockEntity, world);
                    if (storage != null) {
                        target = storage;
                        onStartTravelling(body);
                        setVisitedBlockPos(body, world, this.target.pos());
                    }
                }
            }
        }
    }

    @WrapOperation(method = "hasItemMatchingHandItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private static boolean shulkerBoxTest(ItemStack a, ItemStack b, Operation<Boolean> original) {
        if (b.getComponents().has(DataComponents.CONTAINER)) {
            ItemContainerContents container = b.getComponents().get(DataComponents.CONTAINER);
            ItemStack testItem = container.copyOne();
            if (testItem != ItemStack.EMPTY) {
                return ItemStack.isSameItemSameComponents(a, testItem);
            }
        } else if (b.getComponents().has(DataComponents.BUNDLE_CONTENTS)) {
            BundleContents container = b.getComponents().get(DataComponents.BUNDLE_CONTENTS);
            if (!container.isEmpty()) {
                ItemStack testItem = container.items().getFirst().create();
                if (testItem != ItemStack.EMPTY) {
                    return ItemStack.isSameItemSameComponents(a, testItem);
                }
            }
        } else if (a.getComponents().has(DataComponents.CONTAINER)) {
            ItemContainerContents container = a.getComponents().get(DataComponents.CONTAINER);
            for (ItemStack itemStack2 : container.allItemsCopyStream().toList()) {
                if (ItemStack.isSameItem(b, itemStack2)) {
                    return true;
                }
            }
        } else if (a.getComponents().has(DataComponents.BUNDLE_CONTENTS)) {
            BundleContents container = a.getComponents().get(DataComponents.BUNDLE_CONTENTS);
            for (ItemStack itemStack2 : container.itemCopyStream().toList()) {
                if (ItemStack.isSameItem(b, itemStack2)) {
                    return true;
                }
            }
        } else if (a.getComponents().has(DataComponents.CUSTOM_NAME)) {
            if (a.is(Items.PAPER) || a.is(Items.NAME_TAG)) {
                String string = a.getComponents().get(DataComponents.CUSTOM_NAME).tryCollapseToString();
                if (string.startsWith("#")) {
                    string = string.substring(1);
                    return testTags(b, string);
                }
            }
        }
        return original.call(a, b);
    }

    @Unique
    private static boolean testTags(ItemStack golem, String id) {
        Predicate<Identifier> predicate;
        predicate =  idx -> Objects.equals(idx.getPath(), id);
        AtomicBoolean bool = new AtomicBoolean(false);
        BuiltInRegistries.ITEM.getTags().map(HolderSet.Named::key).filter( tag -> predicate.test(tag.location())).forEach(tag->{
                if (golem.is(tag)) bool.set(true);
        });
        return bool.get();
    }

}
