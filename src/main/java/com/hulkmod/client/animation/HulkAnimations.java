package com.hulkmod.client.animation;

import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.manager.AnimatableManager;

public class HulkAnimations {
    // Animasyon isimleri (geo dosyasındaki ile eşleşmeli)
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    public static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");
    public static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    public static final RawAnimation JUMP = RawAnimation.begin().thenPlay("jump");
    public static final RawAnimation TRANSFORM = RawAnimation.begin().thenPlay("transform");
    public static final RawAnimation REVERT = RawAnimation.begin().thenPlay("revert");
    public static final RawAnimation GROUND_SLAM = RawAnimation.begin().thenPlay("ground_slam");
    public static final RawAnimation ROAR = RawAnimation.begin().thenPlay("roar");
    public static final RawAnimation DASH = RawAnimation.begin().thenPlay("dash");

    public static <T extends GeoAnimatable> void registerAnimationControllers(AnimatableManager.ControllerRegistrar<T> registrar) {
        registrar.add(new AnimationController<>(registrar, "controller", 0, state -> {
            if (state.isMoving()) {
                if (state.getAnimatable().getGeoModelData().getData(software.bernie.geckolib.constant.DataTickets.ENTITY_MODEL_DATA) != null) {
                    var data = state.getAnimatable().getGeoModelData().getData(software.bernie.geckolib.constant.DataTickets.ENTITY_MODEL_DATA);
                    if (data != null && data.isSprinting()) {
                        return state.setAndContinue(RUN);
                    }
                }
                return state.setAndContinue(WALK);
            }
            
            // Özel animasyon durumları için capability kontrolü
            // Bu kısım render layer'da handle edilecek
            return state.setAndContinue(IDLE);
        }));
        
        registrar.add(new AnimationController<>(registrar, "action_controller", 0, state -> {
            // Action animasyonları (attack, jump, transform, etc.) buradan tetiklenir
            return state.setAndContinue(AnimationController.STOP);
        }));
    }
}