package com.EvilNotch.Core.Util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.EvilNotch.Core.MainCommonMod;
import com.EvilNotch.Core.Api.FieldAcess;
import com.EvilNotch.Core.Api.MCPMappings;

import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

public class Registry {
	
	public static HashMap<String,Class> nameToClassMap;
	public static HashMap<Class,String> classToNameMap;
	public static HashMap<Class<? extends TileEntity>,Class<? extends TileEntity> > oldToNewTileClasses = new HashMap();
	public static final String recipe_after = "after";
	public static final String recipe_before = "before"; //so people don't mistype it
	public static final String recipe_shapeless = "shapeless";
	public static final String recipe_shaped = "shaped";
	public static HashMap<String,Block> buckets = new HashMap();//For bucket events gets all blocks from them and puts it to use
	public static ArrayList<Item> items = new ArrayList();
	public static ArrayList<Block> blocks = new ArrayList();
	public static HashMap<String,Material> materialmap = new HashMap();
	public static HashMap<Material,String> materialtoname = new HashMap();
	
	public static void registerAndReplaceVanillaEntity(Class<? extends Entity> newEntityClass, int entityId)
    {
        String name = EntityList.getStringFromID(entityId);
        
        if ((name == null) || (EntityList.classToStringMapping.remove(name) == null) || (EntityList.stringToClassMapping.remove(name) == null) || (EntityList.IDtoClassMapping.remove(Integer.valueOf(entityId)) == null)) {
          throw new IllegalStateException("Error replacing entity with ID " + entityId + ", entity entry missing!");
        }
        EntityList.addMapping(newEntityClass, name, entityId);
    }

	/**
	 * Removes all instanceof recipe from output doesn't care what it is
	 * @param item
	 */
	public static void RemoveCraftingRecipe(Item item)
	{
		Iterator it = CraftingManager.getInstance().getRecipeList().iterator();
    	while(it.hasNext())
    	{
    		IRecipe recipe = (IRecipe)it.next();
    		if(recipe != null)
    		{
    		ItemStack stack = recipe.getRecipeOutput();
    		if(stack != null)
    			if(stack.getItem() == item)
    				it.remove();
    		}
    	}
	}
	/**
	 * Registers IRecipe Class in the proper format of both forge and recipe sorter so it doesn't crash
	 * When registering you can use multiple BUT DON'T USE THIS METHOD!
	 */
	public static void registerIRecipe(String modid_nameOfrecipe,Class<? extends IRecipe> c, Category cat,String before_after,String cats)
	{
		 try{
	      GameRegistry.addRecipe(c.newInstance());
	      RecipeSorter.register(modid_nameOfrecipe, c.getClass(), cat, before_after + ":" + "minecraft" + ":" + cats);
		 }catch(Exception e){e.printStackTrace();}
	}
	/**
	 * Like IRecipe registry but you build your string like "after:minecraft:shapeless"
	 * Also supports multiple like "after:minecraft:shaped before:minecraft:shapeless"
	 */
	public static void registerIRecipeCustom(String modid_nameOfrecipe,Class<? extends IRecipe> c, Category cat,String custom)
	{
		 //Generates IRecipes For Minecart Spawners
		 try{
	      GameRegistry.addRecipe(c.newInstance());
	      RecipeSorter.register(modid_nameOfrecipe, c.getClass(), cat, custom);
		 }catch(Exception e){e.printStackTrace();}
	}
	
	public static void registerFuelHandler(IFuelHandler handler)
    {
		GameRegistry.registerFuelHandler(handler);
    }
	
	 /**
     * Adds Tile Entities into the game with no id conflict checkers
     * For actually getting your tile entity override working a forge block place event is necessary
     */
    public static void RegisterandReplaceTileEntity(Class p_145826_0_, Class oldClass, String p_145826_1_)
    {
    	 nameToClassMap = null;
         classToNameMap = null;
    	try{
    		nameToClassMap = (HashMap) ReflectionHelper.findField(TileEntity.class, MCPMappings.getFeildName(MainCommonMod.isEclipse, "nameToClassMap")).get(null);
    	    classToNameMap = (HashMap) ReflectionHelper.findField(TileEntity.class, MCPMappings.getFeildName(MainCommonMod.isEclipse, "classToNameMap")).get(null);
    	}
    	catch(Exception e){e.printStackTrace();}
    	
    	nameToClassMap.remove(p_145826_1_);
    	classToNameMap.remove(oldClass);
    	
        nameToClassMap.put(p_145826_1_, p_145826_0_);
        classToNameMap.put(p_145826_0_, p_145826_1_);
        oldToNewTileClasses.put(oldClass,p_145826_0_);
    }
    /**
     * Basic Registration no checks
     * @param clazz
     * @param str
     */
	public static void registerEntity(Class<? extends Entity> clazz, String str) 
	{
		FieldAcess.entity_classToStringMapping.put(clazz, str);
		FieldAcess.entity_stringToClassMapping.put(str, clazz);
	}

}
