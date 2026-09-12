package net.greenjab.fixedminecraft.mixin.inventory;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Prediction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player{

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Shadow public abstract @NonNull ServerLevel level();

    //TODO test items on ground
    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZLnet/minecraft/util/Prediction;)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "RETURN"))
    private void onGroundForLonger(ItemStack itemStack, boolean thrownFromHand, Prediction prediction,
                                   CallbackInfoReturnable<ItemEntity> cir,
                                   @Local ItemEntity entity) {
        if (!thrownFromHand && entity != null) {
            int ticks = this.level().getGameRules().get(GameRuleRegistry.ITEM_DEATH_DESPAWN_TIME) * 20 * 60;
            if (ticks == 0) entity.setUnlimitedLifetime();
            else entity.age = 6000-ticks;
        }
    }

    @ModifyExpressionValue(method = "restoreFrom", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"))
    private <T> T noDropSpecialItems(T original, @Local(argsOnly = true) ServerPlayer oldPlayer) {
        if (!oldPlayer.level().getGameRules().get(GameRuleRegistry.PARTIAL_KEEP_INVENTORY)) return original;
        if ((boolean) original || oldPlayer.isSpectator()) {
            return original;
        } else {
            for (int i = 0; i < this.getInventory().getContainerSize(); i++) {
                if (oldPlayer.getInventory().getItem(i).is(ModTags.PARTIAL_KEEP_INVENTORY))
                    this.getInventory().setItem(i, oldPlayer.getInventory().getItem(i));
            }
        }
        return original;
    }

    //copyFrom
    @Inject(method = "transferInventoryXpAndScore", at = @At(value = "TAIL"))
    private void keepInventoryCraftingGrid(Player oldPlayer, CallbackInfo ci) {
        ServerPlayer SPE = (ServerPlayer) (Object)this;
        CraftingContainer craftingGrid = SPE.inventoryMenu.getCraftSlots();
        CraftingContainer craftingGridOriginal = SPE.connection.player.inventoryMenu.getCraftSlots();

        for (int i = 0; i < craftingGridOriginal.getContainerSize(); i++) {
            ItemStack itemStack = craftingGridOriginal.getItem(i);
            if (!itemStack.isEmpty()) {
                craftingGrid.setItem(i, itemStack);
            }
        }
    }
}
