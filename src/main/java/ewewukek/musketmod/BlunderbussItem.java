package ewewukek.musketmod;

import com.google.common.collect.Multimap;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

public class BlunderbussItem extends GunItem {

    public static float bulletStdDev;
    public static float bulletSpeed;
    public static int pelletCount;
    public static float damageMultiplierMin;
    public static float damageMultiplierMax;
    public static int reloadDuration;
    public static int durability;



    public final Multimap<Attribute, AttributeModifier> bayonetAttributeModifiers;

    public BlunderbussItem(Item.Properties properties, boolean withBayonet) {
        super(properties.defaultDurability(durability));
            bayonetAttributeModifiers = null;
    }

    @Override
    public float bulletStdDev() {
        return bulletStdDev;
    }

    @Override
    public float bulletSpeed() {
        return bulletSpeed;
    }
    @Override
    public int pelletCount() {
        return pelletCount;
    }


    @Override
    public float damageMultiplierMin() {
        return damageMultiplierMin;
    }

    @Override
    public float damageMultiplierMax() {
        return damageMultiplierMax;
    }
    @Override
    public int reloadDuration() {
        return reloadDuration;
    }
    @Override
    public int durability() {
        return durability;
    }

    @Override
    public SoundEvent fireSound() {
        return Sounds.MUSKET_FIRE;
    }

    @Override
    public boolean twoHanded() {
        return true;
    }

    @Override
    public boolean ignoreInvulnerableTime() {
        return false;
    }

}
