package com.hulkmod;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class HulkConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue TRANSFORMATION_DURATION;
    public static final ModConfigSpec.DoubleValue HULK_SCALE;
    public static final ModConfigSpec.DoubleValue ATTACK_DAMAGE_BONUS;
    public static final ModConfigSpec.DoubleValue MAX_HEALTH_BONUS;
    public static final ModConfigSpec.DoubleValue KNOCKBACK_RESISTANCE;
    public static final ModConfigSpec.DoubleValue MOVEMENT_SPEED_BONUS;
    public static final ModConfigSpec.DoubleValue MINING_SPEED_BONUS;
    public static final ModConfigSpec.BooleanValue ENABLE_PARTICLES;
    public static final ModConfigSpec.BooleanValue ENABLE_SOUNDS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("general");

        TRANSFORMATION_DURATION = builder
                .comment("Dönüşüm süresi (tick cinsinden, 20 tick = 1 saniye). Varsayılan: 6000 (5 dakika)")
                .defineInRange("transformationDuration", 6000, 100, 120000);

        HULK_SCALE = builder
                .comment("Hulk modelinin ölçeği. Varsayılan: 2.5")
                .defineInRange("hulkScale", 2.5, 1.0, 5.0);

        ATTACK_DAMAGE_BONUS = builder
                .comment("Ekstra saldırı hasarı. Varsayılan: 40.0 (20 kalp)")
                .defineInRange("attackDamageBonus", 40.0, 0.0, 200.0);

        MAX_HEALTH_BONUS = builder
                .comment("Ekstra maksimum sağlık. Varsayılan: 100.0 (50 kalp)")
                .defineInRange("maxHealthBonus", 100.0, 0.0, 500.0);

        KNOCKBACK_RESISTANCE = builder
                .comment("Geri itilme direnci. Varsayılan: 1.0 (tamamen direnc)")
                .defineInRange("knockbackResistance", 1.0, 0.0, 1.0);

        MOVEMENT_SPEED_BONUS = builder
                .comment("Hareket hızı bonusu. Varsayılan: 0.3")
                .defineInRange("movementSpeedBonus", 0.3, 0.0, 2.0);

        MINING_SPEED_BONUS = builder
                .comment("Kazma hızı bonusu (el ile blok kırma). Varsayılan: 10.0")
                .defineInRange("miningSpeedBonus", 10.0, 0.0, 50.0);

        ENABLE_PARTICLES = builder
                .comment("Parçacık efektlerini etkinleştir")
                .define("enableParticles", true);

        ENABLE_SOUNDS = builder
                .comment("Ses efektlerini etkinleştir")
                .define("enableSounds", true);

        builder.pop();

        SPEC = builder.build();
    }
}