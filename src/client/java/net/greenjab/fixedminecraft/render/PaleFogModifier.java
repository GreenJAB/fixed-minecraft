package net.greenjab.fixedminecraft.render;

import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import java.awt.*;

public class PaleFogModifier extends FogModifier {

    @Override
    public int getFogColor(ClientWorld world, Camera camera, int viewDistance, float skyDarkness) {
        int light = world.getLightLevel(LightType.SKY, camera.getBlockPos());
        float l = Math.min(Math.max(0.3f+light/7f, 0.15f), 1);
        Vector3f c = ColorHelper.toVector(-4605511);
        Color fogColor = new Color(c.x * l, c.y* l, c.z* l, FixedMinecraftClient.paleGardenFog*FixedMinecraftClient.paleGardenFog);
        return fogColor.hashCode();
    }

    @Override
    public void applyStartEndModifier(FogData data, Entity cameraEntity, BlockPos cameraPos, ClientWorld world, float viewDistance,
                                      RenderTickCounter tickCounter) {
        float palefog = FixedMinecraftClient.paleGardenFog;
        float palefog2 = palefog*palefog;
        float fogStart = 1024;
        float fogEnd = 1024;
        data.environmentalStart = 3+(fogStart-3) *(1-palefog2)/(75*palefog2+1);
        data.environmentalEnd = 16 + (fogEnd - 16) * (1-palefog2)/(25*palefog2+1);
        data.skyEnd = data.environmentalEnd;
        data.cloudEnd = data.environmentalEnd;
    }

    @Override
    public boolean shouldApply(@Nullable CameraSubmersionType submersionType, Entity camera) {
        float palefog = FixedMinecraftClient.paleGardenFog;
        World world = camera.getWorld();
        boolean inPale = world.getBiome(camera.getBlockPos()).isIn(ModTags.IS_PALE_GARDEN);
        if (inPale) {
            float f = (float) (0.003f * Math.sqrt(1-palefog*palefog));
            FixedMinecraftClient.paleGardenFog = Math.min(palefog + f, 1);
        } else {
            FixedMinecraftClient.paleGardenFog = Math.max(palefog - 0.003f, 0);
        }
        return FixedMinecraftClient.paleGardenFog>0;
    }
}
