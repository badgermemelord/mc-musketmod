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
        Direction faceNormal = blockHitResult.getDirection();
        //System.out.println("facing: " + faceNormal + " vec3: " + projectilePath.toString());
        double impactAngle = getTrajectoryToNormalAngle(projectilePath, getNormalFromFacing(faceNormal));
        if (impactAngle < allRicochetAngle) {
            projectileRicochet();
        }
    }

    public static void projectileRicochet() {

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
