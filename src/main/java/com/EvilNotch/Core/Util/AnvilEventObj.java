package com.EvilNotch.Core.Util;

import net.minecraft.item.ItemStack;

public class AnvilEventObj {
	public int cost;
	public int matcost;
	public ItemStack output;
	
	public AnvilEventObj(int cost, int matcost, ItemStack output)
	{
		this.cost = cost;
		this.matcost = matcost;
		this.output = output;
	}

}
