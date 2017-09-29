package com.EvilNotch.Core.Load;

import java.lang.reflect.Field;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.MainCommonMod;
import com.EvilNotch.Core.Api.BlockApi;
import com.EvilNotch.Core.Api.FieldAcess;
import com.EvilNotch.Core.Api.MCPMappings;
import com.EvilNotch.Core.Biome.Swamp;
import com.EvilNotch.Core.Blocks.CustomAnvil;
import com.EvilNotch.Core.Blocks.BlockFakeMobSpanwer;
import com.EvilNotch.Core.Blocks.BlockOldMushroom;
import com.EvilNotch.Core.Events.AnvilEvent;
import com.EvilNotch.Core.Events.EventBlock;
import com.EvilNotch.Core.Events.EventEnhancedVanilla;
import com.EvilNotch.Core.Events.GuiEvent;
import com.EvilNotch.Core.Events.ItemToolTipEvent;
import com.EvilNotch.Core.Events.TickHandler;
import com.EvilNotch.Core.Items.EmptyMap;
import com.EvilNotch.Core.Items.ItemCustomAnvil;
import com.EvilNotch.Core.Items.ItemLeatherArmor;
import com.EvilNotch.Core.TileEntity.TileVMobSpawner;
import com.EvilNotch.Core.TileEntity.TileVFurnace;
import com.EvilNotch.Core.TileEntity.TileVMobSpawner;
import com.EvilNotch.Core.TileEntity.Render.TileVMobSpawnerRender;
import com.EvilNotch.Core.Util.Util.BlockUtil;
import com.EvilNotch.Core.Util.Util.ItemUtil;
import com.EvilNotch.Core.Util.Util.Registry;
import com.EvilNotch.Core.Util.Util.Util;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;

public class LoadRegister {
	
	public static Item helmet;
	public static Item chestplate;
	public static Item leggings;
	public static Item boots;
    public static Block brown_mushroom = new BlockOldMushroom().setBlockName("brown_mushroom").setStepSound(Block.soundTypeGrass);
    public static Block red_mushroom = new BlockOldMushroom().setBlockName("red_mushroom").setStepSound(Block.soundTypeGrass);
    public static Item oldMap;
	public static Block mob_spawner = new BlockFakeMobSpanwer().setBlockTextureName("minecraft:glass");
	public static Block black_anvil;
	public static CustomAnvil smooth_anvil;
	
	public static void load()
	{
		registerItems();
		registerBlocks();
		loadTileEntities();
		loadEvents();
		registerEntities();
	}
	/**
	 * No Entities to register yet
	 */
	public static void registerEntities() 
	{
		
	}

	public static void registerItems()
	{
		Items.command_block_minecart.setCreativeTab(CreativeTabs.tabRedstone);
		helmet = (new ItemLeatherArmor( 0, 0)).setUnlocalizedName("leather_helmet").setTextureName("leather_helmet");
        chestplate =  (new ItemLeatherArmor(0, 1)).setUnlocalizedName("leather_chestplate").setTextureName("leather_chestplate");
        leggings = (new ItemLeatherArmor(0, 2)).setUnlocalizedName("leather_leggings").setTextureName("leather_leggings");
    	boots =  (new ItemLeatherArmor(0, 3)).setUnlocalizedName("leather_boots").setTextureName("leather_boots");
    	oldMap = new EmptyMap().setUnlocalizedName("old_map").setTextureName("map_empty");
    	Items.leather_helmet.setCreativeTab(null);
    	Items.leather_chestplate.setCreativeTab(null);
    	Items.leather_leggings.setCreativeTab(null);
    	Items.leather_boots.setCreativeTab(null);
    	helmet.setCreativeTab(CreativeTabs.tabCombat);
    	chestplate.setCreativeTab(CreativeTabs.tabCombat);
    	leggings.setCreativeTab(CreativeTabs.tabCombat);
    	boots.setCreativeTab(CreativeTabs.tabCombat);

    	
    	Registry.RemoveCraftingRecipe(Items.leather_helmet);
    	Registry.RemoveCraftingRecipe(Items.leather_chestplate);
    	Registry.RemoveCraftingRecipe(Items.leather_leggings);
    	Registry.RemoveCraftingRecipe(Items.leather_boots);
    	
    	GameRegistry.registerItem(oldMap, oldMap.getUnlocalizedName().substring(5));
    	GameRegistry.registerItem(helmet, helmet.getUnlocalizedName().substring(5));
    	GameRegistry.registerItem(chestplate, chestplate.getUnlocalizedName().substring(5));
    	GameRegistry.registerItem(leggings, leggings.getUnlocalizedName().substring(5));
    	GameRegistry.registerItem(boots, boots.getUnlocalizedName().substring(5));
    	
    	GameRegistry.addRecipe(new ItemStack(helmet), new Object[]{"xxx","x x",'x',Items.leather});
    	GameRegistry.addRecipe(new ItemStack(chestplate), new Object[]{"x x","xxx","xxx",'x',Items.leather});
    	GameRegistry.addRecipe(new ItemStack(leggings), new Object[]{"xxx","x x","x x",'x',Items.leather});
    	GameRegistry.addRecipe(new ItemStack(boots), new Object[]{"x x","x x",'x',Items.leather});
    	
	}
	public static void registerBlocks()
	{
		Blocks.command_block.setCreativeTab(CreativeTabs.tabRedstone);
		Blocks.mob_spawner.setCreativeTab(CreativeTabs.tabRedstone);
		//Mushroom Stuff
    	brown_mushroom.setBlockTextureName("mushroom_brown");
    	red_mushroom.setBlockTextureName("mushroom_red");
    	brown_mushroom.setCreativeTab(null);
    	red_mushroom.setCreativeTab(null);
    	mob_spawner.setBlockName("glass_spawner").setCreativeTab(CreativeTabs.tabCombat);
    	black_anvil = new CustomAnvil(true);
    	smooth_anvil = new CustomAnvil(false);

    	//Registry
    	GameRegistry.registerBlock(smooth_anvil, ItemCustomAnvil.class,smooth_anvil.getUnlocalizedName().substring(5));
    	GameRegistry.registerBlock(black_anvil, ItemCustomAnvil.class,black_anvil.getUnlocalizedName().substring(5));
    	GameRegistry.registerBlock(mob_spawner, mob_spawner.getUnlocalizedName().substring(5));
    	GameRegistry.registerBlock(brown_mushroom, brown_mushroom.getUnlocalizedName().substring(5));
    	GameRegistry.registerBlock(red_mushroom, red_mushroom.getUnlocalizedName().substring(5));
	}
	public static void loadTileEntities()
	{
		Registry.RegisterandReplaceTileEntity(TileVFurnace.class, TileEntityFurnace.class, "Furnace"); //Replace Vanilla Tile Enity Furnace at it's index for int fuel time
    	//Registry.RegisterandReplaceTileEntity(TileVMobSpawner.class, TileEntityMobSpawner.class, "MobSpawner");//Patch for vanilla spawners as it's bs for not supporting dynamics multiple mobs/rendering stacks
		GameRegistry.registerTileEntity(TileVMobSpawner.class, "glassSpawner");
	}
	/**
	 * Client Only Events Are Registered in the Proxy To Prevent Server Crashings
	 */
	public static void loadEvents()
	{
		MinecraftForge.EVENT_BUS.register(new EventEnhancedVanilla());
		MinecraftForge.EVENT_BUS.register(new EventBlock());
		MinecraftForge.EVENT_BUS.register(new AnvilEvent());
		MinecraftForge.EVENT_BUS.register(new ItemToolTipEvent());
		FMLCommonHandler.instance().bus().register(new TickHandler());//All it does it update float variables to get ItemMobRender spinning
	}

