package com.EvilNotch.Core.Util;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

//Code copied from perfect spawn no credit from me here
public class PerfectSpawnTeleporter extends Teleporter
{

	public PerfectSpawnTeleporter(WorldServer worldServer)
	{
		super(worldServer);
	}
	
	public void placeInPortal(Entity entityIn, double x, double y, double z, float yaw)
    {
		placeInExistingPortal(entityIn,x,y,z, yaw);
    }
	
	public boolean placeInExistingPortal(Entity par1Entity, double par2, double par4, double par6, float par8)
    {
		return false;
    }
	
	public boolean makePortal(Entity par1Entity)
    {
		return false;
    }
	
	public void removeStalePortalLocations(long par1)
    {
		
    }
}