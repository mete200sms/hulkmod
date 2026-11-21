package com.hulkmod.item;

import com.hulkmod.HulkMod;
import com.hulkmod.capability.HulkCapability;
import com.hulkmod.effect.HulkEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HulkPotionItem extends Item {
    public HulkPotionItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (!level.isClientSide()) {
                HulkCapability.getHulkCapability(player).ifPresent(cap -> {
                    if (!cap.isTransformed()) {
                        cap.startTransformation(6000); // 5 dakika (tick)
                        player.addEffect(new MobEffectInstance(HulkMod.HULK_EFFECT.get(), 6000, 0, false, true));
                        
                        // Ses ve parçacık efekti
                        if (HulkConfig.ENABLE_SOUNDS.get()) {
                            level.playSound(null, player.blockPosition(), net.minecraft.sound.SoundEvents.BEACON_POWER_SELECT, net.minecraft.sound.SoundSource.PLAYERS, 1.0f, 0.5f);
                        }
                    }
                });
            }
            
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 40; // 2 saniye içme animasyonu
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.hulkmod.hulk_potion.desc1").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("item.hulkmod.hulk_potion.desc2").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.hulkmod.hulk_potion.desc3").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.hulkmod.hulk_potion.desc4").withStyle(ChatFormatting.RED));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Enchanted glint efekti
    }
}