package ewewukek.musketmod.block.entity;

import ewewukek.musketmod.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CannonBlockEntity extends BlockEntity {
        public CannonBlockEntity(BlockPos pos, BlockState state) {
            super(ModBlockEntities.CANNON_BLOCK, pos, state);
        }

    }

