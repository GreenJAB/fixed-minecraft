package net.greenjab.fixedminecraft.mixin.villager;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.ai.gossip.GossipType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GossipType.class)
public abstract class GossipTypeMixin  {

    @WrapOperation(method = "<clinit>", at = @At(value = "NEW", target = "(Ljava/lang/String;ILjava/lang/String;IIII)Lnet/minecraft/world/entity/ai/gossip/GossipType;"))
    private static GossipType increaseMinorGossip(String id, int num, String name, int weight, int max, int decayPerDay, int decayPerTransfer,
                                                  Operation<GossipType> original) {
        if (num ==2) original.call(id, num, name, 3, max, decayPerDay, decayPerTransfer);
        return original.call(id, num, name, weight, max, decayPerDay, decayPerTransfer);
    }
}
