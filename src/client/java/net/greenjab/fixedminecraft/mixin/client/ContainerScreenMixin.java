package net.greenjab.fixedminecraft.mixin.client;

import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.greenjab.fixedminecraft.util.CustomContainerTextureHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.SpecialDates;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContainerScreen.class)
public abstract class ContainerScreenMixin {

    @Unique
    private static BlockHitResult pick(Entity cameraEntity, final double range) {
        Vec3 from = cameraEntity.getEyePosition(0);
        Vec3 viewVector = cameraEntity.getViewVector(0);
        Vec3 to = from.add(viewVector.x * range, viewVector.y * range, viewVector.z * range);
        return cameraEntity.level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, cameraEntity));
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void setUpCustomChestUI(CallbackInfo ci) {
        ContainerScreen CS = (ContainerScreen) (Object)this;
        ((CustomContainerTextureHolder) CS).fixedminecraft$setCustomTexture("");
        if (!FixedMinecraftClient.usingCustomContainers()) return;

        double d = 10f;
        BlockHitResult blockHitResult = pick(Minecraft.getInstance().player, d);
        if (blockHitResult != null) {
            BlockState blockState = Minecraft.getInstance().level.getBlockState(blockHitResult.getBlockPos());
            if (blockState.is(BlockTags.COPPER_CHESTS)) {
                if (blockState.is(Blocks.COPPER_CHEST) || blockState.is(Blocks.WAXED_COPPER_CHEST))
                    ((CustomContainerTextureHolder) CS).fixedminecraft$setCustomTexture("/copper");
                else if (blockState.is(Blocks.EXPOSED_COPPER_CHEST) || blockState.is(Blocks.WAXED_EXPOSED_COPPER_CHEST))
                    ((CustomContainerTextureHolder) CS).fixedminecraft$setCustomTexture("/exposed_copper");
                else if (blockState.is(Blocks.WEATHERED_COPPER_CHEST) || blockState.is(Blocks.WAXED_WEATHERED_COPPER_CHEST))
                    ((CustomContainerTextureHolder) CS).fixedminecraft$setCustomTexture("/weathered_copper");
                else if (blockState.is(Blocks.OXIDIZED_COPPER_CHEST) || blockState.is(Blocks.WAXED_OXIDIZED_COPPER_CHEST))
                    ((CustomContainerTextureHolder) CS).fixedminecraft$setCustomTexture("/oxidized_copper");
                if (CS.getMenu().getRowCount()==3)
                    ((CustomContainerTextureHolder) CS).fixedminecraft$setCustomTexture(((CustomContainerTextureHolder) CS).fixedminecraft$getCustomTexture() + "_small");
            } else {
                if (blockState.is(Blocks.TRAPPED_CHEST))
                    ((CustomContainerTextureHolder) CS).fixedminecraft$setCustomTexture("/trapped");
                else if (blockState.is(Blocks.ENDER_CHEST))
                    ((CustomContainerTextureHolder) CS).fixedminecraft$setCustomTexture("/ender");
                else if (blockState.is(Blocks.BARREL))
                    ((CustomContainerTextureHolder) CS).fixedminecraft$setCustomTexture("/barrel");
                else if (blockState.is(Blocks.CHEST)) {
                    if (SpecialDates.isExtendedChristmas()){
                        ((CustomContainerTextureHolder) CS).fixedminecraft$setCustomTexture("/christmas");
                        if (CS.getMenu().getRowCount()==3)
                            ((CustomContainerTextureHolder) CS).fixedminecraft$setCustomTexture(((CustomContainerTextureHolder) CS).fixedminecraft$getCustomTexture() + "_small");
                    } else ((CustomContainerTextureHolder) CS).fixedminecraft$setCustomTexture("/chest");
                }
            }
        }
    }
    @ModifyArg(method = "extractBackground", at = @At(value = "INVOKE",
                                                      target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V"
    ), index = 1)
    private Identifier useCustomChestUI(Identifier texture) {
        ContainerScreen MS = (ContainerScreen) (Object)this;
        if (!((CustomContainerTextureHolder) MS).fixedminecraft$getCustomTexture().isEmpty())
            return Identifier.withDefaultNamespace("textures/gui/container/chest" + ((CustomContainerTextureHolder) MS).fixedminecraft$getCustomTexture() + ".png");
        return texture;
    }
}
