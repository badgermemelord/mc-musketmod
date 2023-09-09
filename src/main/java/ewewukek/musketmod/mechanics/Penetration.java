package ewewukek.musketmod.mechanics;

import ewewukek.musketmod.BulletEntity;
import ewewukek.musketmod.projectileTypes.ReferenceValuesDeMarre;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static ewewukek.musketmod.projectileTypes.ReferenceValuesDeMarre.rVel;
import static ewewukek.musketmod.projectileTypes.ReferenceValuesDeMarre.rVelTicks;

public class Penetration {

    public static Vec3 penetration1b(BlockHitResult hitResult, Vec3 projectilePath, double impactAngle, Level level, BulletEntity bullet) {
        System.out.println("BLOCK BEING PENETRATED: " + hitResult.getBlockPos());

        System.out.println("projectile path: " + projectilePath);
        double velocity = projectilePath.length()*20;
        double penetration = calculateDeMarrePenetrationOfProjectile(velocity, bullet);
        System.out.println("penetration: " + penetration);
        double LoS = calculateLOSThicknessBackwards(hitResult, projectilePath, bullet, level);
        System.out.println("los: " + LoS);
        BlockState blockstate = level.getBlockState(hitResult.getBlockPos());
        int materialEffectiveness = MaterialProperties.MaterialEffectivenessMap.get(blockstate.getMaterial());
        double effectiveness = LoS * materialEffectiveness;
        System.out.println("effectivenes: " + effectiveness);
        double remainingPenetration = penetration - effectiveness;
        double remainingVelocity = 0;
        if (remainingPenetration > 0) {
            remainingVelocity = deMarreRemainingVelocity(remainingPenetration, penetration);
        }
        System.out.println("remaining vel: " + remainingVelocity);

        if (remainingVelocity > 0) {
            Vec3 exitPos = rayCastExitPosOfFirstBlock(hitResult.getBlockPos(), hitResult.getLocation().add(projectilePath), hitResult.getLocation());
            //bullet.setPos(exitPos);
            bullet.to = exitPos;
            System.out.println("exitpos: " + exitPos);
            return projectilePath.scale(remainingVelocity);
        }
        else {
            //bullet.setPos(hitResult.getLocation());
            bullet.to = hitResult.getLocation();
            return new Vec3(0,0,0);
        }
    }

    public static Vec3 penetrationRoutine(BlockHitResult hitResult, Vec3 projectilePath, double impactAngle, Level level, BulletEntity bullet) {
        BlockHitResult externalHitResult = hitResult;

        while (externalHitResult.getType() == HitResult.Type.BLOCK){
            System.out.println("bulletpos: " + bullet.position());
            projectilePath = attemptPenetrateBlock(externalHitResult, projectilePath, impactAngle, level, bullet);
            Vec3 exitPos = rayCastExitPosOfFirstBlock(externalHitResult.getBlockPos(), hitResult.getLocation().add(projectilePath), hitResult.getLocation());
            System.out.println("exitPos: " + exitPos);
            bullet.setPos(exitPos.add(projectilePath.scale(0.001)));
            System.out.println("new bulletpos: " + bullet.position());
            OnSolidHit.rayCastHitPos(level, exitPos, bullet.position().add(projectilePath), bullet);
            externalHitResult = level.clip(new ClipContext(exitPos, bullet.position().add(projectilePath), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, bullet));
        }
        return projectilePath;
    }

    public static Vec3 attemptPenetrateBlock(BlockHitResult hitResult, Vec3 projectilePath, double impactAngle, Level level, BulletEntity bullet) {
        double velocity = projectilePath.length()*20;
        double area = 3 * bullet.dia * bullet.dia;
        double energy = 0.5 * bullet.mass * velocity * velocity;
        double penetration = 1000;
        double LoS = calculateLOSThicknessBackwards(hitResult, projectilePath, bullet, level);
        System.out.println("los: " + LoS);
        BlockState blockstate = level.getBlockState(hitResult.getBlockPos());
        int materialEffectiveness = MaterialProperties.MaterialEffectivenessMap.get(blockstate.getMaterial());
        double effectiveness = LoS * materialEffectiveness;
        System.out.println("effectivenes: " + effectiveness);
        double remainingPenetration = penetration - effectiveness;
        double ratio = remainingPenetration / penetration;

        if (remainingPenetration > 0) {
            return projectilePath.scale(ratio);
        }
        else {
            return new Vec3(0,0,0);
        }
    }

    public static double calculateLOSThicknessBackwards(BlockHitResult hitResult, Vec3 projectilePath, Entity bullet, Level level) {
        //Forwards cast
        Vec3 from = hitResult.getLocation().add(projectilePath);
        Vec3 to = hitResult.getLocation();
        //Vec3 hitPos = rayCastExitPosOfFirstBlock(hitResult.getBlockPos(), from, to);
        //return hitResult.getLocation().distanceTo(hitPos);
        Vec3 hitPos = rayCastExitPosOfHitBlockWithFallback(hitResult.getBlockPos(), from, to);
        if (hitPos != null) {
            return hitResult.getLocation().distanceTo(hitPos);
        }
        else {
            return 1;
        }

    }


    public static Vec3 rayCastExitPosOfFirstBlock(BlockPos hitBlock, Vec3 from, Vec3 to) {
        AABB aabb = new AABB(hitBlock.getX(), hitBlock.getY(), hitBlock.getZ(), hitBlock.getX()+1, hitBlock.getY()+1, hitBlock.getZ()+1);
        Optional<Vec3> hitPos = aabb.clip(from, to);
        return hitPos.orElse(null);
    }

    public static double cacheDeMarreEquation(BulletEntity bullet) {
        //Reference values
        double rPen = ReferenceValuesDeMarre.rPen;
        double rDia = ReferenceValuesDeMarre.rDia;
        double rMass = ReferenceValuesDeMarre.rMass;

        return rPen * Math.pow((bullet.dia/rDia), 1.0714) * Math.pow((bullet.mass/Math.pow(bullet.dia, 3)), 0.7143) / Math.pow((rMass/Math.pow(rDia, 3)), 0.7143);
    }

    public static double calculateDeMarrePenetrationOfProjectile(double velocity, BulletEntity bullet) {
        return Math.pow(velocity/rVel, 1.4283) * bullet.cachedDeMarre;
    }

    public static double deMarreRemainingVelocity(double remainingPenetration, double initialPenetration) {
        return Math.pow(remainingPenetration/initialPenetration, 1/1.4283);
    }

    //ATTEMPT AT MAKING EXITPOS VS COMPATIBLE

    public static Vec3 rayCastExitPosOfHitBlockWithFallback(BlockPos hitBlock, Vec3 from, Vec3 to) {
        if (hitBlock.closerThan(new Vec3i(0,0,0), 1000000)) {
            AABB aabb = new AABB(hitBlock.getX(), hitBlock.getY(), hitBlock.getZ(), hitBlock.getX()+1, hitBlock.getY()+1, hitBlock.getZ()+1);
            Optional<Vec3> hitPos = aabb.clip(from, to);
            return hitPos.orElse(null);
        }
        else {
            return null;
        }
    }

    public static Vec3 test(BlockPos hitBlock, Vec3 from, Vec3 to, Level level) {
        level.clip(new ClipContext(from, to, ClipContext.Block.OUTLINE, ))
        AABB aabb = new AABB(hitBlock.getX(), hitBlock.getY(), hitBlock.getZ(), hitBlock.getX()+1, hitBlock.getY()+1, hitBlock.getZ()+1);
        Optional<Vec3> hitPos = aabb.clip(from, to);
        return hitPos.orElse(null);
    }

}
