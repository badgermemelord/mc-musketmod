package ewewukek.musketmod;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class Items {
    public static final Item CARTRIDGE = new Item(new Item.Properties());
    public static final Item DIRTY_CLEANING_ROD = new Item(new Item.Properties());
    public static final Item CLEANING_ROD = new Item(new Item.Properties().craftRemainder(DIRTY_CLEANING_ROD));
    public static final Item MUSKET = new MusketItem(new Item.Properties(), false);
    public static final Item MUSKET_WITH_BAYONET = new MusketItem(new Item.Properties(), true);
    public static final Item PISTOL = new PistolItem(new Item.Properties());
    public static final Item RIFLE = new RifleItem(new Item.Properties(), false);
    public static final Item BLUNDERBUSS = new BlunderbussItem(new Item.Properties(), false);

}
