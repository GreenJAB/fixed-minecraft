package net.greenjab.fixedminecraft.access;

import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

public interface SpecialFoodAccessor {
    @Nullable
    Item fixedminecraft$lastEatenSpecial();
}
