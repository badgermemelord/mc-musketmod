package ewewukek.musketmod.mechanics;

import ewewukek.musketmod.BulletEntity;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.UUID;

public class ClientMethods {

    public static void updateTrajectoryOnHit(int entityID, Level world, Vec3 newTrajectory) {
        //Entity projectile = world.getEntity(entityID);
        BulletEntity projectile = (BulletEntity) world.getEntity(entityID);
        projectile.setDeltaMovement(newTrajectory);
    }

    public static void blockHit(int entityID, Level world, BlockHitResult hitResult) {
        System.out.println("client block hit");
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
