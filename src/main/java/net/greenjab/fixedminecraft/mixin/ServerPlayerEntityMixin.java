package net.greenjab.fixedminecraft.mixin;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.greenjab.fixedminecraft.network.SyncHandler;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends Entity
{
    @Shadow
    private int lastArmorScore;

    public ServerPlayerEntityMixin(EntityType<?> entityType, World world)
    {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    void onUpdate(CallbackInfo info)
    {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        SyncHandler.INSTANCE.onPlayerUpdate(player);
    }
    @ModifyConstant(method = "increaseTravelMotionStats", constant = @Constant(floatValue = 0.1f))
    public float armorDrainsStamina(float constant) {
        return 0.1f*((this.lastArmorScore/20.0f)+1.0f);
    }
}
