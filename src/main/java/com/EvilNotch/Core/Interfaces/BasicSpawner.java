package com.EvilNotch.Core.Interfaces;

import java.util.ArrayList;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.TileEntity.MobSpawnerVLogic;
import com.EvilNotch.Core.Util.Util.EntityUtil;
import com.EvilNotch.Core.Util.Util.NBTUtil;
import com.EvilNotch.Core.Util.Util.TileEntityUtil;

import cpw.mods.fml.common.Loader;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BasicSpawner implements IMobSpawnerRender{
	
	public Block block;
	public int meta;
	public boolean isMetaSpecific;
	
	public BasicSpawner(Block b)
	{
		this.block = b;
		this.meta = 0;
		isMetaSpecific = false;
	}
	
	public BasicSpawner(Block b,int meta)
	{
		this.block = b;
		this.meta = meta;
		isMetaSpecific = meta != -1;
	}
	public BasicSpawner(Block b,int meta,boolean metaSpecfic)
	{
		this.block = b;
		this.meta = meta;
		isMetaSpecific = metaSpecfic;
	}

	@Override
	public NBTTagCompound getEntityIdNBT(NBTTagCompound nbt,World w,boolean isItem,int meta) 
	{
		if(nbt == null || !nbt.hasKey("EntityId"))
			return getNEISupportNBT(meta,w);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("id", nbt.getString("EntityId"));
		if(EntityUtil.hasSpawnData(nbt))
			tag.setTag("EntityNBT", nbt.getTag("SpawnData"));
		return (NBTTagCompound) tag.copy();
	}
	public NBTTagCompound getNEISupportNBT(int meta,World w) 
	{
		if(!Loader.isModLoaded("NotEnoughItems"))
			return new NBTTagCompound();
		NBTTagCompound nbt = new NBTTagCompound();
		if(meta == 0)
			meta = EntityUtil.idPig;
		String str = EntityUtil.entityIdToName.get(meta);
		if(str == null)
			 return nbt;
		Entity entity = EntityUtil.getEntityFromCache(str, w);
		if(entity != null)
		{
			nbt.setString("id", str);
			return nbt;
		}
		
		return nbt;
	}

	@Override
	public NBTTagList getMountsNBT(NBTTagCompound nbt,World w,boolean isItem,int meta)
	{
		return NBTUtil.getTagList(nbt,"mounts", 10);
	}
	@Override
	public NBTTagList getAllEntitiesNBT(NBTTagCompound nbt,World w,boolean isItem,int meta)
	{
		NBTTagCompound base = getEntityIdNBT(nbt,w,isItem,meta);
		NBTTagList mounts = getMountsNBT(nbt,w,isItem,meta);
		NBTTagList list = new NBTTagList();
		if(!base.hasNoTags())
			list.appendTag(base);
		for(int i=0;i<mounts.tagCount();i++)
			list.appendTag(mounts.getCompoundTagAt(i));
		
		return list;
	}
	@Override
	public ArrayList<Entity> getAllEntities(NBTTagCompound nbt,World w,boolean isItem,int meta)
	{
		if(nbt == null)
			return getNEISupport(nbt,w,isItem,meta);
		
		return cacheEntities(getAllEntitiesNBT(nbt,w,isItem,meta),w,isItem);
	}
	public ArrayList<Entity> getNEISupport(NBTTagCompound nbt, World w, boolean isItem, int meta) 
	{
		ArrayList<Entity> ents = new ArrayList();
		if(!Loader.isModLoaded("NotEnoughItems"))
			return ents;//If NEI isn't loaded don't load support for it
		if(meta == 0)
			meta = EntityUtil.idPig;
		String str = EntityUtil.entityIdToName.get(meta);
		if(str == null)
			 return ents;
		Entity entity = EntityUtil.getEntityFromCache(str, w);
		if(entity != null)
			ents.add(entity);
		return ents;
	}

	@Override
	public Block getBlock() {
		return this.block;
	}
	@Override
	public boolean hasMountRender(boolean item) {
		return item ? Config.spawnerHasMountItemRender : Config.spawnerHasMountRender;
	}
	@Override
	public boolean hasNEIItemRender() {
		return Config.spawnerNEI_Models;
	}
	public ArrayList<Entity> cacheEntities(NBTTagList list,World w,boolean isItem) 
	{
		ArrayList<Entity> ents = new ArrayList();
		for(int i=0;i<list.tagCount();i++)
		{
			NBTTagCompound nbt = list.getCompoundTagAt(i);
			Entity ent = EntityUtil.createBasicEntity(w, nbt);
			boolean hasNoData = nbt.getTag("EntityNBT") == null;
			
			if(ent == null || EntityUtil.getEntityString(ent) == null || EntityUtil.ent_blacklist.contains(EntityUtil.getEntityString(ent)))
    			continue;
			
			if(hasNoData)
			{
				ent = EntityUtil.getEntityFromCache(ent, w);
				ent = MobSpawnerVLogic.getDisplayEnt(w, ent, isItem);//display
			}
			
			if(Config.spawnerSkeleHasBow && EntityList.getEntityString(ent).equals("Skeleton") && hasNoData)
    			ent.readFromNBT(EntityUtil.getdefaultSkeleton());
			
			ents.add(ent);
		}
		EntityUtil.mountEntities(ents);
		return ents;
	}
	/**
	 * gives an array index view of if the entity is on fire needs to be called before entity is spawned into world for it to work
	 */
	@Override
	public ArrayList<Boolean> getFlagFire(ArrayList<Entity> list) {
		ArrayList<Boolean> flags = new ArrayList();
		for(Entity e : list)
		{
			if(EntityUtil.isEntityOnFire(e))
				flags.add(true);
			else
				flags.add(false);
		}
		return flags;
	}

	@Override
	public boolean hasDelayTag(NBTTagCompound nbt) {
		return true;
	}

	@Override
	public int getDelay(NBTTagCompound nbt) {
		return nbt.getInteger("Delay");
	}

	@Override
	public int getMeta() {
		return this.meta;
	}

	@Override
	public boolean isMetaSpecific() {
		return this.isMetaSpecific;
	}


}
