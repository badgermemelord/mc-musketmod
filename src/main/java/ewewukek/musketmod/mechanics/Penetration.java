package ewewukek.musketmod.mechanics;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class Penetration {

    public static double calculateLOSThicknessForwards(BlockHitResult hitResult, Vec3 projectilePath, Entity bullet, Level level) {
/*        //Forwards cast
        System.out.println("cast from: " + hitResult.getLocation());
        Vec3 from = hitResult.getLocation();
        Vec3 to = from.add(projectilePath);
        Vec3 hitPos = rayCastExitPosOfFirstBlock(from, to, level, bullet);
        System.out.println("cast hit: " + hitPos);*/
        return 5;
    }

    public static double calculateLOSThicknessBackwards(BlockHitResult hitResult, Vec3 projectilePath, Entity bullet, Level level) {
        //Forwards cast
        System.out.println("cast from: " + hitResult.getLocation());
        Vec3 from = hitResult.getLocation().add(projectilePath);
        Vec3 to = hitResult.getLocation();
        Vec3 hitPos = rayCastExitPosOfFirstBlock(hitResult.getBlockPos(), from, to);
        System.out.println("cast hit: " + hitPos);
        return hitResult.getLocation().distanceTo(hitPos);
    }


    public static Vec3 rayCastExitPosOfFirstBlock(BlockPos hitBlock, Vec3 from, Vec3 to) {
        AABB aabb = new AABB(hitBlock.getX(), hitBlock.getY(), hitBlock.getZ(), hitBlock.getX()+1, hitBlock.getY()+1, hitBlock.getZ()+1);
        Optional<Vec3> hitPos = aabb.clip(from, to);
        return hitPos.orElse(null);
    }

}
