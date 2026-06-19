package net.greenjab.fixedminecraft.mixin.client;

import net.greenjab.fixedminecraft.util.DonkeyArmorRenderStateAccess;
import net.minecraft.client.renderer.entity.state.DonkeyRenderState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DonkeyRenderState.class)
public abstract class DonkeyRenderStateMixin implements DonkeyArmorRenderStateAccess {
    @Unique
    private ItemStack armor = ItemStack.EMPTY;

    @Override
    public ItemStack fixedminecraft$getArmor() {
        return this.armor;
    }

    @Override
    public void fixedminecraft$setArmor(ItemStack spotted) {
        this.armor = spotted;
    }
}
