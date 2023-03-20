package ewewukek.musketmod.block.entity;

import ewewukek.musketmod.block.Blocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static BlockEntityType<CannonBlockEntity> CANNON_BLOCK;

    public static void registerAllBlockEntities() {
        CANNON_BLOCK = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new ResourceLocation("musketmod", "cannon_block"),
                FabricBlockEntityTypeBuilder.create(CannonBlockEntity::new,
                        Blocks.CANNON_BLOCK).build(null));
    }
}
