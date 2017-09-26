package com.EvilNotch.Core.Util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.Util.AnvilEventObj;
import com.EvilNotch.Core.Util.Java.JavaUtil;
import cpw.mods.fml.common.Loader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTUtil {
	
	public static NBTTagCompound getFileNBT(File file) 
	{
		try{
			return CompressedStreamTools.readCompressed(new FileInputStream(file));
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	/**
	 * Like JsonToNBT Class but, handels the exception automatically
	 * @param string
	 * @return
	 */
	public static NBTTagCompound JsonToNBT(String string) 
	{
		try{
			return (NBTTagCompound) net.minecraft.nbt.JsonToNBT.func_150315_a(string);
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	/**
	 * Copies all nbt tags from tocopy to base
	 * @param tocopy
	 * @param base
	 */
	public static void copyNBT(NBTTagCompound base, NBTTagCompound tocopy)
	{
		Set<String> list = tocopy.func_150296_c();
		for(String s : list)
			base.setTag(s, tocopy.getTag(s).copy());
	}
	/**
	 * Removes tags from base from toremove
	 * @param base
	 * @param tocopy
	 */
	public static void removeNBT(NBTTagCompound base, NBTTagCompound toremove)
	{
		Set<String> list = toremove.func_150296_c();
		for(String s : list)
			base.removeTag(s);
	}
	/**
	 * Inverts NBTTagList specifically used for ToSpawner or ToMounting Processes
	 * @param nbt
	 */
	public static NBTTagList reverseNBTList(NBTTagList l)
	{
		NBTTagList list = new NBTTagList();
		
		if(l.tagCount() > 0)
		for(int i=l.tagCount()-1;i>=0;i--)
		{
			list.appendTag(l.getCompoundTagAt(i));
		}
		return list;
	}
	
	/**
     * Returns Correct NBT if any
     */
	public static NBTTagCompound getNBT(ItemStack stack)
	{
		if (stack.getTagCompound() != null)
		return (NBTTagCompound)stack.getTagCompound().copy();
		
		return new NBTTagCompound();
	}
	/**
     * Returns Correct NBT if any from NBT With Null Protection
     */
	public static NBTTagCompound getNBT(NBTTagCompound nbt, String str)
	{
		if (nbt == null)
			return new NBTTagCompound();
		
		if(nbt.getTag(str) != null)
			return (NBTTagCompound)nbt.getTag(str).copy();
		
		return new NBTTagCompound();
	}
	 /**
     * Returns Correct NBTTagList if any from an itemstack
     */
	public static NBTTagList getTagList(ItemStack stack, String str, int integer)
	{
		NBTTagCompound tag = getNBT(stack);
			
		NBTTagList list = tag.getTagList(str, integer);
		if (list != null && list.tagCount() > 0)
			return (NBTTagList)tag.getTagList(str, integer).copy();

		return new NBTTagList();
	}
	 /**
     * Returns Correct NBTTagList if any from nbt if any
     */
	public static NBTTagList getTagList(NBTTagCompound nbt, String str, int integer)
	{
		if (nbt == null)
			return new NBTTagList();
		
		NBTTagList list = nbt.getTagList(str, integer);
		if (list != null && list.tagCount() > 0)
			return (NBTTagList)nbt.getTagList(str, integer).copy();

		return new NBTTagList();
	}

	public static NBTTagList getEnchantments(ItemStack stack)
	{
		if(stack.getEnchantmentTagList() != null && getTagList(getNBT(stack),"ench",10).tagCount() > 0)
			return getTagList(getNBT(stack),"ench",10);
		else
			return getTagList(getNBT(stack),"StoredEnchantments",10);
	}
	
	public static boolean canAnvilCombine(ItemStack left, ItemStack right) 
	{
		if(!Config.overrideAvnilCombine || !left.isItemStackDamageable() || !right.isItemStackDamageable() || left.getItem() != right.getItem())
			return false;
		return true;
	}
	public static boolean isAnvilUnitRepair(ItemStack left, ItemStack right) 
	{
		if(!Config.overrideAvnilUnitRepair || !left.getItem().isDamageable() || !left.getItem().getIsRepairable(left, right))
			return false;
		 int k = Math.min(left.getItemDamageForDisplay(), left.getMaxDamage() / 4);
		 if(k<=0)
			 return false;
		return true;
	}
	/**
	 * Combine Book With Item and SilkSpawners Support
	 * @param left
	 * @param right
	 * @return
	 */
	public static boolean canAnvilCombineBook(ItemStack left, ItemStack right)
	{
		if(!Config.overrideAvnilCombineBook)
			return false;//Return if override is false
		if(left.getItem() == Items.enchanted_book && right.getItem() == Items.enchanted_book || left.isItemStackDamageable() && right.getItem() == Items.enchanted_book || Config.enchantAnyItem && right.getItem() == Items.enchanted_book || Loader.isModLoaded("silkspawners") && left.getItem() instanceof ItemBucket && right.getItem() == Items.enchanted_book)
			return true;
		return false;
	}
	/**
	 * Combines enchantment from two stacks
	 * IF BOOK ON BOOK SET VALUES NOT TO ENCH~!!!
	 * @param left
	 * @param right
	 * @return
	 */
	public static void combineEnchantments(ItemStack left, ItemStack right) 
	{
		if(!hasEnchantments(left) && !hasEnchantments(right))
			return;
		boolean isbook = left.getItem() == right.getItem() && left.getItem() == Items.enchanted_book;
		NBTTagList left_list = getEnchantments(left);
		NBTTagList right_list = getEnchantments(right);
		//System.out.println(right_list);
		//Set enchantment to highest ids before combing them
		HashMap<Integer, Integer> lench = putEnchantsToHashMap(left_list,new HashMap<Integer,Integer>());
		HashMap<Integer,Integer> rench = putEnchantsToHashMap(right_list,new HashMap<Integer,Integer>());
		
		NBTTagList enchantments = new NBTTagList();
		
		//Combine Enchantments finally
		Iterator<Map.Entry<Integer,Integer>> Lit = lench.entrySet().iterator();
		while(Lit.hasNext())
		{
			Map.Entry<Integer, Integer> lindex = Lit.next();
			int id = lindex.getKey();
			int lvl = lindex.getValue();
			Iterator<Map.Entry<Integer,Integer>> rit = rench.entrySet().iterator();
			while(rit.hasNext())
			{
				Map.Entry<Integer, Integer> rindex = rit.next();
				int rid = rindex.getKey();
				int rlvl = rindex.getValue();
				if(rid == id)
				{
					if(lvl < rlvl)
					{
						lvl = rlvl;
						continue;
					}
					if(lvl == rlvl)
						lvl++;
				}
			}
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("id", id);
			nbt.setInteger("lvl", lvl);
			enchantments.appendTag(nbt);
		}
		//put missing enchantments from the right
		Iterator<Map.Entry<Integer,Integer>> it = rench.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<Integer, Integer> index = it.next();
			if(!lench.containsKey(index.getKey()))
			{
				int id = index.getKey();
				int lvl = index.getValue();
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("id",id);
				nbt.setInteger("lvl", lvl);
			  try{
				  if(canCombineEnchantments(lench,Enchantment.enchantmentsList[id]))
					  enchantments.appendTag(nbt);//If can combine do this
				}catch(Exception e){e.printStackTrace();}
			}
		}
		if(enchantments.tagCount() == 0)
			return;//Do nothing if tagcount is = 0
		NBTTagCompound tag = left.getTagCompound() == null ? new NBTTagCompound() : left.getTagCompound();
		String ench = isbook ? "StoredEnchantments" : "ench";
		tag.removeTag(ench);
		tag.setTag(ench, enchantments);
		left.setTagCompound(tag);//Hotfix for if left argument has null nbt
		
	}
	public static boolean canCombineEnchantments(HashMap<Integer, Integer> map, Enchantment ench) 
	{
		Iterator<Map.Entry<Integer,Integer>> it = map.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<Integer,Integer> pair = it.next();
			int id = pair.getKey();
			Enchantment ench2 = Enchantment.enchantmentsList[id];
			if(Config.anvilVanillaLogic)
			{
				if(!ench.canApplyTogether(ench2) || !ench2.canApplyTogether(ench))
					return false;
			}
			if(!Config.anvilFireandWaterEnchants)
			{
				 //__________________________AQUA_____________________________________________________________________________________________________________________________________________________________\\
		    	if (ench.effectId == 6 && id == 20 || ench.effectId == 20 && id == 6 || ench.effectId == 6 && id == 50 || ench.effectId == 50 && id == 6 || ench.effectId == 6 && id == 1 || ench.effectId == 1 && id == 6)
		    		 return false;
		    	//__________________________LUCK OF SEA_____________________________________________________________________________________________________________________________________________________________\\
		    	if (ench.effectId == 61 && id == 20 || ench.effectId == 20 && id == 61 || ench.effectId == 61 && id == 50 || ench.effectId == 50 && id == 61 || ench.effectId == 61 && id == 1 || ench.effectId == 1 && id == 61 )
		    		return false;
		    	//__________________________LURE_____________________________________________________________________________________________________________________________________________________________\\
		    	if (ench.effectId == 62 && id == 20 || ench.effectId == 20 && id == 62 || ench.effectId == 62 && id == 50 || ench.effectId == 50 && id == 62 || ench.effectId == 62 && id == 1 || ench.effectId == 1 && id == 62 )
		    		return false;
			}
		}
		return true;
	}
	public static HashMap<Integer,Integer> putEnchantsToHashMap(NBTTagList list,HashMap<Integer,Integer> map)
	{
		//from left list add and combine enchantments
		for(int i=0;i<list.tagCount();i++)
		{
			NBTTagCompound nbt = list.getCompoundTagAt(i);
			int id = nbt.getInteger("id");
			int lvl = nbt.getInteger("lvl");
			if(map.containsKey(id))
			{
				int ench_lvl = map.get(id);
				if(ench_lvl < lvl)
					map.put(id, lvl);
			}
			else
				map.put(id, lvl);
		}
		return map;
	}
	public static boolean hasEnchantments(ItemStack stack) 
	{
		NBTTagCompound nbt = getNBT(stack);
		if(stack.getEnchantmentTagList() == null && !nbt.hasKey("StoredEnchantments",9))
			return false;
		return true;
	}
	public static AnvilEventObj combineRepairTool(ItemStack left, ItemStack right) 
	{
		left = ItemStack.copyItemStack(left);
		right = ItemStack.copyItemStack(right);
		
		int max = left.getMaxDamage();
		int repair = 0;
		int j = left.getMaxDamage() - left.getItemDamage();
		int k = right.getMaxDamage() - right.getItemDamage();
		int l = k + left.getMaxDamage() * 12 / 100;
		int i1 = j + l;
		 if (i1 > max)
			 i1 = max;
		repair = max - i1;
		
		left.setItemDamage(repair);
		combineEnchantments(left,right);
		int cost = 0 + getAnvilEnchantCost(getEnchantments(left));//Gets combined enchantment count
		if(NBTUtil.getEnchantments(left).tagCount() == 0)
			cost = 1;//Hotfix for if not combining enchantments
		if(left.getTagCompound() == null && right.getTagCompound() != null)
			left.setTagCompound(right.getTagCompound());
		
		return new AnvilEventObj(cost,right.stackSize,left);
	}
	/**
	 * Returns the amount of damage left from unit repair in an anvil
	 * @param left
	 * @param right
	 * @return
	 */
	public static AnvilEventObj unitRepairTool(ItemStack left, ItemStack right) 
	{	 
		left = ItemStack.copyItemStack(left);
		int k = Math.min(left.getItemDamageForDisplay(), left.getMaxDamage() / 4);
		 
        int matcost = 0;
        int cost = 0;
        for (int l = 0; k > 0 && l < right.stackSize; ++l)
        {
            int i1 = left.getItemDamageForDisplay() - k;
            left.setItemDamage(i1);
            matcost += 1;
            cost += 1;
            k = Math.min(left.getItemDamageForDisplay(), left.getMaxDamage() / 4);
        }
        if(Config.anvilAlwaysonelvl)
        	cost = 1;
         return new AnvilEventObj(cost,matcost,left);
	}
	public static AnvilEventObj combineBook(ItemStack left, ItemStack right) 
	{
		left = ItemStack.copyItemStack(left);
		right = ItemStack.copyItemStack(right);
		
		combineEnchantments(left,right);
		int cost = getAnvilEnchantCost(getEnchantments(left));//Gets combined enchantment count
		
		
		if(left.getTagCompound() == null && right.getTagCompound() != null)
			left.setTagCompound(right.getTagCompound());
		
		return new AnvilEventObj(cost,right.stackSize,left);
	}
	public static int getAnvilEnchantCost(NBTTagList enchantments) 
	{
		if(!Config.anvilAlwaysonelvl && !Config.anvilCostPerLevel)
			return enchantments.tagCount();
		if(Config.anvilAlwaysonelvl)
			return 1;
		if(Config.anvilCostPerLevel)
		{
			int cost = 0;
			for(int i=0;i<enchantments.tagCount();i++)
			{
				NBTTagCompound tag = enchantments.getCompoundTagAt(i);
				cost += tag.getInteger("lvl");
			}
			return cost;
		}
		return 0;
	}

	public static void updateNBTFile(File file, NBTTagCompound nbt) 
	{
		try{
			 CompressedStreamTools.writeCompressed(nbt,new FileOutputStream(file));
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * Only copy nbt from tocopy to base if doesn't have it
	 * @param base
	 * @param tocopy
	 */
	public static void copyNBTSafeley(NBTTagCompound base, NBTTagCompound tocopy)
	{
		Set<String> list = tocopy.func_150296_c();
		for(String s : list)
		{
			if(!base.hasKey(s))
				base.setTag(s, tocopy.getTag(s).copy());
		}
	}
	/**
	 * Copies all tags of all branches from least priotrity to base if base already has that tag do nothing
	 * WIP doesn't do it's function yet NBTPathAPi hasn't been made yet
	 */
	public static void copyNBTSafeleyPrioritized(NBTTagCompound basenbt, NBTTagCompound leastpriority) 
	{
		NBTUtil.copyNBTSafeley(basenbt, leastpriority);
	}
	
}
