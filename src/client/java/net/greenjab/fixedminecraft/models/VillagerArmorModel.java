package net.greenjab.fixedminecraft.models;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.VillagerEntityRenderState;
import net.minecraft.util.math.MathHelper;

/** Credit: Viola-Siemens */
public class VillagerArmorModel<S extends VillagerEntityRenderState> extends EntityModel<S> implements HumanoidModel {
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

    public static TexturedModelData createBodyLayer(Dilation cubeDeformation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head",
                ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -9.65F, -4.0F, 8.0F, 8.0F, 8.0F, cubeDeformation),
                ModelTransform.NONE);
        modelPartData.addChild("body",
                ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, 1.0F, -2.0F, 8.0F, 12.0F, 4.0F, cubeDeformation.add(0.2F, 0.0F, 1.0F)),
                ModelTransform.NONE);
        modelPartData.addChild("arms",
                ModelPartBuilder.create()
                        .uv(40, 16).cuboid(-8.0F, -2.0F, -1.0F, 4.0F, 8.0F, 4.0F, cubeDeformation.add(-0.25F))
                        .uv(40, 16).mirrored().cuboid(4.0F, -2.0F, -1.0F, 4.0F, 8.0F, 4.0F, cubeDeformation.add(-0.25F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));
        modelPartData.addChild("right_leg",
                ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.add(0.19F, 0.26F, 0.26F)),
                ModelTransform.pivot(-2.0F, 12.0F, 0.0F));
        modelPartData.addChild("left_leg",
                ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.add(0.19F,0.26F, 0.26F)),
                ModelTransform.pivot(2.0F, 12.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 32);
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
        model.head.copyTransform(this.head);
        model.body.copyTransform(this.body);
        model.arms.copyTransform(this.arms);
        model.rightLeg.copyTransform(this.rightLeg);
        model.leftLeg.copyTransform(this.leftLeg);
    }

    @Override
    public <E extends EntityRenderState> void propertiesCopyFrom(EntityModel<E> model) {
        if(model instanceof VillagerResemblingModel villagerModel) {
            this.head.copyTransform(villagerModel.getHead());
            //System.out.println(this.head.originY);
            this.head.pivotY = 1.5f;
            this.body.copyTransform(villagerModel.getRootPart().getChild("body"));
            this.arms.copyTransform(villagerModel.getRootPart().getChild("arms"));
            this.rightLeg.copyTransform(villagerModel.getRootPart().getChild("right_leg"));
            this.leftLeg.copyTransform(villagerModel.getRootPart().getChild("left_leg"));
        }
    }

    @Override
    public void setAngles(S state) {
        super.setAngles(state);
        this.head.yaw = state.yawDegrees * (float) (Math.PI / 180.0);
        this.head.pitch = state.pitch * (float) (Math.PI / 180.0);
        if (state.shaking) {
            this.head.roll = 0.3F * MathHelper.sin(0.45F * state.age);
            this.head.pitch = 0.4F;
        } else {
            this.head.roll = 0.0F;
        }

        this.rightLeg.pitch = MathHelper.cos(state.limbFrequency * 0.6662F) * 1.4F * state.limbAmplitudeMultiplier * 0.5F;
        this.leftLeg.pitch = MathHelper.cos(state.limbFrequency * 0.6662F + (float) Math.PI) * 1.4F * state.limbAmplitudeMultiplier * 0.5F;
        this.rightLeg.yaw = 0.0F;
        this.leftLeg.yaw = 0.0F;
    }
}
