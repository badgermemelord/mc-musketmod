package ewewukek.musketmod;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;

import static net.minecraft.world.item.Item.BASE_ATTACK_DAMAGE_UUID;
import static net.minecraft.world.item.Item.BASE_ATTACK_SPEED_UUID;

public class MusketItem extends GunItem {
    public static final int BAYONET_DAMAGE = 4;
    public static final float BAYONET_SPEED = -2.0f;
    public static float bulletStdDev;
    public static float bulletSpeed;
    public static int pelletCount;
    public static float damageMultiplierMin;
    public static float damageMultiplierMax;
    public static int reloadDuration;
    public static int durability;



    public final Multimap<Attribute, AttributeModifier> bayonetAttributeModifiers;

    public MusketItem(Item.Properties properties, boolean withBayonet) {
        super(properties.defaultDurability(durability));
        if (withBayonet) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", BAYONET_DAMAGE, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(
                BASE_ATTACK_SPEED_UUID, "Weapon modifier", BAYONET_SPEED, AttributeModifier.Operation.ADDITION));
            bayonetAttributeModifiers = builder.build();
        } else {
            bayonetAttributeModifiers = null;
        }
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

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND && bayonetAttributeModifiers != null
                ? bayonetAttributeModifiers : super.getDefaultAttributeModifiers(slot);
    }
}
