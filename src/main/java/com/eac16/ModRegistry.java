package com.eac16;

import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.*;
import java.util.*;
import java.util.function.Supplier;

public final class ModRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Eac16Mod.ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Eac16Mod.ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Eac16Mod.ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, Eac16Mod.ID);

    public static final RegistryObject<Block> WINE_RACK = BLOCKS.register("wine_rack", () -> new WineRackBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0f).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistryObject<Block> WINE_BARREL = BLOCKS.register("wine_barrel", () -> new FermenterBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5f).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistryObject<Block> QVEVRI = BLOCKS.register("qvevri", () -> new FermenterBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(2.2f).sound(SoundType.DECORATED_POT).noOcclusion()));
    public static final RegistryObject<Block> PLACED_BOTTLE = BLOCKS.register("placed_wine_bottle", () -> new PlacedBottleBlock(BlockBehaviour.Properties.of().noOcclusion().noCollission().strength(0.3f).sound(SoundType.GLASS)));

    public static final RegistryObject<BlockEntityType<WineRackEntity>> RACK_ENTITY = BLOCK_ENTITIES.register("wine_rack", () -> BlockEntityType.Builder.of(WineRackEntity::new, WINE_RACK.get()).build(null));
    public static final RegistryObject<BlockEntityType<FermenterEntity>> FERMENTER_ENTITY = BLOCK_ENTITIES.register("fermenter", () -> BlockEntityType.Builder.of(FermenterEntity::new, WINE_BARREL.get(), QVEVRI.get()).build(null));
    public static final RegistryObject<BlockEntityType<PlacedBottleEntity>> BOTTLE_ENTITY = BLOCK_ENTITIES.register("placed_bottle", () -> BlockEntityType.Builder.of(PlacedBottleEntity::new, PLACED_BOTTLE.get()).build(null));

    public static final RegistryObject<Item> WINE_RACK_ITEM = block("wine_rack", WINE_RACK);
    public static final RegistryObject<Item> WINE_BARREL_ITEM = block("wine_barrel", WINE_BARREL);
    public static final RegistryObject<Item> QVEVRI_ITEM = block("qvevri", QVEVRI);
    public static final RegistryObject<Item> GRAPE_MUST = simple("grape_must");
    public static final RegistryObject<Item> IRON_SHOT = simple("iron_shot");

    public static final RegistryObject<Item> GEORGIAN_MAIL_HELMET = armor("georgian_mail_helmet", ArmorItem.Type.HELMET, false);
    public static final RegistryObject<Item> GEORGIAN_MAIL_CHEST = armor("georgian_mail_chest", ArmorItem.Type.CHESTPLATE, false);
    public static final RegistryObject<Item> GEORGIAN_MAIL_LEGGINGS = armor("georgian_mail_leggings", ArmorItem.Type.LEGGINGS, false);
    public static final RegistryObject<Item> GEORGIAN_MAIL_BOOTS = armor("georgian_mail_boots", ArmorItem.Type.BOOTS, false);
    public static final RegistryObject<Item> GEORGIAN_REINFORCED_CHEST = armor("georgian_reinforced_chest", ArmorItem.Type.CHESTPLATE, true);
    public static final RegistryObject<Item> KHELNAVI_ARMGUARD = simple("khelnavi_armguard");
    public static final RegistryObject<Item> GEORGIAN_ROUND_SHIELD = ITEMS.register("georgian_round_shield", () -> new ShieldItem(new Item.Properties().durability(330)));

    public static final RegistryObject<Item> HANDGONNE = gun("handgonne", 13f, 0.11f, 72, 40, 1.0f);
    public static final RegistryObject<Item> MATCHLOCK_ARQUEBUS = gun("matchlock_arquebus", 17f, 0.055f, 64, 50, 1.8f);
    public static final RegistryObject<Item> EARLY_MATCHLOCK_MUSKET = gun("early_matchlock_musket", 23f, 0.035f, 85, 65, 2.4f);

    public static final Map<String, RegistryObject<Item>> DRINKS = new LinkedHashMap<>();
    static {
        drink("kakhetian_red_wine", 150, 12);
        drink("kakhetian_amber_wine", 150, 12);
        drink("armenian_grape_wine", 150, 12);
        drink("moldavian_wine", 150, 11);
        drink("polish_mead", 200, 10);
        drink("lithuanian_mead", 200, 10);
        drink("ruthenian_beer", 300, 4);
        drink("polish_ale", 300, 5);
        drink("grain_aqua_vitae", 40, 38);
        // No anachronistic grape-spirit item in the default gameplay registry.
    }

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("main", () -> CreativeModeTab.builder()
      .title(Component.translatable("itemGroup.eac16"))
      .icon(() -> MATCHLOCK_ARQUEBUS.get().getDefaultInstance())
      .displayItems((params, output) -> {
          for (var entry : ITEMS.getEntries()) output.accept(entry.get());
      }).build());

    private static RegistryObject<Item> block(String id, RegistryObject<Block> b) {
        return ITEMS.register(id, () -> new BlockItem(b.get(), new Item.Properties()));
    }
    private static RegistryObject<Item> simple(String id) { return ITEMS.register(id, () -> new Item(new Item.Properties())); }
    private static RegistryObject<Item> armor(String id, ArmorItem.Type type, boolean reinforced) {
        return ITEMS.register(id, () -> new ArmorItem(reinforced ? GeorgianArmor.REINFORCED : GeorgianArmor.MAIL, type, new Item.Properties()));
    }
    private static RegistryObject<Item> gun(String id, float damage, float spread, int load, int cool, float durabilityCost) {
        return ITEMS.register(id, () -> new FirearmItem(new Item.Properties().stacksTo(1).durability(384), damage, spread, load, cool));
    }
    private static void drink(String id, int volume, int abv) {
        DRINKS.put(id, ITEMS.register(id, () -> new DrinkItem(new Item.Properties().stacksTo(16), volume, abv)));
    }
    private ModRegistry() {}
}
