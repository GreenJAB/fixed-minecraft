package net.greenjab.fixedminecraft.util;

import net.minecraft.entity.player.PlayerEntity;

public class ExhaustionHelper
{
    public interface ExhaustionManipulator
    {
        float getExhaustion();

        void setExhaustion(float exhaustion);
    }

    public static float getExhaustion(PlayerEntity player)
    {
        return 0;
        //return ((ExhaustionManipulator) player.getHungerManager()).getExhaustion();
    }

    public static void setExhaustion(PlayerEntity player, float exhaustion)
    {
        //((ExhaustionManipulator) player.getHungerManager()).setExhaustion(exhaustion);
    }
}
