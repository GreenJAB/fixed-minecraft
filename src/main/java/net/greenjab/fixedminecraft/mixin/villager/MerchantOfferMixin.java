package net.greenjab.fixedminecraft.mixin.villager;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.world.item.trading.MerchantOffer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MerchantOffer.class)
public abstract class MerchantOfferMixin {
    @ModifyExpressionValue(method = "updateDemand", at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/trading/MerchantOffer;uses:I", opcode = Opcodes.GETFIELD, ordinal = 0))
    private int strongerDemand(int original) {
        if (!FixedMinecraft.SERVER.getGameRules().get(GameRuleRegistry.VILLAGERS_STRONGER_DEMAND)) return original;
        MerchantOffer MO = (MerchantOffer)(Object)this;
        int uses = MO.getUses();
        int max = MO.getMaxUses();
        int mul = MO.getPriceMultiplier() < 0.1f?2:1;
        if (uses > 0) return (int)((uses / (max + 0.0f)) * 10 * mul) + (max - uses);
        else return Math.max(-MO.getDemand(), -2 * mul) + (max - uses);
    }
}
