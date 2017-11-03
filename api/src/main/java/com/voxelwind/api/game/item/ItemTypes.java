package com.voxelwind.api.game.item;

import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.data.Coal;
import com.voxelwind.api.game.item.data.Dyed;
import com.voxelwind.api.game.item.data.GenericDamageValue;
import com.voxelwind.api.game.level.block.BlockTypes;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Represents all items available on the server.
 */
public class ItemTypes
{
	private static TIntObjectMap<ItemType> BY_ID = new TIntObjectHashMap<> (206);

	public static final ItemType IRON_SHOVEL = new IntItem (256, "iron_shovel", 1, GenericDamageValue.class);
	public static final ItemType IRON_PICKAXE = new IntItem (257, "iron_pickaxe", 1, GenericDamageValue.class);
	public static final ItemType IRON_AXE = new IntItem (258, "iron_axe", 1, GenericDamageValue.class);
	public static final ItemType FLINT_AND_STEEL = new IntItem (259, "flint_and_steel", 1, GenericDamageValue.class);
	public static final ItemType APPLE = new IntItem (260, "apple", 64, null);
	public static final ItemType BOW = new IntItem (261, "bow", 1, GenericDamageValue.class);
	public static final ItemType ARROW = new IntItem (262, "arrow", 64, null);
	public static final ItemType COAL = new IntItem (263, "coal", 64, Coal.class);
	public static final ItemType DIAMOND = new IntItem (264, "diamond", 64, null);
	public static final ItemType IRON_INGOT = new IntItem (265, "iron_ingot", 64, null);
	public static final ItemType GOLD_INGOT = new IntItem (266, "gold_ingot", 64, null);
	public static final ItemType IRON_SWORD = new IntItem (267, "iron_sword", 1, null);
	public static final ItemType WOODEN_SWORD = new IntItem (268, "wooden_sword", 1, GenericDamageValue.class);
	public static final ItemType WOODEN_SHOVEL = new IntItem (269, "wooden_shovel", 1, GenericDamageValue.class);
	public static final ItemType WOODEN_PICKAXE = new IntItem (270, "wooden_pickaxe", 1, GenericDamageValue.class);
	public static final ItemType WOODEN_AXE = new IntItem (271, "wooden_axe", 1, GenericDamageValue.class);
	public static final ItemType STONE_SWORD = new IntItem (272, "stone_sword", 1, GenericDamageValue.class);
	public static final ItemType STONE_SHOVEL = new IntItem (273, "stone_shovel", 1, GenericDamageValue.class);
	public static final ItemType STONE_PICKAXE = new IntItem (274, "stone_pickaxe", 1, GenericDamageValue.class);
	public static final ItemType STONE_AXE = new IntItem (275, "stone_axe", 1, GenericDamageValue.class);
	public static final ItemType DIAMOND_SWORD = new IntItem (276, "diamond_sword", 1, GenericDamageValue.class);
	public static final ItemType DIAMOND_SHOVEL = new IntItem (277, "diamond_shovel", 1, GenericDamageValue.class);
	public static final ItemType DIAMOND_PICKAXE = new IntItem (278, "diamond_pickaxe", 1, GenericDamageValue.class);
	public static final ItemType DIAMOND_AXE = new IntItem (279, "diamond_axe", 1, GenericDamageValue.class);
	public static final ItemType STICK = new IntItem (280, "stick", 64, null);
	public static final ItemType BOWL = new IntItem (281, "bowl", 64, null);
	public static final ItemType MUSHROOM_STEW = new IntItem (282, "mushroom_stew", 1, null);
	public static final ItemType GOLDEN_SWORD = new IntItem (283, "golden_sword", 1, GenericDamageValue.class);
	public static final ItemType GOLDEN_SHOVEL = new IntItem (284, "golden_shovel", 1, GenericDamageValue.class);
	public static final ItemType GOLDEN_PICKAXE = new IntItem (285, "golden_pickaxe", 1, GenericDamageValue.class);
	public static final ItemType GOLDEN_AXE = new IntItem (286, "golden_axe", 1, GenericDamageValue.class);
	public static final ItemType STRING = new IntItem (287, "string", 64, null);
	public static final ItemType FEATHER = new IntItem (288, "feather", 64, null);
	public static final ItemType GUNPOWDER = new IntItem (289, "gunpowder", 64, null);
	public static final ItemType WOODEN_HOE = new IntItem (290, "wooden_hoe", 1, GenericDamageValue.class);
	public static final ItemType STONE_HOE = new IntItem (291, "stone_hoe", 1, GenericDamageValue.class);
	public static final ItemType IRON_HOE = new IntItem (292, "iron_hoe", 1, GenericDamageValue.class);
	public static final ItemType DIAMOND_HOE = new IntItem (293, "diamond_hoe", 1, GenericDamageValue.class);
	public static final ItemType GOLDEN_HOE = new IntItem (294, "golden_hoe", 1, GenericDamageValue.class);
	public static final ItemType SEEDS = new IntItem (295, "wheat_seeds", 64, null);
	public static final ItemType WHEAT = new IntItem (296, "wheat", 64, null);
	public static final ItemType BREAD = new IntItem (297, "bread", 64, null);
	public static final ItemType LEATHER_CAP = new IntItem (298, "leather_helmet", 1, GenericDamageValue.class);
	public static final ItemType LEATHER_TUNIC = new IntItem (299, "leather_chestplate", 1, GenericDamageValue.class);
	public static final ItemType LEATHER_PANTS = new IntItem (300, "leather_leggings", 1, GenericDamageValue.class);
	public static final ItemType LEATHER_BOOTS = new IntItem (301, "leather_boots", 1, GenericDamageValue.class);
	public static final ItemType CHAIN_HELMET = new IntItem (302, "chain_helmet", 1, GenericDamageValue.class);
	public static final ItemType CHAIN_CHESTPLATE = new IntItem (303, "chain_chestplate", 1, GenericDamageValue.class);
	public static final ItemType CHAIN_LEGGINGS = new IntItem (304, "chain_leggings", 1, GenericDamageValue.class);
	public static final ItemType CHAIN_BOOTS = new IntItem (305, "chain_boots", 1, GenericDamageValue.class);
	public static final ItemType IRON_HELMET = new IntItem (306, "iron_helmet", 1, GenericDamageValue.class);
	public static final ItemType IRON_CHESTPLATE = new IntItem (307, "iron_chestplate", 1, GenericDamageValue.class);
	public static final ItemType IRON_LEGGINGS = new IntItem (308, "iron_leggings", 1, GenericDamageValue.class);
	public static final ItemType IRON_BOOTS = new IntItem (309, "iron_boots", 1, GenericDamageValue.class);
	public static final ItemType DIAMOND_HELMET = new IntItem (310, "diamond_helmet", 1, GenericDamageValue.class);
	public static final ItemType DIAMOND_CHESTPLATE = new IntItem (311, "diamond_chestplate", 1, GenericDamageValue.class);
	public static final ItemType DIAMOND_LEGGINGS = new IntItem (312, "diamond_leggings", 1, GenericDamageValue.class);
	public static final ItemType DIAMOND_BOOTS = new IntItem (313, "diamond_boots", 1, GenericDamageValue.class);
	public static final ItemType GOLDEN_HELMET = new IntItem (314, "golden_helmet", 1, GenericDamageValue.class);
	public static final ItemType GOLDEN_CHESTPLATE = new IntItem (315, "golden_chestplate", 1, GenericDamageValue.class);
	public static final ItemType GOLDEN_LEGGINGS = new IntItem (316, "golden_leggings", 1, GenericDamageValue.class);
	public static final ItemType GOLDEN_BOOTS = new IntItem (317, "golden_boots", 1, GenericDamageValue.class);
	public static final ItemType FLINT = new IntItem (318, "flint", 64, null);
	public static final ItemType RAW_PORKCHOP = new IntItem (319, "porkchop", 64, null);
	public static final ItemType COOKED_PORKCHOP = new IntItem (320, "cooked_porkchop", 64, null);
	public static final ItemType PAINTING = new IntItem (321, "painting", 64, null);
	public static final ItemType GOLDEN_APPLE = new IntItem (322, "golden_apple", 64, null);
	public static final ItemType SIGN = new IntItem (323, "sign", 16, null);
	public static final ItemType WOODEN_DOOR = new IntItem (324, "wooden_door", 64, null);
	public static final ItemType BUCKET = new IntItem (325, "bucket", 16, null);
	public static final ItemType MINECART = new IntItem (328, "minecart", 1, null);
	public static final ItemType SADDLE = new IntItem (329, "saddle", 1, null);
	public static final ItemType IRON_DOOR = new IntItem (330, "iron_door", 64, null);
	public static final ItemType REDSTONE = new IntItem (331, "redstone", 64, null);
	public static final ItemType SNOWBALL = new IntItem (332, "snowball", 16, null);
	public static final ItemType BOAT = new IntItem (333, "boat", 1, null);
	public static final ItemType LEATHER = new IntItem (334, "leather", 64, null);
	public static final ItemType BRICK = new IntItem (336, "brick", 64, null);
	public static final ItemType CLAY = new IntItem (337, "clay_ball", 64, null);
	public static final ItemType SUGAR_CANE = new IntItem (338, "reeds", 64, null);
	public static final ItemType PAPER = new IntItem (339, "paper", 64, null);
	public static final ItemType BOOK = new IntItem (340, "book", 64, null);
	public static final ItemType SLIMEBALL = new IntItem (341, "slimeball", 64, null);
	public static final ItemType MINECART_WITH_CHEST = new IntItem (342, "chest_minecart", 1, null);
	public static final ItemType EGG = new IntItem (344, "egg", 16, null);
	public static final ItemType COMPASS = new IntItem (345, "compass", 64, null);
	public static final ItemType FISHING_ROD = new IntItem (346, "fishing_rod", 1, null);
	public static final ItemType CLOCK = new IntItem (347, "clock", 64, null);
	public static final ItemType GLOWSTONE_DUST = new IntItem (348, "glowstone_dust", 64, null);
	public static final ItemType RAW_FISH = new IntItem (349, "fish", 64, null);
	public static final ItemType COOKED_FISH = new IntItem (350, "cooked_fish", 64, null);
	public static final ItemType DYE = new IntItem (351, "dye", 64, Dyed.class);
	public static final ItemType BONE = new IntItem (352, "bone", 64, null);
	public static final ItemType SUGAR = new IntItem (353, "sugar", 64, null);
	public static final ItemType CAKE = new IntItem (354, "cake", 1, null);
	public static final ItemType BED = new IntItem (355, "bed", 1, null);
	public static final ItemType REDSTONE_REPEATER = new IntItem (356, "repeater", 64, null);
	public static final ItemType COOKIE = new IntItem (357, "cookie", 64, null);
	public static final ItemType FILLED_MAP = new IntItem (358, "map_filled", 64, null);
	public static final ItemType SHEARS = new IntItem (359, "shears", 1, null);
	public static final ItemType MELON = new IntItem (360, "melon", 64, null);
	public static final ItemType PUMPKIN_SEEDS = new IntItem (361, "pumpkin_seeds", 64, null);
	public static final ItemType MELON_SEEDS = new IntItem (362, "melon_seeds", 64, null);
	public static final ItemType RAW_BEEF = new IntItem (363, "beef", 64, null);
	public static final ItemType STEAK = new IntItem (364, "cooked_beef", 64, null);
	public static final ItemType RAW_CHICKEN = new IntItem (365, "chicken", 64, null);
	public static final ItemType COOKED_CHICKEN = new IntItem (366, "cooked_chicken", 64, null);
	public static final ItemType ROTTEN_FLESH = new IntItem (367, "rotten_flesh", 64, null);
	public static final ItemType ENDER_PEARL = new IntItem (367, "ender_pearl", 64, null);
	public static final ItemType BLAZE_ROD = new IntItem (369, "blaze_rod", 64, null);
	public static final ItemType GHAST_TEAR = new IntItem (370, "ghast_tear", 64, null);
	public static final ItemType GOLD_NUGGET = new IntItem (371, "gold_nugget", 64, null);
	public static final ItemType NETHER_WART = new IntItem (372, "nether_wart", 64, null);
	public static final ItemType POTION = new IntItem (373, "potion", 1, null);
	public static final ItemType GLASS_BOTTLE = new IntItem (374, "glass_bottle", 64, null);
	public static final ItemType SPIDER_EYE = new IntItem (375, "spider_eye", 64, null);
	public static final ItemType FERMENTED_SPIDER_EYE = new IntItem (376, "fermented_spider_eye", 64, null);
	public static final ItemType BLAZE_POWDER = new IntItem (377, "blaze_powder", 64, null);
	public static final ItemType MAGMA_CREAM = new IntItem (378, "magma_cream", 64, null);
	public static final ItemType BREWING_STAND = new IntItem (379, "brewing_stand", 64, null);
	public static final ItemType CAULDRON = new IntItem (380, "cauldron", 64, null);
	public static final ItemType EYE_OF_ENDER = new IntItem (381, "ender_eye", 64, null);
	public static final ItemType GLISTERING_MELON = new IntItem (382, "speckled_melon", 64, null);
	public static final ItemType SPAWN_EGG = new IntItem (383, "spawn_egg", 64, null);
	public static final ItemType BOTTLE_O_ENCHANTING = new IntItem (384, "experience_enchanting", 64, null);
	public static final ItemType FIRE_CHARGE = new IntItem (385, "fireball", 64, null);
	public static final ItemType BOOK_AND_QUILL = new IntItem (386, "writable_book", 1, null);
	public static final ItemType WRITTEN_BOOK = new IntItem (387, "written_book", 16, null);
	public static final ItemType EMERALD = new IntItem (388, "emerald", 64, null);
	public static final ItemType ITEM_FRAME = new IntItem (389, "frame", 64, null);
	public static final ItemType FLOWER_POT = new IntItem (390, "flower_pot", 64, null);
	public static final ItemType CARROT = new IntItem (391, "carrot", 64, null);
	public static final ItemType POTATO = new IntItem (392, "potato", 64, null);
	public static final ItemType BAKED_POTATO = new IntItem (393, "baked_potato", 64, null);
	public static final ItemType POISONOUS_POTATO = new IntItem (394, "poisonous_potato", 64, null);
	public static final ItemType MAP = new IntItem (395, "emptymap", 64, null);
	public static final ItemType GOLDEN_CARROT = new IntItem (396, "golden_carrot", 64, null);
	public static final ItemType MOB_HEAD = new IntItem (397, "skull", 64, null);
	public static final ItemType CARROT_ON_A_STICK = new IntItem (398, "carrotonastick", 1, null);
	public static final ItemType NETHER_STAR = new IntItem (399, "netherstar", 64, null);
	public static final ItemType PUMPKIN_PIE = new IntItem (400, "pumpkin_pie", 64, null);
	public static final ItemType FIREWORK_ROCKET = new IntItem (401, "fireworks", 64, null);
	public static final ItemType FIREWORK_STAR = new IntItem (402, "fireworkscharge", 64, null);
	public static final ItemType ENCHANTED_BOOK = new IntItem (403, "enchanted_book", 1, null);
	public static final ItemType COMPARATOR = new IntItem (404, "comparator", 64, null);
	public static final ItemType NETHER_BRICK = new IntItem (405, "netherbrick", 64, null);
	public static final ItemType NETHER_QUARTZ = new IntItem (406, "quartz", 64, null);
	public static final ItemType MINECART_WITH_TNT = new IntItem (407, "tnt_minecart", 1, null);
	public static final ItemType MINECART_WITH_HOPPER = new IntItem (408, "hopper_minecart", 1, null);
	public static final ItemType PRISMARINE_SHARD = new IntItem (409, "prismarine_shard", 64, null);
	public static final ItemType HOPPER = new IntItem (410, "hopper", 64, null);
	public static final ItemType RAW_RABBIT = new IntItem (411, "rabbit", 64, null);
	public static final ItemType COOKED_RABBIT = new IntItem (412, "cooked_rabbit", 64, null);
	public static final ItemType RABBIT_STEW = new IntItem (413, "rabbit_stew", 1, null);
	public static final ItemType RABBITS_FOOT = new IntItem (414, "rabbit_foot", 64, null);
	public static final ItemType RABBIT_HIDE = new IntItem (415, "rabbit_hide", 64, null);
	public static final ItemType LEATHER_HORSE_ARMOR = new IntItem (416, "horsearmorleather", 1, null);
	public static final ItemType IRON_HORSE_ARMOR = new IntItem (417, "horsearmoriron", 1, null);
	public static final ItemType GOLDEN_HORSE_ARMOR = new IntItem (418, "horsearmorgolden", 1, null);
	public static final ItemType DIAMOND_HORSE_ARMOR = new IntItem (419, "horsearmordiamond", 1, null);
	public static final ItemType LEAD = new IntItem (420, "lead", 64, null);
	public static final ItemType NAME_TAG = new IntItem (421, "nametag", 64, null);
	public static final ItemType PRISMARINE_CRYSTALS = new IntItem (422, "prismarine_crystals", 64, null);
	public static final ItemType MUTTON = new IntItem (423, "muttonraw", 64, null);
	public static final ItemType COOKED_MUTTON = new IntItem (424, "muttoncooked", 64, null);
	public static final ItemType ARMOR_STAND = new IntItem (425, "armor_stand", 64, null);
	public static final ItemType END_CRYSTAL = new IntItem (426, "end_crystal", 64, null);
	public static final ItemType SPRUCE_DOOR = new IntItem (427, "spruce_door", 64, null);
	public static final ItemType BIRCH_DOOR = new IntItem (428, "birch_door", 64, null);
	public static final ItemType JUNGLE_DOOR = new IntItem (429, "jungle_door", 64, null);
	public static final ItemType ACACIA_DOOR = new IntItem (430, "acacia_door", 64, null);
	public static final ItemType DARK_OAK_DOOR = new IntItem (431, "dark_oak_door", 64, null);
	public static final ItemType CHORUS_FRUIT = new IntItem (432, "chorus_fruit", 64, null);
	public static final ItemType POPPED_CHORUS_FRUIT = new IntItem (433, "chorus_fruit_popped", 64, null);
	public static final ItemType DRAGONS_BREATH = new IntItem (437, "dragon_breath", 64, null);
	public static final ItemType SPLASH_POTION = new IntItem (438, "splash_potion", 1, null);
	public static final ItemType LINGERING_POTION = new IntItem (441, "lingering_potion", 1, null);
	public static final ItemType MINECART_WITH_COMMAND_BLOCK = new IntItem (443, "command_block_minecart", 1, null);
	public static final ItemType ELYTRA = new IntItem (444, "elytra", 1, null);
	public static final ItemType SHULKER_SHELL = new IntItem (445, "shulker_shell", 64, null);
	public static final ItemType BANNER = new IntItem (446, "banner", 16, null);
	public static final ItemType TOTEM_OF_UNDYING = new IntItem (450, "totem", 1, null);
	public static final ItemType CHALKBOARD = new IntItem (454, "board", 16, null);
	public static final ItemType PORTFOLIO = new IntItem (456, "portfolio", 64, null);
	public static final ItemType IRON_NUGGET = new IntItem (457, "iron_nugget", 64, null);
	public static final ItemType BEETROOT = new IntItem (457, "beetroot", 64, null);
	public static final ItemType BEETROOT_SEEDS = new IntItem (458, "beetroot_seeds", 64, null);
	public static final ItemType BEETROOT_SOUP = new IntItem (459, "beetroot_soup", 1, null);
	public static final ItemType RAW_SALMON = new IntItem (460, "salmon", 64, null);
	public static final ItemType CLOWNFISH = new IntItem (461, "clownfish", 64, null);
	public static final ItemType PUFFERFISH = new IntItem (462, "pufferfish", 64, null);
	public static final ItemType COOKED_SALMON = new IntItem (463, "cooked_salmon", 64, null);
	public static final ItemType ENCHANTED_GOLDEN_APPLE = new IntItem (466, "appleenchanted", 64, null);
	public static final ItemType CAMERA = new IntItem (498, "camera", 64, null);
	public static final ItemType DISC_13 = new IntItem (500, "record_13", 1, null);
	public static final ItemType DISC_CAT = new IntItem (500, "record_cat", 1, null);
	public static final ItemType DISC_BLOCKS = new IntItem (500, "record_blocks", 1, null);
	public static final ItemType DISC_CHIRP = new IntItem (500, "record_chirp", 1, null);
	public static final ItemType DISC_FAR = new IntItem (500, "record_far", 1, null);
	public static final ItemType DISC_MALL = new IntItem (500, "record_mall", 1, null);
	public static final ItemType DISC_MELLOHI = new IntItem (500, "record_mellohi", 1, null);
	public static final ItemType DISC_STAL = new IntItem (500, "record_stal", 1, null);
	public static final ItemType DISC_STRAD = new IntItem (500, "record_strad", 1, null);
	public static final ItemType DISC_WARD = new IntItem (500, "record_ward", 1, null);
	public static final ItemType DISC_11 = new IntItem (500, "record_11", 1, null);
	public static final ItemType WAIT_DISC = new IntItem (500, "record_wait", 1, null);

