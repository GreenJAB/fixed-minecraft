package net.greenjab.fixedminecraft.mixin.villager;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderMixin extends AbstractVillager {

    public WanderingTraderMixin(EntityType<? extends AbstractVillager> type, Level level) {
        super(type, level);
    }

    @Inject(method = "updateTrades", at = @At("TAIL"))
    private void addSpecial(ServerLevel level, CallbackInfo ci, @Local MerchantOffers offers){
        WanderingTrader WTE = (WanderingTrader)(Object)this;
        WTE.addOffersFromTradeSet(level, offers, ModTags.WANDERING_TRADER_SPECIAL);
    }

    @Inject(method = "mobInteract", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/wanderingtrader/WanderingTrader;setTradingPlayer(Lnet/minecraft/world/entity/player/Player;)V"
    ))
    private void msgPlayer(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        player.sendSystemMessage(Component.translatable("entity.fixedminecraft.villager.wandering", this.getName()));
    }
}
