package ewewukek.musketmod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.ArrayList;
import java.util.List;

public class Sounds {

    public static List<SoundEvent> soundList = new ArrayList<>(6);
    public static final SoundEvent MUSKET_LOAD_0 = new SoundEvent(new ResourceLocation(MusketMod.MODID, "musket_load0"));
    public static final SoundEvent MUSKET_LOAD_1 = new SoundEvent(new ResourceLocation(MusketMod.MODID, "musket_load1"));
    public static final SoundEvent MUSKET_LOAD_2 = new SoundEvent(new ResourceLocation(MusketMod.MODID, "musket_load2"));
    public static final SoundEvent MUSKET_READY = new SoundEvent(new ResourceLocation(MusketMod.MODID, "musket_ready"));
    public static final SoundEvent MUSKET_FIRE = new SoundEvent(new ResourceLocation(MusketMod.MODID, "musket_fire"));
    public static final SoundEvent PISTOL_FIRE = new SoundEvent(new ResourceLocation(MusketMod.MODID, "pistol_fire"));

    public static void addSoundsToList() {
        soundList.add(0, MUSKET_LOAD_0);
        soundList.add(1, MUSKET_LOAD_1);
        soundList.add(2, MUSKET_LOAD_2);
        soundList.add(3, MUSKET_READY);
        soundList.add(4, MUSKET_FIRE);
        soundList.add(5, PISTOL_FIRE);
    }
}
