package net.greenjab.fixedminecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.Text;

public class CustomData {
    public static void setData(LivingEntity entity, String name, int data) {
        if (entity.getWorld().getScoreboard().getNullableObjective(name) == null) entity.getWorld().getScoreboard().addObjective(
                name, ScoreboardCriterion.DUMMY, Text.of(name), ScoreboardCriterion.DUMMY.getDefaultRenderType(),false, null);
        entity.getWorld().getScoreboard().getOrCreateScore(entity, entity.getWorld().getScoreboard().getNullableObjective(name))
                .setScore(data);
    }

    public static int getData(LivingEntity entity, String name)  {
        if (entity.getWorld().getScoreboard().getNullableObjective(name) == null) entity.getWorld().getScoreboard().addObjective(
                name, ScoreboardCriterion.DUMMY, Text.of(name), ScoreboardCriterion.DUMMY.getDefaultRenderType(),false, null);
        return entity.getWorld().getScoreboard().getOrCreateScore(entity, entity.getWorld().getScoreboard().getNullableObjective(name))
                .getScore();
    }
}
