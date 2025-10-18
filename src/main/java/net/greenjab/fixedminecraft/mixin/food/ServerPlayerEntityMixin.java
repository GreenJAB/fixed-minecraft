package net.greenjab.fixedminecraft.mixin.food;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.greenjab.fixedminecraft.network.SyncHandler;

import java.util.Iterator;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends Entity
{

    public ServerPlayerEntityMixin(EntityType<?> entityType, World world)
    {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    void onUpdate(CallbackInfo info)
    {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        SyncHandler.onPlayerUpdate(player);
    }

    @ModifyConstant(method = "increaseTravelMotionStats", constant = @Constant(floatValue = 0.1f))
    public float armorDrainsStamina(float constant) {
        int weight = 0;
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        Iterator<ItemStack> e = player.getArmorItems().iterator();
        for (int i = 0;i<4;i++) {
            String s = e.next().getName().toString();
            if (s.contains("iron")||s.contains("gold")) weight+=1;
            if (s.contains("diamond")||s.contains("netherite")) weight+=2;
        }
        int diff = this.getWorld().getDifficulty().getId();
        float multiplier = (diff*weight)/48.0f;
        return 0.1f*(multiplier+1.0f);
    }

    @ModifyConstant(method = "increaseTravelMotionStats", constant = @Constant(floatValue = 0.01f, ordinal = 0))
    public float swimDrainsStamina(float constant) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (player.isUsingRiptide()) {
            return 0;
        }
        int weight = 0;
        Iterator<ItemStack> e = player.getArmorItems().iterator();
        for (int i = 0;i<4;i++) {
            String s = e.next().getName().toString();
            if (s.contains("iron")||s.contains("gold")) weight+=1;
            if (s.contains("diamond")||s.contains("netherite")) weight+=2;
        }
        int diff = this.getWorld().getDifficulty().getId();
        float multiplier = (diff*weight)/48.0f;
        return 0.06f*(multiplier+1.0f);
    }

    @ModifyConstant(method = "increaseTravelMotionStats", constant = @Constant(floatValue = 0.01f, ordinal = 2))
    public float walkSwimNoStamina(float constant) { return 0; }
    @ModifyConstant(method = "increaseTravelMotionStats", constant = @Constant(floatValue = 0.01f, ordinal = 4))
    public float walkSwimNoStamina2(float constant) { return 0; }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void shieldDrainsStamina(CallbackInfo ci) {
        ServerPlayerEntity SPE = (ServerPlayerEntity) (Object)this;
        if (SPE.isBlocking()) SPE.addExhaustion(0.03f);
    }

    @ModifyConstant(method = "jump", constant = @Constant(floatValue = 0.05f))
    private float noStaminaNormalJump(float constant) {
        return 0;
    }
}
