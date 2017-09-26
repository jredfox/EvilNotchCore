package com.EvilNotch.Core.Events;

import java.util.ArrayList;
import java.util.Iterator;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.MainCommonMod;
import com.EvilNotch.Core.Api.MCPMappings;
import com.EvilNotch.Core.Load.LoadBaseConfig;
import com.EvilNotch.Core.Util.Line.LineBase;
import com.EvilNotch.Core.Util.Line.LineEnhanced;
import com.EvilNotch.Core.Util.Line.LineItemStack;
import com.EvilNotch.Core.Util.Util.BlockUtil;
import com.EvilNotch.Core.Util.Util.EntityUtil;
import com.EvilNotch.Core.Util.Util.ItemUtil;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class EventBlock {
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void silkTouchVanillaFix(BlockEvent.BreakEvent e) 
	{
		if(e.world.isRemote || e.getPlayer() == null || e.getPlayer().getCurrentEquippedItem() == null || !EntityUtil.isSurvival(e.getPlayer()) || !Config.silkTouchFix)
			return;
		ItemStack stack = e.getPlayer().getCurrentEquippedItem();
		int meta = e.world.getBlockMetadata(e.x, e.y, e.z);
		if(BlockUtil.isBlock(e.block, Config.silktouchFix_blacklist, meta) || !BlockUtil.isBlockShearable(e.block,meta, stack, e.world, e.x,e.y,e.z) && !BlockUtil.isBlockPlant(e.block) || !BlockUtil.isHarvestLevel(e.block, stack, meta,false) || EnchantmentHelper.getEnchantmentLevel(33, stack) <= 0)
			return;
		Block b = e.block;
		if(b instanceof IShearable)
		{
			IShearable shear = (IShearable)b;
			ArrayList<ItemStack> stacks = shear.onSheared(stack, e.world, e.x, e.y, e.z, EnchantmentHelper.getEnchantmentLevel(35, stack));
			for(ItemStack s : stacks)
				BlockUtil.DropBlock(e.world, e.x, e.y, e.z, s);
			e.world.setBlockToAir(e.x,e.y,e.z);
			e.setCanceled(true);
			return;
		}
		e.world.setBlockToAir(e.x,e.y,e.z);
		BlockUtil.DropBlock(e.world, e.x, e.y, e.z, new ItemStack(e.block,1,meta));
		e.setCanceled(true);
	}
	
	//Event used for vanilla behavior by dropping block with silk touch
	//Use SilkSpawners for SpawnerBlocks/TileEntities Only use this for normal blocks with vanilla logic
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void canSilkHarvest(BlockEvent.BreakEvent e) 
	{
		if(e.world.isRemote || e.getPlayer() == null || e.getPlayer().getCurrentEquippedItem() == null || !EntityUtil.isSurvival(e.getPlayer()))
			return;
		ItemStack stack = e.getPlayer().getCurrentEquippedItem();
		int meta = e.world.getBlockMetadata(e.x, e.y, e.z);
		LineItemStack line = (LineItemStack)BlockUtil.getBlockLine(e.block, LoadBaseConfig.silk_tocuh,true,meta);
		if(line == null || !BlockUtil.isHarvestLevel(e.block, stack, meta,false) || EnchantmentHelper.getEnchantmentLevel(33, stack) <= 0)//|| 
			return;
		if(!line.bhead)
		{
			e.world.setBlockToAir(e.x,e.y,e.z);
			e.setCanceled(true);
			//Master drop block method compatible with other forge events
			BlockUtil.dropBlockAsItemWithChance(e.world,e.block,e.getPlayer(), e.x, e.y, e.z, meta,EnchantmentHelper.getEnchantmentLevel(35, stack),1.0F,true);
			return;
		}
		e.world.setBlockToAir(e.x,e.y,e.z);
		BlockUtil.DropBlock(e.world, e.x, e.y, e.z, new ItemStack(e.block,1,meta));
		e.setCanceled(true);
	}

	//Event for patching breakspead if vanilla decides to break it's own rules like pickaxe and stone
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void harvestLevelBreak(PlayerEvent.BreakSpeed e) 
	{
		if(!e.entityPlayer.worldObj.isRemote || e.entityPlayer == null || e.entityPlayer.getCurrentEquippedItem() == null || !EntityUtil.isSurvival(e.entityPlayer))
			return;//Doesn't return to fix vanilla if player has no tool but, can break block material shouldn't be able to drop it
		ItemStack stack = e.entityPlayer.getCurrentEquippedItem();
		if(!(stack.getItem() instanceof ItemTool))
			return;
		try{
			ItemTool tool = (ItemTool)stack.getItem();
			float k = tool.func_150893_a(stack, e.block);
			boolean harvestevent = !Config.fixMaterialBreakSpeed;//inverted so it doesn't return true if not on virtual harvest level stats
			
			if(!BlockUtil.isHarvestLevel(e.block, stack, e.metadata,harvestevent) || BlockUtil.isImproperTool(e.block,stack,e.metadata) && Config.fixMaterialBreakSpeed)
				e.newSpeed = 1.0F;
//			System.out.println(e.newSpeed);
			
		}catch(Exception ee){ee.printStackTrace();}
	}
	//Patch for harvest drops event setblock to air if can't harvest block
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void harvestLevel(BlockEvent.BreakEvent e) 
	{
		if(e.world.isRemote || e.getPlayer() == null || !EntityUtil.isSurvival(e.getPlayer()))
			return;//Doesn't return to fix vanilla if player has no tool but, can break block material shouldn't be able to drop it
		if(!BlockUtil.isHarvestLevel(e.block, e.getPlayer().getCurrentEquippedItem(),e.world.getBlockMetadata(e.x, e.y, e.z),true) )
		{
			e.world.setBlockToAir(e.x,e.y,e.z);
			e.setCanceled(true);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void quantityDropped(BlockEvent.HarvestDropsEvent e) 
	{
		if(e.world.isRemote || e.harvester == null || !EntityUtil.isSurvival(e.harvester))
			return;
		//Remove old drops here
		int quantity = -1;
		LineItemStack line = (LineItemStack)BlockUtil.getBlockLine(e.block, LoadBaseConfig.quantityDroped_list,true,e.blockMetadata);
		if(line == null)
			return;//If not defined return
		
		quantity = line.head;
		ArrayList<ItemStack> stackdrops = new ArrayList();
		for(int i=0;i<e.drops.size();i++)
		{
			Item item = e.drops.get(i).getItem();
			if(item != Item.getItemFromBlock(e.block))
				stackdrops.add(e.drops.get(i));//Remove all instances of this block
		}
		e.drops.clear();
		for(ItemStack stack : stackdrops)
			e.drops.add(stack);
		
		if(quantity != 0 && line.meta == -1)
			e.drops.add(new ItemStack(Item.getItemFromBlock(e.block),quantity,e.blockMetadata));
		if(quantity != 0 && line.meta != -1)
			e.drops.add(new ItemStack(Item.getItemFromBlock(e.block),quantity,line.meta));
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void disableDrops(BreakEvent e) 
	{
		if(e.world.isRemote || e.getPlayer() == null)
			return;
		if(!BlockUtil.isBlock(e.block, LoadBaseConfig.disableDrops_list,e.world.getBlockMetadata(e.x,e.y,e.z)))
			return;
		e.world.setBlockToAir(e.x, e.y, e.z);
		e.setCanceled(true);
	}

}
