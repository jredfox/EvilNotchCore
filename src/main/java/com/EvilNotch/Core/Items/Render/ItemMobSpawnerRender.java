package com.EvilNotch.Core.Items.Render;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.Api.FieldAcess;
import com.EvilNotch.Core.Api.OpenGlFixer;
import com.EvilNotch.Core.TileEntity.MobSpawnerVLogic;
import com.EvilNotch.Core.Util.Util.EntityUtil;
import com.EvilNotch.Core.Util.Util.Util;

import cpw.mods.fml.common.Loader;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;

public class ItemMobSpawnerRender implements IItemRenderer{
	public Block block;
	public ArrayList<Entity> cache_ents = new ArrayList();
	public ItemMobSpawnerRender(Block b)
	{
		this.block = b;
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {return true;}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper){return true;}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
	    switch(type)
	    {
	        case EQUIPPED:
	        case EQUIPPED_FIRST_PERSON:
	            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	        case INVENTORY:
	        case ENTITY:
	            renderInventoryItem((RenderBlocks)data[0], item, type == net.minecraftforge.client.IItemRenderer.ItemRenderType.INVENTORY);
	        break;
	    }
	}

	public void renderInventoryItem(RenderBlocks render, ItemStack item,boolean inventory) 
	{
		String bossName = BossStatus.bossName;
		int bossTimeout = BossStatus.statusBarTime;
		try
		{
		  Util.changeTexture(TextureMap.locationBlocksTexture);
		  Object[] cache = OpenGlFixer.cacheOpenGlHelper();
		  GL11.glEnable(GL11.GL_ALPHA_TEST);
		  render.renderBlockAsItem(this.block, 0, 1F);
	      NBTTagCompound nbt = item.getTagCompound();
	
		  World world = Minecraft.getMinecraft().theWorld;
		  cacheEnts(item.getTagCompound(),item.getItemDamage());

		  if(this.cache_ents.size() == 0 || !Config.spawnerNEI_Models)
		  	return;

		  float f1 = OpenGlHelper.lastBrightnessX;
		  float f2 = OpenGlHelper.lastBrightnessY;
		  final float brightx = f1;
		  final float brighty = f2;
		  float floating = 0;
		  int index = 0;
		  Entity previous = null;
		  float height = 0.0F;
		  
		 if(Config.spawnerHasMountItemRender)
		 {
		   for(int i=this.cache_ents.size()-1;i>=0;i--)
		   {
		   	 Entity entity = this.cache_ents.get(i);
			 floating = 0;
		   	 if (entity != null && index < Config.spawnerRenderItemMount)
			 {
		   		GL11.glPushMatrix();
				openGlUpdate(cache,brightx,brighty,i,this.cache_ents,inventory);
		   		entity.setWorld(world);
		   		if(index != 0 && previous != null)
		   			height += previous.getMountedYOffset() + entity.getYOffset();
		   		if(EntityUtil.entityHasPumkin(entity) && index + 1 < this.cache_ents.size())
	   				floating = Util.getRenderFrame()/2;
		   		entity.setLocationAndAngles(0, 0, 0, 0.0F, 0.0F);
		   		RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D+height, 0.0D, 0.0F, floating);
		   		index++;
		   		previous = entity;
		   		GL11.glPopMatrix();
		   		
		   		//Spawn Top Mob
				if(index == Config.spawnerRenderItemMount && index < this.cache_ents.size())
				{
					GL11.glPushMatrix();
					openGlUpdate(cache,brightx,brighty,0,this.cache_ents,inventory);
					height += previous.getMountedYOffset() + entity.getYOffset();
					RenderManager.instance.renderEntityWithPosYaw(this.cache_ents.get(0), 0.0D, 0.0D + height, 0.0D, 0.0F, floating);
					GL11.glPopMatrix();
				}
			  }
		   	 
		   }
	     }
		   if(Config.spawnerRenderItemMount == 0 || !Config.spawnerHasMountItemRender)
		   {
		   	  GL11.glPushMatrix();
			  openGlUpdate(cache,brightx,brighty,0,this.cache_ents,inventory);
	    	  RenderManager.instance.renderEntityWithPosYaw(this.cache_ents.get(0), 0.0D, 0.0D+height, 0.0D, 0.0F, floating);
	    	  GL11.glPopMatrix();
		   }
		    /*
		    //Old NEI code use sophisticated updater for OpenGl Code
		    GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		    OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		    GL11.glDisable(GL11.GL_TEXTURE_2D);
		    OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);*/
		   OpenGlFixer.updateOpenGlHelper(cache, false);
	 }
		catch(Exception e)
		{
			boolean isDrawing = FieldAcess.isTessellatorDrawing(Tessellator.instance);
		    if(isDrawing)
		        Tessellator.instance.draw();
		    System.out.println("y u here:" + isDrawing);
//		    e.printStackTrace();
		}
		BossStatus.bossName = bossName;
		BossStatus.statusBarTime = bossTimeout;
	}

	private void openGlUpdate(Object[] cache,float brightx,float brighty,int index,ArrayList<Entity> ents,boolean inventory) 
	{
	  Entity e = this.cache_ents.get(index);
	  float kk = 10.0F;
	  if(cache_ents.size() > 5)
	   	   kk -=2;
	  GL11.glRotatef((float) (Util.getRenderTime()*kk), 0.0F, 1.0F, 0.0F);//Hard Coded because Is Already Slow enough
	           		
	  if(this.cache_ents.size() <= Config.mount_rotatedItemLimit && !Config.alwaysStraightMounts && this.cache_ents.size() >= 2 && !Config.isMountRenderDynamic || this.cache_ents.size() < 2 && !Config.isMountRenderDynamic || Config.disableStraightMounts && !Config.isMountRenderDynamic)
	  GL11.glRotatef(-20F, 1.0F, 0.0F, 0.0F);
	            		
	  //A more dynamic non static implementation however if people want static they could have it
	  if(Config.isMountRenderDynamic)
	  {	
	     float k = -20.0F;
	     if(!Config.disableStraightMounts)
	    	 k+= (this.cache_ents.size()-1 )* 4;
	          				
	     if(k > Config.spawner_minRotationItem || k < -20.0F || Config.alwaysStraightMounts && this.cache_ents.size() >=2)
	       	 k=Config.spawner_minRotationItem;//if is truncated above value or is always straight for mounts do this
	            			
	      GL11.glRotatef(k, 1.0F, 0.0F, 0.0F);
	   }
	      GL11.glTranslatef(0.0F, -0.4F, 0.0F);
	      float f1 = EntityUtil.getSpawnerItemScaleBasedOnShadow(e, Config.mob_render_scaleItem);
	      GL11.glScalef(f1, f1, f1);
	      
	      //Light map fixes, if inventory use full lighting to display mobs so it looks proper
	      if(!inventory)
	    	  OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,brightx,brighty);
	      else
	    	  OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,240.0F,240.0F);
	      OpenGlFixer.updateOpenGlHelper(cache, false);
	}

	public void cacheEnts(NBTTagCompound nbt,int meta) 
	{
		
		this.cache_ents = new ArrayList();//BugFixes
		World w = Minecraft.getMinecraft().theWorld;
		if(nbt == null || !nbt.hasKey("EntityId"))
		{
			if(!Loader.isModLoaded("NotEnoughItems"))
				return;//If NEI isn't loaded don't load support for it
			if(meta == 0)
				meta = EntityUtil.idPig;
			String str = EntityUtil.entityIdToName.get(meta);
			if(str == null)
				return;
			Entity entity = EntityUtil.getEntityFromCache(str, w);
			if(entity != null)
				this.cache_ents.add(entity);
			return;
		}
		nbt = (NBTTagCompound) nbt.copy();
    	NBTTagList list = new NBTTagList();
    	if(nbt.getTagList("mounts", 9) != null)
    	{
    		if(nbt.getTagList("mounts", 10).tagCount() != 0)
    			list = (NBTTagList) nbt.getTagList("mounts", 10).copy();
    	}
    	
    	ArrayList<Entity> ents = new ArrayList();
    	
    	Entity base = EntityUtil.getEntityFromCache(EntityUtil.getEntityFromStack(nbt, w), w);
    	if(base != null)
    	{
    		String strbase = EntityUtil.getEntityString(base);
    		if(EntityUtil.getEntityString(base) != null && !EntityUtil.ent_blacklist.contains(strbase))
    		{
    			if(EntityUtil.hasSpawnData(nbt) && !EntityUtil.ent_blacklist_nbt.contains(strbase))
    				EntityUtil.readFromNBTSafely(base,EntityUtil.getEntityNBTFromStack(nbt));
    			if(Config.spawnerSkeleHasBow && EntityList.getEntityString(base).equals("Skeleton") && EntityUtil.hasSpawnData(nbt))
    				base.readFromNBT(EntityUtil.getdefaultSkeleton());
    			ents.add(base);//Sets display entity
    		}
    	}
		
    	for(int i=0;i<list.tagCount();i++)
    	{
    		NBTTagCompound tag = list.getCompoundTagAt(i);
    		Entity ent = EntityUtil.getEntityFromCache(EntityUtil.createBasicEntity(w, tag, 0, 0, 0), w);
    		if(ent == null || EntityUtil.getEntityString(ent) == null || EntityUtil.ent_blacklist.contains(EntityUtil.getEntityString(ent)))
    			continue;
    		if(tag.hasKey("EntityNBT") && !EntityUtil.ent_blacklist_nbt.contains(tag.getString("id")))
    			EntityUtil.readFromNBTSafely(ent,(NBTTagCompound)tag.getTag("EntityNBT"));
    		if(Config.spawnerSkeleHasBow && EntityList.getEntityString(ent).equals("Skeleton") && EntityUtil.hasSpawnData(tag))
    			ent.readFromNBT(EntityUtil.getdefaultSkeleton());
    		ents.add(ent);
    	}
    	
    	EntityUtil.mountEntities(ents);//Makes them all mounted to each other
    	cache_ents = ents;
	}

}
