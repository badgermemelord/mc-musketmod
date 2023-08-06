package ewewukek.musketmod.mechanics;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class OnSolidHit {

    public static void blockHit(HitResult hitResult, Vec3 projectilePath) {
        BlockHitResult blockHitResult = ((BlockHitResult) hitResult);
        Direction faceNormal = blockHitResult.getDirection();
        System.out.println("facing: " + faceNormal + " vec3: " + projectilePath.toString());

    }

    public static void getPlaneFromFacing(Direction facing) {
        
    }

}
