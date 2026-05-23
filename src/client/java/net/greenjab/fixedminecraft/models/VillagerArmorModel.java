package net.greenjab.fixedminecraft.models;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

/** Credit: Viola-Siemens */
public class VillagerArmorModel <S extends VillagerRenderState> extends EntityModel<S>  implements CustomHumanoidModel {
    protected final ModelPart root;
    protected final ModelPart head;
    protected final ModelPart body;
    protected final ModelPart arms;
    protected final ModelPart leftLeg;
    protected final ModelPart rightLeg;

    public VillagerArmorModel(ModelPart root) {
        super(root);
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.arms = root.getChild("arms");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer(CubeDeformation cubeDeformation) {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -9.65F, -4.0F, 8.0F, 8.0F, 8.0F, cubeDeformation),
                PartPose.ZERO);
        modelPartData.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 1.0F, -2.0F, 8.0F, 12.0F, 4.0F, cubeDeformation.extend(0.2F, 0.0F, 1.0F)),
                PartPose.ZERO);
        modelPartData.addOrReplaceChild("arms",
                CubeListBuilder.create()
                        .texOffs(40, 16).addBox(-8.0F, -2.0F, -1.0F, 4.0F, 8.0F, 4.0F, cubeDeformation.extend(-0.25F))
                        .texOffs(40, 16).mirror().addBox(4.0F, -2.0F, -1.0F, 4.0F, 8.0F, 4.0F, cubeDeformation.extend(-0.25F)),
                //ModelTransform.origin(0.0F, 2.0F, 0.0F));
                PartPose.rotation(-0.75f, 0, 0).translated(0.0F, 2.0F, 0.0F));
        modelPartData.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.extend(0.19F, 0.26F, 0.26F)),
                PartPose.offset(-2.0F, 12.0F, 0.0F));
        modelPartData.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.extend(0.19F,0.26F, 0.26F)),
                PartPose.offset(2.0F, 12.0F, 0.0F));
        return LayerDefinition.create(modelData, 64, 32);
    }

    public void setHeadVisible(boolean visible) {
        this.head.visible = visible;
    }

    public void setHatVisible(boolean visible) {
    }

    public void setBodyVisible(boolean visible) {
        this.body.visible = visible;
    }

    public void setArmsVisible(boolean visible) {
        this.arms.visible = visible;
    }

    public void setLegsVisible(boolean visible) {
        this.leftLeg.visible = this.rightLeg.visible = visible;
    }

    public void setAllVisible(boolean visible) {
        this.setHeadVisible(visible);
        this.setHatVisible(visible);
        this.setBodyVisible(visible);
        this.setArmsVisible(visible);
        this.setLegsVisible(visible);
    }

    @SuppressWarnings("unused")
    public void copyPropertiesTo(VillagerArmorModel<S> model) {
        model.head.loadPose(this.head.storePose());
        model.body.loadPose(this.body.storePose());
        model.arms.loadPose(this.arms.storePose());
        model.rightLeg.loadPose(this.rightLeg.storePose());
        model.leftLeg.loadPose(this.leftLeg.storePose());
    }

    @Override
    public <E extends EntityRenderState> void propertiesCopyFrom(EntityModel<E> model) {
        if(model instanceof VillagerModel villagerModel) {
            this.head.loadPose(villagerModel.getHead().storePose());
            //System.out.println(this.head.originY);
            this.head.y = 1.5f;
            this.body.loadPose(villagerModel.root().getChild("body").storePose());
            this.arms.loadPose(villagerModel.root().getChild("arms").storePose());
            this.rightLeg.loadPose(villagerModel.root().getChild("right_leg").storePose());
            this.leftLeg.loadPose(villagerModel.root().getChild("left_leg").storePose());
        }
    }

    @Override
    public void setupAnim(@NonNull S state) {
        super.setupAnim(state);
        this.head.yRot = state.yRot * (float) (Math.PI / 180.0);
        this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
        if (state.isFullyFrozen) {
            this.head.zRot = 0.3F * Mth.sin(0.45F * state.ageInTicks);
            this.head.xRot = 0.4F;
        } else {
            this.head.zRot = 0.0F;
        }

        this.rightLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F) * 1.4F * state.walkAnimationSpeed * 0.5F;
        this.leftLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F + (float) Math.PI) * 1.4F * state.walkAnimationSpeed * 0.5F;
        this.rightLeg.yRot = 0.0F;
        this.leftLeg.yRot = 0.0F;
    }
}
