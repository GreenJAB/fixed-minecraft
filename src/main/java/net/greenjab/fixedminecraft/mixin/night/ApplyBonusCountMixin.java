package net.greenjab.fixedminecraft.mixin.night;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ApplyBonusCount.class)
public abstract class ApplyBonusCountMixin {

    @ModifyVariable(method = "run", at = @At("STORE"), ordinal = 0)
    private int nightFortune(int level, @Local(argsOnly = true) LootContext context) {
        Entity entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (entity!=null) {
            Level world = entity.level();
            if (entity instanceof ServerPlayer) {
                if (world.getBrightness(LightLayer.SKY, entity.blockPosition()) > 10) {
                    MoonPhase moonPhase = (world).environmentAttributes().getValue(EnvironmentAttributes.MOON_PHASE, entity.blockPosition());
                    if (world.isDarkOutside() && moonPhase.index() == 6) level++;
                }
            }
        }
        return level;
    }
}
