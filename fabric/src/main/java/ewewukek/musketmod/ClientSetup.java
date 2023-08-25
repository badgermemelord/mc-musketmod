package ewewukek.musketmod;

import ewewukek.musketmod.mechanics.ClientMethods;
import ewewukek.musketmod.networking.ModPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class ClientSetup implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(MusketMod.BULLET_ENTITY_TYPE, (ctx) -> new BulletRenderer(ctx));

        ClampedItemPropertyFunction loaded = (stack, world, player, seed) -> {
            return GunItem.isLoaded(stack) ? 1 : 0;
        };
        FabricModelPredicateProviderRegistry.register(Items.MUSKET, new ResourceLocation("loaded"), loaded);
        FabricModelPredicateProviderRegistry.register(Items.RIFLE, new ResourceLocation("loaded"), loaded);
        FabricModelPredicateProviderRegistry.register(Items.BLUNDERBUSS, new ResourceLocation("loaded"), loaded);
        FabricModelPredicateProviderRegistry.register(Items.MUSKET_WITH_BAYONET, new ResourceLocation("loaded"), loaded);
        FabricModelPredicateProviderRegistry.register(Items.PISTOL, new ResourceLocation("loaded"), loaded);

        ClientPlayNetworking.registerGlobalReceiver(MusketMod.SMOKE_EFFECT_PACKET_ID, (client, handler, buf, responseSender) -> {
            ClientLevel world = handler.getLevel();
            Vec3 origin = new Vec3(buf.readFloat(), buf.readFloat(), buf.readFloat());
            Vec3 direction = new Vec3(buf.readFloat(), buf.readFloat(), buf.readFloat());
            GunItem.fireParticles(world, origin, direction);
        });
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.CLIENT_PLAY_MUSKET_SOUND, (client, handler, buf, responseSender) -> {
            int soundIndex = buf.readInt();
            client.execute(() ->  {
                Entity entity = client.player;
                SoundEvent sound = Sounds.soundList.get(soundIndex);
                entity.playSound(sound, 0.8f, 1);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.CLIENT_BLOCKHIT_PACKET, (client, handler, buf, responseSender) -> {
            int entityID = buf.readInt();
            boolean shouldRicochet = buf.readBoolean();
            BlockHitResult hitResult = buf.readBlockHitResult();
            Level world = client.level;
            double vectorX = buf.readDouble();
            double vectorY = buf.readDouble();
            double vectorZ = buf.readDouble();
            double posX = hitResult.getLocation().x;
            double posY = hitResult.getLocation().y;
            double posZ = hitResult.getLocation().z;
            Vec3 newTrajectory = new Vec3(vectorX, vectorY, vectorZ);
            Vec3 newPos = new Vec3(posX, posY, posZ);
            client.execute(() ->  {
                if (shouldRicochet) {
                    System.out.println("newposclient is: " + newPos);
                    ClientMethods.updateTrajectoryOnHit(entityID, world, newTrajectory);
                    ClientMethods.updateEntityPos(entityID, world, newPos);
                } else {
                    ClientMethods.blockHit(entityID, world, hitResult);
                }
            });
        });
    }
}
