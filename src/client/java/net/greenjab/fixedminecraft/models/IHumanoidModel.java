package net.greenjab.fixedminecraft.models;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public interface IHumanoidModel {
    void setHeadVisible(boolean visible);
    void setHatVisible(boolean visible);
    void setBodyVisible(boolean visible);
    void setArmsVisible(boolean visible);
    void setLegsVisible(boolean visible);

    default void setAllVisible(boolean visible) {
        this.setHeadVisible(visible);
        this.setHatVisible(visible);
        this.setBodyVisible(visible);
        this.setArmsVisible(visible);
        this.setLegsVisible(visible);
    }

    default <E extends Entity> void afterSetPartVisibility(EntityModel<E> model) {
    }

    <E extends Entity> void propertiesCopyFrom(EntityModel<E> model);

}
