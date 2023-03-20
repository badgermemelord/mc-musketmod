package ewewukek.musketmod.block.entity;

import ewewukek.musketmod.entity.ModBlockEntities;
import ewewukek.musketmod.item.Items;
import ewewukek.musketmod.item.inventory.ImplementedInventory;
import ewewukek.musketmod.screen.CannonBlockAbstractContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import javax.annotation.Nullable;

public class CannonBlockEntity extends BlockEntity implements MenuProvider, ImplementedInventory {
    private final NonNullList<ItemStack> inventory =
            NonNullList.withSize(4, ItemStack.EMPTY);

    public CannonBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CANNON_BLOCK, pos, state);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Cannon Block");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        return new CannonBlockAbstractContainerMenu(syncId, inv, this);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        ContainerHelper.saveAllItems(nbt, inventory);
    }

    @Override
    public void load(CompoundTag nbt) {
        ContainerHelper.loadAllItems(nbt, inventory);
        super.load(nbt);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, CannonBlockEntity entity) {
        if(hasRecipe(entity) && hasNotReachedStackLimit(entity)) {
            craftItem(entity);
        }
    }

    private static void craftItem(CannonBlockEntity entity) {
        entity.removeItem(0, 1);
        entity.removeItem(1, 1);
        entity.removeItem(2, 1);

        entity.setItem(3, new ItemStack(Items.MUSKET,
                entity.getItem(3).getCount() + 1));
    }

    private static boolean hasRecipe(CannonBlockEntity entity) {
        boolean hasItemInFirstSlot = entity.getItem(0).getItem() == Items.CANNON_BLOCK;
        boolean hasItemInSecondSlot = entity.getItem(1).getItem() == Items.CARTRIDGE;
        boolean hasItemInThirdSlot = entity.getItem(2).getItem() == Items.PISTOL;

        return hasItemInFirstSlot && hasItemInSecondSlot && hasItemInThirdSlot;
    }

    private static boolean hasNotReachedStackLimit(CannonBlockEntity entity) {
        return entity.getItem(3).getCount() < entity.getItem(3).getMaxStackSize();
    }
}