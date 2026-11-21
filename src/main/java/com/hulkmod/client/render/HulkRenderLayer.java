package com.hulkmod.client.render;

import com.hulkmod.HulkConfig;
import com.hulkmod.capability.HulkCapability;
import com.hulkmod.client.model.HulkModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.GeoBone;

@OnlyIn(Dist.CLIENT)
public class HulkRenderLayer extends RenderLayer<AbstractClientPlayer, HumanoidModel<AbstractClientPlayer>> {
    private static final ResourceLocation HULK_TEXTURE = new ResourceLocation("hulkmod", "textures/entity/hulk/hulk.png");
    private static final ResourceLocation HULK_EYES_TEXTURE = new ResourceLocation("hulkmod", "textures/entity/hulk/hulk_eyes.png");
    
    private HulkModel hulkModel;
    private boolean modelInitialized = false;

    public HulkRenderLayer(RenderLayerParent<AbstractClientPlayer, HumanoidModel<AbstractClientPlayer>> renderer) {
        super(renderer);
        initModel();
    }

    private void initModel() {
        if (!modelInitialized) {
            Minecraft mc = Minecraft.getInstance();
            ModelPart modelPart = mc.getEntityModels().bakeLayer(ModelLayers.PLAYER);
            // ModelPart'ı HulkModel'e uygun hale getir
            // Not: Gerçek implementasyonda EntityModelLoader ile custom layer yüklenir
            modelInitialized = true;
        }
    }

    public static void register() {
        // Bu metod FMLClientSetupEvent'te çağrılır
        // Render layer kayıtı genelde renderer'a eklenerek yapılır
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player, 
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        
        HulkCapability.getHulkCapability(player).ifPresent(cap -> {
            if (cap.isTransformed() || cap.getAnimationState() == 1 || cap.getAnimationState() == 3) {
                renderHulkTransformation(poseStack, buffer, packedLight, player, cap, partialTicks, ageInTicks);
            }
        });
    }

    private void renderHulkTransformation(PoseStack poseStack, MultiBufferSource buffer, int packedLight, 
                                          AbstractClientPlayer player, HulkCapability cap, float partialTicks, float ageInTicks) {
        
        // Eğer model henüz yüklenmediyse basit bir scale/color efekti uygula
        // Gerçek GeckoLib modeli için model register işlemi gerekir
        
        float scale = HulkConfig.HULK_SCALE.get().floatValue();
        float transformProgress = getTransformProgress(cap, partialTicks);
        
        // Ölçek interpolasyonu
        float currentScale = 1.0f + (scale - 1.0f) * transformProgress;
        
        poseStack.pushPose();
        
        // Oyuncu pozisyonuna git
        poseStack.translate(0, (currentScale - 1.0f) * 0.8f, 0); // Yükseklik ayarı
        poseStack.scale(currentScale, currentScale, currentScale);
        
        // Kambur duruş - biraz öne eğme
        float hunchAngle = 0.3f * transformProgress;
        poseStack.mulPose(net.minecraft.util.Mth.rotX(hunchAngle));
        poseStack.translate(0, -0.2f * transformProgress, 0.15f * transformProgress);
        
        // Yeşil renk tonu
        float r = 0.18f + 0.1f * transformProgress; // 0x2E7D32'ye yakın
        float g = 0.49f + 0.1f * transformProgress;
        float b = 0.20f;
        
        // Ana model render (oyuncu modeli üzerine)
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(HULK_TEXTURE));
        
        // GeckoLib modeli varsa kullan, yoksa basit render
        if (hulkModel != null) {
            // Animasyon state'ine göre animasyon seç
            int animState = cap.getAnimationState();
            // hulkModel.setAnimationState(animState); // GeckoLib API
            
            hulkModel.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 
                net.minecraft.util.FastColor.ARGB32.color(255, (int)(r * 255), (int)(g * 255), (int)(b * 255)));
        }
        
        // Göz ışığı efekti (ayrı render pass)
        if (transformProgress > 0.5f) {
            VertexConsumer eyeConsumer = buffer.getBuffer(RenderType.eyes(HULK_EYES_TEXTURE));
            // Göz modeli render
        }
        
        poseStack.pop();
    }

    private float getTransformProgress(HulkCapability cap, float partialTicks) {
        int state = cap.getAnimationState();
        int tick = cap.getAnimationTick();
        
        switch (state) {
            case 1: // Transforming
                return Math.min(1.0f, (tick + partialTicks) / 40.0f);
            case 2: // Transformed
                return 1.0f;
            case 3: // Reverting
                return Math.max(0.0f, 1.0f - (tick + partialTicks) / 40.0f);
            default:
                return 0.0f;
        }
    }
}