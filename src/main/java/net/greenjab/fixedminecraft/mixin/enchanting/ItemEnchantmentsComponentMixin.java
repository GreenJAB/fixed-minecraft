package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;

@Mixin(ItemEnchantmentsComponent.class)
public class ItemEnchantmentsComponentMixin {

    private static HashMap<String, String> enchantToBiome;
    private static HashMap<String, String> biomeToIcon;

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

        biomeToIcon.put("desert", "\u00a7f\ufd00");
        biomeToIcon.put("jungle", "\u00a7f\ufd01");
        biomeToIcon.put("plains", "\u00a7f\ufd02");
        biomeToIcon.put("savanna", "\u00a7f\ufd03");
        biomeToIcon.put("snow", "\u00a7f\ufd04");
        biomeToIcon.put("swamp", "\u00a7f\ufd05");
        biomeToIcon.put("taiga", "\u00a7f\ufd06");

        biomeToIcon.put("fisherman", "\u00a7f\ufd10");
        biomeToIcon.put("deep_dark", "\u00a7f\ufd11");
        biomeToIcon.put("piglin", "\u00a7f\ufd12");

    }


    @ModifyExpressionValue(method = "appendTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getName(Lnet/minecraft/registry/entry/RegistryEntry;I)Lnet/minecraft/text/Text;"))
    private Text addEnchantLocationTooltip(Text original, @Local(argsOnly = true) TooltipType type, @Local RegistryEntry<Enchantment> registryEntry) {

        if (type == TooltipType.ADVANCED) {
            MutableText mt = (MutableText) original;
            if (enchantToBiome.containsKey(registryEntry.getIdAsString())) {
                if (biomeToIcon.containsKey(enchantToBiome.get(registryEntry.getIdAsString()))) {
                    mt.append(" " + biomeToIcon.get(enchantToBiome.get(registryEntry.getIdAsString())));
                } else {
                    mt.append(" (" + enchantToBiome.get(registryEntry.getIdAsString()) + ")");
                }
            }
        }
        return original;
    }

    /*@ModifyExpressionValue(method = "appendTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getName(Lnet/minecraft/registry/entry/RegistryEntry;I)Lnet/minecraft/text/Text;"))
    private Text addEnchantLocationTooltip(Text original, @Local(argsOnly = true) TooltipType type, @Local RegistryEntry<Enchantment> registryEntry) {
        MutableText text = (MutableText)Text.of("");
        if (type == TooltipType.ADVANCED) {
            if (enchantToBiome.containsKey(registryEntry.getIdAsString())) {
                if (biomeToIcon.containsKey(enchantToBiome.get(registryEntry.getIdAsString()))) {
                    text.append(biomeToIcon.get(enchantToBiome.get(registryEntry.getIdAsString())) + " ");
                }
            }
        }
        text.append(original);
        return text;
    }*/

    /*@Redirect(method = "appendTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getName(Lnet/minecraft/registry/entry/RegistryEntry;I)Lnet/minecraft/text/Text;"))
    private Text addEnchantLocationTooltip(RegistryEntry<Enchantment> enchantment, int level,
                                           @Local(argsOnly = true) TooltipType type) {

        if (type == TooltipType.ADVANCED) {
            MutableText mt = (MutableText) original;
            if (enchantToBiome.containsKey(enchantment.getIdAsString())) {
                if (biomeToIcon.containsKey(enchantToBiome.get(enchantment.getIdAsString()))) {
                    mt.append(" " + biomeToIcon.get(enchantToBiome.get(enchantment.getIdAsString())));
                } else {
                    mt.append(" (" + enchantToBiome.get(enchantment.getIdAsString()) + ")");
                }
            }
        }
        Enchantment.getName(enchantment, level);
        return original;
    }*/

}
