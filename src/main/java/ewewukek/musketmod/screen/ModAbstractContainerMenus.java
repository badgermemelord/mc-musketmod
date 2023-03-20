package ewewukek.musketmod.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class ModAbstractContainerMenus {
    public static MenuType<CannonBlockAbstractContainerMenu> CANNON_BLOCK_ABSTRACT_CONTAINER_MENU =
            ScreenHandlerRegistry.registerSimple(new ResourceLocation("musketmod", "cannon_block"),
                    CannonBlockAbstractContainerMenu::new);
}

