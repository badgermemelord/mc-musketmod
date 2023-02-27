package ewewukek.musketmod.block;

import ewewukek.musketmod.block.blocks.CannonBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.Properties;

public class Blocks {
    public static final Block CANNON_BLOCK = new CannonBlock(FabricBlockSettings.of(Material.METAL));


}
