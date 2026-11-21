package com.hulkmod.keybind;

import com.hulkmod.HulkMod;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = HulkMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class HulkKeybinds {
    public static final KeyMapping SUPER_JUMP = new KeyMapping(
            "key.hulkmod.super_jump",
            KeyConflictContext.IN_GAME,
            GLFW.GLFW_KEY_SPACE,
            "key.categories.hulkmod"
    );

    public static final KeyMapping GROUND_SLAM = new KeyMapping(
            "key.hulkmod.ground_slam",
            KeyConflictContext.IN_GAME,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            "key.categories.hulkmod"
    );

    public static final KeyMapping DASH = new KeyMapping(
            "key.hulkmod.dash",
            KeyConflictContext.IN_GAME,
            GLFW.GLFW_KEY_W,
            "key.categories.hulkmod"
    );

    public static final KeyMapping ROAR = new KeyMapping(
            "key.hulkmod.roar",
            KeyConflictContext.IN_GAME,
            GLFW.GLFW_KEY_R,
            "key.categories.hulkmod"
    );

    private static boolean wasSpacePressed = false;
    private static long lastSpacePress = 0;
    private static boolean wasWPressed = false;
    private static long lastWPress = 0;

    public static void register() {
        net.minecraft.client.KeyMapping.registerKeyMapping(SUPER_JUMP);
        net.minecraft.client.KeyMapping.registerKeyMapping(GROUND_SLAM);
        net.minecraft.client.KeyMapping.registerKeyMapping(DASH);
        net.minecraft.client.KeyMapping.registerKeyMapping(ROAR);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        // Double-tap Space for Super Jump
        boolean spacePressed = SUPER_JUMP.consumeClick();
        if (spacePressed) {
            long now = System.currentTimeMillis();
            if (wasSpacePressed && (now - lastSpacePress) < 300) {
                triggerSuperJump(mc.player);
            }
            wasSpacePressed = true;
            lastSpacePress = now;
        } else {
            wasSpacePressed = false;
        }

        // Double-tap W for Dash
        boolean wPressed = DASH.consumeClick();
        if (wPressed) {
            long now = System.currentTimeMillis();
            if (wasWPressed && (now - lastWPress) < 300) {
                triggerDash(mc.player);
            }
            wasWPressed = true;
            lastWPress = now;
        } else {
            wasWPressed = false;
        }

        // Ground Slam (Shift + Right Click)
        if (GROUND_SLAM.consumeClick()) {
            if (mc.options.keyUse.isDown()) {
                triggerGroundSlam(mc.player);
            }
        }

        // Roar
        if (ROAR.consumeClick()) {
            triggerRoar(mc.player);
        }
    }

    private static void triggerSuperJump(net.minecraft.world.entity.player.Player player) {
        if (!player.level().isClientSide()) return;
        
        com.hulkmod.capability.HulkCapability.getHulkCapability(player).ifPresent(cap -> {
            if (cap.isTransformed()) {
                player.setDeltaMovement(player.getDeltaMovement().x, 2.5, player.getDeltaMovement().z);
                player.hasImpulse = true;
                
                // Parçacık efekti
                if (com.hulkmod.HulkConfig.ENABLE_PARTICLES.get()) {
                    for (int i = 0; i < 20; i++) {
                        player.level().addParticle(
                            net.minecraft.core.particles.ParticleTypes.CLOUD,
                            player.getX(), player.getY(), player.getZ(),
                            (Math.random() - 0.5) * 0.5, 0.2, (Math.random() - 0.5) * 0.5
                        );
                    }
                }
                
                // Ses
                if (com.hulkmod.HulkConfig.ENABLE_SOUNDS.get()) {
                    player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), 
                        net.minecraft.sound.SoundEvents.ENTITY_IRON_GOLEM_STEP, net.minecraft.sound.SoundSource.PLAYERS, 1.0f, 0.5f, false);
                }
            }
        });
    }

    private static void triggerDash(net.minecraft.world.entity.player.Player player) {
        if (!player.level().isClientSide()) return;
        
        com.hulkmod.capability.HulkCapability.getHulkCapability(player).ifPresent(cap -> {
            if (cap.isTransformed()) {
                double yaw = Math.toRadians(player.getYRot());
                double pitch = Math.toRadians(player.getXRot());
                double dashStrength = 2.0;
                
                double dx = -Math.sin(yaw) * Math.cos(pitch) * dashStrength;
                double dy = -Math.sin(pitch) * dashStrength * 0.5;
                double dz = Math.cos(yaw) * Math.cos(pitch) * dashStrength;
                
                player.setDeltaMovement(player.getDeltaMovement().add(dx, dy, dz));
                player.hasImpulse = true;
                
                // Parçacık efekti
                if (com.hulkmod.HulkConfig.ENABLE_PARTICLES.get()) {
                    for (int i = 0; i < 30; i++) {
                        player.level().addParticle(
                            net.minecraft.core.particles.ParticleTypes.SMOKE,
                            player.getX(), player.getY() + 1, player.getZ(),
                            (Math.random() - 0.5) * 0.3, 0.1, (Math.random() - 0.5) * 0.3
                        );
                    }
                }
                
                // Ses
                if (com.hulkmod.HulkConfig.ENABLE_SOUNDS.get()) {
                    player.level().playLocalSound(player.getX(), player.getY(), player.getZ(),
                        net.minecraft.sound.SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST, net.minecraft.sound.SoundSource.PLAYERS, 1.0f, 0.7f, false);
                }
            }
        });
    }

    private static void triggerGroundSlam(net.minecraft.world.entity.player.Player player) {
        if (!player.level().isClientSide()) return;
        
        com.hulkmod.capability.HulkCapability.getHulkCapability(player).ifPresent(cap -> {
            if (cap.isTransformed() && player.onGround()) {
                // Alan hasarı
                player.level().getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,
                    player.getBoundingBox().inflate(5.0), e -> e != player && e.isAlive())
                    .forEach(target -> {
                        target.hurt(player.level().damageSources().playerAttack(player), 20.0f);
                        target.setDeltaMovement(target.getDeltaMovement().add(0, 1.5, 0));
                        target.hasImpulse = true;
                    });
                
                // Blok kırma efekti
                if (com.hulkmod.HulkConfig.ENABLE_PARTICLES.get()) {
                    for (int i = 0; i < 50; i++) {
                        double angle = Math.random() * Math.PI * 2;
                        double radius = Math.random() * 5;
                        double px = player.getX() + Math.cos(angle) * radius;
                        double pz = player.getZ() + Math.sin(angle) * radius;
                        player.level().addParticle(
                            net.minecraft.core.particles.ParticleTypes.EXPLOSION,
                            px, player.getY(), pz, 0, 0.1, 0
                        );
                    }
                    // Shockwave ring
                    for (int ring = 0; ring < 3; ring++) {
                        for (int i = 0; i < 36; i++) {
                            double angle = i * Math.PI * 2 / 36;
                            double radius = (ring + 1) * 1.5;
                            player.level().addParticle(
                                net.minecraft.core.particles.ParticleTypes.CRIT,
                                player.getX() + Math.cos(angle) * radius,
                                player.getY() + 0.1,
                                player.getZ() + Math.sin(angle) * radius,
                                0, 0.2, 0
                            );
                        }
                    }
                }
                
                // Ses
                if (com.hulkmod.HulkConfig.ENABLE_SOUNDS.get()) {
                    player.level().playLocalSound(player.getX(), player.getY(), player.getZ(),
                        net.minecraft.sound.SoundEvents.ENTITY_GENERIC_EXPLODE, net.minecraft.sound.SoundSource.PLAYERS, 2.0f, 0.5f, false);
                    player.level().playLocalSound(player.getX(), player.getY(), player.getZ(),
                        net.minecraft.sound.SoundEvents.ENTITY_IRON_GOLEM_STEP, net.minecraft.sound.SoundSource.PLAYERS, 1.5f, 0.3f, false);
                }
            }
        });
    }

    private static void triggerRoar(net.minecraft.world.entity.player.Player player) {
        if (!player.level().isClientSide()) return;
        
        com.hulkmod.capability.HulkCapability.getHulkCapability(player).ifPresent(cap -> {
            if (cap.isTransformed()) {
                // Yakındaki mobları korkut/it
                player.level().getEntitiesOfClass(net.minecraft.world.entity.Mob.class,
                    player.getBoundingBox().inflate(10.0), e -> e != player && e.isAlive())
                    .forEach(target -> {
                        target.setTarget(null); // Aggro reset
                        double dx = target.getX() - player.getX();
                        double dz = target.getZ() - player.getZ();
                        double dist = Math.sqrt(dx * dx + dz * dz);
                        if (dist > 0) {
                            target.setDeltaMovement(target.getDeltaMovement().add(dx / dist * 2, 0.5, dz / dist * 2));
                            target.hasImpulse = true;
                        }
                    });
                
                // Parçacık efekti
                if (com.hulkmod.HulkConfig.ENABLE_PARTICLES.get()) {
                    for (int i = 0; i < 40; i++) {
                        double angle = Math.random() * Math.PI * 2;
                        double radius = Math.random() * 3;
                        player.level().addParticle(
                            net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME,
                            player.getX() + Math.cos(angle) * radius,
                            player.getY() + 1.5 + Math.random() * 2,
                            player.getZ() + Math.sin(angle) * radius,
                            0, 0.1, 0
                        );
                    }
                }
                
                // Ses
                if (com.hulkmod.HulkConfig.ENABLE_SOUNDS.get()) {
                    player.level().playLocalSound(player.getX(), player.getY(), player.getZ(),
                        net.minecraft.sound.SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, net.minecraft.sound.SoundSource.PLAYERS, 2.0f, 0.4f, false);
                    player.level().playLocalSound(player.getX(), player.getY(), player.getZ(),
                        net.minecraft.sound.SoundEvents.ENTITY_RAVAGER_ROAR, net.minecraft.sound.SoundSource.PLAYERS, 1.5f, 0.6f, false);
                }
            }
        });
    }
}