package ewewukek.musketmod.screen.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class ModFuelSlot extends Slot {
    public ModFuelSlot(Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return AbstractFurnaceBlockEntity.isFuel(stack) || ModFuelSlot.isBucket(stack);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return ModFuelSlot.isBucket(stack) ? 1 : super.getMaxStackSize(stack);
    }

    public static boolean isBucket(ItemStack stack) {
        return stack.is(Items.BUCKET);
    }
}
