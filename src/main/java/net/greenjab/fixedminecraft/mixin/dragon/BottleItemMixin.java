package net.greenjab.fixedminecraft.mixin.dragon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.end.EnderDragonFight;

@Mixin(BottleItem.class)
public abstract class BottleItemMixin {

    @Inject(method = "use", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/BottleItem;turnBottleIntoItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;", ordinal = 0
    ))
    private void doubleOmenBreath(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (level instanceof ServerLevel serverWorld) {
            EnderDragonFight enderDragonFight = serverWorld.getDragonFight();
            if (enderDragonFight!=null) {
               UUID uuid = enderDragonFight.dragonUUID();
               Entity entity = serverWorld.getEntity(uuid);
               if (entity!=null && entity.entityTags().contains("omen")) {
                   player.getInventory().add(Items.DRAGON_BREATH.getDefaultInstance());
               }
            }
        }
    }

}
