package ewewukek.musketmod.event;

import com.mojang.blaze3d.platform.InputConstants;
import ewewukek.musketmod.block.blocks.CannonBlock;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.TextComponent;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final String KEY_CATEGORY_CANNONS = "key.category.musketmod.cannons";
    public static final String KEY_AIM_CANNON = "key.musketmod.aim_cannon";

    public static KeyMapping cannonAimingKey;


    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(cannonAimingKey.consumeClick()) {
                //System.out.println("hehe");
                //client.player.sendMessage((TextComponent) "hehe");
                CannonBlock.aimIncreaseYaw();
            }
        });
    }

    public static void register() {
        cannonAimingKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                KEY_AIM_CANNON,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                KEY_CATEGORY_CANNONS
        ));
        registerKeyInputs();
    }
}
