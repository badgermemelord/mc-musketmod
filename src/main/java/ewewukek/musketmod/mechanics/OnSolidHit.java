package ewewukek.musketmod.mechanics;

import com.mojang.math.Vector3d;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.lang.Math;

public class OnSolidHit {

    public static void blockHit(HitResult hitResult, Vec3 projectilePath) {
        double noRicochetAngle = 40.0;
        double allRicochetAngle = 20.0;
        BlockHitResult blockHitResult = ((BlockHitResult) hitResult);
        Vec3 faceNormalVec = getNormalFromFacing(blockHitResult.getDirection());
        double impactAngle = getTrajectoryToNormalAngle(projectilePath, faceNormalVec);
        if (impactAngle < allRicochetAngle) {
            Vec3 newTrajectory = getRicochetVector(projectilePath, faceNormalVec);
            projectileRicochet(hitResult);
        }
    }

    public static void projectileRicochet(HitResult hitResult) {
    }

    public static Vec3 getRicochetVector(Vec3 trajectory, Vec3 normal) {
        double a0 = normal.x;
        double b0 = normal.y;
        double c0 = normal.z;

        double a1 = - trajectory.x;
        double b1 = - trajectory.y;
        double c1 = - trajectory.z;

        double a2 = a0*a1;
        double b2 = b0*b1;
        double c2 = c0*c1;

        return new Vec3(a2, b2, c2);
    }

    public static Vec3 getNormalFromFacing(Direction facing) {
        return new Vec3(facing.getNormal().getX(), facing.getNormal().getY(), facing.getNormal().getZ());
    }

    public static double getTrajectoryToNormalAngle(Vec3 trajectory, Vec3 normal) {
        double dotProduct = trajectory.dot(normal);
        double normalMagnitude = normal.length();
        double trajectoryMagnitude = trajectory.length();
        double angle =  Math.acos(dotProduct/(trajectoryMagnitude*normalMagnitude));
        double angleDeg = angle*57.296;

        System.out.println("l'angle est: " + angleDeg);
        System.out.println("l'angle final est: " + (angleDeg-90.0));
        return angleDeg;
    }

}
