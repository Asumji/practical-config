package config.practical.widgets.color;

import config.practical.utilities.Constants;
import config.practical.utilities.DrawHelper;
import config.practical.widgets.abstracts.ConfigParent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ConfigColor extends ConfigParent {

    public static final int WIDTH = Constants.WIDGET_WIDTH;
    public static final int HEIGHT = 30;

    public static final int COLOR_SIZE = 20;
    public static final int COLOR_RIGHT_PADDING = 8;

    public static final int CHILD_WIDTH = 68;
    public static final int CHILD_OFFSET = 3;
    public static final int SLIDER_HEIGHT = 14;

    private final Consumer<Integer> consumer;
    private final Supplier<Integer> supplier;

    private final HexInput hexInput;
    private final SBSelector selector;
    private final HueSlider hueSlider;
    private AlphaSlider alphaSlider;

    private boolean displayColorSelector;
    private final boolean transparency;

    private float hueValue;
    private int alphaValue;

    /**
     *
     * @param message      The text of the widget
     * @param supplier     a supplier that returns the value of the current color
     * @param consumer     a consumer that sets the current color
     * @param identifier   a string that's unique to link the sb-mask to
     * @param transparency true for transparency, else false
     */
    public ConfigColor(Text message, Supplier<Integer> supplier, Consumer<Integer> consumer, String identifier, boolean transparency) {
        super(0, 0, Constants.WIDGET_WIDTH, HEIGHT, message);
        this.supplier = supplier;
        this.consumer = consumer;

        //get current hue value
        float[] hsb = new float[3];
        int color = supplier.get();

        alphaValue = (color >> 24 & 0xff);
        int r = (color >> 16 & 0xff);
        int g = (color >> 8 & 0xff);
        int b = (color & 0xff);

        Color.RGBtoHSB(r, g, b, hsb);
        hueValue = hsb[0];

        hexInput = new HexInput(this, supplier.get());
        this.addChild(hexInput);

        selector = new SBSelector(this, identifier, hueValue);
        this.addChild(selector);

        hueSlider = new HueSlider(this, hueValue);
        this.addChild(hueSlider);

        this.transparency = transparency;
        if (transparency) {
            alphaSlider = new AlphaSlider(this, alphaValue);
            this.addChild(alphaSlider);
        } else {
            alphaValue = 0;
        }

        displayColorSelector = false;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {

        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        DrawHelper.drawBackground(context, x, y, width, height);
        context.drawText(MinecraftClient.getInstance().textRenderer, getMessage(), x + Constants.TEXT_PADDING, y + (HEIGHT - Constants.TEXT_HEIGHT) / 2, Constants.WHITE_COLOR, true);

        if (transparency) {
            DrawHelper.drawBackground(context, x + width - COLOR_SIZE - COLOR_RIGHT_PADDING, y + (height - COLOR_SIZE) / 2, COLOR_SIZE, COLOR_SIZE, supplier.get());
        } else {
            DrawHelper.drawBackground(context, x + width - COLOR_SIZE - COLOR_RIGHT_PADDING, y + (height - COLOR_SIZE) / 2, COLOR_SIZE, COLOR_SIZE, 255 << 24 | supplier.get());
        }
    }

    public boolean displayColorSelector() {
        return displayColorSelector;
    }

    @Override
    public void onClick(Click click, boolean doubled) {
        super.onClick(click, doubled);
        displayColorSelector = !displayColorSelector;
        update();
    }

    public void setHueValue(float hue) {
        selector.makeSBMask(hue);
        hueValue = hue;
    }

    public void setColor(int color) {

        if (transparency) {
            consumer.accept(alphaValue << 24 | color);
        } else {
            consumer.accept(color);
        }
        float[] hsb = new float[3];

        int r = (color >> 16 & 0xff);
        int g = (color >> 8 & 0xff);
        int b = (color & 0xff);

        Color.RGBtoHSB(r, g, b, hsb);

        hueValue = hsb[0];
        hueSlider.setHueValue(hueValue);
        selector.makeSBMask(hueValue);
    }

    public void setAlphaValue(int alpha) {
        int color = supplier.get();

        int r = (color >> 16 & 0xff);
        int g = (color >> 8 & 0xff);
        int b = (color & 0xff);

        int newColor = alpha << 24 | r << 16 | g << 8 | b;
        consumer.accept(newColor);
        hexInput.updateText(newColor);
        alphaValue = alpha;

    }

    public void setSBValue(float saturation, float brightness) {
        int color = Color.HSBtoRGB(hueValue, saturation, brightness);
        hexInput.updateText(color);
        if (transparency) {
            consumer.accept(alphaValue << 24 | color);
        } else {
            consumer.accept(color);
        }
    }

    @Override
    public void hideAll() {
        displayColorSelector = false;
    }
}
