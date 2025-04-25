package net.greenjab.fixedminecraft.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    @ModifyConstant(method = "applyFog(Lnet/minecraft/client/render/Camera;Lorg/joml/Vector4f;FZF)V", constant = @Constant(floatValue = 5.0f))
    private float lessLavaFogFireRes(float constant) { return 9f;}

    @ModifyConstant(method = "applyFog(Lnet/minecraft/client/render/Camera;Lorg/joml/Vector4f;FZF)V", constant = @Constant(floatValue = 1.0f))
    private float lessLavaFog(float constant,
                              @Local Entity entity) {
        int i = 0;
        for (ItemStack item : FixedMinecraft.getArmor((PlayerEntity) entity)) {
            i += FixedMinecraftEnchantmentHelper.enchantLevel(item, "fire_protection");
        }
        return 2.5f + 0.25f*Math.min(2*i,25);
    }

    @ModifyConstant(method = "applyFog(Lnet/minecraft/client/render/Camera;Lorg/joml/Vector4f;FZF)V", constant = @Constant(floatValue = 4.0f))
    private float moreSkyFog(float constant,
                             @Local(ordinal = 0, argsOnly = true) float viewDistance) { return Math.min(64f, viewDistance / 2);}

    /*@Redirect(method = "applyFog(Lnet/minecraft/client/render/Camera;Lorg/joml/Vector4f;FZF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BackgroundRenderer;applyFog(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;Lnet/minecraft/client/render/FogShape;FFFF)V"))
    private void paleFog(
            BackgroundRenderer instance, ByteBuffer buffer, int i, Vector4f fogColor, FogShape fogShape, float fogStart, float fogEnd,
            float skyEnd, float cloudEnd,
            @Local(argsOnly = true) Camera camera,
            @Local(ordinal = 0, argsOnly = true) float viewDistance,
            @Local BackgroundRenderer.FogData fogType) {
            float palefog = FixedMinecraftClient.paleGardenFog;
            float palefog2 = palefog*palefog;
            World world = camera.getFocusedEntity().getWorld();
            int light = world.getLightLevel(LightType.SKY, camera.getBlockPos());
            float caveGradiant = Math.min(Math.max(0.85f-light/7f, 0.15f), 1);

            Vector4f c = getFogColor(camera, camera.getLastTickProgress(), (ClientWorld) world);
            float r3 = (1-caveGradiant) * fogColor.x + caveGradiant * c.x;
            float g3 = (1-caveGradiant) * fogColor.y + caveGradiant * c.y;
            float b3 = (1-caveGradiant) * fogColor.z + caveGradiant * c.z;
            float a3 = (1-caveGradiant) * fogColor.w + caveGradiant * c.w;


        BackgroundRenderer.FogData fog = new BackgroundRenderer.FogData( 3+(f-3) *(1-palefog2)/(75*palefog2+1), 16 + (g - 16) * (1-palefog2)/(25*palefog2+1), FogShape.SPHERE, r3, g3, b3, a3);
            if (world.getBiome(camera.getBlockPos()).isIn(ModTags.IS_PALE_GARDEN) ) {
                FixedMinecraftClient.paleGardenFog = Math.min(palefog + 0.003f, 1);
                return fog;
            } else {
                FixedMinecraftClient.paleGardenFog = Math.max(palefog - 0.005f, 0);

                if (palefog > 0) {
                    return fog;
                }
            }

            BackgroundRenderer backgroundRenderer = (BackgroundRenderer)(Object)this;

        backgroundRenderer.applyFog(mappedView.data(), 0, fogColor, fogData.fogShape, fogData.fogStart, fogData.fogEnd, fogData.skyEnd, fogData.cloudEnd);

    }*/

    /*//TODO test
    @ModifyVariable(method = "applyFog(Lnet/minecraft/client/render/Camera;Lorg/joml/Vector4f;FZF)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/BackgroundRenderer;applyFog(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;Lnet/minecraft/client/render/FogShape;FFFF)V"
    ))
    private BackgroundRenderer.FogData i(BackgroundRenderer.FogData fog, @Local(argsOnly = true) Camera camera,
                                         @Local(argsOnly = true) Vector4f fogColor) {
            float palefog = FixedMinecraftClient.paleGardenFog;
            float palefog2 = palefog*palefog;
            World world = camera.getFocusedEntity().getWorld();

            if (world.getBiome(camera.getBlockPos()).isIn(ModTags.IS_PALE_GARDEN) ) {
                FixedMinecraftClient.paleGardenFog = Math.min(palefog + 0.003f, 1);

                fog.fogStart = 3+(fog.fogStart-3) *(1-palefog2)/(75*palefog2+1);
                fog.fogEnd = 16 + (fog.fogEnd - 16) * (1-palefog2)/(25*palefog2+1);
                fog.fogShape = FogShape.SPHERE;

            } else {
                FixedMinecraftClient.paleGardenFog = Math.max(palefog - 0.005f, 0);

                if (palefog > 0) {
                    fog.fogStart = 3+(fog.fogStart-3) *(1-palefog2)/(75*palefog2+1);
                    fog.fogEnd = 16 + (fog.fogEnd - 16) * (1-palefog2)/(25*palefog2+1);
                    fog.fogShape = FogShape.SPHERE;
                }
            }

            return fog;
    }

    @ModifyVariable(
            method = "applyFog(Lnet/minecraft/client/render/Camera;Lorg/joml/Vector4f;FZF)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/BackgroundRenderer;applyFog(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;Lnet/minecraft/client/render/FogShape;FFFF)V"
    ), argsOnly = true
    )
    private Vector4f fogColor(Vector4f fogColor, @Local(argsOnly = true) Camera camera) {

        float palefog = FixedMinecraftClient.paleGardenFog;
        World world = camera.getFocusedEntity().getWorld();
        int light = world.getLightLevel(LightType.SKY, camera.getBlockPos());
        float caveGradiant = Math.min(Math.max(0.85f-light/7f, 0.15f), 1);
        Vector4f c = getFogColor(camera, camera.getLastTickProgress(), (ClientWorld) world);
        float r3 = (1-caveGradiant) * fogColor.x + caveGradiant * c.x;
        float g3 = (1-caveGradiant) * fogColor.y + caveGradiant * c.y;
        float b3 = (1-caveGradiant) * fogColor.z + caveGradiant * c.z;
        float a3 = (1-caveGradiant) * fogColor.w + caveGradiant * c.w;

        if (world.getBiome(camera.getBlockPos()).isIn(ModTags.IS_PALE_GARDEN) ) {
            FixedMinecraftClient.paleGardenFog = Math.min(palefog + 0.003f, 1);

            return new Vector4f(r3, g3, b3, a3);

        } else {
            FixedMinecraftClient.paleGardenFog = Math.max(palefog - 0.005f, 0);

            if (palefog > 0) {
                return new Vector4f(r3, g3, b3, a3);
            }
        }

        return fogColor;
    }*/

    @ModifyArgs(method = "applyFog(Lnet/minecraft/client/render/Camera;Lorg/joml/Vector4f;FZF)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/BackgroundRenderer;applyFog(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;Lnet/minecraft/client/render/FogShape;FFFF)V"
    ))
    private void i(Args args,
                   @Local(argsOnly = true) Camera camera,
                   @Local(argsOnly = true) Vector4f fogColor) {
        float palefog = FixedMinecraftClient.paleGardenFog;
        World world = camera.getFocusedEntity().getWorld();

        //0 ByteBuffer buffer, 1 int i, 2 Vector4f fogColor, 3 FogShape fogShape, 4 float fogStart, 5 float fogEnd, 6 float skyEnd, 7 float cloudEnd
        boolean inPale = world.getBiome(camera.getBlockPos()).isIn(ModTags.IS_PALE_GARDEN);
        if (inPale) {
            FixedMinecraftClient.paleGardenFog = Math.min(palefog + 0.003f, 1);
        } else {
            FixedMinecraftClient.paleGardenFog = Math.max(palefog - 0.005f, 0);
        }
        if (inPale || palefog > 0) {
            float palefog2 = palefog*palefog;
            int light = world.getLightLevel(LightType.SKY, camera.getBlockPos());
            float caveGradiant = Math.min(Math.max(0.85f-light/7f, 0.15f), 1);
            Vector4f c = getFogColor(camera, camera.getLastTickProgress(), (ClientWorld) world);
            float r3 = (1-caveGradiant) * fogColor.x + caveGradiant * c.x;
            float g3 = (1-caveGradiant) * fogColor.y + caveGradiant * c.y;
            float b3 = (1-caveGradiant) * fogColor.z + caveGradiant * c.z;
            float a3 = (1-caveGradiant) * fogColor.w + caveGradiant * c.w;

            float fogStart = args.get(4);
            float fogEnd = args.get(5);

            args.set(2, new Vector4f(r3, g3, b3, a3));
            args.set(3, FogShape.SPHERE);
            args.set(4, 3+(fogStart-3) *(1-palefog2)/(75*palefog2+1));
            args.set(5, 16 + (fogEnd - 16) * (1-palefog2)/(25*palefog2+1));

        }
    }


    @Unique
    private static Vector4f getFogColor(Camera camera, float tickDelta, ClientWorld world) {
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        Entity entity = camera.getFocusedEntity();
        float r;
        float s;
        float t;

        float u = 0.25F + 0.75F * (float) 12 / 32.0F;
        u = 1.0F - (float) Math.pow(u, 0.25);
        int v = -4605511;
        float w = ColorHelper.getRedFloat(v);
        float x = ColorHelper.getGreenFloat(v);
        float y = ColorHelper.getBlueFloat(v);
        float z = MathHelper.clamp(MathHelper.cos(0.82667f * (float) (Math.PI * 2)) * 2.0F + 0.5F, 0.0F, 1.0F);
        Vec3d vec3d = camera.getPos().subtract(2.0, 2.0, 2.0).multiply(0.25);
        Vec3d vec3d2 = CubicSampler.sampleColor(
                vec3d,
                /* method_62186 */ (ix, j, k) -> world.getDimensionEffects()
                        //.adjustFogColor(Vec3d.unpackRgb(((Biome) biomeAccess.getBiomeForNoiseGen(ix, j, k).value()).getFogColor()), z)
                        .adjustFogColor(Vec3d.unpackRgb(3421752), z)
        );
        r = (float) vec3d2.getX();
        s = (float) vec3d2.getY();
        t = (float) vec3d2.getZ();

        r += (w - r) * u;
        s += (x - s) * u;
        t += (y - t) * u;


        float ux = ((float) camera.getPos().y - (float) world.getBottomY()) * world.getLevelProperties().getHorizonShadingRatio();
        if (ux < 1.0F && cameraSubmersionType != CameraSubmersionType.LAVA && cameraSubmersionType != CameraSubmersionType.POWDER_SNOW) {
            if (ux < 0.0F) {
                ux = 0.0F;
            }

            ux *= ux;
            r *= ux;
            s *= ux;
            t *= ux;
        }

        float wx;
        label86:
        {
            if (entity instanceof LivingEntity livingEntity2
                && livingEntity2.hasStatusEffect(StatusEffects.NIGHT_VISION)
                && !livingEntity2.hasStatusEffect(StatusEffects.DARKNESS)) {
                wx = GameRenderer.getNightVisionStrength(livingEntity2, tickDelta);
                break label86;
            }

            wx = 0.0F;
        }

        if (r != 0.0F && s != 0.0F && t != 0.0F) {
            float xx = Math.min(1.0F / r, Math.min(1.0F / s, 1.0F / t));
            r = r * (1.0F - wx) + r * xx * wx;
            s = s * (1.0F - wx) + s * xx * wx;
            t = t * (1.0F - wx) + t * xx * wx;
        }

        return new Vector4f(r, s, t, 1.0F);
    }

}
