package net.greenjab.fixedminecraft.mixin.client;


import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ZombieVillagerEntityModel.class)
public class ZombieVillagerEntityModelMixin{

    @Redirect(method = "getArmorTexturedModelData",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPartBuilder;cuboid(FFFFFFLnet/minecraft/client/model/Dilation;)Lnet/minecraft/client/model/ModelPartBuilder;", ordinal = 1))
    private static ModelPartBuilder leggingsRemoveHalfOfCloak(ModelPartBuilder instance, float offsetX, float offsetY, float offsetZ,
                                                              float sizeX,
                                                              float sizeY, float sizeZ, Dilation extra) {
        return instance.cuboid(-4.0F, 1.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.7F, 0.5F, 1.2F));
    }
}
