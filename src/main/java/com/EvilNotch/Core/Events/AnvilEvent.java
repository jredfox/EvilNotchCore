package com.EvilNotch.Core.Events;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.Util.AnvilEventObj;
import com.EvilNotch.Core.Util.Util.NBTUtil;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.event.AnvilUpdateEvent;

public class AnvilEvent {
	
	/**
	 * Supports Combining Items,Unit Repair,Combining with Book
	 */
	@SubscribeEvent
	public void AnvilForgeEgg(AnvilUpdateEvent e)
	{
		if(!Config.overrideAnvil || e.left.stackSize > 1)
			return;//Return if not config and first statement so it's optimized
		
		boolean combine = NBTUtil.canAnvilCombine(e.left,e.right);
		boolean unit = NBTUtil.isAnvilUnitRepair(e.left,e.right);
		boolean combine_book = NBTUtil.canAnvilCombineBook(e.left,e.right);
		
		if(!combine && !unit && !combine_book)
			return;//Return if ItemStacks are not applicable
		
		e.output = ItemStack.copyItemStack(e.left);
		AnvilEventObj anvil = null;
		if(combine)
			anvil =  NBTUtil.combineRepairTool(e.left,e.right);
		if(unit)
			anvil = NBTUtil.unitRepairTool(e.left, e.right);
		if(combine_book)
			anvil = NBTUtil.combineBook(e.left, e.right);
		
		e.cost = anvil.cost;
		e.materialCost = anvil.matcost;
		e.output = anvil.output;
	}

}
