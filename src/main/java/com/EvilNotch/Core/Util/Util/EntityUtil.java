package com.EvilNotch.Core.Util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.MainCommonMod;
import com.EvilNotch.Core.Api.FieldAcess;
import com.EvilNotch.Core.Events.TickHandler;
import com.EvilNotch.Core.TileEntity.MobSpawnerVLogic;
import com.EvilNotch.Core.Util.Java.JavaUtil;
import com.EvilNotch.Core.Util.Line.LineBase;
import com.EvilNotch.Core.Util.Line.LineItemStack;
import com.EvilNotch.Core.Util.Line.LineItemStackBase;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenEnd;

public class EntityUtil {
	public static boolean isEntityCached = false;
	public static List<String> end_ents = new ArrayList<String>();
	public static HashMap<String,Entity> livingEntityCache = new HashMap();//Used for displaying
	public static HashMap<String,String> livingEntity_names = new HashMap<String, String>();//Used for everything else
	
	public static HashMap<String,Entity> nonLivingEntityCache = new HashMap();//Used for displaying
	public static HashMap<String,String> nonLivingEntitiy_names = new HashMap<String, String>();//Used for everything else
	
	public static HashMap<String,Entity> livingBaseEntityCache = new HashMap();//Used for displaying
	public static HashMap<String,String> livingBaseEntitiy_names = new HashMap<String, String>();//Used for everything else
	public static ArrayList<String> forgemobs = new ArrayList();//forge mobs are mobs that have no global id only mod specific useful for mods that only support global ids
	public static ArrayList<String> livingforgemobs = new ArrayList();//forge mobs that are entity living
	public static HashMap<String,Entity> entityCache = new HashMap();//Used for displaying
	public static HashMap<String,String> entity_names = new HashMap();//Used for everything
	public static HashMap<Integer,String> entityIdToName = new HashMap();
	
	public static ArrayList<String> ents_worldneedy = new ArrayList();//List of entities that need the world how greedy?
	public static ArrayList<String> ent_blacklist = new ArrayList();//List of all failed Entities
	public static ArrayList<String> ent_blacklist_commandsender = new ArrayList();//List of all failed Entities
	public static ArrayList<String> ent_blacklist_nbt = new ArrayList();
	public static int idPig = 0;

	
	public static String getEntityString(Entity e)
	{
		return EntityList.getEntityString(e);
	}
	
	/**
	 * Gets Entity's NBT
	 */
	public static NBTTagCompound getEntityNBT(Entity e)
	{
		if(e == null)
			return null;
		NBTTagCompound nbt = new NBTTagCompound();
		e.writeToNBT(nbt);
		return nbt;
	}
	
	/**
	 * Returns the uuidFile or cached file based on uuid boolean
	 * @param player
	 * @param uuid
	 * @return
	 */
	public static File getPlayerFile(EntityPlayer player,boolean uuid)
	{
		if(uuid)
		{
			return new File(TickHandler.dir.getParent(),player.getUniqueID().toString() + ".dat");
		}
		else
			return new File(TickHandler.dir,player.getCommandSenderName() + ".dat");
	}
	
