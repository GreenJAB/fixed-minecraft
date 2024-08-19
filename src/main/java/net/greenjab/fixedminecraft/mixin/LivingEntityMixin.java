package net.greenjab.fixedminecraft.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.ItemRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionTypeRegistrar;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;


@SuppressWarnings("unchecked")
@Mixin(LivingEntity.class)
public class LivingEntityMixin  {

    /*@Redirect(method = "tryUseTotem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private boolean requireUsingTotem(ItemStack itemStack2, Item item, DamageSource source) {
        return (itemStack2.isOf(item) && ((LivingEntity)(Object)this).isUsingItem());
    }*/

    @Redirect(method = "tryUseTotem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private boolean echoTotem(ItemStack instance, Item item) {
        return instance.isOf(item)||instance.isOf(ItemRegistry.INSTANCE.getECHO_TOTEM());
    }

   /* @Redirect(method = "tryUseTotem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    private void brokenTotem(ItemStack instance, int amount) {
        ItemStack broken = new ItemStack(ItemRegistry.INSTANCE.getBROKEN_TOTEM());
        if ((LivingEntity)(Object)this instanceof PlayerEntity) {
            PlayerEntity user = (PlayerEntity) (Object) this;
            //user.giveItemStack(broken);

            //couldn't work out how to replace the totem with a broken as it seems to delete the item even if
            // "decrement" is redirected so just giving the player a broken one
            ItemStack itemStack3 = ItemUsage.exchangeStack(instance, user, broken);
        }
    }*/

    @Inject(method = "tryUseTotem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER))
    private void brokenTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir,
                             @Local ItemStack itemStack,@Local ItemStack itemStack2) {
        PlayerEntity user = (PlayerEntity) (Object) this;
        ItemStack broken = new ItemStack(ItemRegistry.INSTANCE.getBROKEN_TOTEM());
        //ItemStack itemStack3 = ItemUsage.exchangeStack(itemStack2, user, broken);

        //couldn't work out how to replace the totem with a broken as it seems to delete the item even if
        // "decrement" is redirected so just giving the player a broken one
        if (!user.getInventory().insertStack(broken)) {
            user.dropItem(broken, true);
        }
    }


    @Inject(method = "tryUseTotem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getWorld()Lnet/minecraft/world/World;", shift = At.Shift.AFTER))
    private void echoTeleport(DamageSource source, CallbackInfoReturnable<Boolean> cir, @Local ItemStack itemStack) {
        if ((LivingEntity)(Object)this instanceof PlayerEntity) {
            if (itemStack.isOf(ItemRegistry.INSTANCE.getECHO_TOTEM())) {
                ServerPlayerEntity user = (ServerPlayerEntity) (Object) this;
                Optional<Vec3d> pos = PlayerEntity.findRespawnPosition(user.getServerWorld(), user.getSpawnPointPosition(), user.getSpawnAngle(), user.isSpawnForced(), true);
                //user.getSpawnPointDimension().getValue().
                //ServerWorld serverWorld = user.getServerWorld();
                //MinecraftServer minecraftServer = serverWorld.getServer();
                //ServerWorld serverWorld2 = minecraftServer.getWorld(user.getSpawnPointDimension());
                //user.moveToWorld(serverWorld2);
                //user.teleport(serverWorld2, pos.get().x,pos.get().y,pos.get().z,user.getYaw(),user.getPitch());
                //user.setServerWorld(serverWorld2);
                //user.moveToWorld(serverWorld2);
                //user.teleport(pos.get().x,pos.get().y,pos.get().z);


                user.teleport(user.getServerWorld(), pos.get().x,pos.get().y,pos.get().z,user.getYaw(),user.getPitch());
            }
        }
    }
}
