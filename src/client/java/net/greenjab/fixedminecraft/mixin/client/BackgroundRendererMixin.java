package net.greenjab.fixedminecraft.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.greenjab.fixedminecraft.data.ModTags;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
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
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    @ModifyConstant(method = "applyFog", constant = @Constant(floatValue = 5.0f))
    private static float lessLavaFogFireRes(float constant) { return 9f;}

    @ModifyConstant(method = "applyFog", constant = @Constant(floatValue = 1.0f))
    private static float lessLavaFog(float constant, @Local Entity entity) {
        //nt i = EnchantmentHelper.getProtectionAmount(entity.getArmorItems(), (entity.getDamageSources().lava()));
        int i = 0;
        for (ItemStack item : ((PlayerEntity) entity).getArmorItems()) {
            i += FixedMinecraftEnchantmentHelper.enchantLevel(item, "fire protection");
        }
        return 2.5f + 0.25f*Math.min(2*i,25);
    }

    @ModifyConstant(method = "applyFog", constant = @Constant(floatValue = 4.0f))
    private static float moreSkyFog(float constant, @Local(ordinal = 0, argsOnly = true) float viewDistance) { return Math.min(64f, viewDistance/2);}

    @Inject(method = "getFogColor", at = @At("HEAD"))
    private static void i(Camera camera, float tickDelta, ClientWorld world, int clampedViewDistance, float skyDarkness,
                          CallbackInfoReturnable<Vector4f> cir) {
        //System.out.println("b: " + world.getSkyColor(camera.getPos(), tickDelta) + ", c: " + world.getSkyAngleRadians(tickDelta) + ", d: " + clampedViewDistance + ", e: " + world.getSkyAngle(tickDelta));
    }

    @Redirect(method = "applyFog", at = @At(value = "NEW", target = "(FFLnet/minecraft/client/render/FogShape;FFFF)Lnet/minecraft/client/render/Fog;"))
    private static Fog paleFog(float f, float g, FogShape fogShape, float r1, float g1, float b1, float k,
                               @Local(argsOnly = true)Camera camera, @Local(ordinal = 0, argsOnly = true) float viewDistance, @Local(argsOnly = true)BackgroundRenderer.FogType fogType) {
        if (fogType == BackgroundRenderer.FogType.FOG_TERRAIN) {
            float palefog = FixedMinecraftClient.INSTANCE.getPaleGardenFog();
            float palefog2 = palefog*palefog;
            World world = camera.getFocusedEntity().getWorld();
            int light = world.getLightLevel(LightType.SKY, camera.getBlockPos());
            float caveGradiant = Math.clamp(0.85f-light/7f,0.15f,1);

            Vector4f c = getFogColor(camera, camera.getLastTickDelta(), (ClientWorld) world);
            float r3 = (1-caveGradiant) * r1 + caveGradiant * c.x;
            float g3 = (1-caveGradiant) * g1 + caveGradiant * c.y;
            float b3 = (1-caveGradiant) * b1 + caveGradiant * c.z;
            float a3 = (1-caveGradiant) * k + caveGradiant * c.w;

            Fog fog = new Fog( 3+(f-3) *(1-palefog2)/(75*palefog2+1), 16 + (g - 16) * (1-palefog2)/(25*palefog2+1), FogShape.SPHERE, r3, g3, b3, a3);
            if (world.getBiome(camera.getBlockPos()).isIn(ModTags.INSTANCE.getIS_PALE_GARDEN()) ) {
                FixedMinecraftClient.INSTANCE.setPaleGardenFog(Math.min(palefog+0.003f, 1));
                return fog;
            } else {
                FixedMinecraftClient.INSTANCE.setPaleGardenFog(Math.max(palefog-0.005f, 0));

                if (palefog > 0) {
                    return fog;
                }
            }
        }

        return  new Fog(f, g, fogShape, r1, g1, b1, k);
    }

    @Unique
    private static Vector4f getFogColor(Camera camera, float tickDelta, ClientWorld world) {
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        Entity entity = camera.getFocusedEntity();
        float r;
        float s;
        float t;

        float u = 0.25F + 0.75F * (float) 12 / 32.0F;
        u = 1.0F - (float) Math.pow((double) u, 0.25);
        int v = -4605511;
        float w = ColorHelper.getRedFloat(v);
        float x = ColorHelper.getGreenFloat(v);
        float y = ColorHelper.getBlueFloat(v);
        float z = MathHelper.clamp(MathHelper.cos(0.82667f * (float) (Math.PI * 2)) * 2.0F + 0.5F, 0.0F, 1.0F);
        BiomeAccess biomeAccess = world.getBiomeAccess();
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
        float f = MathHelper.sin(5.194121f) > 0.0F ? -1.0F : 1.0F;
        Vector3f vector3f = new Vector3f(f, 0.0F, 0.0F);
        float h = camera.getHorizontalPlane().dot(vector3f);
        if (h < 0.0F) {
            h = 0.0F;
        }

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
