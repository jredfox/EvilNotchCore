package com.EvilNotch.Core.Items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemCustomAnvil extends ItemBlockWithMetadata{

	public ItemCustomAnvil(Block b) 
	{
		super(b, b);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
	    return this.getUnlocalizedName() + "_" + stack.getItemDamage();
	}
	
	/**
     * Returns the metadata of the block which this Item (ItemBlock) can place
     */
    public int getMetadata(int p_77647_1_)
    {
        return p_77647_1_ << 2;
    }

}
