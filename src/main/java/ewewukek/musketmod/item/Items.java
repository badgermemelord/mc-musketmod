package ewewukek.musketmod.item;

import ewewukek.musketmod.MusketItem;
import ewewukek.musketmod.PistolItem;
import ewewukek.musketmod.RifleItem;
import ewewukek.musketmod.block.Blocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class Items {
    public static final Item CARTRIDGE = new Item(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT));
    public static final Item MUSKET = new MusketItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT), false);
    public static final Item MUSKET_WITH_BAYONET = new MusketItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT), true);
    public static final Item PISTOL = new PistolItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT));
    public static final Item RIFLE = new RifleItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT), false);

    //Block Items
    public static final BlockItem CANNON_BLOCK = new BlockItem(Blocks.CANNON_BLOCK, new FabricItemSettings().group(CreativeModeTab.TAB_COMBAT));


}
