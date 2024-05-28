package net.greenjab.fixedminecraft.mixin;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
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

import java.util.Iterator;

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
        int weight = 0;
        Iterator<ItemStack> e = this.getArmorItems().iterator();
        for (int i = 0;i<4;i++) {
            String s = e.next().getName().toString();
            if (s.contains("iron")||s.contains("gold")) weight+=1;
            if (s.contains("diamond")||s.contains("netherite")) weight+=2;
        }
        return 0.1f*((weight/16.0f)+1.0f);
    }
}