	public static void loadpost() 
	{
		if(Config.golden_carrot)
		{
			Registry.RemoveCraftingRecipe(Items.golden_carrot);//Removes Nugget for golden carrot
			GameRegistry.addRecipe(new ItemStack(Items.golden_carrot), new Object[]{"xxx","xyx","xxx",'x',Items.gold_ingot,'y',Items.carrot});
		}
		if(Config.oldMap)
		{
			Registry.RemoveCraftingRecipe(Items.map);//Removes Old Map Recipe
			Items.map.setCreativeTab(null);//Removes old map from creative tab
			GameRegistry.addRecipe(new ItemStack(oldMap), new Object[]{"xxx","xyx","xxx",'x',Items.paper,'y',Items.compass});//Adds My map to the registry
		}
		if(Config.CustomSwamp)
    	{
    		BiomeGenBase.Height height = null;
    		try{
    			height = (BiomeGenBase.Height)ReflectionHelper.findField(BiomeGenBase.class, MCPMappings.getFeildName(MainCommonMod.isEclipse, "height_PartiallySubmerged")).get(null);
    			BiomeGenBase swamp = new Swamp(6).setColor(522674).setBiomeName("Swampland").func_76733_a(9154376).setHeight(height).setTemperatureRainfall(0.8F, 0.9F);
        		swamp.createMutation();
        		//ReflectionHelper.findField(BiomeGenBase.class, MCPMappings.getFeildName(MainCommonMod.isEclipse, "swampland")).set(null, swamp); //changes the swamp biome variable to mine
    		}
    			catch(Exception e){e.printStackTrace();}
    	}
		Registry.items = Util.toArray(Item.itemRegistry);
		Registry.blocks = Util.toArray(Block.blockRegistry);
		try{
			
		//Cache Material Classes
		for(Block b : Registry.blocks)
		{
			Material mat = b.getMaterial();
			if(!Registry.materialtoname.containsKey(mat) && mat != null)
			{
			try{
				String variablename = getFieldName(mat);
				if(variablename == null || mat.getClass().getTypeName() == null)
					continue;
				String classkey = mat.getClass().getTypeName() + "." + variablename;
				if(!Registry.materialmap.containsKey(classkey))
					Registry.materialmap.put(classkey, mat);
					
				if(!Registry.materialtoname.containsKey(mat))
					Registry.materialtoname.put(mat, classkey);
				}catch(Throwable t){t.printStackTrace();}
		   }
		}
		//cache buckets
		for(Item item : Registry.items)
		{
			if(item instanceof ItemBucket)
			{
				Block block = (Block) ReflectionHelper.findField(ItemBucket.class, MCPMappings.getFeildName(MainCommonMod.isEclipse, "isFull")).get(item);
				Registry.buckets.put(ItemUtil.getItemString(item),block);
			}
		}
		}catch(Exception e){e.printStackTrace();}
	}
	public static String getFieldName(Object mat)
	{
		Field[] fields = mat.getClass().getFields();
		for(Field f: fields)
		{
			try{
				Object obj = (Object)f.get(mat);
				if(obj == mat)
					return f.getName();
			}catch(Throwable t){t.printStackTrace();}
		}
		return null;
	}

}
