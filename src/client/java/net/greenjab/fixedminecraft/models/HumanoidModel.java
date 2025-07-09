package net.greenjab.fixedminecraft.models;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;

/** Credit: Viola-Siemens */
public interface HumanoidModel {
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

    <S extends EntityRenderState> void propertiesCopyFrom(EntityModel<S> model);
}
