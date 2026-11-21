package com.hulkmod.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.util.Lazy;

public class HulkCapability {
    public static final net.neoforged.neoforge.capabilities.Capability<HulkCapability> HULK_CAPABILITY = net.neoforged.neoforge.capabilities.Capability.get(new net.neoforged.neoforge.capabilities.CapabilityToken<>() {});

    private boolean transformed = false;
    private int transformationTimer = 0;
    private int maxTransformationTime = 6000;
    private int animationState = 0; // 0=idle, 1=transforming, 2=transformed, 3=reverting
    private int animationTick = 0;

    public boolean isTransformed() {
        return transformed;
    }

    public void setTransformed(boolean transformed) {
        this.transformed = transformed;
        if (transformed) {
            this.animationState = 2;
        } else {
            this.animationState = 3;
        }
        this.animationTick = 0;
    }

    public void startTransformation(int duration) {
        this.maxTransformationTime = duration;
        this.transformationTimer = duration;
        this.transformed = true;
        this.animationState = 1;
        this.animationTick = 0;
    }

    public void tick() {
        if (transformed) {
            if (transformationTimer > 0) {
                transformationTimer--;
            } else {
                transformed = false;
                animationState = 3;
                animationTick = 0;
            }
        }

        if (animationState == 1 || animationState == 3) {
            animationTick++;
            if (animationTick >= 40) { // 2 saniye animasyon
                animationState = transformed ? 2 : 0;
                animationTick = 0;
            }
        }
    }

    public int getTransformationTimer() {
        return transformationTimer;
    }

    public int getMaxTransformationTime() {
        return maxTransformationTime;
    }

    public float getTransformationProgress() {
        return maxTransformationTime > 0 ? 1.0f - (float) transformationTimer / maxTransformationTime : 0.0f;
    }

    public int getAnimationState() {
        return animationState;
    }

    public int getAnimationTick() {
        return animationTick;
    }

    public void resetTransformationTimer() {
        transformationTimer = 0;
        maxTransformationTime = 0;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("Transformed", transformed);
        tag.putInt("Timer", transformationTimer);
        tag.putInt("MaxTime", maxTransformationTime);
        tag.putInt("AnimState", animationState);
        tag.putInt("AnimTick", animationTick);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        this.transformed = tag.getBoolean("Transformed");
        this.transformationTimer = tag.getInt("Timer");
        this.maxTransformationTime = tag.getInt("MaxTime");
        this.animationState = tag.getInt("AnimState");
        this.animationTick = tag.getInt("AnimTick");
    }

    public static class Provider implements net.neoforged.neoforge.capabilities.CapabilityProvider<HulkCapability> {
        private final Lazy<HulkCapability> capability = Lazy.of(HulkCapability::new);

        @Override
        public <T> Lazy<? extends T> getCapability(net.neoforged.neoforge.capabilities.Capability<T> cap, Direction side) {
            return HULK_CAPABILITY.orEmpty(cap, capability);
        }
    }

    public static java.util.Optional<HulkCapability> getHulkCapability(Entity entity) {
        return entity.getCapability(HULK_CAPABILITY).resolve();
    }

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(Capabilities.Entity, HULK_CAPABILITY, (ent, ctx) -> new Provider());
    }
}