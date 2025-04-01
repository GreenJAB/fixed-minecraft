package net.greenjab.fixedminecraft.models;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.VillagerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

/** Credit: Viola-Siemens */
/** Not cleaning up until I can make it work */
//public class VillagerArmorModel<T extends VillagerEntity> extends CompositeEntityModel<T> implements ModelWithHat {
public class VillagerArmorModel extends BipedEntityModel<BipedEntityRenderState> implements ModelWithArms, ModelWithHead {// implements ModelWithHat {
    protected final ModelPart root;
    protected final ModelPart hat;
    protected final ModelPart head;
    protected final ModelPart body;
    protected final ModelPart left_arm;
    protected final ModelPart right_arm;
    protected final ModelPart leftLeg;
    protected final ModelPart rightLeg;

    public VillagerArmorModel(ModelPart root) {
        super(root);
        this.root = root;
        this.head = root.getChild("head");
        this.hat = this.head.getChild("hat");
        this.body = root.getChild("body");
        this.left_arm = root.getChild("left_arm");
        this.right_arm = root.getChild("right_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static TexturedModelData createBodyLayer(Dilation cubeDeformation, float y, float legsExtend) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("head",
                ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 8.0F, 8.0F, cubeDeformation),
                ModelTransform.origin(0.0F, 0.0F + y, 0.0F));
        modelPartData2.addChild(
                "hat", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, cubeDeformation.add(0.5F)), ModelTransform.NONE
        );
        modelPartData.addChild("body",
                ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, 1.0F, -2.0F, 8.0F, 12.0F, 4.0F, cubeDeformation.add(0.25F, 1.0F, 1.0F)),
                ModelTransform.origin(0.0F, 0.0F + y, 0.0F));
        modelPartData.addChild(
                "right_arm",
                ModelPartBuilder.create().
                        uv(40, 16).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation),
                ModelTransform.origin(-5.0F, 2.0F + y, 0.0F)
        );
        modelPartData.addChild(
                "left_arm",
                ModelPartBuilder.create().uv(40, 16).mirrored().cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation),
                ModelTransform.origin(5.0F, 2.0F + y, 0.0F)
        );
        modelPartData.addChild("right_leg",
                ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.add(legsExtend)),
                ModelTransform.origin(-2.0F, 12.0F + y, 0.0F));
        modelPartData.addChild("left_leg",
                ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.add(legsExtend)),
                ModelTransform.origin(2.0F, 12.0F + y, 0.0F));
        return TexturedModelData.of(modelData, 64, 32);
    }

    public void setHeadVisible(boolean visible) {
        this.head.visible = visible;
    }

    public void setHatVisible(boolean visible) {
    }

    /*@Override
    public void rotateArms(MatrixStack stack) {

    }*/

    public void setBodyVisible(boolean visible) {
        this.body.visible = visible;
    }

    public void setArmsVisible(boolean visible) {
        //this.arms.visible = visible;
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

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {

    }

    /*public <E extends Entity> void propertiesCopyFrom(EntityModel<E> model) {
        this.handSwingProgress = model.handSwingProgress;
        this.riding = model.riding;
        this.child = model.child;
        if(model instanceof VillagerResemblingModel<?> villagerModel) {
            this.head.copyTransform(villagerModel.getHead());
            this.body.copyTransform(villagerModel.getPart().getChild("body"));
            this.arms.copyTransform(villagerModel.getPart().getChild("arms"));
            this.rightLeg.copyTransform(villagerModel.getPart().getChild("right_leg"));
            this.leftLeg.copyTransform(villagerModel.getPart().getChild("left_leg"));
        }
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        boolean bl = false;
        if (entity != null) {
            bl = entity.getHeadRollingTimeLeft() > 0;
        }

        this.head.yaw = headYaw * 0.017453292F;
        this.head.pitch = headPitch * 0.017453292F;
        if (bl) {
            this.head.roll = 0.3F * MathHelper.sin(0.45F * animationProgress);
            this.head.pitch = 0.4F;
        } else {
            this.head.roll = 0.0F;
        }

        this.rightLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance * 0.5F;
        this.leftLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance * 0.5F;
        this.rightLeg.yaw = 0.0F;
        this.leftLeg.yaw = 0.0F;
    }

    @Override @NotNull
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.head, this.body, this.leftLeg, this.rightLeg, this.arms);
    }*/
}
