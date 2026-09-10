package net.greenjab.fixedminecraft.mixin.effects;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(SulfurCube.class)
public abstract class SulfurCubeMixin {

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void waterAreaEffect(CallbackInfo ci) {
        SulfurCube SC = (SulfurCube)(Object)this;
        ArrayList<Holder<MobEffect>> list = new ArrayList<>();
        if (!SC.hasBodyItem()) SC.getActiveEffects().forEach(effect -> {if (effect.isAmbient())list.add(effect.getEffect());});
        list.forEach(SC::removeEffect);
    }
}
