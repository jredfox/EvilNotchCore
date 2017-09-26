package com.EvilNotch.Core.Gui;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class ContainerRepairFixed extends ContainerRepair{

	public static final Logger logger = LogManager.getLogger();
    /** Here comes out item you merged and/or renamed. */
	public IInventory outputSlot = new InventoryCraftResult();
    /** The 2slots where you put your items in that you want to merge and/or rename. */
	public IInventory inputSlots = new InventoryBasic("Repair", true, 2)
    {
        /**
         * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think
         * it hasn't changed and skip it.
         */
        public void markDirty()
        {
            super.markDirty();
            ContainerRepairFixed.this.onCraftMatrixChanged(this);
        }
    };
    public World theWorld;
    public int field_82861_i;
    public int field_82858_j;
    public int field_82859_k;
    public String repairedItemName;
    public final EntityPlayer thePlayer;

	public ContainerRepairFixed(InventoryPlayer p_i1800_1_, final World p_i1800_2_, final int p_i1800_3_, final int p_i1800_4_,final int p_i1800_5_, EntityPlayer p)
	{
		super(p_i1800_1_, p_i1800_2_, p_i1800_3_, p_i1800_4_, p_i1800_5_, p);
		  this.theWorld = p_i1800_2_;
	        this.field_82861_i = p_i1800_3_;
	        this.field_82858_j = p_i1800_4_;
	        this.field_82859_k = p_i1800_5_;
	        this.thePlayer = p;
	        this.addSlotToContainer(new Slot(this.inputSlots, 0, 27, 47));
	        this.addSlotToContainer(new Slot(this.inputSlots, 1, 76, 47));
	        this.addSlotToContainer(new Slot(this.outputSlot, 2, 134, 47)
	        {
	            /**
	             * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
	             */
	            public boolean isItemValid(ItemStack p_75214_1_)
	            {
	                return false;
	            }
	            /**
	             * Return whether this slot's stack can be taken from this slot.
	             */
	            public boolean canTakeStack(EntityPlayer p_82869_1_)
	            {
	                return (p_82869_1_.capabilities.isCreativeMode || p_82869_1_.experienceLevel >= ContainerRepairFixed.this.maximumCost) && ContainerRepairFixed.this.maximumCost > 0 && this.getHasStack();
	            }
	            public void onPickupFromSlot(EntityPlayer p_82870_1_, ItemStack p_82870_2_)
	            {
	                if (!p_82870_1_.capabilities.isCreativeMode)
	                {
	                    p_82870_1_.addExperienceLevel(-ContainerRepairFixed.this.maximumCost);
	                }

	                float breakChance = ForgeHooks.onAnvilRepair(p_82870_1_, p_82870_2_, ContainerRepairFixed.this.inputSlots.getStackInSlot(0), ContainerRepairFixed.this.inputSlots.getStackInSlot(1));

	                ContainerRepairFixed.this.inputSlots.setInventorySlotContents(0, (ItemStack)null);

	                if (ContainerRepairFixed.this.stackSizeToBeUsedInRepair > 0)
	                {
	                    ItemStack itemstack1 = ContainerRepairFixed.this.inputSlots.getStackInSlot(1);

	                    if (itemstack1 != null && itemstack1.stackSize > ContainerRepairFixed.this.stackSizeToBeUsedInRepair)
	                    {
	                        itemstack1.stackSize -= ContainerRepairFixed.this.stackSizeToBeUsedInRepair;
	                        ContainerRepairFixed.this.inputSlots.setInventorySlotContents(1, itemstack1);
	                    }
	                    else
	                    {
	                    	ContainerRepairFixed.this.inputSlots.setInventorySlotContents(1, (ItemStack)null);
	                    }
	                }
	                else
	                {
	                	ContainerRepairFixed.this.inputSlots.setInventorySlotContents(1, (ItemStack)null);
	                }

	                ContainerRepairFixed.this.maximumCost = 0;

	                if (!p_82870_1_.capabilities.isCreativeMode && !p_i1800_2_.isRemote && p_i1800_2_.getBlock(p_i1800_3_, p_i1800_4_, p_i1800_5_) == Blocks.anvil && p_82870_1_.getRNG().nextFloat() < breakChance)
	                {
	                    int i1 = p_i1800_2_.getBlockMetadata(p_i1800_3_, p_i1800_4_, p_i1800_5_);
	                    int k = i1 & 3;
	                    int l = i1 >> 2;
	                    ++l;

	                    if (l > 2)
	                    {
	                        p_i1800_2_.setBlockToAir(p_i1800_3_, p_i1800_4_, p_i1800_5_);
	                        p_i1800_2_.playAuxSFX(1020, p_i1800_3_, p_i1800_4_, p_i1800_5_, 0);
	                    }
	                    else
	                    {
	                        p_i1800_2_.setBlockMetadataWithNotify(p_i1800_3_, p_i1800_4_, p_i1800_5_, k | l << 2, 2);
	                        p_i1800_2_.playAuxSFX(1021, p_i1800_3_, p_i1800_4_, p_i1800_5_, 0);
	                    }
	                }
	                else if (!p_i1800_2_.isRemote)
	                {
	                    p_i1800_2_.playAuxSFX(1021, p_i1800_3_, p_i1800_4_, p_i1800_5_, 0);
	                }
	            }
	        });
	        int i;

	        for (i = 0; i < 3; ++i)
	        {
	            for (int j = 0; j < 9; ++j)
	            {
	                this.addSlotToContainer(new Slot(p_i1800_1_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
	            }
	        }

	        for (i = 0; i < 9; ++i)
	        {
	            this.addSlotToContainer(new Slot(p_i1800_1_, i, 8 + i * 18, 142));
	        }
      
	}
	
	  /**
     * called when the Anvil Input Slot changes, calculates the new result and puts it in the output slot
     */
    public void updateRepairOutput()
    {
        ItemStack itemstack = this.inputSlots.getStackInSlot(0);
        this.maximumCost = 0;
        int i = 0;
        byte b0 = 0;
        int j = 0;

        if (itemstack == null)
        {
            this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
            this.maximumCost = 0;
        }
        else
        {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = this.inputSlots.getStackInSlot(1);
            Map map = EnchantmentHelper.getEnchantments(itemstack1);
            boolean flag = false;
            int k2 = b0 + itemstack.getRepairCost() + (itemstack2 == null ? 0 : itemstack2.getRepairCost());
            this.stackSizeToBeUsedInRepair = 0;
            int k;
            int l;
            int i1;
            int k1;
            int l1;
            Iterator iterator1;
            Enchantment enchantment;

            if (itemstack2 != null)
            {
                if (!ForgeHooks.onAnvilChange(this, itemstack, itemstack2, outputSlot, repairedItemName, k2)) return;
                flag = itemstack2.getItem() == Items.enchanted_book && Items.enchanted_book.func_92110_g(itemstack2).tagCount() > 0;

                if (itemstack1.isItemStackDamageable() && itemstack1.getItem().getIsRepairable(itemstack, itemstack2))
                {
                    k = Math.min(itemstack1.getItemDamageForDisplay(), itemstack1.getMaxDamage() / 4);

                    if (k <= 0)
                    {
                        this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
                        this.maximumCost = 0;
                        return;
                    }

                    for (l = 0; k > 0 && l < itemstack2.stackSize; ++l)
                    {
                        i1 = itemstack1.getItemDamageForDisplay() - k;
                        itemstack1.setItemDamage(i1);
                        i += Math.max(1, k / 100) + map.size();
                        k = Math.min(itemstack1.getItemDamageForDisplay(), itemstack1.getMaxDamage() / 4);
                    }

                    this.stackSizeToBeUsedInRepair = l;
                }
                else
                {
                    if (!flag && (itemstack1.getItem() != itemstack2.getItem() || !itemstack1.isItemStackDamageable()))
                    {
                        this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
                        this.maximumCost = 0;
                        return;
                    }

                    if (itemstack1.isItemStackDamageable() && !flag)
                    {
                        k = itemstack.getMaxDamage() - itemstack.getItemDamageForDisplay();
                        l = itemstack2.getMaxDamage() - itemstack2.getItemDamageForDisplay();
                        i1 = l + itemstack1.getMaxDamage() * 12 / 100;
                        int j1 = k + i1;
                        k1 = itemstack1.getMaxDamage() - j1;

                        if (k1 < 0)
                        {
                            k1 = 0;
                        }

                        if (k1 < itemstack1.getItemDamage())
                        {
                            itemstack1.setItemDamage(k1);
                            i += Math.max(1, i1 / 100);
                        }
                    }

                    Map map1 = EnchantmentHelper.getEnchantments(itemstack2);
                    iterator1 = map1.keySet().iterator();

                    while (iterator1.hasNext())
                    {
                        i1 = ((Integer)iterator1.next()).intValue();
                        enchantment = Enchantment.enchantmentsList[i1];
                        k1 = map.containsKey(Integer.valueOf(i1)) ? ((Integer)map.get(Integer.valueOf(i1))).intValue() : 0;
                        l1 = ((Integer)map1.get(Integer.valueOf(i1))).intValue();
                        int i3;

                        if (k1 == l1)
                        {
                            ++l1;
                            i3 = l1;
                        }
                        else
                        {
                            i3 = Math.max(l1, k1);
                        }

                        l1 = i3;
                        int i2 = l1 - k1;
                        boolean flag1 = enchantment.canApply(itemstack);

                        if (this.thePlayer.capabilities.isCreativeMode || itemstack.getItem() == Items.enchanted_book)
                        {
                            flag1 = true;
                        }

                        Iterator iterator = map.keySet().iterator();

                        while (iterator.hasNext())
                        {
                            int j2 = ((Integer)iterator.next()).intValue();

                            Enchantment e2 = Enchantment.enchantmentsList[j2];
                            if (j2 != i1 && !(enchantment.canApplyTogether(e2) && e2.canApplyTogether(enchantment))) //Forge BugFix: Let Both enchantments veto being together
                            {
                                flag1 = false;
                                i += i2;
                            }
                        }

                        if (flag1)
                        {
                            if (l1 > enchantment.getMaxLevel())
                            {
                                l1 = enchantment.getMaxLevel();
                            }

                            map.put(Integer.valueOf(i1), Integer.valueOf(l1));
                            int l2 = 0;

                            switch (enchantment.getWeight())
                            {
                                case 1:
                                    l2 = 8;
                                    break;
                                case 2:
                                    l2 = 4;
                                case 3:
                                case 4:
                                case 6:
                                case 7:
                                case 8:
                                case 9:
                                default:
                                    break;
                                case 5:
                                    l2 = 2;
                                    break;
                                case 10:
                                    l2 = 1;
                            }

                            if (flag)
                            {
                                l2 = Math.max(1, l2 / 2);
                            }

                            i += l2 * i2;
                        }
                    }
                }
            }

            if (StringUtils.isBlank(this.repairedItemName))
            {
                if (itemstack.hasDisplayName())
                {
                    j = itemstack.isItemStackDamageable() ? 7 : itemstack.stackSize * 5;
                    i += j;
                    itemstack1.func_135074_t();
                }
            }
            else if (!this.repairedItemName.equals(itemstack.getDisplayName()))
            {
                j = itemstack.isItemStackDamageable() ? 7 : itemstack.stackSize * 5;
                i += j;

                if (itemstack.hasDisplayName())
                {
                    k2 += j / 2;
                }

                itemstack1.setStackDisplayName(this.repairedItemName);
            }

            k = 0;

            for (iterator1 = map.keySet().iterator(); iterator1.hasNext(); k2 += k + k1 * l1)
            {
                i1 = ((Integer)iterator1.next()).intValue();
                enchantment = Enchantment.enchantmentsList[i1];
                k1 = ((Integer)map.get(Integer.valueOf(i1))).intValue();
                l1 = 0;
                ++k;

                switch (enchantment.getWeight())
                {
                    case 1:
                        l1 = 8;
                        break;
                    case 2:
                        l1 = 4;
                    case 3:
                    case 4:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    default:
                        break;
                    case 5:
                        l1 = 2;
                        break;
                    case 10:
                        l1 = 1;
                }

                if (flag)
                {
                    l1 = Math.max(1, l1 / 2);
                }
            }

            if (flag)
            {
                k2 = Math.max(1, k2 / 2);
            }

            if (flag && !itemstack1.getItem().isBookEnchantable(itemstack1, itemstack2)) itemstack1 = null;

            this.maximumCost = k2 + i;

            if (i <= 0)
            {
                itemstack1 = null;
            }

            if (itemstack1 != null)
            {
                l = itemstack1.getRepairCost();

                if (itemstack2 != null && l < itemstack2.getRepairCost())
                {
                    l = itemstack2.getRepairCost();
                }

                if (itemstack1.hasDisplayName())
                {
                    l -= 9;
                }

                if (l < 0)
                {
                    l = 0;
                }

                l += 2;
                itemstack1.setRepairCost(l);
                EnchantmentHelper.setEnchantments(map, itemstack1);
            }

            this.outputSlot.setInventorySlotContents(0, itemstack1);
            this.detectAndSendChanges();
        }
    }

}
