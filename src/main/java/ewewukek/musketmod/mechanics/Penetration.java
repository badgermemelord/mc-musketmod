package ewewukek.musketmod.mechanics;

import ewewukek.musketmod.BulletEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class Penetration {

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
        double area = 3 * bullet.diameter * bullet.diameter;
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
        Vec3 hitPos = rayCastExitPosOfFirstBlock(hitResult.getBlockPos(), from, to);
        return hitResult.getLocation().distanceTo(hitPos);
    }


    public static Vec3 rayCastExitPosOfFirstBlock(BlockPos hitBlock, Vec3 from, Vec3 to) {
        AABB aabb = new AABB(hitBlock.getX(), hitBlock.getY(), hitBlock.getZ(), hitBlock.getX()+1, hitBlock.getY()+1, hitBlock.getZ()+1);
        Optional<Vec3> hitPos = aabb.clip(from, to);
        return hitPos.orElse(null);
    }

}
