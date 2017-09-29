package com.EvilNotch.Core.Events;

import java.awt.Event;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.EvilNotch.Core.Config;
import com.EvilNotch.Core.MainCommonMod;
import com.EvilNotch.Core.Api.BlockApi;
import com.EvilNotch.Core.Api.FieldAcess;
import com.EvilNotch.Core.Api.MCPMappings;
import com.EvilNotch.Core.Items.ForgeItemBucket;
import com.EvilNotch.Core.Load.LoadRegister;
import com.EvilNotch.Core.TileEntity.TileVFurnace;
import com.EvilNotch.Core.Util.Java.JavaUtil;
import com.EvilNotch.Core.Util.Line.LineBase;
import com.EvilNotch.Core.Util.Util.BlockUtil;
import com.EvilNotch.Core.Util.Util.EntityUtil;
import com.EvilNotch.Core.Util.Util.ItemUtil;
import com.EvilNotch.Core.Util.Util.NBTUtil;
import com.EvilNotch.Core.Util.Util.Registry;
import com.EvilNotch.Core.Util.Util.TileEntityUtil;
import com.EvilNotch.Core.Util.Util.Util;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockVine;
import net.minecraft.block.IGrowable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenSwamp;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.WorldEvent;

public class EventEnhancedVanilla {

	public boolean blacklisted = false;
	
	//Fixes Cows and not being able to determine you should have no NBT on your milk bucket
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void vanillaBucketMilkPickupFix(EntityInteractEvent e)
	{
		EntityPlayer player = e.entityPlayer;
		World w = player.worldObj;
		if(!Config.milk_bucket || player == null || player.getCurrentEquippedItem() == null || !(e.target instanceof EntityCow) || player.getCurrentEquippedItem().getItem() != Items.bucket)
			return;
		ItemStack stack = player.getCurrentEquippedItem();
		ItemStack k = new ItemStack(Items.milk_bucket);
		if(!ItemUtil.bucketHasCustomData(stack,k))
			return;
		NBTTagCompound nbt = ItemUtil.getBucketData(stack,k);
		if(!player.capabilities.isCreativeMode)
			stack.stackSize--;
		
		k.setTagCompound(nbt);
		EntityUtil.addItemStackToInventory(player, k,true,stack);
		e.setResult(Result.ALLOW);
		e.setCanceled(true);
	}
	//Fixes Vanilla Milk Buckets not preserving enchantments/NBT
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void finishMilk(PlayerUseItemEvent.Finish e)
	{
		if(!Config.milk_bucket || e.result == null || e.result.getItem() != Items.bucket || e.item.getItem() != Items.milk_bucket || e.item.getTagCompound() == null)
			return;
		//since inverted the tagcompound stack and the previous are the same
		if(!ItemUtil.bucketHasCustomData(e.item,e.item))
			return;
		NBTTagCompound nbt = ItemUtil.getBucketData(e.item,e.item);
		e.result.setTagCompound(nbt);
	}
	
	//Fixes Liquid TileEntites used in silkspawners
	@SubscribeEvent(priority = EventPriority.LOW)
	public void vanillaBucketLiquidFixer(FillBucketEvent e)
	{
		if(e.world.isRemote || !(e.current.getItem() instanceof ItemBucket) || !Config.vanillaBucketTileUpdate || e.target.typeOfHit == null)
			return;
		ItemStack stack = e.current;
		int x = e.target.blockX;
		int y = e.target.blockY;
		int z = e.target.blockZ;
		Block b = e.world.getBlock(x,y,z);
		TileEntity tile = e.world.getTileEntity(x, y, z);
		if(BlockUtil.isFluid(b))
		{
		   //TileEntity Liquid Fixer
		   if(tile != null)
		   {
			   tile.readFromNBT(TileEntityUtil.getTileNBT(tile));
			   TileEntityUtil.updateTileEntity(e.world, tile, x, y, z);
		   }
		   return;//TileEntity render update fixed if is still there
		}
	}
	
