package net.greenjab.fixedminecraft.mixin.client;

import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.greenjab.fixedminecraft.util.CustomContainerTextureHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ShulkerBoxScreen.class)
public abstract class ShulkerBoxScreenMixin {

    @Unique
    private static BlockHitResult pick(Entity cameraEntity, final double range) {
        Vec3 from = cameraEntity.getEyePosition(0);
        Vec3 viewVector = cameraEntity.getViewVector(0);
        Vec3 to = from.add(viewVector.x * range, viewVector.y * range, viewVector.z * range);
        return cameraEntity.level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, cameraEntity));
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void setUpCustomShulkerUI(CallbackInfo ci) {
        ShulkerBoxScreen MS = (ShulkerBoxScreen) (Object)this;
        ((CustomContainerTextureHolder) MS).fixedminecraft$setCustomTexture("");
        if (!FixedMinecraftClient.usingCustomContainers()) return;

        double d = 10f;
        BlockHitResult blockHitResult = pick(Minecraft.getInstance().player, d);
        if (blockHitResult != null) {
            BlockState blockState = Minecraft.getInstance().level.getBlockState(blockHitResult.getBlockPos());
            if (blockState.is(BlockTags.SHULKER_BOXES)) {
                String[] type = BuiltInRegistries.BLOCK.wrapAsHolder(blockState.getBlock()).getRegisteredName().split(":");
                if (!Objects.equals(type[0], "minecraft")) return;
                ((CustomContainerTextureHolder) MS).fixedminecraft$setCustomTexture("/" + type[1].split("_")[0]);
                if (!MS.getTitle().getString().isEmpty())
                    ((CustomContainerTextureHolder) MS).fixedminecraft$setCustomTexture(((CustomContainerTextureHolder) MS).fixedminecraft$getCustomTexture() + "_label");
            }
        }
    }
    @ModifyArg(method = "extractBackground", at = @At(value = "INVOKE",
                                                      target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V"
    ), index = 1)
    private Identifier useCustomShulkerUI(Identifier texture) {
        ShulkerBoxScreen MS = (ShulkerBoxScreen) (Object)this;
        if (!((CustomContainerTextureHolder) MS).fixedminecraft$getCustomTexture().isEmpty())
            return Identifier.withDefaultNamespace("textures/gui/container/shulker_box" + ((CustomContainerTextureHolder) MS).fixedminecraft$getCustomTexture() + ".png");
        return texture;
    }
}
