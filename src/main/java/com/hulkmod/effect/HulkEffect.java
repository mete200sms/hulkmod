package com.hulkmod.effect;

import com.hulkmod.HulkConfig;
import com.hulkmod.capability.HulkCapability;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class HulkEffect extends MobEffect {
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c2d");
    private static final UUID MAX_HEALTH_UUID = UUID.fromString("8b9c0d1e-2f3a-4b5c-6d7e-8f9a0b1c2d3e");
    private static final UUID KNOCKBACK_RESISTANCE_UUID = UUID.fromString("9c0d1e2f-3a4b-5c6d-7e8f-9a0b1c2d3e4f");
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("0d1e2f3a-4b5c-6d7e-8f9a-0b1c2d3e4f5a");
    private static final UUID MINING_SPEED_UUID = UUID.fromString("1e2f3a4b-5c6d-7e8f-9a0b-1c2d3e4f5a6b");

    public HulkEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);

        if (entity instanceof Player player) {
            // Saldırı hasarı bonusu
            attributeMap.getInstance(Attributes.ATTACK_DAMAGE).ifPresent(attr -> {
                attr.addTransientModifier(new AttributeModifier(ATTACK_DAMAGE_UUID, "Hulk Attack Bonus", HulkConfig.ATTACK_DAMAGE_BONUS.get(), AttributeModifier.Operation.ADD_VALUE));
            });

            // Maksimum sağlık bonusu
            attributeMap.getInstance(Attributes.MAX_HEALTH).ifPresent(attr -> {
                attr.addTransientModifier(new AttributeModifier(MAX_HEALTH_UUID, "Hulk Health Bonus", HulkConfig.MAX_HEALTH_BONUS.get(), AttributeModifier.Operation.ADD_VALUE));
                // Hemen sağlığı doldur
                if (!entity.level().isClientSide()) {
                    player.setHealth(player.getMaxHealth());
                }
            });

            // Geri itilme direnci
            attributeMap.getInstance(Attributes.KNOCKBACK_RESISTANCE).ifPresent(attr -> {
                attr.addTransientModifier(new AttributeModifier(KNOCKBACK_RESISTANCE_UUID, "Hulk Knockback Resistance", HulkConfig.KNOCKBACK_RESISTANCE.get(), AttributeModifier.Operation.ADD_VALUE));
            });

            // Hareket hızı
            attributeMap.getInstance(Attributes.MOVEMENT_SPEED).ifPresent(attr -> {
                attr.addTransientModifier(new AttributeModifier(MOVEMENT_SPEED_UUID, "Hulk Speed Bonus", HulkConfig.MOVEMENT_SPEED_BONUS.get(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            });

            // Kazma hızı (el ile blok kırma için)
            attributeMap.getInstance(Attributes.MINING_EFFICIENCY).ifPresent(attr -> {
                attr.addTransientModifier(new AttributeModifier(MINING_SPEED_UUID, "Hulk Mining Bonus", HulkConfig.MINING_SPEED_BONUS.get(), AttributeModifier.Operation.ADD_VALUE));
            });

            // Capability'yi güncelle
            HulkCapability.getHulkCapability(player).ifPresent(cap -> {
                cap.setTransformed(true);
            });
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);

        if (entity instanceof Player player) {
            attributeMap.getInstance(Attributes.ATTACK_DAMAGE).ifPresent(attr -> attr.removeModifier(ATTACK_DAMAGE_UUID));
            attributeMap.getInstance(Attributes.MAX_HEALTH).ifPresent(attr -> attr.removeModifier(MAX_HEALTH_UUID));
            attributeMap.getInstance(Attributes.KNOCKBACK_RESISTANCE).ifPresent(attr -> attr.removeModifier(KNOCKBACK_RESISTANCE_UUID));
            attributeMap.getInstance(Attributes.MOVEMENT_SPEED).ifPresent(attr -> attr.removeModifier(MOVEMENT_SPEED_UUID));
            attributeMap.getInstance(Attributes.MINING_EFFICIENCY).ifPresent(attr -> attr.removeModifier(MINING_SPEED_UUID));

            HulkCapability.getHulkCapability(player).ifPresent(cap -> {
                cap.setTransformed(false);
                cap.resetTransformationTimer();
            });
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // Her tick'te efekt uygula (parçacıklar için)
    }

    @Override
    public Codec<Holder<MobEffect>> codec() {
        return super.codec();
    }
}