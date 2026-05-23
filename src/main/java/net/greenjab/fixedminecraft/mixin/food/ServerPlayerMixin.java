package net.greenjab.fixedminecraft.mixin.food;


import net.greenjab.fixedminecraft.FixedMinecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.greenjab.fixedminecraft.network.SyncHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Entity
{
    public ServerPlayerMixin(EntityType<?> entityType, Level world)
    {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    void onUpdate(CallbackInfo info)
    {
        ServerPlayer player = (ServerPlayer) (Object) this;
        SyncHandler.onPlayerUpdate(player);
    }

    @ModifyConstant(method = "checkMovementStatistics", constant = @Constant(floatValue = 0.1f))
    public float armorDrainsStamina(float constant) {
        int weight = 0;
        ServerPlayer player = (ServerPlayer) (Object) this;
        for (ItemStack item : FixedMinecraft.getArmor(player)) {
            String s = item.getItemName().toString();
            if (s.contains("iron")||s.contains("gold")) weight+=1;
            if (s.contains("diamond")||s.contains("netherite")) weight+=2;
        }
        int diff = this.level().getDifficulty().getId();
        float multiplier = (diff*weight)/48.0f;
        return 0.1f*(multiplier+1.0f);
    }

    @ModifyConstant(method = "checkMovementStatistics", constant = @Constant(floatValue = 0.01f, ordinal = 0))
    public float swimDrainsStamina(float constant) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (player.isAutoSpinAttack()) {
            return 0;
        }
        int weight = 0;
        for (ItemStack item : FixedMinecraft.getArmor(player)) {
            String s = item.getItemName().toString();
            if (s.contains("iron")||s.contains("gold")) weight+=1;
            if (s.contains("diamond")||s.contains("netherite")) weight+=2;
        }
        int diff = this.level().getDifficulty().getId();
        float multiplier = (diff*weight)/48.0f;
        return 0.06f*(multiplier+1.0f);
    }

    @ModifyConstant(method = "checkMovementStatistics", constant = @Constant(floatValue = 0.01f, ordinal = 2))
    public float walkSwimNoStamina(float constant) { return 0; }
    @ModifyConstant(method = "checkMovementStatistics", constant = @Constant(floatValue = 0.01f, ordinal = 4))
    public float walkSwimNoStamina2(float constant) { return 0; }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void shieldDrainsStamina(CallbackInfo ci) {
        ServerPlayer SPE = (ServerPlayer) (Object)this;
        if (SPE.isBlocking()) SPE.causeFoodExhaustion(0.03f);
    }

    @ModifyConstant(method = "jumpFromGround", constant = @Constant(floatValue = 0.05f))
    private float noStaminaNormalJump(float constant) {
        return 0;
    }
}
