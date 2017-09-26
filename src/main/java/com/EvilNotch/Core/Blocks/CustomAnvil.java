package com.EvilNotch.Core.Blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockAnvil;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class CustomAnvil extends BlockAnvil{
	public static final String[] anvil_IconNames = new String[] {"evil_notch_core:fanvil_top_damaged_0", "evil_notch_core:fanvil_top_damaged_1", "evil_notch_core:fanvil_top_damaged_2"};
	public static final String[] anvil_IconNames_grey = new String[] {"evil_notch_core:anvil_top_damaged_0", "evil_notch_core:anvil_top_damaged_1", "evil_notch_core:anvil_top_damaged_2"};
	public boolean isBlack;
    @SideOnly(Side.CLIENT)
    public IIcon[] anvilIconss;
    
    public CustomAnvil(boolean black)
    {
    	super();
    	this.setHardness(5.0F);
    	this.setStepSound(soundTypeAnvil);
    	this.setResistance(2000.0F);
    	if(black)
    		this.setBlockName("blackAnvil");
    	else
    		this.setBlockName("greyAnvil");
    	this.setCreativeTab(CreativeTabs.tabBlock);
    	this.isBlack = black;
    }
    
    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        if (this.anvilRenderSide == 3 && side == 1)
        {
        	int k = (meta >> 2) % this.anvilIconss.length;
            return this.anvilIconss[k];
        }
        else
        {
            return this.blockIcon;
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {
        this.anvilIconss = new IIcon[anvil_IconNames.length];
        if(this.isBlack)
        	this.blockIcon = p_149651_1_.registerIcon("evil_notch_core:fanvil_base");
        else
        	this.blockIcon = p_149651_1_.registerIcon("evil_notch_core:anvil_base");
        for (int i = 0; i < this.anvilIconss.length; ++i)
        {
        	if(this.isBlack)
        		this.anvilIconss[i] = p_149651_1_.registerIcon(anvil_IconNames[i]);
        	else
        		this.anvilIconss[i] = p_149651_1_.registerIcon(anvil_IconNames_grey[i]);
        }
    }
}