	public static ItemType forId (int data)
	{
		return forId (data, false);
	}

	public static ItemType forId (int data, boolean itemsOnly)
	{
		ItemType type = BY_ID.get (data);
		if (type == null)
		{
			if (itemsOnly)
			{
				throw new IllegalArgumentException ("ID " + data + " is not valid.");
			} else
			{
				return BlockTypes.forId (data);
			}
		}
		return type;
	}

	private static class IntItem implements ItemType
	{
		private final int id;
		private final String name;
		private final int maxStackSize;
		private final Class<? extends Metadata> data;

		public IntItem (int id, String name, int maxStackSize, Class<? extends Metadata> data)
		{
			this.id = id;
			this.name = name.toUpperCase ();
			this.maxStackSize = maxStackSize;
			this.data = data;

			BY_ID.put (id, this);
		}

		@Override
		public int getId ()
		{
			return id;
		}

		@Override
		public String getName ()
		{
			return name;
		}

		@Override
		public boolean isBlock ()
		{
			return false;
		}

		@Override
		public Class<? extends Metadata> getMetadataClass ()
		{
			return data;
		}

		@Override
		public int getMaximumStackSize ()
		{
			return maxStackSize;
		}

		@Override
		public String toString ()
		{
			return getName ();
		}
	}
}
