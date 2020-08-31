package dzwdz.recently_used.mixin;

import dzwdz.recently_used.EntryPoint;
import dzwdz.recently_used.TrashButtonAction;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen {
    public CreativeInventoryScreenMixin(ScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/screen/slot/SlotActionType;SWAP:Lnet/minecraft/screen/slot/SlotActionType;", ordinal = 0),
            method = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V")
    private void onMouseClick(@Nullable Slot slot, int invSlot, int clickData, SlotActionType actionType, CallbackInfo callbackInfo) {
        ItemStack stack = slot.getStack().copy();
        if (!stack.isEmpty()) EntryPoint.addToRecent(stack);
    }

    @Inject(at = @At("TAIL"),
            method = "init")
    private void createButton(CallbackInfo callbackInfo) {
        addButton(new TexturedButtonWidget(x + 180, y + 6, 8, 8, 0, 0, 8, EntryPoint.TRASH_ICON, 8, 16, new TrashButtonAction(this), new LiteralText("trash")));
    }
}
