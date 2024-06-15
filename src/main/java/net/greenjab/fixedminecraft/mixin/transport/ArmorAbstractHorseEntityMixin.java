package net.greenjab.fixedminecraft.mixin.transport;

import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import net.greenjab.fixedminecraft.registry.ItemRegistry;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * Prevents the horse wearing netherite armor from getting into angry pose when taking damage.
 */
@Mixin(AbstractHorseEntity.class)
public class ArmorAbstractHorseEntityMixin {
    @Unique
    private static final Map<Item, Float> rageChance = new Object2FloatArrayMap<>();

    @Shadow
    protected SimpleInventory items;

    static {
        rageChance.put(ItemRegistry.INSTANCE.getNETHERITE_HORSE_ARMOR(), 1F);
        rageChance.put(Items.DIAMOND_HORSE_ARMOR, 0.9F);
        rageChance.put(Items.IRON_HORSE_ARMOR, 0.75F);
        rageChance.put(Items.GOLDEN_HORSE_ARMOR, 0.6F);
        rageChance.put(Items.LEATHER_HORSE_ARMOR, 0.45F);
    }

    @Inject(method = "updateAnger", at = @At("HEAD"), cancellable = true)
    private void rejectAngryWhenDrip(CallbackInfo ci) {
        ItemStack armor = items.getStack(1);
        float chance = rageChance.getOrDefault(armor.getItem(), 0F);
        if (chance > 0 && chance < 1 || Math.random() <= chance) ci.cancel();
    }
}
