package com.EvilNotch.Core.Events;

import java.util.ArrayList;
import java.util.List;

import com.EvilNotch.Core.Util.Java.RomanNumerals;
import com.EvilNotch.Core.Util.Util.NBTUtil;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class ItemToolTipEvent {
	
	//Vanilla Enchantment ToolTip Fixer
	@SubscribeEvent (priority = EventPriority.HIGHEST)
	public void onToolTip(ItemTooltipEvent e)
	{
		if(e.itemStack == null || e.itemStack.getTagCompound() == null || !NBTUtil.hasEnchantments(e.itemStack))
			return;
		NBTTagList ench = NBTUtil.getEnchantments(e.itemStack);
		List<String> list = new ArrayList();
		
		for(int j=0;j<e.toolTip.size();j++)
			list.add(e.toolTip.get(j)); //Allocates
		
		//Remove All Enchantments from my list
		for(int i=0;i<list.size();i++)
		{
			String s = list.get(i);
			for(int j=0;j<ench.tagCount();j++)
			{
				NBTTagCompound nbt = (NBTTagCompound)ench.getCompoundTagAt(j);
				int id = nbt.getInteger("id");
				int lvl = nbt.getInteger("lvl");
				String Roman = RomanNumerals.translateIntToRoman(lvl);
				try{
					Enchantment enchantment = Enchantment.enchantmentsList[id];
					String enchname = StatCollector.translateToLocal(enchantment.getName());
					if(s.contains(enchname + " " + Roman) || s.contains(enchname + " enchantment.level"))
						list.set(i,enchname + " " + Roman);
				}
				catch(Exception ee){ee.printStackTrace();}
			}
		}
		e.toolTip.clear();
		
		for(String s : list)
			e.toolTip.add(s);
	}

}
