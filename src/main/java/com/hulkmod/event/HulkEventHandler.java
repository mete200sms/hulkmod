package com.hulkmod.event;

import com.hulkmod.HulkConfig;
import com.hulkmod.capability.HulkCapability;
import com.hulkmod.effect.HulkEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = "hulkmod")
public class HulkEventHandler {

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide()) {
            HulkCapability.getHulkCapability(player).ifPresent(cap -> {
                if (cap.isTransformed()) {
                    // Hulk formundayken alınan hasarı %80 azalt
                    event.setAmount(event.getAmount() * 0.2f);
                    
                    // Hasar aldığında particik efekti
                    if (HulkConfig.ENABLE_PARTICLES.get()) {
                        spawnDamageParticles(player);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        // Hulk vurduğunda one-hit kill (mob'lar için)
        if (event.getSource().getEntity() instanceof Player player && !player.level().isClientSide()) {
            HulkCapability.getHulkCapability(player).ifPresent(cap -> {
                if (cap.isTransformed() && event.getEntity() instanceof LivingEntity target && target != player) {
                    // Normal mob'ları tek vuruşta öldür (boss'lar hariç)
                    if (!isBoss(target) && target.getMaxHealth() <= 200) {
                        event.setAmount(target.getHealth() + 100); // Garantili ölüm
                        
                        // Vuruş efekti
                        if (HulkConfig.ENABLE_PARTICLES.get()) {
                            spawnHitParticles(target);
                        }
                        if (HulkConfig.ENABLE_SOUNDS.get()) {
                            player.level().playSound(null, target.blockPosition(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.5f, 0.5f);
                        }
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null || player.level().isClientSide()) return;

        HulkCapability.getHulkCapability(player).ifPresent(cap -> {
            if (cap.isTransformed()) {
                // Hızlı blok kırma (el ile de kırılabilir)
                BlockState state = event.getState();
                BlockPos pos = event.getPos();
                Level level = player.level();
                
                // El ile kırma hızı artırıcı
                if (player.getMainHandItem().isEmpty() || player.getMainHandItem().is(Items.AIR)) {
                    event.setExpToDrop(event.getExpToDrop() * 2); // 2x XP
                }
                
                // Parçacık efekti
                if (HulkConfig.ENABLE_PARTICLES.get()) {
                    for (int i = 0; i < 10; i++) {
                        level.addParticle(ParticleTypes.BLOCK, 
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            (Math.random() - 0.5) * 0.5, 0.2, (Math.random() - 0.5) * 0.5);
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerTick(net.neoforged.neoforge.event.tick.PlayerTickEvent event) {
        Player player = event.getEntity();
        if (player == null || player.level().isClientSide()) return;

        HulkCapability.getHulkCapability(player).ifPresent(cap -> {
            cap.tick();
            
            // Dönüşüm süresi dolduğunda efekti kaldır
            if (cap.isTransformed() && cap.getTransformationTimer() <= 0) {
                player.removeEffect(HulkEffect.class);
            }
            
            // Dönüşüm sırasında sürekli parçacık (isteğe bağlı)
            if (cap.isTransformed() && HulkConfig.ENABLE_PARTICLES.get() && player.tickCount % 10 == 0) {
                spawnAuraParticles(player);
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        // Ölüm sonrası capability'yi kopyala
        HulkCapability.getHulkCapability(event.getOriginal()).ifPresent(oldCap -> {
            HulkCapability.getHulkCapability(event.getEntity()).ifPresent(newCap -> {
                newCap.deserializeNBT(oldCap.serializeNBT());
            });
        });
    }

    private static boolean isBoss(LivingEntity entity) {
        return entity.getType().is(net.minecraft.tags.EntityTypeTags.RAIDERS) || 
               entity.getMaxHealth() > 200 ||
               entity.getType().toString().contains("dragon") ||
               entity.getType().toString().contains("wither") ||
               entity.getType().toString().contains("warden");
    }

    private static void spawnDamageParticles(Player player) {
        Level level = player.level();
        for (int i = 0; i < 15; i++) {
            level.addParticle(ParticleTypes.DAMAGE_INDICATOR,
                player.getX() + (Math.random() - 0.5) * player.getBbWidth(),
                player.getY() + Math.random() * player.getBbHeight(),
                player.getZ() + (Math.random() - 0.5) * player.getBbWidth(),
                0, 0.1, 0);
        }
    }

    private static void spawnHitParticles(LivingEntity target) {
        Level level = target.level();
        for (int i = 0; i < 20; i++) {
            level.addParticle(ParticleTypes.CRIT,
                target.getX() + (Math.random() - 0.5) * target.getBbWidth(),
                target.getY() + Math.random() * target.getBbHeight(),
                target.getZ() + (Math.random() - 0.5) * target.getBbWidth(),
                (Math.random() - 0.5) * 0.3, 0.3, (Math.random() - 0.5) * 0.3);
        }
    }

    private static void spawnAuraParticles(Player player) {
        Level level = player.level();
        double radius = player.getBbWidth() * 1.5;
        for (int i = 0; i < 3; i++) {
            double angle = Math.random() * Math.PI * 2;
            double r = radius * Math.random();
            level.addParticle(ParticleTypes.GLOW,
                player.getX() + Math.cos(angle) * r,
                player.getY() + Math.random() * player.getBbHeight(),
                player.getZ() + Math.sin(angle) * r,
                0, 0.05, 0);
        }
    }
}