package net.greenjab.fixedminecraft.mixin.night;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ApplyBonusLootFunction.class)
public class ApplyBonusLootFunctionMixin {

    @ModifyVariable(method = "process", at = @At("STORE"), ordinal = 0)
    private int nightFortune(int i, @Local(argsOnly = true) LootContext context) {
        Entity entity = context.get(LootContextParameters.THIS_ENTITY);
        if (entity!=null) {
            World world = entity.getEntityWorld();
            if (entity instanceof PlayerEntity) {
                if (world.getLightLevel(LightType.SKY, entity.getBlockPos()) > 10) {
                    if (world.isNight() && world.getMoonPhase() == 6) i++;
                }
            }
        }
        return i;
    }
}
