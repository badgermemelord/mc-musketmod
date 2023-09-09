package ewewukek.musketmod.mixin;

import ewewukek.musketmod.GunItem;
import ewewukek.musketmod.MusketItem;
import ewewukek.musketmod.PistolItem;
import ewewukek.musketmod.mechanics.Cooldowns;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class MixinPlayer {

    @Shadow @Final private Inventory inventory;

    @Unique
    Player player = Player.class.cast(this);


    @Shadow public abstract ItemCooldowns getCooldowns();

    @Inject(method = "tick", at = @At("HEAD"), cancellable = false)
    public void tick(CallbackInfo ci) {
        if (player.level.isClientSide) {
            Inventory inventory = this.inventory;
            if ((Cooldowns.playerSelectedMap.get(player)) != null && (inventory.selected != Cooldowns.playerSelectedMap.get(player))) {
                Item item = inventory.getItem(inventory.selected).getItem();
                if (item instanceof PistolItem) {
                    this.getCooldowns().addCooldown(inventory.getItem(inventory.selected).getItem(), 60);
                }
                else if (item instanceof GunItem){
                    this.getCooldowns().addCooldown(item, 80);
                }
            }
            Cooldowns.playerSelectedMap.put(player, inventory.selected);
        }

    }

}
