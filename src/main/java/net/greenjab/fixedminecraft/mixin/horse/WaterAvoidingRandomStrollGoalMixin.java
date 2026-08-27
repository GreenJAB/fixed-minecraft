package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WaterAvoidingRandomStrollGoal.class)
public abstract class WaterAvoidingRandomStrollGoalMixin extends RandomStrollGoal {

    public WaterAvoidingRandomStrollGoalMixin(PathfinderMob mob, double speedModifier) {
        super(mob, speedModifier);
    }

    @Override
    public boolean canUse() {
        if (this.mob.entityTags().contains("locate") && this.mob.tickCount>20 * 60 * 5) return false;
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.mob.entityTags().contains("locate") && this.mob.tickCount>20 * 60 * 5) return false;
        return super.canContinueToUse();
    }
}
