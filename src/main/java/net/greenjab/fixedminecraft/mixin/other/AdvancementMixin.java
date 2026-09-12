package net.greenjab.fixedminecraft.mixin.other;

import net.minecraft.advancements.Advancement;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Advancement.class)
public abstract class AdvancementMixin {

    /*@Shadow
    public abstract AdvancementRewards rewards();

    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementRequirements;write(Lnet/minecraft/network/FriendlyByteBuf;)V"))
    private void storeExperienceReward(RegistryFriendlyByteBuf output, CallbackInfo ci) {
        output.writeUtf(String.valueOf(this.rewards().experience()));
    }

    @ModifyExpressionValue(method = "read", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/advancements/AdvancementRewards;EMPTY:Lnet/minecraft/advancements/AdvancementRewards;",
            opcode = Opcodes.GETSTATIC
    ))
    private static AdvancementRewards readExperienceReward(AdvancementRewards original, @Local(argsOnly = true) RegistryFriendlyByteBuf input) {
        return new AdvancementRewards.Builder().addExperience(Integer.parseInt(input.readUtf())).build();
    }*/
    //TODO advancement experience
}
