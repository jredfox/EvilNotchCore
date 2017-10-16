package com.EvilNotch.Core.Api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.EvilNotch.Core.Util.Java.JavaUtil;
import com.EvilNotch.Core.Util.Line.LineBase;
import com.EvilNotch.Core.Util.Util.NBTUtil;
import com.google.common.base.Strings;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;

public class NBTPathApi   {
	
	public NBTTagCompound nbt;
	public int logic = 0;
	public ArrayList<NBTPathApi.Entry> tags = new ArrayList();
	
	public NBTPathApi (NBTTagCompound nbt)
	{
		this.nbt = (NBTTagCompound) nbt.copy();
		this.tags = getPaths(this.nbt);
	}
	public NBTPathApi (NBTTagCompound nbt, int logic)
	{
		this.nbt = (NBTTagCompound) nbt.copy();
		this.logic = logic;
		this.tags = getPaths(this.nbt);
	}
	public NBTPathApi(ArrayList<Entry> tags, int logic, NBTTagCompound nbt) 
	{
		this.tags = tags;
		this.logic = logic;
		this.nbt = nbt;
	}
	/**
	 * Decompiles NBTTagCompounds/NBTTagList/NBT Arrays for easy comparing them Technically NBT should have done paths similar to this when putting a tagcompound inside of another tagcompound
	 */
	public ArrayList<NBTPathApi.Entry> getPaths(NBTTagCompound tag) 
	{
		ArrayList<NBTPathApi.Entry> entries = new ArrayList();
		NBTTagCompound nbt = (NBTTagCompound) tag.copy();
		
		Set<String> nbtset = NBTUtil.getSet(nbt);
		//Initialize First NBT to entry array
		for(String str : nbtset)
		{
			String name = str;
			NBTBase base = nbt.getTag(str).copy();
			entries.add(new NBTPathApi.Entry(name,base));
		}
		
		//Convert nbt to paths by extracting them
		boolean hasPaths = true;
		while(hasPaths)
		{
			boolean used = false;
			for(int i=0;i<entries.size();i++)
			{
				Entry e = entries.get(i);
				
				if(!e.used && e.tag instanceof NBTTagCompound)
				{
					NBTTagCompound entrynbt = (NBTTagCompound)e.tag;
					Set<String> setnbt = NBTUtil.getSet(entrynbt);
					for(String str : setnbt)
					{
						String name = e.path + "/" + str;
						Entry entry = new NBTPathApi.Entry(name, entrynbt.getTag(str).copy());
						entries.add(entry);
					}
					e.tag = new NBTTagCompound();
					e.used = true;
					used = true;
				}
				if(!e.used && e.tag instanceof NBTTagByteArray)
				{
					NBTTagByteArray arr = (NBTTagByteArray)e.tag;
					byte[] bytes = arr.func_150292_c();
					int index = 0;
					for(byte b : bytes)
					{
						NBTTagByte bytetag = new NBTTagByte(b);
						entries.add(new NBTPathApi.Entry(e.path + " \"" + index + ":\"", bytetag,index));
						index++;
					}
					e.tag = new NBTTagByteArray(new byte[]{});
					e.used = true;
					used = true;
				}
				if(!e.used && e.tag instanceof NBTTagIntArray)
				{
					NBTTagIntArray arr = (NBTTagIntArray)e.tag;
					int[] ints = arr.func_150302_c();
					int index = 0;
					for(int j : ints)
					{
						NBTTagInt inttag = new NBTTagInt(j);
						entries.add(new NBTPathApi.Entry(e.path + " \"" + index + ":\"", inttag,index));
						index++;
					}
					e.tag = new NBTTagIntArray(new int[]{});
					e.used = true;
					used = true;
				}
				if(!e.used && e.tag instanceof NBTTagList)
				{
					NBTTagList list = (NBTTagList)e.tag;
					int type = NBTUtil.getTagListType(list);
					int id = list.func_150303_d();
					Iterator<NBTBase> it = NBTUtil.getTagListIterator(list);
					int index = 0;
					
					while(it.hasNext())
					{
						NBTBase base = it.next();
						String path = e.path + " \"" + index + ":\"";
						Entry entry = new NBTPathApi.Entry(path, base.copy(),index);
						entries.add(entry);
						index++;
					}
					e.tag = new NBTTagList();
					NBTUtil.setTagListType((NBTTagList)e.tag, type);
					e.used = true;
					used = true;
				}
				if(!e.used && NBTUtil.isNBTPrimitive(e.tag))
				{
					e.used = true;
					used = true;
				}
			}
			if(!used)
				hasPaths = false;
		}
		return entries;
	}
	public static String getRawName(String p)
	{
		return getRawPath(getPathName(p));
	}
	/**
	 * Converts path to having no index used for compiling
	 */
	public static String getRawPath(String p)
	{
		String[] indexes = NBTPathApi.getPaths(p);
		String path = "";
		for(int i=0;i<indexes.length;i++)
		{
			String str = "";
			if(i != 0)
				str += "/";
			str += indexes[i];
			if(getArrayIndexFromPath(str) != -1 && i+1 == indexes.length)
				str = str.substring(0,str.indexOf("\"")-1);//Make the path raw again for compiling if has an index
			path += str;
		}
		return path;
	}
	/**
	 * Returns index from path head and if -1 doesn't have one
	 */
	public static int getArrayIndexFromPath(String p)
	{
		String str = NBTPathApi.getPathName(p);
		
		String ints = "";
		
		if(p.endsWith("\""))
		{
			int start = JavaUtil.findLastChar(p, ":");
			for(int j=start;j>=0;j--)
			{
				String s = p.substring(j, j+1);
				if(LineBase.isCharNum(s))
					ints += s;
				else{
					if(j != start)
						break;
				}
				
			}
		}
		if(!ints.equals(""))
			return Integer.parseInt(JavaUtil.reverseString(ints));
		return -1;
	}
	/**
	 * Compares paths returns true if the compared NBTPathAPi contains all paths
	 */
	public boolean doesTagHaveKeys(NBTPathApi compare)
	{
		ArrayList<String> paths = toPathArray(this);
		ArrayList<String> compare_paths = toPathArray(compare);
		return paths.containsAll(compare_paths);
	}
	/**
	 * does this NBTPathAPi have all tags with >= logic for compare NBTPathAPi
	 * @param compare
	 * @return
	 */
	public boolean hasTags(NBTPathApi compare)
	{
		return hasTags(compare,this.logic);
	}
	/**
	 * does NBTPathAPi have all tags if(is exact) compare with == else compare >= with the exception of boolean and strings
	 * @param compare
	 * @param exact
	 * @return
	 */
	public boolean hasTags(NBTPathApi compare,int logic)
	{
		if(!doesTagHaveKeys(compare))
			return false;
		for(int i=0;i<this.tags.size();i++)
		{
			boolean used = false;
			Entry e = this.tags.get(i);
			for(int j=0;j<compare.tags.size();j++)
			{
				Entry e2 = compare.tags.get(j);
				if(e.path.equals(e2.path))
				{
					if(!areValuesEqual(e.tag,e2.tag,logic))
						return false;//based on >= logic
				}
			}
		}
		return true;
	}
	public boolean hasTags(NBTTagCompound compare)
	{
		try{
		return hasTags(new NBTPathApi(compare));
		}catch(Exception e){e.printStackTrace(); return false;}
	}
	public boolean hasTags(NBTTagCompound compare,int logic){
		try{
		return hasTags(new NBTPathApi(compare,logic));
		}catch(Exception e){e.printStackTrace(); return false;}
	}
	/**
	 * Compares base to second NBTTagCompound's based on logic of ==
	 */
	public static boolean hasTags(NBTTagCompound b,NBTTagCompound c)
	{
		NBTPathApi base = new NBTPathApi(b);
		NBTPathApi compare = new NBTPathApi(c);
		return base.hasTags(compare);
	}
	/**
	 * Compares base to second NBTTagCompound's values based on logic
	 * Logic is how it will be compared[0 "==", 1 "var1 <= var2" ,2 "var1 >= var2"]
	 */
	public static boolean hasTags(NBTTagCompound b,NBTTagCompound c,int logic)
	{
		NBTPathApi base = new NBTPathApi(b);
		NBTPathApi compare = new NBTPathApi(c);
		return base.hasTags(compare,logic);
	}
	/**
	 * Removes tags form current path to set them with your defined tag
	 * Basically set tag at path is what the method does
	 */
	public void setTag(String path,NBTBase tag)
	{
		NBTBase compare = getTagFromPath(path);
		removeTag(path);
		addTag(path,tag,compare,false,true);
	}
	/**
	 * Adds a tag if and only if the tags and inner tags don't conflict
	 * @Path Default path is ""
	 */
	public void addTag(String path,NBTBase tag)
	{
		addTag(path,tag,getTagFromPath(path),true,false);
	}
	/**
	 * Adds the tags and if conflicts with current api repalces it
	 */
	public void addAndReplaceTag(String path,NBTBase tag)
	{
		addTag(path,tag,getTagFromPath(path),false,false);
	}
	/**
	 * Adds a NBTBase to the specified path note even at the base level give your path a name
	 * Adds all decompiled paths if they don't exists yet
	 * @FAQ use Util.JsonToNBT(String) to create a specified tag
	 * @Format path/name where "/" represents  being inside of an NBTTagCompound similar to how file paths works
	 */
	public void addTag(String path,NBTBase tag,NBTBase compare,boolean copySafley,boolean set)
	{
		if(path == null || tag == null)
			return;
		boolean primitive = NBTUtil.isNBTPrimitive(tag);
		if(NBTUtil.isNBTPrimitive(compare) && !primitive && !path.equals("") && !set)
		{
			System.out.println("WARNING Cannot Add Non Primative NBT To a Primitive!" + "\nPath:\"" + path + "\" NBT:" + tag);
			return;
		}
		//if it's not primitive the data needs to get decompiled starting at the else statement
		if(primitive)
		{
			Entry e = new Entry(path,tag);
			if(copySafley)
			{
				if(!hasPath(this,path))
					this.tags.add(e);
			}
			else{
				int index = getPathIndex(this, path);
				if(index != -1)
					this.tags.remove(index);
				this.tags.add(e);
			}
			return;
		}
		else{
			NBTTagCompound nbt = new NBTTagCompound();
			String name = getPathName(path);
			if(name == null)
				return;
			NBTUtil.setNBT(nbt,tag,name);
			NBTPathApi api = new NBTPathApi(nbt);
//			System.out.println(name);
			reassignFirstPaths(api,path,set);
			if(copySafley)
				api.copyDataSafely(this,api);
			else
				api.copyAndReplaceData(this,api);
		}
	}
	public static String getPathName(String path) 
	{
		if(!path.contains("/"))
			return path;
		String[] parts = LineBase.getParts(path, "/");
		if(parts.length == 0)
			return null;
		String name = parts[parts.length-1];
		return name;
	}
	public Entry getEntryFromPath(String path) 
	{
		for(Entry e : this.tags)
			if(e.path.equals(path))
				return e;
		return null;
	}
	public NBTBase getTagFromPath(String path) 
	{
		for(Entry e : this.tags)
			if(e.path.equals(path))
				return e.tag;
		return null;
	}
	/**
	 * Used for merging another this api with another
	 */
	public static void reassignFirstPaths(NBTPathApi api, String path,boolean set)
	{
		if(path == null)
			return;
		for(Entry e : api.tags)
		{
			if(!Strings.isNullOrEmpty(path) && !set)
				e.path = path + "/" + e.path;
			if(set && !Strings.isNullOrEmpty(path))
			{
				String parrent = getParrentPath(path);
				if(path.contains("/"))
					parrent += "/";
				e.path = parrent + e.path;
			}
		}
	}
	public static String getParrentPath(String path) 
	{
		String parrent = path;
		int index = JavaUtil.findLastChar(path, "/");
		if(index == -1)
			return "";
		return path.substring(0,index);
	}
	/**
	 * Copies data if a tag isn't already there
	 */
	public void copyDataSafely(NBTPathApi api, NBTPathApi api2) {
		for(Entry e : api2.tags)
			if(!NBTPathApi.hasPath(api,e.path))
				api.tags.add(e);
	}
	/**
	 * NO CHECKS CHECK YOURSELF BEFORE BREAKING THE API
	 */
	public void copyAndReplaceData(NBTPathApi api, NBTPathApi api2) {
		for(Entry e : api2.tags)
		{
			int index = NBTPathApi.getPathIndex(api, e.path);
			if(index != -1)
				api.tags.remove(index);
			api.tags.add(e);
		}
	}
	/**
	 * Removes Tag From Specified Path returns The Tag Removed
	 * @Format path/name where "/" represents  being inside of an NBTTagCompound similar to how file paths works
	 */
	public NBTBase removeTag(String path)
	{
		return removeTag(path,true);
	}
	/**
	 * Removes a tag based on the path. If remove all && NBTTagCompound removes all instances of the paths
	 */
	public NBTBase removeTag(String path,boolean removeall)
	{
		if(path == null || path.equals("") || !hasPath(this, path))
			return null;
		
		NBTBase tag = null;
		int index = getPathIndex(this,path);
		if(NBTUtil.isNBTPrimitive(this.tags.get(index).tag))
		{
			NBTBase base = this.tags.get(index).tag;
			this.tags.remove(index);
			return base;
		}
		else{
			Iterator<Entry> it = this.tags.iterator();
			while(it.hasNext())
			{
				Entry e = it.next();
				if(e.path.equals(path))
				{
					tag = e.tag;
					if(!removeall)
					{
						it.remove();
						return tag;
					}
				}
				if(e.path.contains(path) && removeall)
					it.remove();
			}
		}
		return tag;
	}
	/**
	 * Copies NBT over from tocopy if and only if there is not a tag there already
	 */
	public static void copyNBTSafley(NBTTagCompound b,NBTTagCompound tc)
	{
		NBTPathApi base = new NBTPathApi(b);
		NBTPathApi tocopy = new NBTPathApi(tc);
		ArrayList<NBTPathApi.Entry> entries = tocopy.tags;
		for(int i=0;i<entries.size();i++)
		{
			NBTPathApi.Entry e = entries.get(i);
			String path = e.path;
			if(!hasPath(base,path))
				base.tags.add(e);
		}
		NBTTagCompound nbt = compilePaths(base);
		HashMap map = (HashMap) ReflectionUtil.getObject(nbt, NBTTagCompound.class, MCPMappings.getFeildName("tagMap"));
		ReflectionUtil.setObject(b, map, NBTTagCompound.class, MCPMappings.getFeildName("tagMap"));
	}
	/**
	 * After the api has been messed with and no longer used recompile to become and NBTTagCompound Again!
	 */
	public static NBTTagCompound compilePaths(NBTPathApi api)
	{
		api = api.copy();
		NBTTagCompound nbt = new NBTTagCompound();
		for(int i=0;i<api.tags.size();i++)
			api.tags.get(i).used = false;//makes them unused and ready for action
		for(int i=0;i<api.tags.size();i++)
		{
			Entry e = api.tags.get(i);
			if(!e.used)
				NBTUtil.createTags(api,e,nbt);
		}
		return nbt;
	}
	public static String[] getPaths(String path)
	{
		String[] indexes = {};
		if(path.contains("/"))
			indexes = LineBase.getParts(path, "/");
		else
			indexes = new String[]{path};
		return indexes;
	}
	public NBTPathApi copy() 
	{
		ArrayList<Entry> tags = JavaUtil.copyArray(this.tags);
		int logic = this.logic;
		NBTTagCompound nbt = (NBTTagCompound) this.nbt.copy();
		return new NBTPathApi(tags,logic,nbt);
	}
	public static boolean hasPath(NBTPathApi api, String path) 
	{
		return getPathIndex(api,path) != -1;
	}
	public static int getPathIndex(NBTPathApi api, String path)
	{
		if(api == null || api.tags == null || path == null)
			return -1;
		for(int i=0;i<api.tags.size();i++)
		{
			String apiPath = api.tags.get(i).path;
			if(apiPath == null)
				continue;
			if(apiPath.equals(path))
				return i;
		}
		return -1;
	}
	/**
	 * Only supports NBTBase Compares Values by Logic[0,1,2] 0: == logic, 1: <= logic(harvest level), 2: >= used for defining item/block via xml
	 */
	public boolean areValuesEqual(NBTBase tag, NBTBase tag2,int logic) 
	{
		if(tag.getId() != tag2.getId())
			return false;
		byte id = tag.getId();
		if(logic == 0 || id == 0 || id == 7 || id == 8 || id == 9 || id == 10 || id == 11)//0:end,10:tagcompound,8:string
			return tag.equals(tag2);//since is decompiled test to see if is same values
		
		//Logic 1 is used for harvest levels if tag 2 <= harvest level it's harvestable tag 2 = block
		if(logic == 1)
		{
		if(id == 1)
			return ((NBTTagByte)tag).func_150290_f() <= ((NBTTagByte)tag2).func_150290_f();//byte
		if(id == 2)
			return ((NBTTagShort)tag).func_150289_e() <= ((NBTTagShort)tag2).func_150289_e();//short
		if(id == 3)
			return ((NBTTagInt)tag).func_150287_d() <= ((NBTTagInt)tag2).func_150287_d();//int
		if(id == 4)
			return ((NBTTagLong)tag).func_150291_c() <= ((NBTTagLong)tag2).func_150291_c();//long
		if(id == 5)
			return ((NBTTagFloat)tag).func_150288_h() <= ((NBTTagFloat)tag2).func_150288_h();//float
		if(id == 6)
			return ((NBTTagDouble)tag).func_150286_g() <= ((NBTTagDouble)tag2).func_150286_g();//double
		}
		//This is used for defining an item/block with nbt >= logic
		if(logic == 2){
			if(id == 1)
				return ((NBTTagByte)tag).func_150290_f() >= ((NBTTagByte)tag2).func_150290_f();//byte
			if(id == 2)
				return ((NBTTagShort)tag).func_150289_e() >= ((NBTTagShort)tag2).func_150289_e();//short
			if(id == 3)
				return ((NBTTagInt)tag).func_150287_d() >= ((NBTTagInt)tag2).func_150287_d();//int
			if(id == 4)
				return ((NBTTagLong)tag).func_150291_c() >= ((NBTTagLong)tag2).func_150291_c();//long
			if(id == 5)
				return ((NBTTagFloat)tag).func_150288_h() >= ((NBTTagFloat)tag2).func_150288_h();//float
			if(id == 6)
				return ((NBTTagDouble)tag).func_150286_g() >= ((NBTTagDouble)tag2).func_150286_g();//double
		}
		return false;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof NBTPathApi))
			return false;
		return ((NBTPathApi)obj).tags.equals(this.tags);
	}
	@Override
	public String toString()
	{
		String str = "[";
		for(Entry e : this.tags)
			str += e.toString() + ",";
		if(str.length() > 1)
			str = str.substring(0, str.length()-1);//OCD Fixer Upper
		return str + "]";
	}
	
	/**
	 * Used for getting keys of NBTPathApi
	 * @param NBTPathApi
	 * @return ArrayList<String>
	 */
	public static ArrayList<String> toPathArray(NBTPathApi api) 
	{
		ArrayList<String> list = new ArrayList();
		for(Entry e : api.tags)
			list.add(e.path);
		return list;
	}
	
	public class Entry
	{
		public String path;
		public NBTBase tag;
		public int index = -1;
		public boolean used = false;
		public boolean hasIndex = false;
		public Entry(String str, NBTBase base)
		{
			this.path = str;
			this.tag = base;
		}
		public Entry(String str, NBTBase base,int index)
		{
			this.path = str;
			this.tag = base;
			this.index = index;
			this.hasIndex = index != -1;
		}
		@Override
		public boolean equals(Object obj)
		{
			if(!(obj instanceof NBTPathApi.Entry))
				return false;
			Entry e = (Entry)obj;
			return this.path.equals(e.path) && this.tag.equals(e.tag);
		}
		@Override
		public String toString()
		{
			String str = "<" + "\"" + this.path;
			if(this.hasIndex)
				str += " ";
			str += "\"";
			str += tag.toString();
			return str + ">";
		}
	}

}
