package net.greenjab.fixedminecraft.mixin.transport;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(LootItem.class)
public abstract class LootPoolMixin {

    @Shadow
    @Final
    private Holder<Item> item;

    @WrapOperation(method = "createItemStack", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"))
    private <T> void noVanillaNautilusArmour(Consumer<ItemStack> instance, T t, Operation<Void> original) {
        if (this.item.is(ModTags.VANILLA_NAUTILUS_ARMOR)) {
            if (FixedMinecraft.SERVER != null && FixedMinecraft.SERVER.getGameRules().get(GameRuleRegistry.REMOVE_VANILLA_NAUTILUS_ARMOUR)) return;
        }
        original.call(instance, t);
    }

}
