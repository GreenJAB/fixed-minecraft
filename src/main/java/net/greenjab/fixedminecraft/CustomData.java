package net.greenjab.fixedminecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.Text;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public class CustomData {
    public static RegistryKey<Biome> biomeSearch = BiomeKeys.FOREST;
    public static void setData(LivingEntity entity, String name, int data) {
        if (entity.getWorld().getScoreboard().getNullableObjective(name) == null) {
            entity.getWorld().getScoreboard().addObjective(
                    name, ScoreboardCriterion.DUMMY, Text.of(name), ScoreboardCriterion.DUMMY.getDefaultRenderType(), false, null);
        }

        if(name == "airTime") {
            if (entity.getWorld()
                    .getScoreboard()
                    .getObjectiveForSlot(ScoreboardDisplaySlot.TEAM_AQUA) == null) {
                entity.getWorld()
                        .getScoreboard()
                        .setObjectiveSlot(ScoreboardDisplaySlot.TEAM_AQUA, entity.getWorld().getScoreboard().getNullableObjective(name));
            } else if (entity.getWorld()
                        .getScoreboard()
                        .getObjectiveForSlot(ScoreboardDisplaySlot.TEAM_AQUA)
                        .getDisplayName()
                        .getLiteralString() != name) {

                entity.getWorld()
                        .getScoreboard()
                        .setObjectiveSlot(ScoreboardDisplaySlot.TEAM_AQUA, entity.getWorld().getScoreboard().getNullableObjective(name));
            }
        }

        entity.getWorld().getScoreboard().getOrCreateScore(entity, entity.getWorld().getScoreboard().getNullableObjective(name))
                .setScore(data);
    }

    public static int getData(LivingEntity entity, String name)  {
        if (entity.getWorld().getScoreboard().getNullableObjective(name) != null) {
            return entity.getWorld().getScoreboard().getOrCreateScore(entity, entity.getWorld().getScoreboard().getNullableObjective(name))
                    .getScore();
        }
        return 0;
    }
}
