package com.EvilNotch.Core.TileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.MainCommonMod;
import com.EvilNotch.Core.Util.Util.EntityUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public abstract class MobSpawnerVLogic extends MobSpawnerBaseLogic
{
	 /** The delay to spawn. */
    public int spawnDelay = 20;
    public String entityTypeName = "";
    /** List of entities to spawn. */
    public List<WeightedRandomVMinecart> potentialEntitySpawns;
    public MobSpawnerBaseLogic.WeightedRandomMinecart randomEntity;
    public double field_98287_c;
    public double field_98284_d;
    public int minSpawnDelay = 200;
    public int maxSpawnDelay = 800;
    /** A counter for spawn tries. */
    public int spawnCount = 4;
    public Entity field_98291_j;
    public int maxNearbyEntities = 6;
    /** The distance from which a player activates the spawner. */
    public int activatingRangeFromPlayer = 16;
    /** The range coefficient for spawning entities around. */
    public int spawnRange = 4;
	public NBTTagList mounts = new NBTTagList();
	@SideOnly(Side.CLIENT)
	public ArrayList<Entity> cache_ents = new ArrayList();//For visual display of mounted spawners
	public boolean cached = false;


	/**
     * For displaying the entity in mobspawner
     * @param ent
     * @return
     */
    public static Entity getDisplayEnt(World w,Entity ent,boolean inventory)
    {
    	if(ent == null || EntityList.getEntityString(ent) == null)
    		return null;
    	ent = EntityUtil.copyEntity(ent,w);//so it doesn't get interfaces used below returns copy in case entity cache is used
    	String str = EntityList.getEntityString(ent);
    	
        if(Config.spawnerNEI_Egg && !inventory)
        {
        	//NEI Logic not cached at all like vanilla wanted
            if(!str.equals("Skeleton") || Config.spawnerSkeleHasBow && str.equals("Skeleton"))
                ((EntityLiving)ent).onSpawnWithEgg((IEntityLivingData)null);
        }
        else
        {
            if(str.equals("Skeleton") && Config.spawnerSkeleHasBow)
                ((EntityLiving)ent).onSpawnWithEgg((IEntityLivingData)null);
        }
        return ent;
    }
    
    public void resetTimer()
    {
        if (this.maxSpawnDelay <= this.minSpawnDelay)
        {
            this.spawnDelay = this.minSpawnDelay;
        }
        else
        {
            int i = this.maxSpawnDelay - this.minSpawnDelay;
            this.spawnDelay = this.minSpawnDelay + this.getSpawnerWorld().rand.nextInt(i);
        }

        if (this.potentialEntitySpawns != null && this.potentialEntitySpawns.size() > 0)
        {
            this.setRandomEntity((WeightedRandomVMinecart)WeightedRandom.getRandomItem(this.getSpawnerWorld().rand, this.potentialEntitySpawns));
            WeightedRandomVMinecart cart = ((WeightedRandomVMinecart)this.getRandomEntity());
    		this.mounts = cart.indexMounts;//Don't care if has specified mounts tag if new index doesn't have it reset it
    		System.out.println("cartMounts:" + cart.indexMounts);
        }
        else{
        	if(this.randomEntity != null)
        	{
        		WeightedRandomVMinecart cart = ((WeightedRandomVMinecart)this.getRandomEntity());
        		if(cart.hasMounts)
        			this.mounts = cart.indexMounts;//Spawn Data Without Multi Index Support
        	}
        }
        this.func_98267_a(1);
        this.cached = false;
    }
	
	/**
     * Gets the entity name that should be spawned.
     */
    public String getEntityNameToSpawn()
    {
        if (this.getRandomEntity() == null)
        {
            if (this.entityTypeName.equals("Minecart"))
            {
                this.entityTypeName = "MinecartRideable";
            }

            return this.entityTypeName;
        }
        else
        {
            return this.getRandomEntity().entityTypeName;
        }
    }

    public void setEntityName(String p_98272_1_)
    {
        this.entityTypeName = p_98272_1_;
    }

    /**
     * Returns true if there's a player close enough to this mob spawner to activate it.
     */
    public boolean isActivated()
    {
        return this.getSpawnerWorld().getClosestPlayer((double)this.getSpawnerX() + 0.5D, (double)this.getSpawnerY() + 0.5D, (double)this.getSpawnerZ() + 0.5D, (double)this.activatingRangeFromPlayer) != null;
    }

    public void updateSpawner()
    {
        if (this.isActivated())
        {
            double d2;

            if (this.getSpawnerWorld().isRemote)
            {
                double d0 = (double)((float)this.getSpawnerX() + this.getSpawnerWorld().rand.nextFloat());
                double d1 = (double)((float)this.getSpawnerY() + this.getSpawnerWorld().rand.nextFloat());
                d2 = (double)((float)this.getSpawnerZ() + this.getSpawnerWorld().rand.nextFloat());
                this.getSpawnerWorld().spawnParticle("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);
                this.getSpawnerWorld().spawnParticle("flame", d0, d1, d2, 0.0D, 0.0D, 0.0D);

                if (this.spawnDelay > 0)
                {
                    --this.spawnDelay;
                }

                this.field_98284_d = this.field_98287_c;
                this.field_98287_c = (this.field_98287_c + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0D;
            }
            else
            {
                if (this.spawnDelay == -1)
                {
                    this.resetTimer();
                }

                if (this.spawnDelay > 0)
                {
                    --this.spawnDelay;
                    return;
                }

                boolean flag = false;

                for (int i = 0; i < this.spawnCount; ++i)
                {
                    Entity entity = EntityList.createEntityByName(this.getEntityNameToSpawn(), this.getSpawnerWorld());

                    if (entity == null)
                        return;

                    int j = this.getSpawnerWorld().getEntitiesWithinAABB(entity.getClass(), AxisAlignedBB.getBoundingBox((double)this.getSpawnerX(), (double)this.getSpawnerY(), (double)this.getSpawnerZ(), (double)(this.getSpawnerX() + 1), (double)(this.getSpawnerY() + 1), (double)(this.getSpawnerZ() + 1)).expand((double)(this.spawnRange * 2), 4.0D, (double)(this.spawnRange * 2))).size();

                    if (j >= this.maxNearbyEntities)
                    {
                        this.resetTimer();
                        return;
                    }

                    d2 = (double)this.getSpawnerX() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * (double)this.spawnRange;
                    double d3 = (double)(this.getSpawnerY() + this.getSpawnerWorld().rand.nextInt(3) - 1);
                    double d4 = (double)this.getSpawnerZ() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * (double)this.spawnRange;
                    EntityLiving entityliving = entity instanceof EntityLiving ? (EntityLiving)entity : null;
                    entity.setLocationAndAngles(d2, d3, d4, this.getSpawnerWorld().rand.nextFloat() * 360.0F, 0.0F);

                    if (entityliving == null || entityliving.getCanSpawnHere())
                    {
                        this.func_98265_a(entity);
                        this.setMounts(entity);//Spawn All mounts if any
                        this.getSpawnerWorld().playAuxSFX(2004, this.getSpawnerX(), this.getSpawnerY(), this.getSpawnerZ(), 0);

                        if (entityliving != null)
                        {
                            entityliving.spawnExplosionParticle();
                        }

                        flag = true;
                    }
                    
                }

                if (flag)
                {
                    this.resetTimer();
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public Entity func_98281_h()
    {
        if (this.field_98291_j == null)
        {
            Entity entity = EntityList.createEntityByName(this.getEntityNameToSpawn(), this.getSpawnerWorld());
            if(this.getRandomEntity() == null || !((WeightedRandomVMinecart)(this.getRandomEntity())).hasProps)
            	 entity = this.getDisplayEnt(this.getSpawnerWorld(), entity,false);
            else
            	entity = this.func_98265_a(entity);
            
            this.field_98291_j = entity;
        }
        return this.field_98291_j;
    }
    @SideOnly(Side.CLIENT)
    public ArrayList<Entity> getEntities()
    {
    	if(!cached)
    		cacheEnts();
    	return cache_ents;
    	
    }
    public void cacheEnts()
    {
    	this.cache_ents = new ArrayList();//BugFixes
    	
    		
    	NBTTagList list = new NBTTagList();
    	if(this.mounts != null)
    	{
    		if(this.mounts.tagCount() != 0)
    			list = (NBTTagList) this.mounts.copy();
    	}
    	
    	ArrayList<Entity> ents = new ArrayList();
    	Entity base = this.func_98281_h();
		ents.add(base);//Sets display entity
		
    	for(int i=0;i<list.tagCount();i++)
    	{
    		NBTTagCompound tag = list.getCompoundTagAt(i);
    		Entity ent = EntityUtil.createBasicEntity(this.getSpawnerWorld(), tag, 0, 0, 0);
    		if(tag.getTag("EntityNBT") == null)
    			ent = this.getDisplayEnt(this.getSpawnerWorld(),ent,false);//Makes it normalized
    		if(ent != null)
    			ents.add(ent);
    	}
    	EntityUtil.mountEntities(ents);//Makes them all mounted to each other
    	cache_ents = ents;
    	cached = true;
    }
    /**
     * Sets the remaining mounts based on the tags. Base is top mob aka the jockey
     */
    public void setMounts(Entity base)
    {
    	if(this.mounts == null || this.mounts.tagCount() < 1 || base == null)
    		return;
    	
    	NBTTagList list = new NBTTagList();
    	for(int i=0;i<this.mounts.tagCount();i++)
    	{
    		NBTTagCompound tag = (NBTTagCompound) this.mounts.getCompoundTagAt(i).copy();
    		if(EntityList.createEntityByName(tag.getString("id"), this.getSpawnerWorld()) != null)
    			list.appendTag(tag);
    	}
    	Entity entity2 = base;
    	for(int i=0;i<list.tagCount();i++)
    		entity2 = EntityUtil.spawnEntity(this.getSpawnerWorld(), entity2, list.getCompoundTagAt(i), base.posX, base.posY, base.posZ, true, i);
    }

    public Entity func_98265_a(Entity p_98265_1_)
    {
        if (this.getRandomEntity() != null)
        {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            p_98265_1_.writeToNBTOptional(nbttagcompound);
            if( ((WeightedRandomVMinecart)(this.getRandomEntity())).hasProps)
            {
            	Iterator iterator = this.getRandomEntity().field_98222_b.func_150296_c().iterator();
            	while (iterator.hasNext())
            	{
            		String s = (String)iterator.next();
            		NBTBase nbtbase = this.getRandomEntity().field_98222_b.getTag(s);
            		nbttagcompound.setTag(s, nbtbase.copy());
            	}
            	p_98265_1_.readFromNBT(nbttagcompound);
            	//System.out.println("Why You HERE: " + this.getRandomEntity().field_98222_b + " HasProps:" + ((WeightedRandomVMinecart)this.getRandomEntity()).hasProps);
            }
            else{
            	((EntityLiving)p_98265_1_).onSpawnWithEgg((IEntityLivingData)null);
            }

            if (p_98265_1_.worldObj != null)
                p_98265_1_.worldObj.spawnEntityInWorld(p_98265_1_);

            NBTTagCompound nbttagcompound2;

            for (Entity entity1 = p_98265_1_; nbttagcompound.hasKey("Riding", 10); nbttagcompound = nbttagcompound2)
            {
                nbttagcompound2 = nbttagcompound.getCompoundTag("Riding");
                Entity entity2 = EntityList.createEntityByName(nbttagcompound2.getString("id"), p_98265_1_.worldObj);

                if (entity2 != null)
                {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    entity2.writeToNBTOptional(nbttagcompound1);
                    Iterator iterator1 = nbttagcompound2.func_150296_c().iterator();

                    while (iterator1.hasNext())
                    {
                        String s1 = (String)iterator1.next();
                        NBTBase nbtbase1 = nbttagcompound2.getTag(s1);
                        nbttagcompound1.setTag(s1, nbtbase1.copy());
                    }

                    entity2.readFromNBT(nbttagcompound1);
                    entity2.setLocationAndAngles(entity1.posX, entity1.posY, entity1.posZ, entity1.rotationYaw, entity1.rotationPitch);

                    if (p_98265_1_.worldObj != null)
                    {
                        p_98265_1_.worldObj.spawnEntityInWorld(entity2);
                    }

                    entity1.mountEntity(entity2);
                }

                entity1 = entity2;
            }
        }
        else if (p_98265_1_ instanceof EntityLivingBase && p_98265_1_.worldObj != null)
        {
            ((EntityLiving)p_98265_1_).onSpawnWithEgg((IEntityLivingData)null);
            this.getSpawnerWorld().spawnEntityInWorld(p_98265_1_);
        }

        return p_98265_1_;
    }

    public void readFromNBT(NBTTagCompound p_98270_1_)
    {
        this.entityTypeName = p_98270_1_.getString("EntityId");
        this.spawnDelay = p_98270_1_.getShort("Delay");

        if (p_98270_1_.hasKey("SpawnPotentials", 9))
        {
            this.potentialEntitySpawns = new ArrayList();
            NBTTagList nbttaglist = p_98270_1_.getTagList("SpawnPotentials", 10);
            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
            	NBTTagCompound nbt = nbttaglist.getCompoundTagAt(i);
            	if(!nbt.hasKey("Properties"))
            	{
            		nbt.setTag("Properties", new NBTTagCompound());
            		NBTTagCompound p = (NBTTagCompound) nbt.getTag("Properties");
            		p.setBoolean("remove", true);
            	}
                this.potentialEntitySpawns.add(new WeightedRandomVMinecart(nbt));
            }
        }
        else
        {
            this.potentialEntitySpawns = null;
        }

        if (p_98270_1_.hasKey("SpawnData", 10) )
        {
            this.setRandomEntity((WeightedRandomVMinecart)new WeightedRandomVMinecart(p_98270_1_.getCompoundTag("SpawnData"), this.entityTypeName));
        }
        else
        {
            this.setRandomEntity((MobSpawnerBaseLogic.WeightedRandomMinecart)null);
        }

        if (p_98270_1_.hasKey("MinSpawnDelay", 99))
        {
            this.minSpawnDelay = p_98270_1_.getShort("MinSpawnDelay");
            this.maxSpawnDelay = p_98270_1_.getShort("MaxSpawnDelay");
            this.spawnCount = p_98270_1_.getShort("SpawnCount");
        }

        if (p_98270_1_.hasKey("MaxNearbyEntities", 99))
        {
            this.maxNearbyEntities = p_98270_1_.getShort("MaxNearbyEntities");
            this.activatingRangeFromPlayer = p_98270_1_.getShort("RequiredPlayerRange");
        }

        if (p_98270_1_.hasKey("SpawnRange", 99))
        {
            this.spawnRange = p_98270_1_.getShort("SpawnRange");
        }

        if (this.getSpawnerWorld() != null && this.getSpawnerWorld().isRemote)
        {
            this.field_98291_j = null;
        }
        if(p_98270_1_.hasKey("mounts",9))
    		this.mounts = p_98270_1_.getTagList("mounts", 10);
        this.cached = false;
    }

    public void writeToNBT(NBTTagCompound p_98280_1_)
    {
        p_98280_1_.setString("EntityId", this.getEntityNameToSpawn());
        p_98280_1_.setShort("Delay", (short)this.spawnDelay);
        p_98280_1_.setShort("MinSpawnDelay", (short)this.minSpawnDelay);
        p_98280_1_.setShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        p_98280_1_.setShort("SpawnCount", (short)this.spawnCount);
        p_98280_1_.setShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
        p_98280_1_.setShort("RequiredPlayerRange", (short)this.activatingRangeFromPlayer);
        p_98280_1_.setShort("SpawnRange", (short)this.spawnRange);

        if (this.getRandomEntity() != null && this.getRandomEntity().field_98222_b != null)
        {
            p_98280_1_.setTag("SpawnData", this.getRandomEntity().field_98222_b.copy());
        }

        if (this.getRandomEntity() != null || this.potentialEntitySpawns != null && this.potentialEntitySpawns.size() > 0)
        {
            NBTTagList nbttaglist = new NBTTagList();

            if (this.potentialEntitySpawns != null && this.potentialEntitySpawns.size() > 0)
            {
                Iterator iterator = this.potentialEntitySpawns.iterator();

                while (iterator.hasNext())
                {
                    MobSpawnerBaseLogic.WeightedRandomMinecart weightedrandomminecart = (MobSpawnerBaseLogic.WeightedRandomMinecart)iterator.next();
                    nbttaglist.appendTag(weightedrandomminecart.func_98220_a());
                }
            }
            else
            {
                nbttaglist.appendTag(this.getRandomEntity().func_98220_a());
            }

            p_98280_1_.setTag("SpawnPotentials", nbttaglist);
        }
        if(this.mounts != null)
        	p_98280_1_.setTag("mounts", this.mounts);
//        this.cached = false; no need write to nbt simply saves the tag so only read from nbt do I say redo the cache
    }

    /**
     * Sets the delay to minDelay if parameter given is 1, else return false.
     */
    public boolean setDelayToMin(int p_98268_1_)
    {
        if (p_98268_1_ == 1 && this.getSpawnerWorld().isRemote)
        {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        }
        else
        {
            return false;
        }
    }

    public MobSpawnerBaseLogic.WeightedRandomMinecart getRandomEntity()
    {
        return this.randomEntity;
    }

    public void setRandomEntity(MobSpawnerBaseLogic.WeightedRandomMinecart p_98277_1_)
    {
        this.randomEntity = p_98277_1_;
    }

    public abstract void func_98267_a(int p_98267_1_);

    public abstract World getSpawnerWorld();

    public abstract int getSpawnerX();

    public abstract int getSpawnerY();

    public abstract int getSpawnerZ();
    public class WeightedRandomVMinecart extends WeightedRandomMinecart
    {
    	public boolean hasProps = true;
    	public boolean hasMounts = true;
    	public NBTTagList indexMounts = new NBTTagList();

		public WeightedRandomVMinecart(NBTTagCompound nbt) 
		{
			super(nbt);
			if(nbt.getCompoundTag("Properties").hasKey("remove"))
				hasProps = false;
			
			if(nbt.getCompoundTag("Properties").hasKey("mounts",9))
				this.indexMounts = nbt.getCompoundTag("Properties").getTagList("mounts", 10);
			if(nbt.hasKey("mounts",9))
				this.indexMounts = nbt.getTagList("mounts", 10);
			else
				hasMounts = false;
		}
		public WeightedRandomVMinecart(NBTTagCompound p_i1946_2_, String p_i1946_3_)
        {
			super(p_i1946_2_,p_i1946_3_);
			if(p_i1946_2_.hasKey("remove"))
				hasProps = false;
			
			if(p_i1946_2_.getCompoundTag("Properties").hasKey("mounts",9))
				this.indexMounts = p_i1946_2_.getCompoundTag("Properties").getTagList("mounts", 10);
			
			if(p_i1946_2_.hasKey("mounts",9))
				this.indexMounts = p_i1946_2_.getTagList("mounts", 10);
			else
				hasMounts = false;
        }
    	
    }
   

   
}