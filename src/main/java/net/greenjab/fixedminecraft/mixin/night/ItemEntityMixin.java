package net.greenjab.fixedminecraft.mixin.night;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    public ItemEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target ="Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private boolean dontKill(boolean original) {
        if (this.getFirstPassenger()!=null) {
            this.refreshDimensions();
            return false;
        }
        return original;
    }
}
