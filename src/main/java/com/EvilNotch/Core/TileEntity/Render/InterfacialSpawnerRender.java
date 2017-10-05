package com.EvilNotch.Core.TileEntity.Render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.Api.OpenGlFixer;
import com.EvilNotch.Core.Interfaces.BasicSpawnerEntry;
import com.EvilNotch.Core.Interfaces.IMobSpawnerRender;
import com.EvilNotch.Core.Interfaces.InterfaceRegistry;
import com.EvilNotch.Core.Load.LoadRegister;
import com.EvilNotch.Core.TileEntity.MobSpawnerVLogic;
import com.EvilNotch.Core.TileEntity.TileVMobSpawner;
import com.EvilNotch.Core.Util.Java.JavaUtil;
import com.EvilNotch.Core.Util.Java.MapEntry;
import com.EvilNotch.Core.Util.Util.EntityUtil;
import com.EvilNotch.Core.Util.Util.TileEntityUtil;
import com.EvilNotch.Core.Util.Util.Util;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityMobSpawnerRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

public class InterfacialSpawnerRender extends TileEntitySpecialRenderer{
	
	public static HashMap<TileEntity,BasicSpawnerEntry> tiles = new HashMap();

	public static void renderSpawner(TileEntity tile, double x, double y, double z, float yaw)
	{
		World w = tile.getWorldObj();
		Block b = w.getBlock(tile.xCoord,tile.yCoord,tile.zCoord);
		int meta = w.getBlockMetadata(tile.xCoord,tile.yCoord,tile.zCoord);
		
		IMobSpawnerRender spawner = InterfaceRegistry.getRenderForBlock(b, meta);
		if(spawner == null)
			return;
		NBTTagCompound nbt = TileEntityUtil.getTileNBT(tile);
		
		
		NBTTagList taglist = spawner.getAllEntitiesNBT(nbt, w, false, meta);
		ArrayList<Entity> lients = spawner.getAllEntities(nbt, w, false, meta);
		BasicSpawnerEntry map = tiles.get(tile);
		int delay = spawner.getDelay(nbt);
		//Cache initial map
		if(map == null)
		{
			tiles.put(tile,new BasicSpawnerEntry(taglist,lients,delay,spawner));
			map = tiles.get(tile);
			if(map == null)
				return;
		}
		//Update Map
		NBTTagList list = map.list;
		if(!taglist.equals(list))
		{
			tiles.put(tile,new BasicSpawnerEntry(taglist,lients,delay,spawner));
			map = tiles.get(tile);
		}
		ArrayList<Entity> ents = map.ents;
	 	ArrayList<Boolean> flagfire = spawner.getFlagFire(ents);
	 	EntityUtil.mountEntities(ents);
	 	//Make Them animated
	 	for(Entity ent : ents)
	 	{
	 		if(Config.NEI_WorldSpawner)
				ent.worldObj.spawnEntityInWorld(ent);
	 	}
	 	boolean isItem = false;
	 	float height = 0;
	  	int index = 0;
	   	Entity previous = null;
	   	float brightx = OpenGlHelper.lastBrightnessX;
	   	float brighty = OpenGlHelper.lastBrightnessY;
	   	Object[] cache = OpenGlFixer.cacheOpenGlHelper();
       	if(Config.spawnerHasMountRender && spawner.hasMountRender(isItem))
       	{
	       	for(int i=ents.size()-1;i>=0;i--)
	    	{
	       		Entity entity = ents.get(i);
	       		
	       		if (entity != null && index < Config.spawnerRenderMount)
	       		{
	       			GL11.glPushMatrix();
		   	        GL11.glTranslatef((float)x + 0.5F, (float)y, (float)z + 0.5F);
		       		openGlUpdate(cache,spawner,map,spawner.hasDelayTag(nbt),i,ents,yaw);
		       		
	       			entity.setWorld(w);
	       			if(index != 0 && previous != null)
	       				height += previous.getMountedYOffset() + entity.getYOffset();
	       			
	       			entity.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
	       		
	       			if(flagfire.get(i) && Config.NEI_WorldSpawner)
	       				entity.setFire(1);
	       			RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D + height, 0.0D, 0.0F, yaw);
	       			index++;
	       			previous = entity;
	       			GL11.glPopMatrix();
	       			
	       			//Spawn Top Mob
	       			if(index == Config.spawnerRenderMount && index < ents.size())
	       			{
	       				if(ents.get(0) == null)
	       		    		return;
	       				GL11.glPushMatrix();
	       				GL11.glTranslatef((float)x + 0.5F, (float)y, (float)z + 0.5F);
	       				openGlUpdate(cache,spawner,map,spawner.hasDelayTag(nbt),0,ents,yaw);
	       				if(flagfire.get(0) && Config.NEI_WorldSpawner)
		       				ents.get(0).setFire(1);
	       				height += previous.getMountedYOffset() + entity.getYOffset();
	       				RenderManager.instance.renderEntityWithPosYaw(ents.get(0), 0.0D, 0.0D + height, 0.0D, 0.0F, yaw);
	       				GL11.glPopMatrix();
	       			}
	       		}
	    	}
       	}
	   	//If no mounts do this
	    if(Config.spawnerRenderMount == 0 || !Config.spawnerHasMountRender || !spawner.hasMountRender(isItem))
	    {
	    	if(ents.get(0) == null)
	    		return;
	    	GL11.glPushMatrix();
		    GL11.glTranslatef((float)x + 0.5F, (float)y, (float)z + 0.5F);
			openGlUpdate(cache,spawner,map,spawner.hasDelayTag(nbt),0,ents,yaw);
			if(flagfire.get(0) && Config.NEI_WorldSpawner)
   				ents.get(0).setFire(1);
			RenderManager.instance.renderEntityWithPosYaw(ents.get(0), 0.0D, 0.0D + height, 0.0D, 0.0F, yaw);
			GL11.glPopMatrix();
	    }
	    
