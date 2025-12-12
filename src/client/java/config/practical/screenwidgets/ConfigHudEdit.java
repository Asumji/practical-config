package config.practical.screenwidgets;

import config.practical.hud.ComponentEditScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class ConfigHudEdit extends ClickableWidget {

    private static final Text TEXT = Text.literal("Modify GUI");

    public static final int WIDTH = 150;
    public static final int HEIGHT = 20;

    public static final int TEXT_Y_OFFSET = 6;

    public static final int BACKGROUND_COLOR = 0xff666666;
    public static final int BLACK_COLOR = 0xff000000;
    public static final int WHITE_COLOR = 0xffffffff;

    public final Screen screen;

    public ConfigHudEdit(Screen screen, int x, int y) {
        super(x, y, WIDTH, HEIGHT, TEXT);
        this.screen = screen;
    }

    @Override
    protected void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int x = getX();
        int y = getY();
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        Text message = getMessage();

        context.fill(x, y, x + WIDTH, y + HEIGHT, BACKGROUND_COLOR);
        //context.drawBorder(x, y, WIDTH, HEIGHT, this.isFocused() ? WHITE_COLOR : BLACK_COLOR);

        int textWidth = textRenderer.getWidth(message);

        context.drawText(textRenderer, message, x + (WIDTH - textWidth) / 2, y + TEXT_Y_OFFSET, WHITE_COLOR, true);
    }

    @Override
    public void onClick(Click click, boolean doubled) {
        MinecraftClient.getInstance().setScreen(new ComponentEditScreen(screen));
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
