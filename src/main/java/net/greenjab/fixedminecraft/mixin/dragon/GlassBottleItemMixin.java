package net.greenjab.fixedminecraft.mixin.dragon;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

@Mixin(GlassBottleItem.class)
public class GlassBottleItemMixin {

    @Inject(method = "use", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/GlassBottleItem;fill(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", ordinal = 0
    ))
    private void doubleOmenBreath(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir,
                                  @Local List<AreaEffectCloudEntity> areaEffectCloudEntity) {
        if (world instanceof ServerWorld serverWorld) {
            EnderDragonFight enderDragonFight = serverWorld.getEnderDragonFight();
            if (enderDragonFight!=null) {
               UUID uuid = enderDragonFight.getDragonUuid();
               Entity entity = serverWorld.getEntity(uuid);
               if (entity.getCommandTags().contains("omen")) {
                   user.getInventory().insertStack(Items.DRAGON_BREATH.getDefaultStack());
               }
            }
        }
    }

}
