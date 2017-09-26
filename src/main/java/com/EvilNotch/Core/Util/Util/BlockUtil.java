package com.EvilNotch.Core.Util.Util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.Api.MCPMappings;
import com.EvilNotch.Core.Api.ReflectionUtil;
import com.EvilNotch.Core.Load.LoadBaseConfig;
import com.EvilNotch.Core.Util.Java.JavaUtil;
import com.EvilNotch.Core.Util.Line.LineBase;
import com.EvilNotch.Core.Util.Line.LineDWNF;
import com.EvilNotch.Core.Util.Line.LineEnhanced;
import com.EvilNotch.Core.Util.Line.LineItemStack;
import com.EvilNotch.Core.Util.Line.LineItemStackBase;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.IFluidBlock;

public class BlockUtil {
	public static String[] materialnames = {"air","grass","ground","wood","rock","iron","anvil","water","lava","leaves","plants","vine","sponge","cloth","fire","sand","circuits","carpet","glass","redstoneLight","tnt","coral","ice","packedIce","snow","craftedSnow","cactus","clay","gourd","dragonEgg","portal","cake","web"};
	
	
	 public static Event fireBlockHarvesting(ArrayList<ItemStack> drops, World world, Block block, int x, int y, int z, int meta, int fortune, float dropChance, boolean silkTouch, EntityPlayer player)
	 {
	        BlockEvent.HarvestDropsEvent event = new BlockEvent.HarvestDropsEvent(x, y, z, world, block, meta, fortune, dropChance, drops, player, silkTouch);
	        MinecraftForge.EVENT_BUS.post(event);
	        return event;
	}
	
	  /**
     * Drops the block with forge event firing
     */
    public static void dropBlockAsItemWithChance(World w,Block b,EntityPlayer player, int x, int y, int z, int meta, int fortune, float chance,boolean silktouchevent)
    {
        if (!w.isRemote && !w.restoringBlockSnapshots) // do not drop items while restoring blockstates, prevents item dupe
        {
            ArrayList<ItemStack> items = b.getDrops(w, x, y, z, meta, fortune);
            if(silktouchevent)
            {
            	for(ItemStack stack : items)
            		if(stack.getItem() == Item.getItemFromBlock(b))
            			items.remove(stack);
            }
            BlockEvent.HarvestDropsEvent e = (HarvestDropsEvent) fireBlockHarvesting(items, w, b, x, y, z, meta, fortune, chance, false, player);
            
            chance = e.dropChance;
            items = e.drops;
            
            for (ItemStack item : items)
            {
                if (w.rand.nextFloat() <= chance)
                {
                	DropBlock(w, x, y, z, item);
                }
            }
        }
    }
	
	public static String getBlockString(Block b)
	{
		return GameData.getBlockRegistry().getNameForObject(b).toString();
	}
	
	//Drops Block Without NBT
	public static void DropBlock(Block b, World w, int x, int y, int z, int metadata) 
	{
		w.setBlockToAir(x, y, z);
		DropBlock(w, x, y, z, new ItemStack(b,1,metadata));
	}
		//Drops Block With NBT
	public static void DropBlock(Block b, World w, int x, int y, int z, int metadata, NBTTagCompound nbt) 
	{
		w.setBlockToAir(x, y, z);
		ItemStack stack = new ItemStack(b,1,metadata);
		stack.setTagCompound(nbt);
		DropBlock(w, x, y, z, stack);
	}
	
