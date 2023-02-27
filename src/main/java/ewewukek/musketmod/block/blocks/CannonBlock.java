package ewewukek.musketmod.block.blocks;

import ewewukek.musketmod.GunItem;
import ewewukek.musketmod.MusketMod;
import ewewukek.musketmod.entity.bullet.BulletEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class CannonBlock extends BaseEntityBlock implements EntityBlock {
    public CannonBlock(Properties properties) {
        super(properties);
    }


    //Block Entity stuff

    float x = 1;
    float z = 1;
    float y = 1;

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide) {
            System.out.println("success");
            //Vec3 front = Vec3.directionFromRotation(player.getXRot(), player.getYRot());
            Vec3 front = new Vec3(0,0,0.5);
            cannonFire(player, front, front, pos);
        }
        return InteractionResult.SUCCESS;
    }

    float bulletStdDev() {
        return 0;
    }
    float bulletSpeed() {
        return 10;
    }
    float damageMultiplierMin() {
        return 100;
    }
    float damageMultiplierMax() {
        return 101;
    }


    public void cannonFire(LivingEntity shooter, Vec3 direction, Vec3 smokeOriginOffset, BlockPos pos) {


        Random random = shooter.getRandom();
        Level level = shooter.level;

        float angle = (float) Math.PI * 2 * random.nextFloat();
        float gaussian = Math.abs((float) random.nextGaussian());
        if (gaussian > 4) gaussian = 4;

        float spread = bulletStdDev() * gaussian;

        // a plane perpendicular to direction
        Vec3 n1;
        Vec3 n2;
        if (Math.abs(direction.x) < 1e-5 && Math.abs(direction.z) < 1e-5) {
            n1 = new Vec3(1, 0, 0);
            n2 = new Vec3(0, 0, 1);
        } else {
            n1 = new Vec3(-direction.z, 0, direction.x).normalize();
            n2 = direction.cross(n1);
        }

        Vec3 motion = direction.scale(Mth.cos(spread))
                .add(n1.scale(Mth.sin(spread) * Mth.sin(angle))) // signs are not important for random angle
                .add(n2.scale(Mth.sin(spread) * Mth.cos(angle)))
                .scale(bulletSpeed());

        Vec3 origin = new Vec3(pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5);

        BulletEntity bullet = new BulletEntity(level);
        bullet.setOwner(shooter);
        bullet.setPos(origin);
        bullet.setInitialSpeed(bulletSpeed());
        bullet.setDeltaMovement(motion);
        float t = random.nextFloat();
        bullet.damageMultiplier = t * damageMultiplierMin() + (1 - t) * damageMultiplierMax();
        //bullet.ignoreInvulnerableTime = ignoreInvulnerableTime();
        bullet.ignoreInvulnerableTime = false;

        level.addFreshEntity(bullet);
        MusketMod.sendSmokeEffect(shooter, origin.add(smokeOriginOffset), direction);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return null;
    }
}
