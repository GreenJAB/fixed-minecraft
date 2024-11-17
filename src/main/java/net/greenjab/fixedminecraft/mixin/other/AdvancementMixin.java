package net.greenjab.fixedminecraft.mixin.other;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Advancement.class)
public abstract class AdvancementMixin {

    @Shadow
    public abstract AdvancementRewards rewards();

    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/AdvancementRequirements;writeRequirements(Lnet/minecraft/network/PacketByteBuf;)V"))
    private void injected(PacketByteBuf buf, CallbackInfo ci) {
        buf.writeText(Text.of(String.valueOf(this.rewards().experience())));
    }

    @ModifyExpressionValue(method = "read", at = @At(value = "FIELD", target = "Lnet/minecraft/advancement/AdvancementRewards;NONE:Lnet/minecraft/advancement/AdvancementRewards;"))
    private static AdvancementRewards injected(AdvancementRewards original, @Local(argsOnly = true) PacketByteBuf buf) {
        return new AdvancementRewards.Builder().setExperience(Integer.parseInt(buf.readText().getLiteralString())).build();
    }
}
