package ewewukek.musketmod.mechanics;

import net.minecraft.world.level.levelgen.material.MaterialRuleList;
import net.minecraft.world.level.material.Material;

import java.util.HashMap;

public class MaterialProperties {

    public static HashMap<Material, Integer> MaterialEffectivenessMap = new HashMap<>();

    public static void initiateMaterialEffectivenessMap() {
        MaterialEffectivenessMap.put(Material.AIR, 0);
        MaterialEffectivenessMap.put(Material.WOOD, 50);
        MaterialEffectivenessMap.put(Material.DIRT, 40);
        MaterialEffectivenessMap.put(Material.GRASS, 40);
        MaterialEffectivenessMap.put(Material.STONE, 200);
        MaterialEffectivenessMap.put(Material.METAL, 500);
        MaterialEffectivenessMap.put(Material.AMETHYST, 100);
        MaterialEffectivenessMap.put(Material.GLASS, 60);
        MaterialEffectivenessMap.put(Material.BAMBOO, 10);
        MaterialEffectivenessMap.put(Material.BARRIER, 10000);
        MaterialEffectivenessMap.put(Material.CACTUS, 5);
        MaterialEffectivenessMap.put(Material.BUILDABLE_GLASS, 60);
        MaterialEffectivenessMap.put(Material.CLAY, 100);
        MaterialEffectivenessMap.put(Material.DECORATION, 0);
        MaterialEffectivenessMap.put(Material.FIRE, 0);
        MaterialEffectivenessMap.put(Material.ICE, 30);
        MaterialEffectivenessMap.put(Material.ICE_SOLID, 40);
        MaterialEffectivenessMap.put(Material.LEAVES, 0);
        MaterialEffectivenessMap.put(Material.PLANT, 0);
        MaterialEffectivenessMap.put(Material.SAND, 40);
        MaterialEffectivenessMap.put(Material.SNOW, 1);
        MaterialEffectivenessMap.put(Material.SPONGE, 1);
        MaterialEffectivenessMap.put(Material.WOOL, 2);
        MaterialEffectivenessMap.put(Material.WEB, 0);
        MaterialEffectivenessMap.put(Material.PISTON, 100);
        System.out.println("Loaded material armour values");
    }



}
