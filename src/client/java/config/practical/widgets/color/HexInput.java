package config.practical.widgets.color;

import config.practical.utilities.Constants;
import config.practical.utilities.DrawHelper;
import config.practical.widgets.abstracts.ConfigChild;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;

import java.util.Set;

class HexInput extends ConfigChild {

    public static final int TEXT_BACKGROUND_COLOR = 0xff222222;
    private static final Set<Character> ALLOWED_CHARS = Set.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f');

    private final ConfigColor parent;
    private String text;

    public HexInput(ConfigColor parent, int color) {
        super(parent, ConfigColor.CHILD_WIDTH, ConfigColor.SLIDER_HEIGHT);
        this.parent = parent;
        updateText(color);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.getKeycode() == GLFW.GLFW_KEY_BACKSPACE && !text.isEmpty()) {
            text = text.substring(0, text.length() - 1);
        }

        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (ALLOWED_CHARS.contains(Character.toChars(input.codepoint())[0]) && text.length() < 6) {
            text += (Character.toChars(input.codepoint())[0] + "").toUpperCase();
        }

        if (text.length() == 6) {
            try {
                int color = (int) Long.parseLong(text, 16);
                parent.setColor(color);
            } catch (NumberFormatException ignored) {
            }
        }

        return super.charTyped(input);
    }

    public void updateText(int color) {
        String temp = String.format("%06X", color);
        int length = temp.length();

        if (length > 6) {
            text = temp.substring(length - 6, length);
        }  else {
            text = temp;
        }
    }

    @Override
    protected boolean showWidget() {
        return parent.displayColorSelector();
    }

    @Override
    protected void updatePosition(int x, int y) {
        this.setX(x + ConfigColor.WIDTH + ConfigColor.CHILD_OFFSET);
        this.setY(y);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (!parent.displayColorSelector()) return;

        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        DrawHelper.drawBackground(context, x, y, width, height, TEXT_BACKGROUND_COLOR);
        context.drawText(MinecraftClient.getInstance().textRenderer, "#" + text, x + Constants.TEXT_PADDING, y + (height - Constants.TEXT_HEIGHT) / 2, Constants.WHITE_COLOR, true);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
