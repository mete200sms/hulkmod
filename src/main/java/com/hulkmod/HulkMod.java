package com.hulkmod;

import com.hulkmod.capability.HulkCapability;
import com.hulkmod.client.render.HulkRenderLayer;
import com.hulkmod.effect.HulkEffect;
import com.hulkmod.event.HulkEventHandler;
import com.hulkmod.item.HulkPotionItem;
import com.hulkmod.keybind.HulkKeybinds;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

@Mod(HulkMod.MOD_ID)
public class HulkMod {
    public static final String MOD_ID = "hulkmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredHolder<Item, HulkPotionItem> HULK_POTION = ITEMS.register("hulk_potion", () -> new HulkPotionItem(new Item.Properties().stacksTo(1).fireResistant()));
    public static final DeferredHolder<MobEffect, HulkEffect> HULK_EFFECT = MOB_EFFECTS.register("hulk_transformation", () -> new HulkEffect(MobEffectCategory.BENEFICIAL, 0x2E7D32));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> HULK_TAB = CREATIVE_TABS.register("hulk_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.hulkmod"))
            .icon(() -> new ItemStack(HULK_POTION.get()))
            .displayItems((params, output) -> {
                output.accept(HULK_POTION.get());
            })
            .build());

    public HulkMod(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        MOB_EFFECTS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::registerCapabilities);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new HulkEventHandler());

        modContainer.registerConfig(ModConfig.Type.COMMON, HulkConfig.SPEC);

        GeckoLib.initialize();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            HulkKeybinds.register();
        });
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            HulkRenderLayer.register();
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS || event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(HULK_POTION.get());
        }
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(Capabilities.Entity, HulkCapability.HULK_CAPABILITY, (entity, context) -> new HulkCapability.Provider());
    }

    @SubscribeEvent
    public void onEntityRenderers(EntityRenderersEvent.AddLayers event) {
        event.getSkinCache().ifPresent(skinCache -> {
            event.getEntityRenderers().forEach((type, renderer) -> {
                if (type == net.minecraft.world.entity.player.Player.class) {
                    renderer.addLayer(new HulkRenderLayer(renderer));
                }
            });
        });
    }
}