	//Fixes Vanilla and modded bucket's emptying events, preserves enchantment, and nbt if configured
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void vanillaBucketFillFixer(FillBucketEvent e)
	{
		if(e.world.isRemote || !(e.current.getItem() instanceof ItemBucket) || !Config.vanillaBucketFillFix || e.target.typeOfHit == null)
			return;
		ItemStack stack = e.current;
		ItemBucket item = (ItemBucket) stack.getItem();
		int x = e.target.blockX;
		int y = e.target.blockY;
		int z = e.target.blockZ;
		Block b = e.world.getBlock(x,y,z);
		int meta = e.world.getBlockMetadata(x, y, z);
		if(ItemUtil.isItem(stack.getItem(), Config.bucket_fill_blacklist, stack.getItemDamage()))
			return;
		if(b == Blocks.water)
			b = Blocks.flowing_water;
		if(b == Blocks.lava)
			b = Blocks.flowing_lava;//Since all modded blocks are one fluid only support vanilla here
		
		if(b == Blocks.air || !BlockUtil.isFluid(b))
			return;//if configed otherwise don't continue this event fixer
		
		try{
			ItemStack empty = ItemUtil.getBucketStack(b);
			if(empty == null || !ItemUtil.bucketHasCustomData(stack,empty))
				return;
			NBTTagCompound nbt = ItemUtil.getBucketData(stack,empty);
			e.world.setBlockToAir(x, y, z);
			
			if(!nbt.hasNoTags())
				empty.setTagCompound(nbt);

			e.result = empty;
			e.setResult(Result.ALLOW);
			
		}catch(Exception ee){ee.printStackTrace();}//ignored exceptions for those that are not vanilla
	}
	
