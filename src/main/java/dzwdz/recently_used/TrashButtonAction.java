package dzwdz.recently_used;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;

public class TrashButtonAction implements ButtonWidget.PressAction {
    private CreativeInventoryScreen screen;

    public TrashButtonAction(AbstractInventoryScreen screen) {
        this.screen = (CreativeInventoryScreen) screen;
    }

    @Override
    public void onPress(ButtonWidget button) {
        EntryPoint.recent.clear();
        screen.getScreenHandler().itemList.clear();
        screen.getScreenHandler().scrollItems(0);
    }
}
