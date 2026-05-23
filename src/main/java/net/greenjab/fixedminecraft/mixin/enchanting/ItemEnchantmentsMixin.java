package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

@Mixin(ItemEnchantments.class)
public abstract class ItemEnchantmentsMixin {

    @Unique
    private static final HashMap<String, String> enchantToBiome;
    @Unique
    private static final HashMap<String, String> biomeToIcon;

    static {
        enchantToBiome = new HashMap<>();
        biomeToIcon = new HashMap<>();
        enchantToBiome.put("minecraft:fire_protection", "desert");
        enchantToBiome.put("minecraft:impaling", "desert");
        enchantToBiome.put("minecraft:thorns", "desert");
        enchantToBiome.put("minecraft:efficiency", "desert");
        enchantToBiome.put("minecraft:infinity", "desert");
        enchantToBiome.put("minecraft:wind_burst", "desert");
        enchantToBiome.put("minecraft:feather_falling", "jungle");
        enchantToBiome.put("minecraft:sweeping_edge", "jungle");
        enchantToBiome.put("minecraft:power", "jungle");
        enchantToBiome.put("minecraft:unbreaking", "jungle");
        enchantToBiome.put("minecraft:channeling", "jungle");
        enchantToBiome.put("minecraft:protection", "plains");
        enchantToBiome.put("minecraft:smite", "plains");
        enchantToBiome.put("minecraft:punch", "plains");
        enchantToBiome.put("minecraft:fire_aspect", "plains");
        enchantToBiome.put("minecraft:multishot", "plains");
        enchantToBiome.put("minecraft:lunge", "plains");
        enchantToBiome.put("minecraft:knockback", "savanna");
        enchantToBiome.put("minecraft:sharpness", "savanna");
        enchantToBiome.put("minecraft:depth_strider", "savanna");
        enchantToBiome.put("minecraft:binding_curse", "savanna");
        enchantToBiome.put("minecraft:loyalty", "savanna");
        enchantToBiome.put("minecraft:aqua_affinity", "snow");
        enchantToBiome.put("minecraft:quick_charge", "snow");
        enchantToBiome.put("minecraft:frost_walker", "snow");
        enchantToBiome.put("minecraft:looting", "snow");
        enchantToBiome.put("minecraft:silk_touch", "snow");
        enchantToBiome.put("minecraft:breach", "snow");
        enchantToBiome.put("minecraft:projectile_protection", "swamp");
        enchantToBiome.put("minecraft:piercing", "swamp");
        enchantToBiome.put("minecraft:respiration", "swamp");
        enchantToBiome.put("minecraft:vanishing_curse", "swamp");
        enchantToBiome.put("minecraft:mending", "swamp");
        enchantToBiome.put("minecraft:blast_protection", "taiga");
        enchantToBiome.put("minecraft:bane_of_arthropods", "taiga");
        enchantToBiome.put("minecraft:riptide", "taiga");
        enchantToBiome.put("minecraft:fortune", "taiga");
        enchantToBiome.put("minecraft:flame", "taiga");
        enchantToBiome.put("minecraft:density", "taiga");

        enchantToBiome.put("minecraft:swift_sneak", "deep_dark");
        enchantToBiome.put("minecraft:luck_of_the_sea", "fisherman");
        enchantToBiome.put("minecraft:lure", "fisherman");

        enchantToBiome.put("minecraft:soul_speed", "piglin");

        biomeToIcon.put("desert", "§f\ufd00");
        biomeToIcon.put("jungle", "§f\ufd01");
        biomeToIcon.put("plains", "§f\ufd02");
        biomeToIcon.put("savanna", "§f\ufd03");
        biomeToIcon.put("snow", "§f\ufd04");
        biomeToIcon.put("swamp", "§f\ufd05");
        biomeToIcon.put("taiga", "§f\ufd06");

        biomeToIcon.put("fisherman", "§f\ufd10");
        biomeToIcon.put("deep_dark", "§f\ufd11");
        biomeToIcon.put("piglin", "§f\ufd12");

    }


    @ModifyExpressionValue(method = "addToTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getFullname(Lnet/minecraft/core/Holder;I)Lnet/minecraft/network/chat/Component;"))
    private Component addEnchantLocationTooltip(Component original, @Local(argsOnly = true) TooltipFlag flag, @Local Holder<Enchantment> enchantment) {

        if (flag == TooltipFlag.ADVANCED) {
            MutableComponent mt = (MutableComponent) original;
            if (enchantToBiome.containsKey(enchantment.getRegisteredName())) {
                if (biomeToIcon.containsKey(enchantToBiome.get(enchantment.getRegisteredName()))) {
                    mt.append(" " + biomeToIcon.get(enchantToBiome.get(enchantment.getRegisteredName())));
                } else {
                    mt.append(" (" + enchantToBiome.get(enchantment.getRegisteredName()) + ")");
                }
            }
        }
        return original;
    }
}
