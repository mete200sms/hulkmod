package com.hulkmod.recipe;

import com.hulkmod.HulkMod;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class HulkRecipes {
    public static void register(GatherDataEvent event) {
        RecipeOutput output = event.getGenerator().getPackOutput();
        
        // Hulk Potion Crafting Recipe
        // Şekil:
        // G N G
        // N B N
        // G N G
        // G = Emerald (Yumurta/Yeşil güç)
        // N = Nether Star (Kozmik güç)
        // B = Beacon (Gamma ışını kaynağı)
        
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, HulkMod.HULK_POTION.get())
                .pattern("GNG")
                .pattern("NBN")
                .pattern("GNG")
                .define('G', Items.EMERALD)
                .define('N', Items.NETHER_STAR)
                .define('B', Items.BEACON)
                .unlockedBy("has_beacon", net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEACON))
                .save(output, "hulkmod:hulk_potion");
        
        // Alternatif kolay recepti (config ile değiştirilebilir)
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, HulkMod.HULK_POTION.get())
                .pattern("EDE")
                .pattern("DGD")
                .pattern("EDE")
                .define('E', Items.EMERALD_BLOCK)
                .define('D', Items.DIAMOND_BLOCK)
                .define('G', Items.GOLDEN_APPLE)
                .unlockedBy("has_golden_apple", net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(Items.GOLDEN_APPLE))
                .save(output, "hulkmod:hulk_potion_easy");
    }
}