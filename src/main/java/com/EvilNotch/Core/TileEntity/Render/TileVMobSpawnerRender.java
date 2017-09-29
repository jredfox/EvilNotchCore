package com.EvilNotch.Core.TileEntity.Render;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.Api.OpenGlFixer;
import com.EvilNotch.Core.TileEntity.MobSpawnerVLogic;
import com.EvilNotch.Core.TileEntity.TileVMobSpawner;
import com.EvilNotch.Core.Util.Util.EntityUtil;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityMobSpawnerRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;

public class TileVMobSpawnerRender extends TileEntitySpecialRenderer{
	
    public void renderTileEntityAt(TileVMobSpawner p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_, float p_147500_8_)
	{
	   func_147517_a((MobSpawnerVLogic)p_147500_1_.func_145881_a(), p_147500_2_, p_147500_4_, p_147500_6_, p_147500_8_);
	}

	public static void func_147517_a(MobSpawnerVLogic logic, double p_147517_1_, double p_147517_3_, double p_147517_5_, float p_147517_7_)
	{
	 	ArrayList<Entity> ents = logic.getEntities();
	   	float height = 0;
	  	int index = 0;
	   	Entity previous = null;
	   	float brightx = OpenGlHelper.lastBrightnessX;
	   	float brighty = OpenGlHelper.lastBrightnessY;
	   	Object[] cache = OpenGlFixer.cacheOpenGlHelper();
       	if(Config.spawnerHasMountRender)
       	{
	       	for(int i=ents.size()-1;i>=0;i--)
	    	{
	       		Entity entity = ents.get(i);
	       		
	       		if (entity != null && index < Config.spawnerRenderMount)
	       		{
	       			GL11.glPushMatrix();
		   	        GL11.glTranslatef((float)p_147517_1_ + 0.5F, (float)p_147517_3_, (float)p_147517_5_ + 0.5F);
		       		openGlUpdate(cache,i,ents,logic,p_147517_7_);
		       		
	       			entity.setWorld(logic.getSpawnerWorld());
	       			if(index != 0 && previous != null)
	       				height += previous.getMountedYOffset() + entity.getYOffset();
	       			
	       			entity.setLocationAndAngles(p_147517_1_, p_147517_3_, p_147517_5_, 0.0F, 0.0F);
	       			RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D + height, 0.0D, 0.0F, p_147517_7_);
	       			index++;
	       			previous = entity;
	       			GL11.glPopMatrix();
	       			
	       			//Spawn Top Mob
	       			if(index == Config.spawnerRenderMount && index < ents.size())
	       			{
	       				GL11.glPushMatrix();
	       				GL11.glTranslatef((float)p_147517_1_ + 0.5F, (float)p_147517_3_, (float)p_147517_5_ + 0.5F);
	       				openGlUpdate(cache,0,ents,logic,p_147517_7_);
	       				height += previous.getMountedYOffset() + entity.getYOffset();
	       				RenderManager.instance.renderEntityWithPosYaw(ents.get(0), 0.0D, 0.0D + height, 0.0D, 0.0F, p_147517_7_);
	       				GL11.glPopMatrix();
	       			}
	       		}
	    	}
       	}
	   	//If no mounts do this
	    if(Config.spawnerRenderMount == 0 || !Config.spawnerHasMountRender)
	    {
	    	GL11.glPushMatrix();
		    GL11.glTranslatef((float)p_147517_1_ + 0.5F, (float)p_147517_3_, (float)p_147517_5_ + 0.5F);
			openGlUpdate(cache,0,ents,logic,p_147517_7_);
			RenderManager.instance.renderEntityWithPosYaw(ents.get(0), 0.0D, 0.0D + height, 0.0D, 0.0F, p_147517_7_);
			GL11.glPopMatrix();
	    }
	 }

	    private static void openGlUpdate(Object[] cache,int index,ArrayList<Entity> ents,MobSpawnerVLogic logic,float p_147517_7_) 
	    {
	    	float f1 = EntityUtil.getSpawnerScaleBasedOnShadow(ents.get(index), Config.mob_render_scale);//0.4375F
    		GL11.glTranslatef(0.0F, 0.4F, 0.0F);
    		if(ents.size() > 1 && Config.spawnerRenderMount != 0 && Config.spawnerHasMountRender)
    		{
    			float k = 6.0F-(ents.size()-2);
    			//System.out.println("kk:" + k);
    			if(k < 1.0F)
    				k = 1.0F;
    			GL11.glRotatef((float)(logic.field_98284_d + (logic.field_98287_c - logic.field_98284_d) * (double)p_147517_7_) * k, 0.0F, 1.0F, 0.0F);
    		}
    		else
    			GL11.glRotatef((float)(logic.field_98284_d + (logic.field_98287_c - logic.field_98284_d) * (double)p_147517_7_) * 10.0F, 0.0F, 1.0F, 0.0F);
    		
    		if(ents.size() <= Config.mount_rotatedLimit && !Config.alwaysStraightMounts && ents.size() >= 2 && !Config.isMountRenderDynamic || ents.size() < 2 && !Config.isMountRenderDynamic || Config.disableStraightMounts && !Config.isMountRenderDynamic)
    			GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);//Rotates mob on axis not dynamic
    		
    		//A more dynamic non static implementation however if people want static they could have it
    		if(Config.isMountRenderDynamic)
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

		public void renderTileEntityAt(TileEntity p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_, float p_147500_8_)
	    {
	        this.renderTileEntityAt((TileVMobSpawner)p_147500_1_, p_147500_2_, p_147500_4_, p_147500_6_, p_147500_8_);
	    }
}
