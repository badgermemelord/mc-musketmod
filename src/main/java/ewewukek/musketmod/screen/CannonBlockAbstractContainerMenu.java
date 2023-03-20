package ewewukek.musketmod.screen;

import ewewukek.musketmod.screen.slot.ModFuelSlot;
import ewewukek.musketmod.screen.slot.ModResultSlot;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CannonBlockAbstractContainerMenu extends AbstractContainerMenu {
    private final Container container;

    public CannonBlockAbstractContainerMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(4));
    }

    public CannonBlockAbstractContainerMenu(int syncId, Inventory playerInventory, Container inventory) {
        super(ModAbstractContainerMenus.CANNON_BLOCK_ABSTRACT_CONTAINER_MENU, syncId);
        checkContainerSize(inventory, 4);
        this.container = inventory;
        inventory.startOpen(playerInventory.player);

        //
        this.addSlot(new ModFuelSlot(inventory, 0, 18, 50));
        this.addSlot(new Slot(inventory, 1, 66, 16));
        this.addSlot(new Slot(inventory, 2, 66, 50));
        this.addSlot(new ModResultSlot(inventory, 3, 114, 33));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (invSlot < this.container.getContainerSize()) {
                if (!this.moveItemStackTo(originalStack, this.container.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 0, this.container.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return newStack;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 86 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 144));
        }
    }
}