	//Fixes Vanilla and modded bucket's emptying events, preserves enchantment, and nbt if configured
	//Also fixes liquid tile entities and force an update regardless if it picks up the liquid
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void vanillaBucketEmptyFixer(FillBucketEvent e)
	{
		if(e.world.isRemote || !(e.current.getItem() instanceof ItemBucket) || !Config.vanillaBucketEmptyFix || e.target.typeOfHit == null)
			return;
		ItemStack stack = e.current;
		ItemBucket item = (ItemBucket) stack.getItem();
		int x = e.target.blockX;
		int y = e.target.blockY;
		int z = e.target.blockZ;
		Block b = e.world.getBlock(x,y,z);
		if(!b.isReplaceable(e.world, x, y, z))
		{
		  if (e.target.sideHit == 0)
	  		   y -= 1;
	  	   if (e.target.sideHit == 1)
	  		   y += 1;
	  	   if (e.target.sideHit == 2)
	  		   z -= 1;
	  	   if (e.target.sideHit == 3)
	  		   z += 1;
	  	   if (e.target.sideHit == 4)
	  		   x -= 1;
	  	   if (e.target.sideHit == 5)
	  		   x += 1;
		}
		if(b == Blocks.air || BlockUtil.isFluid(b) || ItemUtil.isItem(stack.getItem(), Config.bucket_empty_blacklist, stack.getItemDamage()))
			return;

		try{
			Block block = (Block) ReflectionHelper.findField(ItemBucket.class, MCPMappings.getFeildName(MainCommonMod.isEclipse, "isFull")).get(e.current.getItem());
			if(!BlockUtil.isFluid(block))
				return;
			e.world.setBlockToAir(x, y, z);
			e.world.setBlock(x, y, z, block,0, 3);
			TileEntity tile = e.world.getTileEntity(x, y, z);
			
			ItemStack empty = new ItemStack(item.getContainerItem());
			if(!ItemUtil.bucketHasCustomData(stack,empty))
				return;
			if(tile != null && stack.getTagCompound() != null && Config.bucketTileRead)
			{
				if(stack.getTagCompound().hasKey("tileNBT"))
				{
					tile.readFromNBT((NBTTagCompound) stack.getTagCompound().getTag("tileNBT"));
					TileEntityUtil.updateTileEntity(e.world, tile, x, y, z);
					stack.getTagCompound().removeTag("tileNBT");
				}
			}
			NBTTagCompound nbt = ItemUtil.getBucketData(stack,empty);
			if(!nbt.hasNoTags())
				empty.setTagCompound(nbt);
			e.result = empty;
			e.setResult(Result.ALLOW);
		}catch(Exception ee){ee.printStackTrace();}//ignored exceptions for those that are not vanilla
	
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void worldLoadEvent(WorldEvent.Load e)
	{
		if(e.world.isRemote)
			return;
		MainCommonMod.world = e.world;//Makes instanceof MainCommonMod a real world if available on server side
		if(blacklisted)
			return;//Return if 2d attempt to cache didn't work
		
		//2d attempt to cache invalid entities
		ArrayList<String> blacklist = new ArrayList();
		for(String s : EntityUtil.ent_blacklist)
		{
			Entity entity = EntityUtil.createEntityByNameQuietly(s, e.world);
			if(entity == null || EntityUtil.TranslateEntity(s) == null)
			{
			   blacklist.add(s);
			   System.out.println("Corrupted Entity Report to Mod Author:" + s);
			   continue;
			}
			if(Util.isLine(Config.ent_blacklistcfg,new LineBase("\"" + s + "\"") ))
			{
				blacklist.add(s);
				System.out.println("BlackListedEntity:" + s);
				continue;
			}
			//Second attempt to get entity living cached the right way
			if(entity instanceof EntityLiving)
			{
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("id", s);
				if(entity instanceof EntitySlime)
					nbt.setInteger("Size",Config.slimeInventorySize);
				Entity ent = EntityUtil.createEntityFromNBTQuietly(nbt, e.world);
				if(ent == null)
				{
					if(Config.Debug)
						System.out.println("Cached EntityLiving Properly Failed Using Create By Name:" + s);
				}
				else
					entity = ent;
			}
			if(entity != null){
				EntityUtil.entity_names.put(s, EntityUtil.TranslateEntity(s));
				EntityUtil.entityCache.put(s, entity);
				EntityUtil.entityIdToName.put(FieldAcess.entity_classToIDMapping.get(entity.getClass()), s);
				if(entity instanceof EntityLiving)
				{
					EntityUtil.livingEntity_names.put(s, EntityUtil.TranslateEntity(s));
					EntityUtil.livingBaseEntityCache.put(s, entity);
				}
				if(entity instanceof EntityLivingBase && !(entity instanceof EntityLiving))
				{
					EntityUtil.livingBaseEntitiy_names.put(s, EntityUtil.TranslateEntity(s));
					EntityUtil.livingBaseEntityCache.put(s, entity);
				}
				if(!(entity instanceof EntityLivingBase) && !(entity instanceof EntityLiving))
				{
					EntityUtil.nonLivingEntitiy_names.put(s, EntityUtil.TranslateEntity(s));
					EntityUtil.nonLivingEntityCache.put(s, entity);
				}
			}
		}
		EntityUtil.ent_blacklist.clear();
		for(String s : blacklist)
			EntityUtil.ent_blacklist.add(s);//Re-add blacklisted entities
		EntityUtil.organizeAllHashMaps();
		
		blacklisted = true;
		System.out.println("zzzzzzzzzEntity BlackList Re-Submited" + EntityUtil.ent_blacklist);
		System.out.println("Command Sender Name Corruption Blacklist:" + EntityUtil.ent_blacklist_commandsender);
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void sheepPuncher(LivingAttackEvent e) 
	{
		if(!Config.sheepDropWoolPunch || e.entity.worldObj.isRemote || !(e.entity instanceof EntitySheep) || ((EntitySheep)e.entity).isChild())
			return;
		if(e.source.getSourceOfDamage() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) e.source.getSourceOfDamage();
			EntitySheep sheep = (EntitySheep) e.entity;
			sheep.setSheared(true);
			
			int amount = (int)(Math.random() * 3-0)+1;
			ArrayList<ItemStack> drops = new ArrayList();
			for(int i=0;i<amount;i++)
			{
				drops.add(new ItemStack(Blocks.wool, 1, sheep.getFleeceColor()));
			}
			 Random rand = new Random();
             for(ItemStack stack : drops)
             {
                 EntityItem ent = sheep.entityDropItem(stack, 1.0F);
                 ent.motionY += rand.nextFloat() * 0.05F;
                 ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                 ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
             }
		}
	}
	/**
	 * Update MainCommonMod boolean for addon modders to use/ Leather armor to use since it uses that boolean
	 * @param e
	 */
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void ResoucePackUpdate(TextureStitchEvent.Post e) 
	{
		List<ResourcePackRepository.Entry> list = Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries();
		if(list.size() > 0)
		{
		Entry k = list.get(list.size()-1);
		if(k.getResourcePackName().equals("Mc Textures 1.2.5.zip") )
			MainCommonMod.resoucePackOld = true;
		}
		list = Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntriesAll();
		Iterator<Entry> it = list.iterator();
		while(it.hasNext())
		{
			ResourcePackRepository.Entry entry = it.next();
			if(entry.getResourcePackName().contains("Mc Textures 1.2.5.zip") && !Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries().contains(entry) || entry.getResourcePackName().contains("Mc Textures 1.2.5.zip") &&  !Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries().get(Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries().size()-1).equals(entry))
				MainCommonMod.resoucePackOld  = false; //Disables mc 1.2.5 resource pack
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void removeOldTileEntities(BlockEvent.PlaceEvent e) 
	{
		if(e.world.isRemote || !(e.block instanceof ITileEntityProvider))
			return;
		ITileEntityProvider tile = (ITileEntityProvider)e.block;
		
		Iterator it = Registry.oldToNewTileClasses.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<Class,Class> entry = (Map.Entry) it.next();
			Class<TileEntity> oldclass = entry.getKey();
			Class<TileEntity> newclass = entry.getValue();
			if(tile.createNewTileEntity(e.world, 0).getClass().equals(oldclass))
			{
				e.world.removeTileEntity(e.x, e.y, e.z);//Removes Vanilla Furnace
				try {
					e.world.setTileEntity(e.x, e.y, e.z, newclass.newInstance());
					TileEntity t = e.world.getTileEntity(e.x, e.y, e.z);
					t.markDirty();
					System.out.println(newclass);
				}catch(Exception ee){ee.printStackTrace();}
				return;
			}
		}
	}
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void bucketEmptyNether(FillBucketEvent e) 
	{
		if(e.world.isRemote || !Config.buckets_in_nether || e.current.getItem() != Items.water_bucket || !e.entityPlayer.worldObj.provider.isHellWorld || e.target.typeOfHit == null)
			return;
		ItemBucket bucket = (ItemBucket)e.current.getItem();
		int side = e.target.sideHit;
		int x = e.target.blockX;
		int y = e.target.blockY;
		int z = e.target.blockZ;
	   if (e.target.sideHit == 0)
  		   y -= 1;
  	   if (e.target.sideHit == 1)
  		   y += 1;
  	   if (e.target.sideHit == 2)
  		   z -= 1;
  	   if (e.target.sideHit == 3)
  		   z += 1;
  	   if (e.target.sideHit == 4)
  		   x -= 1;
  	   if (e.target.sideHit == 5)
  		   x += 1;

		e.world.setBlock(x,y,z, Blocks.flowing_water);
	}
	
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void mushrooms(PlayerInteractEvent e) 
	{
		if(e.action == null)
			return;
		ItemStack stack = e.entityPlayer.getCurrentEquippedItem();
		if(stack == null)
			return;
		if(e.action == e.action.LEFT_CLICK_BLOCK ||stack.getItem() != Item.getItemFromBlock(Blocks.brown_mushroom) && stack.getItem() != Item.getItemFromBlock(Blocks.red_mushroom) || !Config.oldmushrooms || e.entityPlayer.worldObj.isRemote)
			return;

		MovingObjectPosition pos = ItemUtil.getMovingObjectPositionFromPlayer1(e.entityPlayer.worldObj, e.entityPlayer, true);
		if(pos == null || pos.typeOfHit == null)
			return;
		Block block = e.entityPlayer.worldObj.getBlock(pos.blockX,pos.blockY,pos.blockZ);
		if(block instanceof BlockGrass || block instanceof BlockDirt || block instanceof BlockSand && Config.oldsandshrooms)
		{
			if(stack.getItem() == Item.getItemFromBlock(Blocks.brown_mushroom))
				e.entityPlayer.worldObj.setBlock(pos.blockX,pos.blockY+1,pos.blockZ, LoadRegister.brown_mushroom);
			if(stack.getItem() == Item.getItemFromBlock(Blocks.red_mushroom))
				e.entityPlayer.worldObj.setBlock(pos.blockX,pos.blockY+1,pos.blockZ, LoadRegister.red_mushroom);
			
			if(!e.entityPlayer.capabilities.isCreativeMode)
				e.entityPlayer.getCurrentEquippedItem().stackSize -=1;
			e.setCanceled(true);
		}
		
	}
	
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void whitestuff(BonemealEvent e) 
	{
		if(e.world.isRemote)
			return;
		
		int maxTries = Config.maxTries;
		int vinemax = Config.vinemax;
		boolean bush_used = false;
		int meta = e.world.getBlockMetadata(e.x, e.y, e.z);
		Block origin = e.block;
		
		if(!Config.bonemeal)
			return;
		try{
			for(int i=0;i<10;i++)
				System.out.println("BoneMealBlackList:" + Config.bonemeal_blacklist + " isBlackListed:" + BlockUtil.isBlock(e.block, Config.bonemeal_blacklist, meta));
			if(e.block instanceof BlockGrass || e.block instanceof BlockDoublePlant || BlockUtil.isBlock(e.block, Config.bonemeal_blacklist, meta))
				return;
			
		if(e.block instanceof IGrowable)
		{
			IGrowable crops = (IGrowable)e.block;
			Block block2 = null;
			boolean flag = false;
			boolean used = false;
			
			int i = 0;
			if(!(e.block instanceof BlockSapling))
			{
			while(crops.func_149851_a(e.world, e.x, e.y, e.z, false) && i < maxTries && e.block == e.world.getBlock(e.x, e.y,e.z) && !flag)
			{
				crops.func_149853_b(e.world, e.world.rand, e.x, e.y, e.z);
				i++;
				System.out.println("Please exicute me now~!!!!");
				block2 = e.world.getBlock(e.x, e.y, e.z);
				if(block2 instanceof IGrowable)
					crops = (IGrowable) block2;
				else
					flag = true;
				used = true;
			}
				e.setResult(Result.ALLOW);
				if(used)
					return;
			}
			
			if(e.block instanceof BlockSapling)
			{
				((BlockSapling)e.block).func_149878_d(e.world, e.x, e.y, e.z, e.world.rand);
				e.setResult(Result.ALLOW);
				return;
			}
		}

		//Cacti/reeds/vines/lillypads
		if(!Config.other_foilage)
			return;
		if(e.block instanceof IPlantable && !(e.block instanceof BlockBush))
		{
			for(int i=0;i<maxTries;i++)
				e.block.updateTick(e.world, e.x, getTopBlock(e.block,e.world,e.x,e.y,e.z), e.z, e.world.rand);
			//e.world.setBlock(e.x, getTopBlock(e.block,e.world,e.x,e.y,e.z), e.z, e.block,0,2);
			e.setResult(Result.ALLOW);
			return;
		}
		
		//New nether wart format supports all block bushes if meta data or block changes it has been used
		//Returns if used
		if(e.block instanceof BlockBush && !(e.block instanceof IGrowable) && !(e.block instanceof BlockLilyPad))
		{
			for(int i=0;i<maxTries;i++)
				e.block.updateTick(e.world, e.x, getTopBlock(e.block,e.world,e.x,e.y,e.z), e.z, e.world.rand);
			bush_used = e.world.getBlockMetadata(e.x, e.y, e.z) != meta || e.block != origin;
			if(bush_used)
			{
				e.setResult(Result.ALLOW);
				return;
			}
		}

		//System.out.println(e.block instanceof BlockBush);
		if(e.block instanceof BlockBush && !(e.block instanceof IGrowable) && !(e.block instanceof BlockLilyPad))
		{
			if(Item.getItemFromBlock(e.block) != null)
			{
				ItemStack stack = new ItemStack(e.block,1,e.world.getBlockMetadata(e.x, e.y, e.z));
				EntityItem item = new EntityItem(e.world,e.x+0.5,e.y,e.z+0.5,stack);
				e.world.spawnEntityInWorld(item);
			}
			e.setResult(Result.ALLOW);
			return;
		}

		if(e.block instanceof BlockBush && e.block instanceof IGrowable)
		{
			for(int i=0;i<maxTries;i++)
				e.block.updateTick(e.world, e.x, e.y, e.z, e.world.rand);
			e.setResult(Result.ALLOW);
			return;
		}

		//Calculates lily pad position based on if possible..
		if(e.block instanceof BlockLilyPad)
		{
			boolean left_x = e.world.getBlock(e.x-1, e.y-1, e.z) instanceof BlockLiquid && e.world.getBlock(e.x-1, e.y-1, e.z).getMaterial() == Material.water && e.world.getBlock(e.x-1, e.y, e.z) == Blocks.air;
			boolean right_x = e.world.getBlock(e.x+1, e.y-1, e.z) instanceof BlockLiquid && e.world.getBlock(e.x+1, e.y-1, e.z).getMaterial() == Material.water && e.world.getBlock(e.x+1, e.y, e.z) == Blocks.air;
			boolean left_z = e.world.getBlock(e.x, e.y-1, e.z-1) instanceof BlockLiquid && e.world.getBlock(e.x, e.y-1, e.z-1).getMaterial() == Material.water && e.world.getBlock(e.x, e.y, e.z-1) == Blocks.air;
			boolean right_z = e.world.getBlock(e.x, e.y-1, e.z+1) instanceof BlockLiquid && e.world.getBlock(e.x, e.y-1, e.z+1).getMaterial() == Material.water && e.world.getBlock(e.x, e.y, e.z+1) == Blocks.air;
			if(!left_x && !right_x && !left_z && !right_z)
				return;
			Random dir = new Random();
			int dirb = dir.nextInt(4);
			while(!isValidDirection(dirb,left_x,right_x,left_z,right_z))
				dirb = dir.nextInt(4);
			System.out.println(dirb);
			if(dirb == 0)
				e.world.setBlock(e.x-1, e.y, e.z, e.block,e.world.getBlockMetadata(e.x, e.y, e.z),2);
			if(dirb == 1)
				e.world.setBlock(e.x+1, e.y, e.z, e.block,e.world.getBlockMetadata(e.x, e.y, e.z),2);
			if(dirb == 2)
				e.world.setBlock(e.x, e.y, e.z-1, e.block,e.world.getBlockMetadata(e.x, e.y, e.z),2);
			if(dirb == 3)
				e.world.setBlock(e.x, e.y, e.z+1, e.block,e.world.getBlockMetadata(e.x, e.y, e.z),2);
			e.setResult(Result.ALLOW);
		}
		if(e.block instanceof BlockVine)
		{
			int x = e.x;
			int z = e.z;
			Random rnd = new Random();
			
			for(int i=0;i<vinemax/4;i++)
			{
				for(int j=0;j<vinemax;j++)
					e.block.updateTick(e.world, x, getBottomVine(e.block,e.world,x,e.y,e.z), e.z, e.world.rand);
				if(e.world.getBlock(x-1,e.y,e.z) == e.block)
					x-=1;
			}
			x = e.x; //HotFix
			for(int i=0;i<vinemax/4;i++)
			{
				for(int j=0;j<vinemax;j++)
					e.block.updateTick(e.world, x, getBottomVine(e.block,e.world,x,e.y,e.z), e.z, e.world.rand);
				
				if(e.world.getBlock(x+1,e.y,e.z) == e.block)
					x+=1;
			}
			for(int i=0;i<vinemax/4;i++)
			{
				for(int j=0;j<vinemax;j++)
					e.block.updateTick(e.world, e.x, getBottomVine(e.block,e.world,e.x,e.y,z), z, e.world.rand);
				
				if(e.world.getBlock(x,e.y,e.z-1) == e.block)
					z -=1;
			}
			z = e.z;//Hotfix
			for(int i=0;i<vinemax/4;i++)
			{
				for(int j=0;j<vinemax;j++)
					e.block.updateTick(e.world, e.x, getBottomVine(e.block,e.world,e.x,e.y,z), z, e.world.rand);
				
				if(e.world.getBlock(x,e.y,e.z+1) == e.block)
					z +=1;
			}
			
			e.setResult(Result.ALLOW);
			return;
		}
		
		}
		catch(Throwable t){t.printStackTrace();}
	}
	/**
	 * Based on booleans if the block has a valid direction booleans a calculated before the method call
	 * @param dirb
	 * @param left_x
	 * @param right_x
	 * @param left_z
	 * @param right_z
	 * @return
	 */
	private boolean isValidDirection(int dirb,boolean left_x,boolean right_x,boolean left_z,boolean right_z) 
	{
		if(dirb == 0 && left_x || dirb == 1 && right_x || dirb == 2 && left_z || dirb == 3 && right_z)
			return true;
		
		return false;
	}
	private int getBottomVine(Block b,World w,int x, int y, int z) 
	{
		int k = y;
		while(w.getBlock(x,k-1,z) == b)
			k-=1;

		return k;
	}
	public static int getTopBlock(Block b,World w,int x,int y,int z)
	{
		int e = y;
		while (w.getBlock(x,e+1,z) == b) {
		      e++;
		    }
		return e;
	}

}
