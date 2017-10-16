package com.EvilNotch.Core.Util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.Api.MCPMappings;
import com.EvilNotch.Core.Api.NBTPathApi;
import com.EvilNotch.Core.Api.ReflectionUtil;
import com.EvilNotch.Core.Util.AnvilEventObj;
import com.EvilNotch.Core.Util.Java.JavaUtil;
import com.EvilNotch.Core.Util.Line.LineBase;

import cpw.mods.fml.common.Loader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;

public class NBTUtil {
	
	/**
	 * Compiles a single entry to the specified NBTTagCompound
	 */
	public static NBTTagCompound createTags(NBTPathApi api,NBTPathApi.Entry entry, NBTTagCompound init_nbt)
	{
		NBTBase nbt = init_nbt;
		String[] indexes = NBTPathApi.getPaths(entry.path);
		String path = indexes[0];
		
		for(int i=0;i<indexes.length;i++)
		{
			if(i != 0)
				path += "/" + indexes[i];//makes the name advance
			
			NBTPathApi.Entry e = api.getEntryFromPath(path);
			if(e == null || e.path == null || e.tag == null)
				continue;
			
			String name = NBTPathApi.getRawName(path);//makes it loose the path
			int index = NBTPathApi.getArrayIndexFromPath(path);
			NBTBase base = NBTUtil.getNBTFromPath(NBTPathApi.getRawPath(path),init_nbt);
			if(base != null)
				nbt = base;//initialize first path without index
			if(index != -1)
			{
				NBTBase base2 = NBTUtil.getNBTFromPath(path,init_nbt);
				if(base2 != null)
					nbt = base2;//initialize first path without index
			}
//			System.out.println("Before:" + "index:" + i + " " + path + " " + nbt);
			if(!hasPath(path,init_nbt))
				NBTUtil.setTag(nbt,name,e.tag,index,(index != -1));
//			System.out.println("After:" + "index:" + i + " " + path + " " + nbt);
			
			e.used = true;
			if(nbt == null)
				return null;
		}
		return init_nbt;
	}
	/**
	 * Advanced setTag method use if you don't know what it is to set it and returns a tag that it set
	 */
	public static NBTBase setTag(NBTBase nbt, String name, NBTBase add,int index,boolean isIndexHoldeer) 
	{
		if(nbt instanceof NBTTagCompound)
		{
			NBTTagCompound compound = (NBTTagCompound)nbt;
			compound.setTag(name, add);
			return compound.getTag(name);
		}
		if(nbt instanceof NBTTagList)
		{
			NBTTagList list = (NBTTagList)nbt;
			if(NBTUtil.getTagListType(list) != add.getId())
				NBTUtil.setTagListType(list, add.getId());
			List<NBTBase> li = NBTUtil.getTagListArray(list);
			boolean inRange = index < li.size() && li.size() != 0 && index >=0;
//			System.out.println(inRange + " " + name + " index:" + index);
			if(NBTUtil.getTagListType(list) != 10)
			{
				if(inRange)
					li.set(index, add);
				else
					list.appendTag(add);
				return li.get(index);
			}
			else{
				if(isIndexHoldeer)
				{
					if(inRange)
						li.set(index,add);
					else
						li.add(add);
					return list.getCompoundTagAt(index);
				}
				if(inRange)
				{
					NBTTagCompound compound = list.getCompoundTagAt(index);
					compound.setTag(name, add);
					return compound.getTag(name);
				}
				else{
					NBTTagCompound compound = new NBTTagCompound();
					compound.setTag(name, add);
					list.appendTag(compound);
					return compound.getTag(name);
				}
			}
		}
		if(nbt instanceof NBTTagByteArray && add instanceof NBTTagByte)
		{
			NBTTagByteArray compound = (NBTTagByteArray)nbt;
			NBTTagByte tag_byte = (NBTTagByte)add;
			ArrayList<Byte> list = new ArrayList();
			byte[] li = compound.func_150292_c();
			for(byte b : li)
				list.add(b);
			if(index < li.length && li.length != 0)
				list.set(index, tag_byte.func_150290_f());
			else
				list.add(tag_byte.func_150290_f());
			ReflectionUtil.setObject(compound, JavaUtil.arrayToStaticBytes(list), NBTTagByteArray.class, MCPMappings.getFeildName("byteArray"));
		}
		if(nbt instanceof NBTTagIntArray && add instanceof NBTTagInt)
		{
			NBTTagIntArray compound = (NBTTagIntArray)nbt;
			NBTTagInt tag_int = (NBTTagInt)add;
			ArrayList<Integer> list = new ArrayList();
			int[] li = compound.func_150302_c();
			for(int i : li)
				list.add(i);
			if(index < li.length && li.length != 0)
				list.set(index, tag_int.func_150287_d());
			else
				list.add(tag_int.func_150287_d());
			ReflectionUtil.setObject(compound, JavaUtil.arrayToStaticInts(list), NBTTagIntArray.class, MCPMappings.getFeildName("intArray"));
		}
		return null;
	}
	/**
	 * Unlike NBTPathApi's hasPath this is a physical boolean for compiled NBTTagCompounds
	 */
	public static boolean hasPath(String path, NBTBase t) 
	{
		return getNBTFromPath(path,t.copy()) != null;
	}
	/**
	 * Get's the tag from the specified path on compiled NBT and null if doesn't have path
	 */
	public static NBTBase getNBTFromPath(String path,NBTBase t)
	{
		NBTBase nbt = t;
		String[] indexes = NBTPathApi.getPaths(path);
		NBTBase tag = null;
		
		for(int i=0;i<indexes.length;i++)
		{
			String str = indexes[i];
			int index = NBTPathApi.getArrayIndexFromPath(str);
			String name = NBTPathApi.getRawName(str);
			nbt = NBTUtil.getTag(nbt,name,index);
			if(NBTUtil.isNBTArray(nbt) && index != -1)
				nbt = NBTUtil.getTag(nbt,name,index);//grab NBTBase from tag if has index
			if(nbt == null || i+1 < indexes.length && NBTUtil.isNBTPrimitive(nbt))
				return null;
		}
		return nbt;
	}
	/**
	 * returns if NBTBase is NBTTagList[],NBTByteArray:[],NBTTagIntArray[],in the future mc versions NBTTagLongArray[]
	 */
	public static boolean isNBTArray(NBTBase nbt) 
	{
		return nbt instanceof NBTTagList || nbt instanceof NBTTagByteArray || nbt instanceof NBTTagIntArray;
	}
	/**
	 * Get's Tag from name and if has index(index != -1) then return tag array index tag/data
	 */
	public static NBTBase getTag(NBTBase nbt, String name,int index) 
	{
		if(nbt == null || name == null)
			return null;
		if(nbt instanceof NBTTagCompound)
		{
			NBTTagCompound tag = (NBTTagCompound)nbt;
			return tag.getTag(name);
		}
		if(nbt instanceof NBTTagList)
			return NBTUtil.getTagFromList((NBTTagList)nbt,index);
		
		if(nbt instanceof NBTTagByteArray)
		{
			NBTTagByteArray arr = (NBTTagByteArray)nbt;
			byte[] bytes = arr.func_150292_c();
			if(bytes.length == 0 || index < 0 || index >=bytes.length)
				return null;
			return new NBTTagByte(bytes[index]);
		}
		if(nbt instanceof NBTTagIntArray)
		{
			NBTTagIntArray arr = (NBTTagIntArray)nbt;
			int[] ints = arr.func_150302_c();
			if(ints.length == 0 || index < 0 || index >=ints.length)
				return null;
			return new NBTTagInt(ints[index]);
		}
		return null;
	}
	
