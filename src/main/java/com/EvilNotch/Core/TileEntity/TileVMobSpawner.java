package com.EvilNotch.Core.TileEntity;

import java.lang.reflect.InvocationTargetException;

import com.EvilNotch.Core.Util.Util.Registry;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

public class TileVMobSpawner extends TileEntityMobSpawner{
	public final MobSpawnerVLogic logic = new MobSpawnerVLogic()
    {
        public void func_98267_a(int p_98267_1_)
        {
            TileVMobSpawner.this.worldObj.addBlockEvent(TileVMobSpawner.this.xCoord, TileVMobSpawner.this.yCoord, TileVMobSpawner.this.zCoord, Blocks.mob_spawner, p_98267_1_, 0);
        }
        public World getSpawnerWorld()
        {
            return TileVMobSpawner.this.worldObj;
        }
        public int getSpawnerX()
        {
            return TileVMobSpawner.this.xCoord;
        }
        public int getSpawnerY()
        {
            return TileVMobSpawner.this.yCoord;
        }
        public int getSpawnerZ()
        {
            return TileVMobSpawner.this.zCoord;
        }
        public void setRandomEntity(MobSpawnerBaseLogic.WeightedRandomMinecart p_98277_1_)
        {
            super.setRandomEntity(p_98277_1_);

            if (this.getSpawnerWorld() != null)
            {
                this.getSpawnerWorld().markBlockForUpdate(TileVMobSpawner.this.xCoord, TileVMobSpawner.this.yCoord, TileVMobSpawner.this.zCoord);
            }
        }
    };
    /**
     * Called when a client event is received with the event number and argument, see World.sendClientEvent
     */
    @Override
    public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_)
    {
        return this.logic.setDelayToMin(p_145842_1_) ? true : false;
    }
    @Override
	public void updateEntity()
	{
		 this.logic.updateSpawner();
	}
    @Override
    public MobSpawnerBaseLogic func_145881_a()
    {
        return this.logic;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound p_145839_1_)
    {
    	this.xCoord = p_145839_1_.getInteger("x");
        this.yCoord = p_145839_1_.getInteger("y");
        this.zCoord = p_145839_1_.getInteger("z");
        
        this.logic.readFromNBT(p_145839_1_);
        //System.out.println("read:" + p_145839_1_);
    }
    @Override
    public void writeToNBT(NBTTagCompound p_145841_1_)
    {
    	 String s = (String)Registry.classToNameMap.get(this.getClass());
         if (s == null)
             throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
         else
         {
             p_145841_1_.setString("id", s);
             p_145841_1_.setInteger("x", this.xCoord);
             p_145841_1_.setInteger("y", this.yCoord);
             p_145841_1_.setInteger("z", this.zCoord);
         }
        this.logic.writeToNBT(p_145841_1_);
       System.out.println("write:" + p_145841_1_);
    }
    /**
     * Overriden in a sign to provide the text.
     */
    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        nbttagcompound.removeTag("SpawnPotentials");
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbttagcompound);
    }

}
