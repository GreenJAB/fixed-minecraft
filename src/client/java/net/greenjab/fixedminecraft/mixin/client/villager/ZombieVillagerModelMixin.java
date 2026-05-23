package net.greenjab.fixedminecraft.mixin.client.villager;

import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.monster.zombie.ZombieVillagerModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ZombieVillagerModel.class)
public abstract class ZombieVillagerModelMixin {

    @Redirect(method = "createBaseArmorMesh",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/builders/CubeListBuilder;addBox(FFFFFFLnet/minecraft/client/model/geom/builders/CubeDeformation;)Lnet/minecraft/client/model/geom/builders/CubeListBuilder;", ordinal = 1))
    private static CubeListBuilder leggingsRemoveHalfOfCloak(CubeListBuilder instance, float x0, float y0, float z0, float w, float h,
                                                             float d, CubeDeformation g) {
        return instance.addBox(-4.0F, 1.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.7F, 0.5F, 1.2F));
    }
}
