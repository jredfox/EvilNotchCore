package com.EvilNotch.Core.TileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;

public class TileVFurnace extends TileEntityFurnace
{

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
	    super.readFromNBT(nbt);
	    this.furnaceBurnTime = nbt.getInteger("BurnTime");
	    this.furnaceCookTime = nbt.getInteger("CookTime");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		int burntimefurnace = this.furnaceBurnTime;
		int cooktime = this.furnaceCookTime;
	    super.writeToNBT(nbt);
	    nbt.setInteger("BurnTime", burntimefurnace);
	    nbt.setInteger("CookTime", cooktime);
	}
    /**
     * Returns Display of burning overriden so it displays right for higher integer values
     */
	@Override
    @SideOnly(Side.CLIENT)
    public int getBurnTimeRemainingScaled(int p_145955_1_)
    {
		if(this.furnaceBurnTime < 500000)
			return super.getBurnTimeRemainingScaled(p_145955_1_);
		else
			return this.furnaceBurnTime;
    }

}
