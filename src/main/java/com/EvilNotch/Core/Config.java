package com.EvilNotch.Core;

import java.io.File;
import java.util.ArrayList;

import com.EvilNotch.Core.Util.Java.JavaUtil;
import com.EvilNotch.Core.Util.Line.LineBase;
import com.EvilNotch.Core.Util.Line.LineItemStack;
import com.EvilNotch.Core.Util.Line.LineItemStackBase;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {
	public static byte mapScale = 3;
	public static boolean buckets_in_nether = false;
	public static boolean oldmushrooms = true;
	public static boolean oldsandshrooms = true;
	public static boolean other_foilage = true;
	public static int vinemax = 10;
	public static int maxTries = 100;
	public static boolean golden_carrot;
	public static boolean oldMap;
	public static boolean CustomSwamp;
	public static boolean betaSwamp;
	public static int swampColorbaseOne;
	public static int swampColorBaseTwo;
	public static int swampColorFoilage;
	public static int swampGrassColor;
	public static int swampWaterColor= 14745518;
	public static int oldItemLeatherColor;
	public static int itemLeatherPadOverlay;
	public static int itemLeatherColor;
	public static int armorLeatherColor;
	public static boolean regularSwamp;
	public static boolean oldMcResources;
	public static int armorOldLeatherColor;
	public static boolean releaseSwamp;
	public static boolean sheepDropWoolPunch;
	public static double[] oldLeather = {1.0,1.0,1.0};
	public static double[] newLeather = {1.0,1.0,1.0};
	public static boolean bonemeal;
	public static File configDir;
	public static File configServerDir;
	public static boolean disableFullScreen;
	public static boolean rp_Mc125;
	public static boolean rp_C418;
	public static File folder;
	public static File blockproperties;
	public static boolean fixHarvestMaterialChecker;
	public static boolean spawnerNEI_Egg;
	public static boolean spawnerNEI_Models;
	public static boolean spawnerSkeleHasBow;
	public static int spawnerRenderMount;
	public static int spawnerRenderItemMount;
	public static boolean spawnerHasMountRender;
	public static boolean spawnerHasMountItemRender;
	public static int mount_rotatedLimit;
	public static boolean alwaysStraightMounts;
	public static boolean disableStraightMounts;
	public static float mob_render_scale;
	public static boolean isMountRenderDynamic;
	public static float spawner_minRotation = -5.0F;
	public static float spawner_minRotationItem = -10.0F;
	public static float mob_render_scaleItem;
	public static int mount_rotatedItemLimit;
	public static boolean overrideAnvil;
	public static boolean overrideAvnilCombine;
	public static boolean overrideAvnilUnitRepair;
	public static boolean overrideAvnilCombineBook;
	public static boolean enchantAnyItem;
	public static boolean anvilAlwaysonelvl;
	public static boolean anvilCostPerLevel;
	public static boolean anvilVanillaLogic;
	public static boolean anvilFireandWaterEnchants;
	
	public static boolean alpha_bricks;
	public static boolean beta_gravel;
	public static boolean beta_bricks;
	public static boolean beta_mob_spawner;
	public static boolean beta_cobblestone;
	public static boolean beta_cobblestone_mossy;
	public static boolean beta_diamond_block;
	public static boolean beta_end_stone;
	public static boolean beta_endrame;
	public static boolean alpha_furnace_top;
	public static boolean beta_gold_block;
	public static boolean beta_iron_block;
	public static boolean beta_lapis_block;
	public static boolean old_lava;
	public static boolean old_water;
	public static boolean dyanmic_rp_changed = false;
	public static int maxTriesEntityAgeable;
	public static boolean Debug;
	public static boolean beta_hurt;
	public static ArrayList<LineBase> bonemeal_blacklist = new ArrayList();
	public static ArrayList<LineBase> silktouchFix_blacklist = new ArrayList();
	public static ArrayList<LineBase> bucket_fill_blacklist = new ArrayList();
	public static ArrayList<LineBase> bucket_empty_blacklist = new ArrayList();
	public static ArrayList<LineBase> ent_blacklistcfg = new ArrayList();
	
	public static String[] initBonemeal = {"\"minecraft:grass\""};
	public static String[] initBucketFill = {"\"silkspawners:blue_soul_bucket\""};
	public static String[] initBuckeEmpty = {"\"silkspawners:enchanted_soul_bucket\""};
	public static String[] initSilk = {"\"modid:block\"<0>"};
	public static String[] initent = {"\"BuildCraft|Robotics.bcRobot\""};
	public static boolean bucketSaveEnchantments;
	public static boolean bucketSaveNBT;
	public static boolean vanillaBucketTileUpdate;
	public static boolean vanillaBucketFillFix;
	public static boolean vanillaBucketEmptyFix;
	public static boolean bucketTileRead;
	public static boolean milk_bucket;
	public static boolean milkbucketSaveNBT;
	public static boolean silkTouchFix;
	public static int slimeInventorySize;
	public static boolean fixMaterialBreakSpeed;
	public static boolean spawnerDynamicScale;
	public static boolean spawnerDynamicItemScale;
	public static boolean NEI_WorldSpawner;
	public static boolean NEI_Descale;
	public static int spawnerClearMinuets;
	public static boolean spawnerNEI_EggItem;
	
	public static void loadConfig(FMLPreInitializationEvent e)
	{
		if(e.getSide() == Side.SERVER)
			configServerDir = e.getModConfigurationDirectory();
		configDir = e.getModConfigurationDirectory();
		 folder = new File(e.getModConfigurationDirectory(),"EvilNotchCore");
		if(!folder.exists())
			folder.mkdir();
		 blockproperties = new File(folder,"Block Properties");
		if(!blockproperties.exists())
			blockproperties.mkdir();
		File file = new File(folder,"EvilNotchCore.cfg");
		Configuration config = new Configuration(file);
		
		config.load();
		config.addCustomCategoryComment("blacklist", "These Blacklists Support LineItemStackBase Which means They Support Meta Data: Example \"modid:block\" <int>");
		bonemeal_blacklist = JavaUtil.staticToLineArray(config.get("blacklist", "Bonemeal Blacklist",initBonemeal).getStringList());
		bucket_fill_blacklist = JavaUtil.staticToLineArray(config.get("blacklist", "Bucket Fill Blacklist",initBucketFill).getStringList());
		bucket_empty_blacklist = JavaUtil.staticToLineArray(config.get("blacklist", "Bucket Empty Blacklist",initBuckeEmpty).getStringList());
		silktouchFix_blacklist = JavaUtil.staticToLineArray(config.get("blacklist", "SilkTouch Fix Blacklist",initSilk).getStringList());
		ent_blacklistcfg = JavaUtil.staticToLineArray(config.get("blacklist", "Entity Cache Blacklist",initent).getStringList());
		

		oldLeather = config.get("armor", "The Old Leather Color Multipliers (index[0*r,1*g,2*b])", oldLeather).getDoubleList();
		newLeather = config.get("armor", "The New Leather Color Multipliers (index[0*r,1*g,2*b])", oldLeather).getDoubleList();
		oldItemLeatherColor = config.get("armor", "Armor Item Old Leather Color", 12999733).getInt(12999733);
		itemLeatherPadOverlay = config.get("armor", "Armor Item New Leather Pad Overlay", 16777215).getInt(16777215);
		itemLeatherColor = config.get("armor", "Armor Item New Leather Color", 10511680).getInt(10511680);
		armorLeatherColor = config.get("armor", "Armor Model Leather Color", 10511680).getInt(10511680);
		armorOldLeatherColor = config.get("armor", "Armor Model Old Leather Color", 10511680).getInt(10511680);
		maxTriesEntityAgeable = config.get("events", "Entity Util Max Age To Cache Adults", 0).getInt(0);
		mapScale = (byte)config.get("events", "Map Scale 0-4 scale 3 is mc1.2.5", 4).getInt(4);
		swampGrassColor = config.get("biome", "SwampLand Base Color if it is not regular and is nothing else", 5011004).getInt(5011004);
		swampColorbaseOne = config.get("biome", "Swampland Grass Color Base one", 5011004).getInt(5011004);
		swampColorBaseTwo = config.get("biome", "Swampland Grass Color Base two", 6975545).getInt(6975545);
		swampColorFoilage = config.get("biome", "Swampland Foilage Color", 6975545).getInt(6975545);
		swampWaterColor = config.get("biome", "Swampland Water Multiplier", 14745518).getInt(14745518);
		maxTries = config.get("events", "BoneMeal Max Tries", 100).getInt(100);
		vinemax = config.get("events", "BomeMeal Vine Max Tries", 10).getInt(10);
		buckets_in_nether = config.get("events", "Water Buckets in the Nether", false).getBoolean(false);
		golden_carrot = config.get("events", "Golden Carrot Recipe Ingots", true).getBoolean(true);
		oldMap = config.get("events", "Map Old", true).getBoolean(true);
		oldmushrooms = config.get("events", "Mushroom Old Mechanics", true).getBoolean(true);
		oldsandshrooms = config.get("events", "Mushrooms On Sand", true).getBoolean(true);
		other_foilage = config.get("events", "BoneMeal Old Mechanics with Other Foilage", true).getBoolean(true);
		fixHarvestMaterialChecker = config.get("events", "Harvest Level Fix If Requires Tool", true).getBoolean(true);
		fixMaterialBreakSpeed = config.get("events", "Material BreakSpeed Fix", true).getBoolean(true);
		overrideAnvil = config.get("events", "Anvil Overrides", true).getBoolean(true);
		enchantAnyItem = config.get("events", "Anvil Enchant Any Item", true).getBoolean(true);
		Debug = config.get(config.CATEGORY_GENERAL, "Debug", false).getBoolean(false);
		CustomSwamp = config.get("biome", "SwampLand Custom Override With Mutation", true).getBoolean(true);
		betaSwamp = config.get("biome", "SwampLand Beta 1.8.1 Grass/Foilage", true).getBoolean(true);
		regularSwamp = config.get("biome", "Swampland Regular", true).getBoolean(true);
		releaseSwamp = config.get("biome", "Swampland Mc 1.2.5 Foilage/Grass", true).getBoolean(true);

				
		sheepDropWoolPunch = config.get("events", "Sheep Drop Wool Once Punched From Beta Days", false).getBoolean(false);
		bonemeal = config.get("events", "BoneMeal Old Mechanics", true).getBoolean(true);
		
		disableFullScreen = config.get("options", "Disable Full Screen On Startup", true).getBoolean(true);
		oldMcResources = config.get("options", "Dynamic Resouce Packs Enabled", true).getBoolean(true);
		rp_Mc125 = config.get("options", "Mc 1.2.5 Resouce Pack", true).getBoolean(true);
		rp_C418 = config.get("options", "C418 Missing/Unimplemented Music", true).getBoolean(true);
	
		alpha_bricks = config.get("resouce_pack_options", "alpha bricks", false).getBoolean(false);
		beta_hurt = config.get("resouce_pack_options", "beta hurtsound", false).getBoolean(false);
		beta_bricks = config.get("resouce_pack_options", "beta bricks", false).getBoolean(false);
		alpha_furnace_top = config.get("resouce_pack_options", "alpha furnace_top", false).getBoolean(false);
		beta_gravel = config.get("resouce_pack_options", "beta gravel", false).getBoolean(false);
		beta_mob_spawner = config.get("resouce_pack_options", "beta mob_spawner", false).getBoolean(false);
		beta_cobblestone = config.get("resouce_pack_options", "beta cobblestone", false).getBoolean(false);
		beta_cobblestone_mossy = config.get("resouce_pack_options", "beta cobblestone mossy", false).getBoolean(false);
		beta_diamond_block = config.get("resouce_pack_options", "beta diamond_block", false).getBoolean(false);
		beta_end_stone = config.get("resouce_pack_options", "beta endstone", false).getBoolean(false);
		beta_endrame = config.get("resouce_pack_options", "beta end_frame(doesn't work till 1.8+)", false).getBoolean(false);
		beta_gold_block = config.get("resouce_pack_options", "beta gold_block", false).getBoolean(false);
		beta_iron_block = config.get("resouce_pack_options", "beta iron_block", false).getBoolean(false);
		beta_lapis_block = config.get("resouce_pack_options", "beta lapis_block", false).getBoolean(false);
		old_lava = config.get("resouce_pack_options", "Old Lava(mc 1.2.5)", true).getBoolean(true);
		old_water = config.get("resouce_pack_options", "Old Water(mc 1.2.5)", true).getBoolean(true);
		
		spawnerNEI_Egg = config.get("spawner", "Spawner NEI Render Random Spawner", false).getBoolean(false);
		spawnerNEI_EggItem = config.get("spawner", "Spawner NEI Render Random Spawner Item", false).getBoolean(false);
		NEI_WorldSpawner = config.get("spawner", "Spawner NEI Render AnimateMobs", false).getBoolean(false);
		NEI_Descale = config.get("spawner", "Spawner NEI Render Descaling(if false go back to specific NEI version?)", true).getBoolean(true);
				
		spawnerNEI_Models = config.get("spawner", "Spawner NEI Items(NEI spawner Without NEI)", true).getBoolean(true);
		spawnerSkeleHasBow = config.get("spawner", "Spawner Skele/Wither Has Equipment", false).getBoolean(false);
		spawnerHasMountRender = config.get("spawner", "Spawner Render Mount", true).getBoolean(true);
		spawnerRenderMount = config.get("spawner", "Spawner Render Mount Entity Limit", 15).getInt(15);
		spawnerHasMountItemRender = config.get("spawner", "Spawner Render Mount Item", true).getBoolean(true);
		spawnerRenderItemMount = config.get("spawner", "Spawner Render Mount Item Entity Limit", 10).getInt(10);
		alwaysStraightMounts = config.get("spawner", "Spawner Render Mount Always Straight", false).getBoolean(false);
		disableStraightMounts = config.get("spawner", "Spawner Render Mount Disable Straight", false).getBoolean(false);
		mount_rotatedLimit = config.get("spawner", "Spawner Render Mount(Not For Dynamic) Roatation Limit", 5).getInt(5);
		mount_rotatedItemLimit = config.get("spawner", "Spawner Render Mount(Not For Dynamic) Roatation Item Limit", 5).getInt(5);
		mob_render_scale = (float) config.get("spawner", "Spawner Render Entity Scale", 0.4375D).getDouble(0.4375D);
		spawner_minRotation = (float) config.get("spawner", "Spawner Render Minimum Rotation(-30 is standard without mounts)", -5.0D).getDouble(-5.0D);
		
		spawner_minRotationItem = (float) config.get("spawner", "Spawner Render Item Minimum Rotation(-20 is standard without mounts)", -10.0D).getDouble(-10.0D);
		mob_render_scaleItem = (float) config.get("spawner", "Spawner Render Item Entity Scale", 0.4375D).getDouble(0.4375D);
		isMountRenderDynamic = config.get("spawner", "Spawner Render Mount is Dynamic", true).getBoolean(true);
		slimeInventorySize = config.get("spawner", "Spawner Render Item Slime Size", 2).getInt(2);
		spawnerDynamicScale = config.get("spawner", "Spawner Render Dynamic Scaling", false).getBoolean(false);
		spawnerDynamicItemScale = config.get("spawner", "Spawner Render Dynamic Item Scaling", false).getBoolean(false);
		spawnerClearMinuets = config.get("spawner", "Spawner Render Minuets To Clear the Cache", 3).getInt(3);
		
		overrideAvnilCombine = config.get("events", "Anvil Override Combine", true).getBoolean(true);
		overrideAvnilUnitRepair = config.get("events", "Anvil Override Unit Repair", true).getBoolean(true);
		overrideAvnilCombineBook = config.get("events", "Anvil Override Combine Book", true).getBoolean(true);
		anvilAlwaysonelvl = config.get("events", "Anvil Always One Level", false).getBoolean(false);
		anvilCostPerLevel = config.get("events", "Anvil Cost Per Enchantment Level", false).getBoolean(false);
		anvilVanillaLogic = config.get("events", "Anvil Enchantment Combining Vanilla Logic", false).getBoolean(false);
		anvilFireandWaterEnchants = config.get("events", "Anvil Enchantment Combining Fire and Water", false).getBoolean(false);
		
		bucketSaveEnchantments = config.get("events", "Bucket Save Enchantments", true).getBoolean(true);
		bucketSaveNBT = config.get("events", "Bucket Save NBT", false).getBoolean(false);
		vanillaBucketTileUpdate = config.get("events", "Bucket Tile Update Fixes", true).getBoolean(true);
		bucketTileRead = config.get("events", "Bucket Tile Read", true).getBoolean(true);
		vanillaBucketFillFix = config.get("events", "Bucket Fill Fixes", true).getBoolean(true);
		vanillaBucketEmptyFix = config.get("events", "Bucket Empty Fixes", true).getBoolean(true);
		milk_bucket = config.get("events", "Bucket Milk Fixes", true).getBoolean(true);
		milkbucketSaveNBT = config.get("events", "Bucket Milk Save NBT", true).getBoolean(true);
		silkTouchFix =  config.get("events", "Silk Touch Fix(Example Drops With TallGrass)", true).getBoolean(true);
		config.save();
	}

	
}
