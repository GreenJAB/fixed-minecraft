package net.greenjab.fixedminecraft.mixin.inventory;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(ProjectileWeaponItem.class)
public abstract class ProjectileWeaponItemMixin {

    @Inject(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1))
    private static void test(ItemStack weapon, ItemStack projectile, LivingEntity shooter,
                             CallbackInfoReturnable<List<ItemStack>> cir,
                             @Local(ordinal = 3) ItemStack drawnStack,
                             @Local(ordinal = 1) int i) {
       if (i == 0) {
           if (drawnStack.getComponents().getOrDefault(DataComponents.REPAIR_COST, 0)==4) {
               if (shooter instanceof Player player) {
                   Predicate<ItemStack>  supportedProjectiles = ((ProjectileWeaponItem)weapon.getItem()).getAllSupportedProjectiles();
                   for (int j = 0; j < player.getInventory().getContainerSize(); j++) {
                       ItemStack bundle = player.getInventory().getItem(j);
                       if (!bundle.isEmpty() && bundle.getComponents().has(DataComponents.BUNDLE_CONTENTS)){
                           BundleContents bundleComponent = bundle.get(DataComponents.BUNDLE_CONTENTS);
                           assert bundleComponent!=null;
                           for (int k = 0; k < bundleComponent.size();k++) {
                               ItemStack bundleStack = bundleComponent.items().get(k).create();
                               if (supportedProjectiles.test(bundleStack)) {
                                   BundleContents.Mutable contents = new BundleContents.Mutable(bundleComponent);
                                   ItemStack removedStack = contents.items.remove(k);
                                   ItemStack mergedStack = removedStack.copyWithCount(projectile.getCount());
                                   if (!mergedStack.isEmpty()) contents.items.addFirst(mergedStack);
                                   bundle.set(DataComponents.BUNDLE_CONTENTS, contents.toImmutable());
                                   drawnStack.set(DataComponents.REPAIR_COST, removedStack.getComponents().getOrDefault(DataComponents.REPAIR_COST, 0));
                                   return;
                               }
                           }
                       }
                   }
               }
           }
       }
   }
}
