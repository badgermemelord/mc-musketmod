package ewewukek.musketmod.networking;

import ewewukek.musketmod.MusketMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class ModPackets {
    public static final ResourceLocation CLIENT_PLAY_MUSKET_SOUND = new ResourceLocation(MusketMod.MODID, "client_play_musket_sound");
    public static final ResourceLocation CLIENT_BLOCKHIT_PACKET = new ResourceLocation(MusketMod.MODID, "client_blockhit_packet");
    public static final ResourceLocation CLIENT_GUN_COOLDOWN = new ResourceLocation(MusketMod.MODID, "client_gun_cooldown");

}