	    try{
			for(int i=0;i<ents.size();i++)
			{
				Entity ent = ents.get(i);
				ent.mountEntity((Entity)null);
			}
		}catch(Throwable t){}
	 }

	    private static void openGlUpdate(Object[] cache,IMobSpawnerRender spawner,BasicSpawnerEntry entry,boolean hasDelay,int index,ArrayList<Entity> ents,float yaw) 
	    {
	    	float f1 = EntityUtil.getSpawnerScaleBasedOnShadow(ents.get(index), Config.mob_render_scale);//0.4375F
    		GL11.glTranslatef(0.0F, 0.4F, 0.0F);
    		
    		boolean isItem = false;
    		if(ents.size() > 1 && Config.spawnerRenderMount != 0 && Config.spawnerHasMountRender && spawner.hasMountRender(isItem))
    		{
    			float k = 6.0F-(ents.size()-2);
    			if(k < 1.0F)
    				k = 1.0F;
    			if(!hasDelay)
    				GL11.glRotatef((float) (Util.getRenderTime()*10), 0.0F, 1.0F, 0.0F);//Hard Coded because Is Already Slow enough
    			else
    				GL11.glRotatef((float)(entry.field_98284_d + (entry.field_98287_c - entry.field_98284_d) * (double)yaw) * k, 0.0F, 1.0F, 0.0F);
    		}
    		else{
    			if(!hasDelay)
    				GL11.glRotatef((float) (Util.getRenderTime()*10), 0.0F, 1.0F, 0.0F);//Hard Coded because Is Already Slow enough
    			else
    				GL11.glRotatef((float)(entry.field_98284_d + (entry.field_98287_c - entry.field_98284_d) * (double)yaw) * 10.0F, 0.0F, 1.0F, 0.0F);
    		}
    		
    		if(ents.size() <= Config.mount_rotatedLimit && !Config.alwaysStraightMounts && ents.size() >= 2 && !Config.isMountRenderDynamic || ents.size() < 2 && !Config.isMountRenderDynamic || Config.disableStraightMounts && !Config.isMountRenderDynamic || !spawner.hasMountRender(isItem) || !Config.spawnerHasMountRender)
    			GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);//Rotates mob on axis not dynamic
    		
    		//A more dynamic non static implementation however if people want static they could have it
    		if(Config.isMountRenderDynamic && spawner.hasMountRender(isItem) && Config.spawnerHasMountRender)
    		{
    			float k = -30.0F;
    			if(!Config.disableStraightMounts)
    				k+= (ents.size()-1 )* 5;
   				
    			if(k > Config.spawner_minRotation || k < -30.0F || Config.alwaysStraightMounts && ents.size() >=2)
    				k=Config.spawner_minRotation;//if is truncated above value or is always straight for mounts do this

    			GL11.glRotatef(k, 1.0F, 0.0F, 0.0F);//Rotates mob on axis
    		}

    		GL11.glTranslatef(0.0F, -0.4F, 0.0F);

    		GL11.glScalef(f1, f1, f1);
    		
    		//OpenGL reset code to do the light mapping and do disable spider render
    		/*GL11.glDepthMask(true);
    		GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);*/
            //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,brightx,brighty);//Old Stupid code
    		OpenGlFixer.updateOpenGlHelper(cache, true);
	  }

		public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float yaw)
	    {
			renderSpawner(tile, x, y, z, yaw);
	    }
}
