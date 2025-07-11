package com.thunderbear06.screen;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.menu.AndroidMenu;
import dan200.computercraft.client.gui.AbstractComputerScreen;
import dan200.computercraft.client.gui.GuiSprites;
import dan200.computercraft.client.gui.widgets.ComputerSidebar;
import dan200.computercraft.client.gui.widgets.TerminalWidget;
import dan200.computercraft.client.render.RenderTypes;
import dan200.computercraft.client.render.SpriteRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AndroidScreen extends AbstractComputerScreen<AndroidMenu> {
    private static final Identifier BACKGROUND_NORMAL = new Identifier(CCAndroids.MOD_ID, "textures/gui/android_normal.png");
    private static final Identifier BACKGROUND_ADVANCED = new Identifier(CCAndroids.MOD_ID, "textures/gui/android_advanced.png");
    private static final Identifier BACKGROUND_COMMAND = new Identifier(CCAndroids.MOD_ID, "textures/gui/android_command.png");

    public AndroidScreen(AndroidMenu container, PlayerInventory player, Text title) {
        super(container, player, title, 8);
        this.backgroundWidth = 295;
        this.backgroundHeight = 217;
    }

    protected TerminalWidget createTerminal() {
        return new TerminalWidget(this.terminalData, this.input, this.x + 8 + 17, this.y + 6);
    }

    protected void drawBackground(DrawContext graphics, float partialTicks, int mouseX, int mouseY) {
        Identifier texture = switch (family) {
            case NORMAL -> BACKGROUND_NORMAL;
            case ADVANCED -> BACKGROUND_ADVANCED;
            case COMMAND -> BACKGROUND_COMMAND;
        };

        graphics.drawTexture(texture, this.x + 17, this.y, 0, 0.0F, 0.0F, 278, 217, 512, 512);

        SpriteRenderer spriteRenderer = SpriteRenderer.createForGui(graphics, RenderTypes.GUI_SPRITES);
        ComputerSidebar.renderBackground(spriteRenderer, GuiSprites.getComputerTextures(this.family), this.x, this.y + this.sidebarYOffset);
        graphics.draw();
    }
}
