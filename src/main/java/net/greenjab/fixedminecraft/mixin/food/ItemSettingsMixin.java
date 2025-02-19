package net.greenjab.fixedminecraft.mixin.food;


import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.item.Items.register;

@Mixin(Item.Settings.class)
public class ItemSettingsMixin <T>  {

    @Inject(method = "repairable(Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item$Settings;", at = @At(value = "HEAD"))
    private void i(Item repairIngredient, CallbackInfoReturnable<Item.Settings> cir){
        System.out.println(repairIngredient);
    }

    @ModifyArg(method = "repairable(Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item$Settings;", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/entry/RegistryEntryList;of([Lnet/minecraft/registry/entry/RegistryEntry;)Lnet/minecraft/registry/entry/RegistryEntryList$Direct;"))
    private RegistryEntry<T>[] i(RegistryEntry<T>[] entries, @Local(argsOnly = true) Item repairIngredient){
        System.out.println(repairIngredient.getRegistryEntry());
        return new RegistryEntry[0];
    }

}
