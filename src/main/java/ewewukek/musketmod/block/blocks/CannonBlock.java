package ewewukek.musketmod.block.blocks;

import com.mojang.math.Quaternion;
import ewewukek.musketmod.GunItem;
import ewewukek.musketmod.MusketMod;
import ewewukek.musketmod.block.entity.CannonBlockEntity;
import ewewukek.musketmod.block.entity.ModBlockEntities;
import ewewukek.musketmod.entity.bullet.BulletEntity;
import ewewukek.musketmod.entity.shell.ShellEntity;
import ewewukek.musketmod.screen.CannonBlockAbstractContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static java.lang.Math.*;
import static net.minecraft.util.Mth.cos;
import static net.minecraft.util.Mth.sin;
import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class CannonBlock extends BaseEntityBlock implements EntityBlock {
/*    public CannonBlock(Properties properties) {
        super(properties);
    }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }*/
public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public CannonBlock(Properties settings) {
        super(settings);
    }


    //Block Entity stuff

/*    float bulletStdDev() {
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

    float aimAdjustElevation() {
        return (float) (0 * PI / 180);
    }

    float aimAdjustYaw() {
        return (float) (45 * PI / 180);
    }*/
@Nullable
@Override
public BlockState getStateForPlacement(BlockPlaceContext ctx) {
    return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
}

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide) {
            /*Vec3i frontI = state.getValue(FACING).getNormal();
            Vec3 front = new Vec3((float)frontI.getX(), (float)frontI.getY(), (float)frontI.getZ());
            Vec3 aimVector = adjustAim(state.getValue(FACING));
            Vec3 origin = getOrigin(front, pos);

            cannonFire(player, aimVector, aimVector, pos, origin);*/
            MenuProvider screenHandlerFactory = state.getMenuProvider(world, pos);
            if (screenHandlerFactory != null) {
                player.openMenu(screenHandlerFactory);
            }
        }
        return InteractionResult.SUCCESS;
    }

/*    public Vec3 adjustAim(Direction facing) {
        //TODO actual aim code
        //float normalAngle = (float) (asin(front.x)+(acos(front.x)));
        float normalAngle = (float) (facing.toYRot() * PI / 180);
        float finalAngle = normalAngle + aimAdjustYaw();
        float aimX = -sin(finalAngle);
        System.out.println(sin(finalAngle));
        float aimZ = cos(finalAngle);
        float aimY = sin(aimAdjustElevation());

        Vec3 aimVector = new Vec3(aimX, aimY, aimZ);
        return aimVector;
    }

    public Vec3 getOrigin(Vec3 front, BlockPos pos) {
        Vec3 posCenter = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        return new Vec3(posCenter.x + front.x, posCenter.y + 0.5, posCenter.z + front.z);
    }


    public void cannonFire(LivingEntity shooter, Vec3 direction, Vec3 smokeOriginOffset, BlockPos pos, Vec3 origin) {


        Random random = shooter.getRandom();
        Level level = shooter.level;

        float angle = (float) PI * 2 * random.nextFloat();
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
                .add(n1.scale(sin(spread) * sin(angle))) // signs are not important for random angle
                .add(n2.scale(sin(spread) * Mth.cos(angle)))
                .scale(bulletSpeed());

        //Vec3 origin = new Vec3(pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5);

        ShellEntity shell = new ShellEntity(level);
        shell.setOwner(shooter);
        shell.setPos(origin);
        shell.setInitialSpeed(bulletSpeed());
        shell.setDeltaMovement(motion);
        float t = random.nextFloat();
        shell.damageMultiplier = t * damageMultiplierMin() + (1 - t) * damageMultiplierMax();
        //bullet.ignoreInvulnerableTime = ignoreInvulnerableTime();
        shell.ignoreInvulnerableTime = false;

        level.addFreshEntity(shell);
        MusketMod.sendSmokeEffect(shooter, origin, direction);
    }*/

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CannonBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.CANNON_BLOCK, CannonBlockEntity::tick);
    }
}

/*    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return null;
    }

}*/
