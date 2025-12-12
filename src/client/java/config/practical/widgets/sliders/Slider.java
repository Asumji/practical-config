package config.practical.widgets.sliders;

import config.practical.utilities.Constants;
import config.practical.utilities.DrawHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public abstract class Slider extends ClickableWidget {

    private static final int HEIGHT = 30;

    private static final int THUMB_RADIUS = 7;

    private static final int SLIDER_WIDTH = 100;
    private static final int SLIDER_HEIGHT = 14;
    private static final int SLIDER_RIGHT_PADDING = 7;
    private static final int SLIDER_BACKGROUND_COLOR = 0xff444444;


    private int thumbPos;
    private boolean isDragging = false;

    public Slider(Text message) {
        super(0, 0, Constants.WIDGET_WIDTH, HEIGHT, message);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        int sliderX = getSliderX();
        int sliderY = getSliderY();

        String currStr = getCurrValue();

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        //background
        DrawHelper.drawBackground(context, x, y, width, height);
        context.drawText(textRenderer, getMessage(), x + Constants.TEXT_PADDING, y + (HEIGHT - Constants.TEXT_HEIGHT) / 2, Constants.WHITE_COLOR, true);

        //slider
        DrawHelper.drawBackground(context, sliderX, sliderY, SLIDER_WIDTH, SLIDER_HEIGHT, SLIDER_BACKGROUND_COLOR);

        //thumb
        DrawHelper.drawBackground(context, sliderX + thumbPos, sliderY, THUMB_RADIUS * 2, THUMB_RADIUS * 2);

        context.drawText(textRenderer, currStr, sliderX - textRenderer.getWidth(currStr) - 2, y + (HEIGHT - Constants.TEXT_HEIGHT) / 2, Constants.WHITE_COLOR, true);
    }

    abstract String getCurrValue();

    abstract void updateValue(float delta);

    protected int getSliderX() {
        return getX() + getWidth() - SLIDER_WIDTH - SLIDER_RIGHT_PADDING;
    }

    protected int getSliderY() {
        return getY() + (HEIGHT - SLIDER_HEIGHT) / 2;
    }

    protected boolean insideSlider(int x, int y) {
        int sliderX = getSliderX();
        int sliderY = getSliderY();

        return x >= sliderX && x <= sliderX + SLIDER_WIDTH && y >= sliderY && y <= sliderY + SLIDER_HEIGHT;
    }

    protected void updateThumbPosWithDelta(float delta) {
        float clampedDelta = MathHelper.clamp(delta, 0, 1);
        thumbPos = (int)(clampedDelta * (SLIDER_WIDTH - THUMB_RADIUS * 2));
    }

    public void updateThumbPos(double mouseX) {
        thumbPos = MathHelper.clamp((int) mouseX - getSliderX() - THUMB_RADIUS, 0, (SLIDER_WIDTH - THUMB_RADIUS * 2));
        float delta = (float) thumbPos / (SLIDER_WIDTH - THUMB_RADIUS * 2);
        updateValue(delta);
    }

    @Override
    public void onRelease(Click click) {
        super.onRelease(click);
        isDragging = false;
    }

    @Override
    public void onClick(Click click, boolean doubled) {
        super.onClick(click, doubled);
        if (insideSlider((int) click.x(), (int) click.y())) {
            isDragging = true;
            updateThumbPos(click.x());
        }
    }

    @Override
    protected void onDrag(Click click, double offsetX, double offsetY) {
        super.onDrag(click, offsetX, offsetY);
        if (isDragging) {
            updateThumbPos(click.x());
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
