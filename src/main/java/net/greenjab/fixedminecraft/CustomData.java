package net.greenjab.fixedminecraft;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class CustomData {
    public static void setData(LivingEntity entity, String name, int data) {
        if (entity.level().getScoreboard().getObjective(name) == null) {
            entity.level().getScoreboard().addObjective(
                    name, ObjectiveCriteria.DUMMY, Component.nullToEmpty(name), ObjectiveCriteria.DUMMY.getDefaultRenderType(), false, null);
        }

        if(name.contains("airTime")) {
            if (entity.level()
                    .getScoreboard()
                    .getDisplayObjective(DisplaySlot.TEAM_AQUA) == null) {
                entity.level()
                        .getScoreboard()
                        .setDisplayObjective(DisplaySlot.TEAM_AQUA, entity.level().getScoreboard().getObjective(name));
            } else if (!entity.level()
                        .getScoreboard()
                        .getDisplayObjective(DisplaySlot.TEAM_AQUA)
                        .getDisplayName()
                        .tryCollapseToString().contains(name)) {

                entity.level()
                        .getScoreboard()
                        .setDisplayObjective(DisplaySlot.TEAM_AQUA, entity.level().getScoreboard().getObjective(name));
            }
        }

        entity.level().getScoreboard().getOrCreatePlayerScore(entity, entity.level().getScoreboard().getObjective(name))
                .set(data);
    }

    public static int getData(LivingEntity entity, String name)  {
        if (entity.level().getScoreboard().getObjective(name) != null) {
            return entity.level().getScoreboard().getOrCreatePlayerScore(entity, entity.level().getScoreboard().getObjective(name))
                    .get();
        }
        return 0;
    }
}
