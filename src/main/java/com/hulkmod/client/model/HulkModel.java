package com.hulkmod.client.model;

import com.hulkmod.client.animation.HulkAnimations;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class HulkModel extends GeoModel<GeoAnimatable> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public HulkModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("head");
        this.body = this.root.getChild("body");
        this.leftArm = this.root.getChild("left_arm");
        this.rightArm = this.root.getChild("right_arm");
        this.leftLeg = this.root.getChild("left_leg");
        this.rightLeg = this.root.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        
        // Ana kök (kambur duruş için offset)
        PartDefinition mainRoot = root.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        // Kafa - biraz daha büyük ve öne eğik
        mainRoot.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.0F, -11.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.5F))
                .texOffs(32, 0).addBox(-5.0F, -11.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.8F)), // Kafa kası
            PartPose.offset(0.0F, -24.0F, 3.0F)); // Öne eğik

        // Gövde - geniş, kaslı, kambur
        mainRoot.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(16, 20).addBox(-7.0F, -12.0F, -5.0F, 14.0F, 16.0F, 10.0F, new CubeDeformation(0.6F))
                .texOffs(0, 40).addBox(-8.0F, -10.0F, -6.0F, 16.0F, 12.0F, 12.0F, new CubeDeformation(0.3F)), // Kas detayı
            PartPose.offset(0.0F, -24.0F, 2.0F));

        // Sol Kol - kalın, kaslı
        mainRoot.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(48, 20).addBox(-2.0F, -2.0F, -3.0F, 6.0F, 14.0F, 6.0F, new CubeDeformation(0.7F))
                .texOffs(64, 0).addBox(-3.0F, 0.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.4F)), // Omuz kası
            PartPose.offset(7.0F, -22.0F, 2.0F));

        // Sağ Kol - kalın, kaslı
        mainRoot.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(48, 40).addBox(-4.0F, -2.0F, -3.0F, 6.0F, 14.0F, 6.0F, new CubeDeformation(0.7F))
                .texOffs(64, 20).addBox(-5.0F, 0.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.4F)), // Omuz kası
            PartPose.offset(-7.0F, -22.0F, 2.0F));

        // Sol Bacak - güçlü, kalın
        mainRoot.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(0, 60).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F, new CubeDeformation(0.8F))
                .texOffs(24, 60).addBox(-4.0F, 10.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.5F)), // Bacak kası
            PartPose.offset(4.0F, -10.0F, 1.0F));

        // Sağ Bacak - güçlü, kalın
        mainRoot.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(0, 80).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F, new CubeDeformation(0.8F))
                .texOffs(24, 80).addBox(-4.0F, 10.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.5F)), // Bacak kası
            PartPose.offset(-4.0F, -10.0F, 1.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }

    @Override
    public void setCustomAnimations(GeoAnimatable animatable, long instanceId, net.minecraft.client.Minecraft minecraft) {
        // Animasyon verilerini al
        EntityModelData entityData = animatable.getGeoModelData().get(DataTickets.ENTITY_MODEL_DATA);
        
        if (entityData != null) {
            // Kafa hareketleri
            this.head.xRot = entityData.headPitch() * Mth.DEG_TO_RAD;
            this.head.yRot = entityData.netHeadYaw() * Mth.DEG_TO_RAD * 0.5F; // Kambur olduğu için daha az döner
            
            // Kambur duruş - kafa biraz aşağı
            this.head.xRot += 0.3F;
        }
    }
}