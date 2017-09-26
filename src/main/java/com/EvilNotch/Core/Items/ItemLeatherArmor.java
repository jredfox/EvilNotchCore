package com.EvilNotch.Core.Items;

import java.awt.Color;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.MainCommonMod;
import com.EvilNotch.Core.Util.Java.ColorUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemLeatherArmor extends ItemArmor{

	public ItemArmor.ArmorMaterial material;
	public ItemLeatherArmor(int p_i45325_2_, int p_i45325_3_) 
	{
		super(ItemArmor.ArmorMaterial.CLOTH, p_i45325_2_, p_i45325_3_);
		material = ItemArmor.ArmorMaterial.CLOTH;
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack s, int i)
    {
    	if(s.getTagCompound() == null || !s.getTagCompound().hasKey("display") || !((NBTTagCompound)s.getTagCompound().getTag("display")).hasKey("color",3) )
    	{
    		if(MainCommonMod.resoucePackOld)
    			return Config.oldItemLeatherColor;
    		if(i > 0)
                return Config.itemLeatherPadOverlay;
    		else
    			return Config.itemLeatherColor;
    	}
    	else
    		return this.getColor(s);
    }

    /**
     * Return the color for the specified armor ItemStack.
     */
    @Override
    public int getColor(ItemStack p_82814_1_)
    {
        if(this.material != ItemArmor.ArmorMaterial.CLOTH)
            return -1;
        else
        {
            NBTTagCompound nbttagcompound = p_82814_1_.getTagCompound();

            if(nbttagcompound == null && MainCommonMod.resoucePackOld)
                return Config.armorOldLeatherColor;
            if(nbttagcompound == null && !MainCommonMod.resoucePackOld)
                return Config.armorLeatherColor;
            else
            {
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");
                int defaulte = nbttagcompound1 == null ? Config.armorLeatherColor : (nbttagcompound1.hasKey("color", 3) ? ColorUtil.getMs(nbttagcompound1.getInteger("color"),Config.newLeather): Config.armorLeatherColor);
                int old = nbttagcompound1 == null ? Config.armorOldLeatherColor : (nbttagcompound1.hasKey("color", 3) ? ColorUtil.getMs(nbttagcompound1.getInteger("color"),Config.oldLeather) :  Config.armorOldLeatherColor);
              //  System.out.println(nbttagcompound + " " + old);
                if (MainCommonMod.resoucePackOld)
                    return old;
                else
                    return defaulte;
            }
        }
    }

	

}
