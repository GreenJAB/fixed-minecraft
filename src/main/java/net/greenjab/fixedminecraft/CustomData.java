package net.greenjab.fixedminecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.text.Text;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public class CustomData {
    public static RegistryKey<Biome> biomeSearch = BiomeKeys.FOREST;
    public static void setData(LivingEntity entity, String name, int data) {
        if (entity.getEntityWorld().getScoreboard().getNullableObjective(name) == null) {
            entity.getEntityWorld().getScoreboard().addObjective(
                    name, ScoreboardCriterion.DUMMY, Text.of(name), ScoreboardCriterion.DUMMY.getDefaultRenderType(), false, null);
        }

        if(name == "airTime") {
            if (entity.getEntityWorld()
                    .getScoreboard()
                    .getObjectiveForSlot(ScoreboardDisplaySlot.TEAM_AQUA) == null) {
                entity.getEntityWorld()
                        .getScoreboard()
                        .setObjectiveSlot(ScoreboardDisplaySlot.TEAM_AQUA, entity.getEntityWorld().getScoreboard().getNullableObjective(name));
            } else if (entity.getEntityWorld()
                        .getScoreboard()
                        .getObjectiveForSlot(ScoreboardDisplaySlot.TEAM_AQUA)
                        .getDisplayName()
                        .getLiteralString() != name) {

                entity.getEntityWorld()
                        .getScoreboard()
                        .setObjectiveSlot(ScoreboardDisplaySlot.TEAM_AQUA, entity.getEntityWorld().getScoreboard().getNullableObjective(name));
            }
        }

        entity.getEntityWorld().getScoreboard().getOrCreateScore(entity, entity.getEntityWorld().getScoreboard().getNullableObjective(name))
                .setScore(data);
    }

    public static int getData(LivingEntity entity, String name)  {
        if (entity.getEntityWorld().getScoreboard().getNullableObjective(name) != null) {
            return entity.getEntityWorld().getScoreboard().getOrCreateScore(entity, entity.getEntityWorld().getScoreboard().getNullableObjective(name))
                    .getScore();
        }
        return 0;
    }
}
