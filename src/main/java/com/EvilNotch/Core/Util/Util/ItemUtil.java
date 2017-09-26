package com.EvilNotch.Core.Util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.Util.Line.LineBase;
import com.EvilNotch.Core.Util.Line.LineItemStack;
import com.EvilNotch.Core.Util.Line.LineItemStackBase;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemUtil {
	
	public static boolean bucketHasCustomData(ItemStack previous,ItemStack output)
	{
		boolean milk_bucket = output.getItem() == Items.milk_bucket;
		boolean nbt = milk_bucket ? Config.milkbucketSaveNBT : Config.bucketSaveNBT;
		return previous.getEnchantmentTagList() != null && Config.bucketSaveEnchantments || nbt && previous.getTagCompound() != null;
	}
	public static NBTTagCompound getBucketData(ItemStack previous,ItemStack output)
	{
		boolean milk_bucket = output.getItem() == Items.milk_bucket;
		boolean hasnbt = milk_bucket ? Config.milkbucketSaveNBT : Config.bucketSaveNBT;
		NBTTagCompound nbt = previous.getTagCompound();
		if(!hasnbt && Config.bucketSaveEnchantments)
		{
			nbt = new NBTTagCompound();
			if(Config.bucketSaveEnchantments)
			{
				NBTTagList list = NBTUtil.getEnchantments(previous);
				if(list.tagCount() > 0)
					nbt.setTag("ench", list);
			}
		}
		return nbt;
	}
	
	public static String getItemString(Item item)
	{
		return GameData.getItemRegistry().getNameForObject(item).toString();
	}
	public static MovingObjectPosition getMovingObjectPositionFromPlayer1(World p_77621_1_, EntityPlayer p_77621_2_, boolean p_77621_3_)
    {
        float f = 1.0F;
        float f1 = p_77621_2_.prevRotationPitch + (p_77621_2_.rotationPitch - p_77621_2_.prevRotationPitch) * f;
        float f2 = p_77621_2_.prevRotationYaw + (p_77621_2_.rotationYaw - p_77621_2_.prevRotationYaw) * f;
        double d0 = p_77621_2_.prevPosX + (p_77621_2_.posX - p_77621_2_.prevPosX) * (double)f;
        double d1 = p_77621_2_.prevPosY + (p_77621_2_.posY - p_77621_2_.prevPosY) * (double)f + (double)(p_77621_1_.isRemote ? p_77621_2_.getEyeHeight() - p_77621_2_.getDefaultEyeHeight() : p_77621_2_.getEyeHeight()); // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
        double d2 = p_77621_2_.prevPosZ + (p_77621_2_.posZ - p_77621_2_.prevPosZ) * (double)f;
        Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 5.0D;
        if (p_77621_2_ instanceof EntityPlayerMP)
        {
            d3 = ((EntityPlayerMP)p_77621_2_).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3 vec31 = vec3.addVector((double)f7 * d3, (double)f6 * d3, (double)f8 * d3);
        return p_77621_1_.func_147447_a(vec3, vec31, p_77621_3_, !p_77621_3_, false);
    }
	
	/**Returns the string of the classes tool if any
	 * 
	 */
	public static String getToolClass(ItemStack heldItem)
	{
		  int w = 0;
		  String strtool = "pickaxe";
		    
		    //Gets the tool string name.  If it's not null and not = to pickaxe else it will print is tool? with the tool along with keeping the default as a pickaxe...
		   Set<String> settool = heldItem.getItem().getToolClasses(heldItem);
		   Iterator setit = settool.iterator();
		   while (setit.hasNext())
		   {
			   String itset = setit.next().toString();
			   
			   if (w == 0 && itset != null && !itset.equals("pickaxe"))
			   {
				   strtool = itset;
				   w++;
			   }
			   else{
				//   System.out.println("Is Not Tool? " + strtool);
			   	}
			   
		   }
		return strtool;
	}
	public static boolean isItem(Item item, ArrayList<LineBase> list, int itemDamage) 
	{
		return getItemLine(item,list,true, itemDamage) != null;
	}
	
	/**
	 * Gets the blockline from a ConfigBase List. 
	 * MetaData is the override else return regular block line
	 */
	public static LineItemStackBase getItemLine(Item item, List<LineBase> list, boolean hasmeta,int meta) 
	{
		Iterator<LineBase> it = list.iterator();
		LineItemStack stack = null;
		while(it.hasNext())
		{
			LineBase base = it.next();
		   LineItemStack line = new LineItemStack(base.toString());//Creates new string so no exception occur
		   Item item2 = GameRegistry.findItem(line.modid, line.name);
		   if(item2 != null && item2 == item)
		   {
			  if(!hasmeta || hasmeta && line.meta == meta)
				  return line;
			   if(line.meta == -1)
				   stack = line;//If not used store as a possible if it doesn't contain metadata
		   }
		}
		return stack;
	}
	public static ItemStack getBucketStack(Block b) 
	{
		Iterator<Map.Entry<String, Block>> it = Registry.buckets.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<String, Block> pair = it.next();
			String name = pair.getKey();
			Block block = pair.getValue();
			if(b == block)
			{
				LineBase line = new LineBase("\"" + name + "\"");
				return new ItemStack(GameRegistry.findItem(line.modid, line.name));
			}
		}
		return null;
	}
	
	
	

	
	
}