	public static int getTagListType(NBTTagList list)
	{
		return  list.func_150303_d();
	}
	public static void setTagListType(NBTTagList list,int type)
	{
		ReflectionUtil.setObject(list,(byte)type, NBTTagList.class, MCPMappings.getFeildName("tagType"));
	}
	/**
	 * Unoptimized way of setting an NBTTagList at it's index
	 */
	public static NBTBase getTagFromList(NBTTagList list, int index) 
	{
		if(index == -1)
			return null;
		List<NBTBase> arr = getTagListArray(list);
		if(index >= arr.size())
			return null;
		return arr.get(index);
	}
	/**
	 * Unoptimized way of Setting a Tag to a NBTTagList If Possible Otherwise add it to the list
	 */
	public static void setTagFromList(NBTTagList list,NBTBase tag, int index) 
	{
		if(index == -1)
			return;
		List<NBTBase> arr = getTagListArray(list);
		if(index >= arr.size())
			list.appendTag(tag);
		arr.set(index,tag);
	}

	/**
	 * Used for NBTPathApi if NBTTagCompound copy tags over else put them to new tag
	 */
	public static void setNBT(NBTTagCompound nbt, NBTBase tag,String name) 
	{
		if(NBTUtil.isNBTPrimitive(tag))
		{
			nbt.setTag(name, tag);
			return;
		}
		if(tag instanceof NBTTagCompound)
			copyNBT(nbt,(NBTTagCompound)tag);
		if(tag instanceof NBTTagList)
		{
			NBTTagList list = (NBTTagList)tag;
			nbt.setTag(name, list);
		}
		if(tag instanceof NBTTagByteArray)
		{
			NBTTagByteArray tag2 = (NBTTagByteArray)tag;
			nbt.setByteArray(name, tag2.func_150292_c());
		}
		if(tag instanceof NBTTagIntArray)
		{
			NBTTagIntArray tag2 = (NBTTagIntArray)tag;
			nbt.setIntArray(name, tag2.func_150302_c());
		}
	}
	public static List<NBTBase> getTagListArray(NBTTagList list)
	{
		if(list == null)
			return null;
		return (List<NBTBase>)(ReflectionUtil.getObject(list, NBTTagList.class, MCPMappings.getFeildName("tagList")));
	}
	public static Iterator<NBTBase> getTagListIterator(NBTTagList list)
	{
		List li = (List)(ReflectionUtil.getObject(list, NBTTagList.class, MCPMappings.getFeildName("tagList")));
		return li.iterator();
	}
	