	/**
	 * Update Player file
	 */
	public static void updatePlayerFile(File file, NBTTagCompound nbt) 
	{
		if(!file.exists())
		{
			try {
				file.createNewFile();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		NBTUtil.updateNBTFile(file,nbt);
	}
	
	/**
	 * Gets cached playerdata from name needs instantiated world or will cause exceptions every time
	 * @param display
	 * @return
	 */
	public static NBTTagCompound getPlayerFileNBT(String display) 
	{
		try{
			FileInputStream stream = new FileInputStream(new File(TickHandler.dir,display + ".dat"));
			NBTTagCompound nbt = CompressedStreamTools.readCompressed(stream);
			stream.close();
			return nbt;
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	//Returns true for survival mode unless debug mode is on
	public static boolean isSurvival(EntityPlayer player) 
	{
		if(player == null || player.capabilities.isCreativeMode && !Config.Debug|| !player.capabilities.allowEdit && !Config.Debug)
			return false;
		return true;
	}
	
	//couldn't seem to get method anywhere copied from waila.  This is my only copied code for the mod
	//Choonster helped me getting started but, ultimately except for this I got from figuring out..... :(
	public static LineBase getEntityMod(Entity entity)
	{
	   	String modName = "";
	   	String modid = "";
	   	try{
	   		EntityRegistration er = EntityRegistry.instance().lookupModSpawn(entity.getClass(), true);
	   		ModContainer modC = er.getContainer();
	   		modName = modC.getName();
	   		modid = modC.getModId();
	   	} catch (NullPointerException e){
	   		modName = "Minecraft";
    		modid = "minecraft";
    	}
		return new LineBase("\"" + modid + ":" + modName + "\"");
     }
	
	/**
	 * Old but usefull method of translating an entire list of entity strings no longer used nor supported
	 */
	public static HashMap<String,String> TranslateEntityList(List<String> list) 
	{
		//Iterates Through entire array list
		HashMap<String,String> hash = new HashMap<String,String>();
		for(String s : list)
		{
			String EntityName = TranslateEntity(s);
			 if(!entity_names.containsKey(s))
			 	hash.put(s, EntityName);
			 else
			 	hash.put(s, entity_names.get(s));
		}
		return hash;
	}
	 /**
	  * Scans hashmaps for a specific translation or if none of that happens it translates it dynamially
	  */
	 public static String getCurrentTranslatedEntity(Entity e)
	 {
		 if(e == null || EntityUtil.getEntityString(e) == null)
			 return "";
		 if(!isEntityCached)
			 EntityUtil.cacheEntities(MainCommonMod.world);//Heavy on ram only call if need be
		 
		 String id = EntityUtil.getEntityString(e);
		 
		 if(EntityUtil.entity_names.containsKey(id))
			 return EntityUtil.entity_names.get(id);
   	 
		 String name = EntityUtil.TranslateEntity(e);
   		 for(int i=0;i<5;i++)
   			System.out.println("Error 404 Entity Not Found: " + id + " TranslatedForm: " + name);
   		 entity_names.put(id, name);//Hotfix add it to the list if it doesn't already contain it
   		 entityCache.put(id, e);
   	 
   		return name;
	 }
	 /**
	  * Scans hashmaps for a specific translation or if none of that happens it translates it dynamially
	  */
	 public static String getCurrentTranslatedEntity(String s)
	 {
		 String e = "";
		 
		 //Bug fix for HEE Spammed Text
		 if(!isEntityCached)
			 EntityUtil.cacheEntities(MainCommonMod.world);//Heavy on ram only call if need be
		 
    	 if(EntityUtil.entity_names.containsKey(s))
    		return EntityUtil.entity_names.get(s);
    	 
    	 e = EntityUtil.TranslateEntity(s);
    	 if(e == null)
    		 return null;
    	 if(EntityUtil.createEntityByNameQuietly(s, MainCommonMod.world) != null)
    	 {
    		for(int i=0;i<10;i++)
    			System.out.println("Error 404 Entity Not Found: " + s + " TranslatedForm: " + e);
    		entity_names.put(s, e);//Hotfix add it to the list if it doesn't already contain it
    		entityCache.put(s, EntityUtil.createEntityByNameQuietly(s, MainCommonMod.world));
    	 }
    	 
    	 return e;
	 }
	/**
	 * Translates non living and living entities along with a trying method to always get the proper translation...
	 */
	public static String TranslateEntity(Entity entity)
	{
	   if(entity == null)
		   return null;
	   String s = EntityList.getEntityString(entity);
	   if (s == null)
		   return null;
		
	   String EntityName = null;
	   try{
		   EntityName = StatCollector.translateToLocal("entity." + s + ".name");
	   if(EntityName == null)
		   EntityName = s;
		//Corrects if there is no local translation back to default namming...
	    if (EntityName.equals("entity." + s + ".name"))
	     	EntityName = s;
	   }catch(Throwable t){return null;}
	   
	    //Experimental Code_______________________
	    if(s.equals(EntityName))
	    { 
	    	 String commandsender = EntityUtil.getcommandSenderName(entity);
	    	 if(commandsender == null)
	    		 return EntityName;//If entity fails do this
	    	if(!commandsender.equals("generic"))
	    	{
	    		if(!commandsender.equals(EntityName) && !commandsender.equals("entity." + EntityName + ".name"))
	    			EntityName = commandsender;
	    	}
	    }
	    //End Experimental Code___________________
	    
	    return EntityName;
	}
	 
	/**
	 * Translates non living and living entities along with a trying method to always get the proper translation...
	 * It returns null if it can't find a translation This method is crashproof and null proof 
	 */
	public static String TranslateEntity(String s)
	{
	   if (s == null)
		   return null;
		
	   String EntityName = null;
	   try{
		   EntityName = StatCollector.translateToLocal("entity." + s + ".name");
	   if(EntityName == null)
		   EntityName = s;
		//Corrects if there is no local translation back to default namming...
	    if (EntityName.equals("entity." + s + ".name"))
	     	EntityName = s;
	   }catch(Throwable t){return null;}
	   
	    //Experimental Code_______________________
	    if(s.equals(EntityName))
	    { 
	    	if (EntityUtil.createEntityByNameQuietly(EntityName, MainCommonMod.world) != null)
	    	{
	    		 Entity entity = EntityUtil.createEntityByNameQuietly(EntityName, MainCommonMod.world);
	    		 String commandsender = EntityUtil.getcommandSenderName(entity);
	    		 if(commandsender == null)
	    			 return EntityName;//If entity fails do this
	    		if(!commandsender.equals("generic"))
	    		{
	    			if(!commandsender.equals(EntityName) && !commandsender.equals("entity." + EntityName + ".name"))
	    				EntityName = commandsender;
	    		}
	    	}
	    }
	    //End Experimental Code___________________
	    
	    return EntityName;
	}
	
	public static String getcommandSenderName(Entity entity) 
	{
		try{
			if(entity == null)
				return null;
			return entity.getCommandSenderName();
		}catch(Throwable t){
			blacklistCommandSenderEntity(entity);
		}
		return null;
	}

	public static void blacklistCommandSenderEntity(Entity entity)
	{
		try{
			String str = EntityList.getEntityString(entity);
			if(str == null)
				return;
			if(!ent_blacklist_commandsender.contains(str))
				ent_blacklist_commandsender.add(str);
		}catch(Throwable t){}
	}

	/**GetColor Based on Entity Attributes and or classes
	 * returns EnumChatFormatting format to apply to a string to colorize it
	 * Dynamic Colored Text if not vanilla
	 * Custom Vanilla Support
	 * Custom Configed Support
	 */
	public static String getColoredEntityText(Entity e,boolean isEnd)
	{
		if(e == null)//|| Config.colorText == false)
			return EnumChatFormatting.WHITE + "";
		//Sees if it's and ender mob
		if(EntityUtil.end_ents.contains(EntityList.getEntityString(e)) && EntityList.getEntityString(e) != null || isEnd)
				return EnumChatFormatting.DARK_PURPLE + "";
		
		//Scans Enum Class for modded enum types
		EnumCreatureType[] k = EnumCreatureType.values();
		boolean ismoded = false;
		for(Object a : k)
		{
			String b = a.toString();
			if(!b.equals("ambient") && !b.equals("creature") && !b.equals("creature") && !b.equals("monster") && !b.equals("waterCreature"))
				ismoded = e.isCreatureType((EnumCreatureType) a, false);
		}
			if(ismoded)
				return EnumChatFormatting.STRIKETHROUGH + "" + EnumChatFormatting.BOLD + "";
		
		boolean ambient = e.isCreatureType(EnumCreatureType.ambient, false);
		boolean creature = e.isCreatureType(EnumCreatureType.creature, false);
		boolean water = e.isCreatureType(EnumCreatureType.waterCreature, false);
		if(!water && e instanceof EntityLivingBase)
			water = ((EntityLivingBase)e).canBreatheUnderwater();
		boolean fire = e.isImmuneToFire();
		boolean monster = e.isCreatureType(EnumCreatureType.monster, false);

		//Future Code
		if(e instanceof EntityLivingBase)
		{
			EntityLivingBase en = (EntityLivingBase)e;
			EnumCreatureAttribute enums = en.getCreatureAttribute();
			boolean undead = enums == EnumCreatureAttribute.UNDEAD;
			boolean arthropod = enums == EnumCreatureAttribute.ARTHROPOD;
			boolean undefined = enums == EnumCreatureAttribute.UNDEFINED;
		}
		//Checks Enum Attributes
		if(ambient && !fire && !(e instanceof EntityTameable) && !(e instanceof EntityFlying) && !(e instanceof IRangedAttackMob) && !(e instanceof IBossDisplayData) && !(e instanceof IEntityMultiPart))
			return EnumChatFormatting.DARK_GRAY + "";
		if(creature && !fire && !(e instanceof EntityTameable) && !(e instanceof EntityFlying) && !(e instanceof IRangedAttackMob) && !(e instanceof IBossDisplayData) && !(e instanceof IEntityMultiPart))
			return EnumChatFormatting.LIGHT_PURPLE + "";
		if(water && !fire  && !(e instanceof EntityTameable) && !(e instanceof EntityFlying) && !(e instanceof IRangedAttackMob) && !(e instanceof IBossDisplayData) && !(e instanceof IEntityMultiPart))
			return EnumChatFormatting.AQUA + "";
		if(fire && !(e instanceof EntityTameable) && !(e instanceof EntityFlying) && !(e instanceof IRangedAttackMob) && !(e instanceof IBossDisplayData) && !(e instanceof IEntityMultiPart))
			return EnumChatFormatting.GOLD + "";
		if(monster && !fire  && !(e instanceof EntityTameable) && !(e instanceof EntityFlying) && !(e instanceof IRangedAttackMob) && !(e instanceof IBossDisplayData) && !(e instanceof IEntityMultiPart))
			return EnumChatFormatting.RED + "";
		
		
		//Checks Classes if hasn't Returned
		if(e instanceof EntityTameable)
			return EnumChatFormatting.DARK_BLUE + "";
		
		if(e instanceof IBossDisplayData || e instanceof IEntityMultiPart)
			return EnumChatFormatting.BOLD + "" + EnumChatFormatting.DARK_PURPLE;
		
		if(e instanceof EntityFlying)
			return EnumChatFormatting.BOLD + "" + EnumChatFormatting.YELLOW;
		
		if(e instanceof IRangedAttackMob && e instanceof EntityMob)
			return EnumChatFormatting.DARK_RED + "";	
		
		if(e instanceof EntityAmbientCreature && !(e instanceof EntityAnimal))
			return EnumChatFormatting.DARK_GRAY + "";
		
		if(e instanceof EntityAnimal && !(e instanceof EntityCreature) || e instanceof IAnimals && !(e instanceof EntityCreature))
			return EnumChatFormatting.LIGHT_PURPLE + "";
		
		if (e instanceof EntityAgeable)
			return EnumChatFormatting.LIGHT_PURPLE + "";
		
		if(e instanceof EntityCreature)
			return EnumChatFormatting.GREEN + "";
		
		if(fire)
			return EnumChatFormatting.GOLD + "";
		
		if(e instanceof EntityWaterMob)
			return EnumChatFormatting.AQUA + "";
		
		if(e instanceof EntityMob)
			return EnumChatFormatting.RED + "";
		
		return EnumChatFormatting.WHITE + "";
	}
	
	public static String getEntityId(NBTTagCompound nbt)
	{
		return NBTUtil.getNBT(nbt,"EntityId").getString("EntityId");
	}
	
	//Prints Colored Chat from player
	public static void printChat(EntityPlayer player, EnumChatFormatting c_player, EnumChatFormatting c_msg, String messege)
	{
		player.addChatComponentMessage(new ChatComponentText(c_player + player.getDisplayName() + " " + c_msg + messege));
	}
	//Prints Colored Chat from player needs enum chat formatting in string form. Use method below to acess this supports bold font
	public static void printChat(EntityPlayer player, String c_player, String c_msg, String messege)
	{
		player.addChatComponentMessage(new ChatComponentText(c_player + player.getDisplayName() + " " + c_msg + messege));
	}
	 /**
     * Spawns the creature specified by the egg's type in the location specified by the last three parameters.
     *
     */
    public static Entity spawnCreature(World world, ItemStack stack, double x, double y, double z, boolean spawn)
    {
    	if(stack == null || stack.getTagCompound() == null)
    		return null;
    	NBTTagCompound nbt = (NBTTagCompound) stack.getTagCompound().copy();
    	Entity entity = null;
    	NBTTagList list = new NBTTagList();
    	list = getMountsFromStack(list,nbt);
    	if(spawn) //Reverses nbt outside of spawn method if not actual spawning them from dev testing :(
    		list = NBTUtil.reverseNBTList(list);
    	
    	//Removes null entities if spawning them in the world else let's null entities for blank spawners in for later dev features
    	if(spawn)
    	{
    		for(int i=0;i<list.tagCount();i++)
    		{
    			NBTTagCompound tag = list.getCompoundTagAt(i);
    			if(EntityList.createEntityByName(tag.getString("id"),world) == null)
    				list.removeTag(i);
    		}
    	}
    	Entity entity2 = null;
    	for(int i=0;i<list.tagCount();i++)
    	{
    		if(i == 0)
    		{
    			entity = spawnEntity(world,null,list.getCompoundTagAt(i),x,y,z, spawn,i);
    			entity2 = entity;
    		}
    		else
    			entity2 = spawnEntity(world,entity2,list.getCompoundTagAt(i),x,y,z, spawn,i);
    	}
    	
        return entity;
    }
	public static NBTTagList getMountsFromStack(NBTTagList list,NBTTagCompound nbt)
	{
    	NBTTagCompound nbtbase = new NBTTagCompound();
    	nbtbase.setString("id", getEntityId(nbt));
    	if(nbt.hasKey("EntityNBT",10))
    		nbtbase.setTag("EntityNBT", nbt.getTag("EntityNBT"));
    	
    	list.appendTag(nbtbase);
    	NBTTagList iterate = NBTUtil.getTagList(nbt, "mounting", 10);
    	for(int i=0;i<iterate.tagCount();i++)
    		list.appendTag(iterate.getCompoundTagAt(i));
    	
    	return list;
	}
	public static float getSpawnerItemScaleBasedOnShadow(Entity e,float scale)
	{
		if(Config.spawnerDynamicItemScale)
			return getScaleBasedOnShadow(e,scale);
		if(e.getShadowSize() > 1.5 && Config.NEI_Descale)
            scale = 0.1F;
		return scale;
	}
	
	public static float getSpawnerScaleBasedOnShadow(Entity e,float scale)
	{
		if(Config.spawnerDynamicScale)
			return getScaleBasedOnShadow(e,scale);
		else
			return scale;
	}
	/**
	 * Currently Used for only Item Mob Spawners
	 * @param e
	 * @return
	 */
	public static float getScaleBasedOnShadow(Entity e,float scale)
	{
		if(e == null)
			return 0.0F;
		float f1 = scale;//0.4375F;
        if(e.getShadowSize() > 1.5 && e.getShadowSize() < 5.0)
            f1 = 0.20F;//0.20F
        if(e.getShadowSize() >= 5.0 && e.getShadowSize() < 8.0)
        	f1 = 0.125F;
        if(e.getShadowSize() >= 8.0)
        	f1 = 0.09F;
        
        return f1;
	}
	/**
	 * Supports Only Vanilla Spawner's Base Mob Right Now
	 * @param nbt
	 * @param w
	 * @return
	 */
	public static Entity getEntityFromStack(NBTTagCompound nbt,World w)
	{
		if(nbt == null || nbt.getString("EntityId").equals(""))
		{
			return null;
		}
		if(!EntityUtil.isEntityCached)
			EntityUtil.cacheEntities(w);
		String str = nbt.getString("EntityId");
		if(EntityUtil.ent_blacklist.contains(str))
			return null;//blacklisted corrupted mobs are never supported!
		if(nbt.getTag("SpawnData") == null || EntityUtil.ent_blacklist_nbt.contains(str))
			return MobSpawnerVLogic.getDisplayEnt(w,getEntityFromCache(nbt.getString("EntityId"),w),true);
		else{
			NBTTagCompound tag = (NBTTagCompound) nbt.getTag("SpawnData");
			tag.setString("id", nbt.getString("EntityId"));
			return EntityList.createEntityFromNBT(tag, w);
		}
	}
	/**
	 * Supports Only Vanilla Spawner's Base Mob Right Now
	 * @param nbt
	 * @param w
	 * @return
	 */
	public static NBTTagCompound getEntityNBTFromStack(NBTTagCompound nbt)
	{
		if(nbt == null || nbt.getString("EntityId").equals(""))
			return null;
		
			NBTTagCompound tag = (NBTTagCompound) nbt.getTag("SpawnData");
			tag.setString("id", nbt.getString("EntityId"));
			return tag;
	}
	public static Entity getEntityFromCache(String s,World w)
	{
		if(s == null)
			return null;
		return copyEntity(entityCache.get(s),w);
	}
	public static Entity getEntityFromCache(Entity e,World w)
	{
		if(e == null || EntityList.getEntityString(e) == null)
			return null;
		String s = EntityList.getEntityString(e);
		return copyEntity(entityCache.get(s),w);
	}
	public static void printEntityNBT(Entity ent)
	{
		if(ent == null)
			return;
		NBTTagCompound nbt = new NBTTagCompound();
		ent.writeToNBT(nbt);
		System.out.println(nbt);
	}
	
	/**
     * Spawns In a single Entity and mounts it to the base if it has one
     */
    public static Entity spawnEntity(World w, Entity base, NBTTagCompound nbt, double x, double y, double z, boolean spawn, int index)
    {
    	//nbt = (NBTTagCompound) nbt.copy();
    	String entityid = nbt.getString("id");
    	
    	Entity entity;
    	if(nbt.hasKey("EntityNBT",10) && !ent_blacklist_nbt.contains(entityid))
    	{
    		NBTTagCompound ent = (NBTTagCompound) nbt.getTag("EntityNBT");
    		ent.setString("id", entityid);
    		entity = EntityList.createEntityFromNBT(ent, w);
    	}
    	else{
    		entity = EntityList.createEntityByName(entityid, w);
    		if(entity instanceof EntityLiving)
    			((EntityLiving)entity).onSpawnWithEgg((IEntityLivingData)null);
    	}
		if(!spawn && base != null)
			base.mountEntity(entity); //fix for mounting spawners
		
		if(spawn && entity != null)
		{
			entity.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(w.rand.nextFloat() * 360.0F), 0.0F);
			if(entity instanceof EntityLiving)
			{
				((EntityLiving)entity).rotationYawHead = ((EntityLiving)entity).rotationYaw;
				((EntityLiving)entity).renderYawOffset = ((EntityLiving)entity).rotationYaw;
			}
			if(base != null)
				base.mountEntity(entity);
			
			w.spawnEntityInWorld(entity);
			
			if(index < 5 && entity instanceof EntityLiving)
				((EntityLiving)entity).playLivingSound();
		}
		
    	return entity;
    }
    
    /**
     * Create In a single Entity Used for spawner displaying...
     * Doesn't support, interface,spawning,mounting,sounds,
     */
    public static Entity createBasicEntity(World w, NBTTagCompound nbt)
    {
    	//nbt = (NBTTagCompound) nbt.copy();
    	String entityid = nbt.getString("id");
    	
    	Entity entity;
    	if(nbt.hasKey("EntityNBT",10) && !ent_blacklist_nbt.contains(entityid))
    	{
    		NBTTagCompound ent = (NBTTagCompound) nbt.getTag("EntityNBT");
    		ent.setString("id", entityid);
    		entity = EntityList.createEntityFromNBT(ent, w);
    	}
    	else{
    		entity = EntityList.createEntityByName(entityid, w);
    	}
		
    	return entity;
    }
    
    /**
     * Mounts an array list of entities
     * @param list
     */
    public static void mountEntities(ArrayList<Entity> list)
    {
    	if(list.size() < 2)
    		return;
    	Entity previous = null;
    	for(int i=0;i<list.size();i++)
    	{
    		Entity ent = list.get(i);
    		if(ent == null)
    			continue;
    		if(previous != null)
    			previous.mountEntity(ent);
    		previous = ent;
    	}
    }
    
    public static void cacheEntities(World w) 
    {
    	boolean cache = livingEntityCache.isEmpty();
    	boolean nonLivingCache = nonLivingEntityCache.isEmpty();
    	boolean livingBaseCache = livingBaseEntityCache.isEmpty();
    	
    	boolean allcache = entityCache.isEmpty();
    	boolean allnames = entity_names.isEmpty();
    	boolean allids = entityIdToName.isEmpty();
    	
    	boolean living = EntityUtil.livingEntity_names.isEmpty();
    	boolean forge_mobs = EntityUtil.forgemobs.isEmpty();
    	boolean living_forge_mobs = EntityUtil.livingforgemobs.isEmpty();
    	boolean nonliving = EntityUtil.nonLivingEntitiy_names.isEmpty();
    	boolean base = EntityUtil.livingBaseEntitiy_names.isEmpty();
    	
    	if(cache || allnames || allcache || nonLivingCache || livingBaseCache || living || nonliving || base || allids || forge_mobs || living_forge_mobs)
    	{
    		Iterator<String> it = EntityList.stringToClassMapping.keySet().iterator();
    		while(it.hasNext())
    		{
    			String str = it.next();
    			NBTTagCompound nbt = new NBTTagCompound();
    			nbt.setString("id", str);
    			Class EntityLiving = (Class) EntityList.stringToClassMapping.get(str);
    			boolean isAbstract = Modifier.isAbstract(EntityLiving.getModifiers());
    			boolean isInterface = Modifier.isInterface(EntityLiving.getModifiers());
    			boolean hasDefault = true;
				try {
					Constructor k = EntityLiving.getConstructor(new Class[] {World.class});
				} 
				catch (Throwable e) 
				{
					hasDefault = false;
					if(Config.Debug)
						System.out.println("404 Entity Not Found:" + str);
				}
    			if(isAbstract || isInterface || !hasDefault)
    				continue;
    			Entity ent = EntityUtil.createEntityByNameQuietly(str, w);
    			if(ent == null || EntityUtil.TranslateEntity(str) == null || Util.isLine(Config.ent_blacklistcfg,new LineItemStackBase("\"" + str + "\"") ))
    			{
    				ent_blacklist.add(str);//Entity failed cache it's string id for debugging
    				if(Config.Debug)
    					System.out.println("Entity Failed:" + str);
    				continue;//no need to go on if entity is null
    			}
    			try{
					NBTTagCompound tag = EntityUtil.getEntityNBT(ent);
					ent.readFromNBT(tag);
				}catch(Throwable t){ent_blacklist_nbt.add(str); System.out.println("Warning Entity Failed To Read/Write to NBT Contact Mod Author:" + str);}
    			//Check for on spawn with egg for attributes
    			if(ent instanceof EntityLiving)
    			{
    				try{
    					Entity entity = EntityUtil.createEntityByNameQuietly(str, w);
    				((EntityLiving)entity).onSpawnWithEgg((IEntityLivingData)null);
    				}catch(Throwable t){
    					System.out.println("Entity Failed On Spawn With Egg This is Bad Skipping:" + str);
    					ent_blacklist.add(str);
    					continue;
    				}
    			}
    			//Fix cache for slimes and other mobs
    			if(ent instanceof EntitySlime)
    			{
    				try{
    				nbt.setInteger("Size",Config.slimeInventorySize);
    				EntityUtil.readFromNBTSafely(ent, nbt);
    				}catch(Throwable t){System.out.println("EntitySlime Instance Failed Properly:" + str);}
    			}
    			boolean isForgeMob = FieldAcess.entity_classToIDMapping.get(EntityLiving) == null;
    			if(forge_mobs && isForgeMob)
    				EntityUtil.forgemobs.add(str);
    			if(allcache)
    				EntityUtil.entityCache.put(str, ent);
    			if(allnames)
    				EntityUtil.entity_names.put(str, EntityUtil.TranslateEntity(str));
    			if(allids)
    			{
    				Integer id = FieldAcess.entity_classToIDMapping.get(EntityLiving);
    				if(id != null)
    				{
    					entityIdToName.put(id, str);
    					if(str.equals("Pig"))
    						idPig = id;
    				}
    			}
    			if(ent instanceof EntityLiving)
    			{
    				if(ent instanceof EntityAgeable)
    					((EntityAgeable)ent).setGrowingAge(Config.maxTriesEntityAgeable);
    				if(cache)
    					livingEntityCache.put(str,ent);
    				if(living)
    					livingEntity_names.put(str, EntityUtil.TranslateEntity(str));
    				if(living_forge_mobs && isForgeMob)
    					livingforgemobs.add(str);
    			}
    			if(ent instanceof EntityLivingBase && !(ent instanceof EntityLivingBase))
    			{
    				if(livingBaseCache)
    					EntityUtil.livingBaseEntityCache.put(str, ent);
    				if(nonliving)
    					EntityUtil.livingBaseEntitiy_names.put(str, EntityUtil.TranslateEntity(str));
    			}
    			if(!(ent instanceof EntityLivingBase) && !(ent instanceof EntityLiving))
    			{
    				if(nonLivingCache)
    					EntityUtil.nonLivingEntityCache.put(str, ent);
    				if(nonliving)
    					EntityUtil.nonLivingEntitiy_names.put(str, EntityUtil.TranslateEntity(str));
    			}
    		}
        }
    	organizeHashMaps(nonliving,base,living,allnames,forge_mobs,living_forge_mobs);
    	if(end_ents.isEmpty())
    	{
    		BiomeGenBase[] biome = BiomeGenBase.getBiomeGenArray();
            for (BiomeGenBase bio : biome)
            {
          	  if(bio != null)
          	  if(bio instanceof BiomeGenEnd)
          	  {  
          		  EnumCreatureType[] list = EnumCreatureType.values();
          		  for(EnumCreatureType type : list)
          			  setEntityIdFromBiomeList(bio.getSpawnableList(type),end_ents);
              }
            }
    	}
        isEntityCached = true;
        
    }
    public static void setEntityIdFromBiomeList(List<BiomeGenBase.SpawnListEntry> mr_renchen_dies,List<String> list)
    {
		for (BiomeGenBase.SpawnListEntry b : mr_renchen_dies)
	  	{
	  		if(b.entityClass != null && EntityList.classToStringMapping.containsKey(b.entityClass))
	  			list.add(EntityList.classToStringMapping.get(b.entityClass).toString());
	  	}
    }
    public static Entity createEntityByNameQuietly(String s,World w)
	{
		 Entity entity = null;
		 if("Minecart".equals(s))
			 s = "MinecartRideable";//VanillaFix
	     try
	     {
	         Class oclass = (Class)EntityList.stringToClassMapping.get(s);
	         if (oclass != null)
	             entity = (Entity)oclass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {w});
	     }
	     catch (Throwable exception){return null;}

	    return entity;
	}
    public static Entity createEntityFromNBTQuietly(NBTTagCompound nbt,World w)
    {
    	nbt = (NBTTagCompound) nbt.copy();
        if ("Minecart".equals(nbt.getString("id")))
        {
            switch (nbt.getInteger("Type"))
            {
                case 0:
                	nbt.setString("id", "MinecartRideable");
                    break;
                case 1:
                	nbt.setString("id", "MinecartChest");
                    break;
                case 2:
                	nbt.setString("id", "MinecartFurnace");
            }
            nbt.removeTag("Type");
        }
        Entity entity = createEntityByNameQuietly(nbt.getString("id"), w);
    	if(entity == null)
    		return null;
        try{
            entity.readFromNBT(nbt);
        }
        catch (Throwable e){return null;}
        
        return entity;
    }
    /**
     * Returns a copy of the current entity object from nbt. Doesn't set locations or angles just copies from NBT
     * @param ent
     * @return
     */
	public static Entity copyEntity(Entity ent,World w) 
	{
		if(ent == null || EntityUtil.getEntityString(ent) == null)
			return null;
		String str = EntityUtil.getEntityString(ent);
		if(ent_blacklist.contains(str))
			return null;
		if(ent_blacklist_nbt.contains(str))
			return EntityUtil.createEntityByNameQuietly(str, w);
		
		NBTTagCompound nbt = EntityUtil.getEntityNBT(ent);
		nbt.removeTag("UUIDMost");
		nbt.removeTag("UUIDLeast");
		nbt.setString("id", EntityList.getEntityString(ent));
		return EntityUtil.createEntityFromNBTQuietly(nbt, w);
	}

	/**
	 * Caches a list of entities that need the world they are sooo greedy
	 * @param world
	 */
	public static void cacheWorldNeedyEnts() 
	{
		Iterator<String> it = EntityList.stringToClassMapping.keySet().iterator();
		while(it.hasNext())
		{
			String str = it.next();
			Class EntityLiving = (Class) EntityList.stringToClassMapping.get(str);
			boolean isAbstract = Modifier.isAbstract(EntityLiving.getModifiers());
			boolean isInterface = Modifier.isInterface(EntityLiving.getModifiers());
			boolean hasDefault = true;
			try {
				Constructor k = EntityLiving.getConstructor(new Class[] {World.class});
			} 
			catch (Throwable e) {hasDefault = false;}
			if(isAbstract || isInterface || !hasDefault)
				continue;
			Entity ent = createEntityByNameQuietly(str,null);
			if(ent == null)
			{
				ents_worldneedy.add(str);
				if(Config.Debug)
					System.out.println("Entity Needs World:" + str);
			}
		}
	}
	public static void organizeAllHashMaps()
	{
		organizeHashMaps(true,true,true,true,true,true);
	}
	/**
	 * Re-organize hashmaps don't call until they are cached
	 */
	public static void organizeHashMaps(boolean nonliving,boolean base,boolean living,boolean allnames,boolean forge_mobs,boolean living_forge_mobs) 
	{
    	if(allnames)
    		EntityUtil.entity_names = JavaUtil.sortByValues(entity_names);//Sorts list by values
    	if(nonliving)
    		EntityUtil.nonLivingEntitiy_names = JavaUtil.sortByValues(nonLivingEntitiy_names);
    	if(base)
    		EntityUtil.livingBaseEntitiy_names = JavaUtil.sortByValues(livingBaseEntitiy_names);
    	if(living)
    		EntityUtil.livingEntity_names = JavaUtil.sortByValues(livingEntity_names);
    	if(forge_mobs)
    		 Collections.sort(EntityUtil.forgemobs);
    	if(living_forge_mobs)
    		 Collections.sort(EntityUtil.livingforgemobs);
	}
	/**
	 * Basic method to add item to inventory if fail drop item
	 */
	public static void addItemStackToInventory(EntityPlayer p,ItemStack k, boolean drop) 
	{
		boolean sucess = p.inventory.addItemStackToInventory(k);
		if(!sucess && drop)
			p.dropPlayerItemWithRandomChoice(k, false);
	}
	
	/**
	 * Add item to inventory if is previous stacksize <= 0 edit that item slot used for silkspawners,bucket events
	 * With the additional support if inventory is full drop item
	 */
	public static void addItemStackToInventory(EntityPlayer p,ItemStack k, boolean drop,ItemStack hand) 
	{
		if(hand.stackSize <=0)
		{
			p.inventory.setInventorySlotContents(p.inventory.currentItem, k);
			return;
		}
		boolean sucess = p.inventory.addItemStackToInventory(k);
		if(!sucess && drop)
			p.dropPlayerItemWithRandomChoice(k, false);
	}

	public static NBTTagCompound getdefaultSkeleton() 
	{
		NBTTagCompound e = new NBTTagCompound();
		NBTTagList li = new NBTTagList();
		NBTTagCompound a = new NBTTagCompound();
		a.setInteger("id", 261);
		li.appendTag(a);
		li.appendTag(new NBTTagCompound());
		li.appendTag(new NBTTagCompound());
		li.appendTag(new NBTTagCompound());
		li.appendTag(new NBTTagCompound());
		e.setTag("Equipment", li);
		return e;
		
	}
	public static boolean hasSpawnData(NBTTagCompound tag) 
	{
		return tag.hasKey("SpawnData") ? !((NBTTagCompound)tag.getTag("SpawnData")).hasKey("remove") : false;
	}
	/**
	 * Makes nbt from current compound become the full enitties nbt without overriding the basenbt
	 * WIP Doesn't work yet overrides tags NBTPathAPI not created yet
	 */
	public static void readFromNBTSafely(Entity base, NBTTagCompound basenbt) 
	{
		if(base == null || EntityUtil.getEntityString(base) == null || EntityUtil.ent_blacklist_nbt.contains(EntityUtil.getEntityString(base)) )
			return;
		NBTTagCompound nbt = getEntityNBT(base);
		NBTUtil.copyNBTSafeleyPrioritized(basenbt, nbt);
		base.readFromNBT(basenbt);
	}

	public static boolean entityHasPumkin(Entity ent) 
	{
		if(ent == null || EntityUtil.getEntityString(ent) == null || EntityUtil.ent_blacklist.contains(EntityUtil.getEntityString(ent)) || EntityUtil.ent_blacklist_nbt.contains(EntityUtil.getEntityString(ent)) || !EntityUtil.getEntityNBT(ent).hasKey("Equipment"))
			return false;
		NBTTagList list = EntityUtil.getEntityNBT(ent).getTagList("Equipment", 10);
		NBTTagCompound nbt = list.getCompoundTagAt(4);
		if(nbt == null || !nbt.hasKey("id"))
			return false;
		return nbt.getInteger("id") == 86;
	}

	public static Boolean isEntityOnFire(Entity ent) 
	{
		if(ent == null)
			return false;
		String str = EntityList.getEntityString(ent);
		if(str == null)
			return false;
		if(!EntityUtil.ent_blacklist_nbt.contains(str))
    	{
    		NBTTagCompound nbt = EntityUtil.getEntityNBT(ent);
    		int fire = nbt.getInteger("Fire");
    		if(fire > 0)
    			return true;
    		else
    			return false;
    	}
    	else
    		return false;
	}
	

}
