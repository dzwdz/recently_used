package dzwdz.recently_used;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class TrashButton extends TexturedButtonWidget {
    private CreativeInventoryScreen screen;

    public TrashButton(int x, int y, AbstractInventoryScreen screen, Text text) {
        super(x, y, 8, 8, 0, 0, 8, EntryPoint.TRASH_ICON, 8, 16, new TrashButtonAction(screen), text);
        this.screen = (CreativeInventoryScreen) screen;
    }

    private boolean shouldShow() {
        return screen.getSelectedTab() == EntryPoint.RECENT_GROUP.getIndex();
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (shouldShow()) super.render(matrices, mouseX, mouseY, delta);
    }

    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);
        try {
            if (this.isHovered())
                screen.renderTooltip(matrices, getMessage(), mouseX, mouseY);
        } catch (NoSuchMethodError e) {} // 1.16.2 crash workaround
    }

    public void onPress() {
        if (shouldShow()) super.onPress();
    }
}

class TrashButtonAction implements ButtonWidget.PressAction {
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