	public static boolean isNBTPrimitive(NBTBase base){
		return !isNotNBTPrimitive(base);
	}

	public static boolean isNotNBTPrimitive(NBTBase base) {
		return base instanceof NBTTagCompound || base instanceof NBTTagList || base instanceof NBTTagByteArray || base instanceof NBTTagIntArray;
	}
	
	public static boolean isTagBoolean(NBTBase tag) 
	{
		if(tag.getId() != 1)
			return false;
		NBTTagByte bytetag = (NBTTagByte)tag;
		byte value = bytetag.func_150290_f();
		return value == (byte)0 || value == (byte)1;
	}
	
	public static Set<String> getSet(NBTTagCompound nbt)
	{
		return nbt.func_150296_c();
	}
	
	public static NBTTagCompound getFileNBT(File file) 
	{
		try{
			FileInputStream stream = new FileInputStream(file);
			NBTTagCompound nbt = CompressedStreamTools.readCompressed(stream);
			stream.close();
			return nbt;
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
			FileOutputStream outputstream = new FileOutputStream(file);
			 CompressedStreamTools.writeCompressed(nbt,outputstream);
			 outputstream.close();
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 *Copy imediate NBT safeley over if it doesn't have it at the base NBTTagCompound
	 *USE copyNBTSafeleyPrioritized() DEPRECIATED AND DOESN'T WORK AT THE SOURCE LEVEL
	 */
	@Deprecated
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
	 * Copies all tags of all branches if it doesn't have a path
	 */
	public static void copyNBTSafeleyPrioritized(NBTTagCompound base, NBTTagCompound tocopy) 
	{
		NBTPathApi.copyNBTSafley(base, tocopy);
	}
	
}