	public static void DropBlock(World world, int x, int y, int z, ItemStack stack)
    {
        if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops") && !world.restoringBlockSnapshots) // do not drop items while restoring blockstates, prevents item dupe
        {
            float f = 0.7F;
            double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(world, (double)x + d0, (double)y + d1, (double)z + d2, stack);
            entityitem.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entityitem);
        }
    }
	
	/**Gets Whitelisted Spawner from definespawners.txt
	 * returns true if is block
	 */
	public static boolean isBlock(Block b,int meta, List list)
	{
		return getBlockLine(b,list,true,meta) != null;
	}
	
	/**
	 * Gets harvest tool from block based on meta's best guess...
	 * @param blockh
	 * @return
	 */
	public static String getToolFromBlock(Block b,int meta)
	{
		String itool = b.getHarvestTool(meta);
		if(itool == null || itool.equals(""))
			return getToolFromHighestMeta(b);
		else
			return itool;
	}
	public static String getActualToolFromBlock(Block b,int meta)
	{
		return b.getHarvestTool(meta);
	}
	public static String getToolFromHighestMeta(Block blockh)
	{
		String itool = "pickaxe";
        for (int i=0;i<16;i++)
        {
        
              String toolit = blockh.getHarvestTool(i);
              if (toolit == null)
              {
                  toolit = "pickaxe";
              }
            if (!toolit.equals("pickaxe"))
            {
                 itool = toolit;
            }
        }
        return itool;
	}
	public static int getHarvestLevel(Block b)
	{
		int lvl = -1;
		for(int i=0;i<16;i++)
		{
			int harvest = b.getHarvestLevel(i);
			if(harvest > lvl)
				lvl = harvest;
		}
		return lvl;
	}
	public static boolean isHarvestLevel(Block b, ItemStack stack,int meta,boolean isHarvestEvent)
	{
		return isHarvestLevel(b,stack,true,meta,isHarvestEvent);
	}
	/**Sees if the harvest level is applicable to the block
	 * Returns true if player can harvest block
	 */
	public static boolean isHarvestLevel(Block b, ItemStack stack,boolean hasmeta,int meta,boolean isHarvestEvent)
	{
		int blockharvest = getHarvestLevel(b);
		if(hasmeta)
			blockharvest = b.getHarvestLevel(meta);//Since is meta specific use meta specific harvest level
		if(blockharvest < 0)
			blockharvest = 0;
		
		Iterator<LineBase> it = LoadBaseConfig.harvestLvl.iterator();
		LineItemStack line = (LineItemStack)getBlockLine(b,LoadBaseConfig.harvestLvl,hasmeta,meta);
		if(line == null && isHarvestEvent)
			return true;//If doesn't have it on the list do vanilla however if simply calling to see if harvest level and false do the rest of the method
		
		if(line != null)
			blockharvest = line.head;
		
		if(stack == null)
			return blockharvest == 0 && b.getMaterial().isToolNotRequired();//If no item is harvestable if and only if material has no tooland level == 0
		//System.out.println("matty:" + b.getHarvestTool(meta).equals(ItemUtil.getToolClass(stack)));
	//	System.out.println("BlockHarvest:" + blockharvest + " Level:" + stack.getItem().getHarvestLevel(stack, ItemUtil.getToolClass(stack)));
		if(stack.getItem().getHarvestLevel(stack, ItemUtil.getToolClass(stack)) >= blockharvest)
		{
			if(Config.fixHarvestMaterialChecker)
			{
				String tool = b.getHarvestTool(meta);
				if(tool == null)
					tool = ItemUtil.getToolClass(stack);
				if(!b.getMaterial().isToolNotRequired() && tool.equals(ItemUtil.getToolClass(stack)))
					return true;//Fix for vanilla if tool is required and is not the same tool based on meta set to air
				if(b.getMaterial().isToolNotRequired())
					return true;
			}
			else
				return true;
		}
		if(b.getMaterial().isToolNotRequired() && blockharvest == 0)
			return true;//Do default vanilla behavior if harvest lvl is 0 since dirt/grass is 0

		return false;
	}
	/**
	 * Gets the blockline from a ConfigBase List. 
	 * MetaData is the override else return regular block line
	 */
	public static LineItemStackBase getBlockLine(Block b, List<LineBase> list, boolean hasmeta,int meta) 
	{
		Iterator<LineBase> it = list.iterator();
		LineItemStack stack = null;
		while(it.hasNext())
		{
			LineBase base = it.next();
		   LineItemStack line = new LineItemStack(base.toString());//Creates new string so no exception occur
		   Block block = GameRegistry.findBlock(line.modid, line.name);
		   if(block != null && block == b)
		   {
			  if(!hasmeta && line.meta == -1 || hasmeta && line.meta == meta)
				  return line;
			   if(line.meta == -1)
				   stack = line;//If not used store as a possible if it doesn't contain metadata
		   }
		}
		return stack;
	}
	/**Gets Whitelisted Spawner from definespawners.txt
	 * returns true if is block
	 */
	public static boolean isBlock(Block b, List<LineBase> list,int meta)
	{
		return getBlockLine(b,list,true,meta) != null;
	}
	/**Gets Whitelisted Spawner from definespawners.txt
	 * returns true if is block
	 */
	@Deprecated
	public static boolean isBlock(Block b, List<LineBase> list)
	{
		return getBlockLine(b,list) != null;
	}
	/**Returns LineObj if block exists in list
	 * 
	 */
	@Deprecated
	public static LineBase getBlockLine(Block b, List<LineBase> list)
	{
		Iterator<LineBase> it = list.iterator();
		while(it.hasNext())
		{
			LineBase line = it.next();
			Block block = GameRegistry.findBlock(line.modid, line.name);
			if(block != null && block == b)
				return line;
		}
		return null;
	}
	/**
	 * Returns true if the object in the list has played with specified block meta data
	 * @param list
	 * @param b
	 * @param meta
	 * @return
	 */
	public static boolean isBlockHarvestModified(ArrayList<LineBase> list,Block b,int meta) 
	{
		for(LineBase line : list)
		{
			if(line instanceof LineDWNF)
			{	
				LineDWNF li = (LineDWNF)line;
				if(li.getLine(1) instanceof LineItemStack && li.getLine(0) instanceof LineBase)
				{
					LineItemStack stack = (LineItemStack)li.getLine(1);
					if(GameRegistry.findBlock(stack.modid, stack.name) == b && stack.meta == meta)
						return true;//Has been modified if block and meta are equal
				}
			}
			if(line instanceof LineItemStack)
			{
				LineItemStack stack = (LineItemStack)line;
				if(GameRegistry.findBlock(stack.modid, stack.name) == b && stack.meta == meta)
					return true;
			}
		}
		return false;
	}
	/**
	 * Detects if block is a fluid
	 * @param b
	 * @return
	 */
	public static boolean isFluid(Block b) 
	{
		return b instanceof BlockFluidBase || b instanceof BlockLiquid || b instanceof IFluidBlock;
	}
	/**
	 * Used for blocks like tall grass,double tall plants
	 */
	public static boolean isBlockShearable(Block b,int meta,ItemStack stack,World w, int x, int y, int z) 
	{
		if(b == null || !(b instanceof IShearable))
			return false;
		IShearable shear = (IShearable)b;
		if(!shear.isShearable(stack, w, x, y, z))
			return false;
		return true;
	}
	/**
	 * Detects if a block is a plant
	 * @param b
	 * @return
	 */
	public static boolean isBlockPlant(Block b) 
	{
		Material mat = b.getMaterial();
		return b instanceof BlockBush || mat == Material.grass || mat == Material.leaves || mat == Material.vine || mat == Material.plants;
	}
	/**
	 * Get material from shortened name or class
	 */
	public static Material getMaterialFromString(String s) 
	{
		Material mat = null;
		if(JavaUtil.ArrayhasEqualString(materialnames,s) )
		{
			if(s.equals("air"))
				return Material.air;
			if(s.equals("grass"))
				return Material.grass;
			if(s.equals("ground"))
				return Material.ground;
			
			if(s.equals("wood"))
				return Material.wood;
			if(s.equals("rock"))
				return Material.rock;
			if(s.equals("iron"))
				return Material.iron;
			if(s.equals("anvil"))
				return Material.anvil;
			if(s.equals("water"))
				return Material.water;
			if(s.equals("lava"))
				return Material.lava;
			if(s.equals("leaves"))
				return Material.leaves;
			
			if(s.equals("plants"))
				return Material.plants;
			if(s.equals("vine"))
				return Material.vine;
			if(s.equals("sponge"))
				return Material.sponge;
			if(s.equals("cloth"))
				return Material.cloth;
			if(s.equals("fire"))
				return Material.fire;
			if(s.equals("sand"))
				return Material.sand;
			if(s.equals("circuits"))
				return Material.circuits;
			if(s.equals("carpet"))
				return Material.carpet;
			if(s.equals("glass"))
				return Material.glass;
			if(s.equals("redstoneLight"))
				return Material.redstoneLight;
			if(s.equals("tnt"))
				return Material.tnt;
			if(s.equals("coral"))
				return Material.coral;
			
			if(s.equals("ice"))
				return Material.ice;
			if(s.equals("packedIce"))
				return Material.packedIce;
			if(s.equals("snow"))
				return Material.snow;
			if(s.equals("craftedSnow"))
				return Material.craftedSnow;
			if(s.equals("cactus"))
				return Material.cactus;
			if(s.equals("clay"))
				return Material.clay;
			if(s.equals("gourd"))
				return Material.gourd;
			if(s.equals("dragonEgg"))
				return Material.dragonEgg;
			if(s.equals("portal"))
				return Material.portal;
			if(s.equals("cake"))
				return Material.cake;
			if(s.equals("web"))
				return Material.web;
		}
		else{
			try{
				mat = Registry.materialmap.get(s);
			}catch(Exception e){e.printStackTrace();}
		}
		return mat;
	}

	public static boolean isImproperTool(Block b, ItemStack stack, int meta) 
	{
		Material mat = b.getMaterial();
		
		String tool = b.getHarvestTool(meta);
		if(tool == null && ItemUtil.getToolClass(stack) == null)
			return false;
		if(tool == null || ItemUtil.getToolClass(stack) == null)
			return true;
		
		if(!tool.equals(ItemUtil.getToolClass(stack)))
			return true;//Fix for vanilla if tool is required and is not the same tool based on meta set to air
		
		return false;
	}
	
	
	
	
	

}
