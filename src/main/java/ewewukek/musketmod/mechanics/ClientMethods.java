package ewewukek.musketmod.mechanics;

import com.mojang.math.Vector3d;
import ewewukek.musketmod.BulletEntity;
import ewewukek.musketmod.Sounds;
import ewewukek.musketmod.networking.ModPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class ClientMethods {

    public static void updateTrajectoryOnHit(int entityID, Level world, Vec3 newTrajectory) {
        BulletEntity projectile = (BulletEntity) world.getEntity(entityID);
        projectile.setDeltaMovement(newTrajectory);
    }

    public static void updateEntityPos(int entityID, Level world, Vec3 newPos) {
        BulletEntity projectile = (BulletEntity) world.getEntity(entityID);
        projectile.setPos(newPos);
    }

    public static void onBlockHit(HitResult hitResult, Vec3 motion, Entity projectileEntity) {
/*        FriendlyByteBuf buf = PacketByteBufs.create();
        if (OnSolidHit.shouldRicochet(hitResult, motion)) {
            motion = OnSolidHit.getRicochetVector(motion, hitResult);

            buf.writeInt(projectileEntity.getId());
            buf.writeBoolean(true);
            buf.writeBlockHitResult((BlockHitResult) hitResult);
            buf.writeDouble(motion.x);
            buf.writeDouble(motion.y);
            buf.writeDouble(motion.z);

            for (ServerPlayer player : PlayerLookup.tracking(projectileEntity)) {
                //System.out.println("sent packet to " + player.getScoreboardName());
                ServerPlayNetworking.send(player, ModPackets.CLIENT_BLOCKHIT_PACKET, buf);
            }

            level.playSound(
                    null,
                    hitResult.getLocation().x,
                    hitResult.getLocation().y,
                    hitResult.getLocation().z,
                    Sounds.RICOCHET,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
        } else {
            onHit(hitResult);
            projectileEntity;

            buf.writeInt(this.getId());
            buf.writeBoolean(false);
            buf.writeBlockHitResult((BlockHitResult) hitResult);
            buf.writeDouble(motion.x);
            buf.writeDouble(motion.y);
            buf.writeDouble(motion.z);

            for (ServerPlayer player : PlayerLookup.tracking(this)) {
                ServerPlayNetworking.send(player, ModPackets.CLIENT_BLOCKHIT_PACKET, buf);
            }
        }*/
    }

    public static void blockHit(int entityID, Level world, BlockHitResult hitResult) {
        BulletEntity projectile = (BulletEntity) world.getEntity(entityID);
        int impactParticleCount = (int)(projectile.getDeltaMovement().lengthSqr() / 20);
        if (impactParticleCount > 0) {
            BlockState blockstate = world.getBlockState(hitResult.getBlockPos());
            BlockParticleOption particleOption = new BlockParticleOption(ParticleTypes.BLOCK, blockstate);
            Vec3 pos = hitResult.getLocation();
            Random random = new Random();
            for (int i = 0; i < impactParticleCount; ++i) {
                world.addParticle(
                        particleOption,
                        pos.x, pos.y, pos.z,
                        random.nextGaussian() * 0.01,
                        random.nextGaussian() * 0.01,
                        random.nextGaussian() * 0.01
                );
            }
        }
        projectile.discard();
    }

}
