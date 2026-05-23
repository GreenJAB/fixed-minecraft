package net.greenjab.fixedminecraft.mixin.client.villager;


import net.greenjab.fixedminecraft.models.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/** Credit: Viola-Siemens */
@Mixin(VillagerRenderState.class)
public abstract class VillagerRenderStateMixin implements HumanoidRenderState{
    @Unique
    private ItemStack fixed$headEquipment = ItemStack.EMPTY;
    @Unique
    private ItemStack fixed$chestEquipment = ItemStack.EMPTY;
    @Unique
    private ItemStack fixed$legEquipment = ItemStack.EMPTY;
    @Unique
    private ItemStack fixed$feetEquipment = ItemStack.EMPTY;


    @Override
    public ItemStack fixed$headEquipment() {
        return this.fixed$headEquipment;
    }

    @Override
    public ItemStack fixed$chestEquipment() {
        return this.fixed$chestEquipment;
    }

    @Override
    public ItemStack fixed$legEquipment() {
        return this.fixed$legEquipment;
    }

    @Override
    public ItemStack fixed$feetEquipment() {
        return this.fixed$feetEquipment;
    }


    @Override
    public void fixed$setHeadEquipment(ItemStack headItem) {
        this.fixed$headEquipment = headItem;
    }

    @Override
    public void fixed$setChestEquipment(ItemStack chestItem) {
        this.fixed$chestEquipment = chestItem;
    }

    @Override
    public void fixed$setLegEquipment(ItemStack legItem) {
        this.fixed$legEquipment = legItem;
    }

    @Override
    public void fixed$setFeetEquipment(ItemStack feetItem) {
        this.fixed$feetEquipment = feetItem;
    }
}
