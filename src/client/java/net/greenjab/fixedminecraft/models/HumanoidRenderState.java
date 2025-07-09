package net.greenjab.fixedminecraft.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;

/** Credit: Viola-Siemens */
@Environment(EnvType.CLIENT)
public interface HumanoidRenderState {
    ItemStack fixed$headEquipment();
    ItemStack fixed$chestEquipment();
    ItemStack fixed$legEquipment();
    ItemStack fixed$feetEquipment();

    void fixed$setHeadEquipment(ItemStack headItem);
    void fixed$setChestEquipment(ItemStack chestItem);
    void fixed$setLegEquipment(ItemStack legItem);
    void fixed$setFeetEquipment(ItemStack feetItem);

}
