package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Optional;

@Mixin(EndCityGenerator.Piece.class)
public class EndCityGeneratorMixin {

    @Inject(method = "handleMetadata", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/ServerWorldAccess;spawnEntity(Lnet/minecraft/entity/Entity;)Z", ordinal = 0
    ))
    private void dyeableShulkerEntities(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox,
                                        CallbackInfo ci, @Local ShulkerEntity shulkerEntity) {
        if (random.nextInt(10)==0) {
            shulkerEntity.setColor(Optional.of(DyeColor.PURPLE));
        }
    }
}
