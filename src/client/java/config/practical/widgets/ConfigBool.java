package config.practical.widgets;

import config.practical.utilities.Constants;
import config.practical.utilities.DrawHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ConfigBool extends ClickableWidget {

    private static final Identifier BACKGROUND_SPRITE = Identifier.of(Constants.NAMESPACE, "button-background");

    public static final int HEIGHT = 25;

    public static final int TRACK_WIDTH = 38;
    public static final int TRACK_HEIGHT = 14;

    public static final int THUMB_SIZE = TRACK_HEIGHT + 2;
    public static final int THUMB_MARGIN = 8;

    public static final int THUMB_EXTENDED_X = TRACK_WIDTH - THUMB_SIZE + 2;

    public static final int DISABLED_COLOR = 0xffff0000;
    public static final int ENABLED_COLOR = 0xff00ff00;

    private static final float MAX_TICKS = 2f;

    private float totalTicks = MAX_TICKS;

    private final Consumer<Boolean> consumer;
    private final Supplier<Boolean> supplier;

    /**
     * @param message The text that will be displayed
     * @param supplier a supplier to get the current boolean
     * @param consumer a consumer to set the current boolean
     */
    public ConfigBool(Text message, Supplier<Boolean> supplier, Consumer<Boolean> consumer) {
        super(0, 0, Constants.WIDGET_WIDTH, HEIGHT, message);
        this.supplier = supplier;
        this.consumer = consumer;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int x = getX();
        int y = getY();

        if (totalTicks < MAX_TICKS) {
            totalTicks = Math.min(totalTicks + deltaTicks, MAX_TICKS);
        }
        ;

        float delta = totalTicks / MAX_TICKS;
        int lerpedPositon;
        int color;

        if (supplier.get()) {
            lerpedPositon = MathHelper.lerp(delta, 0, THUMB_EXTENDED_X);
            color = ENABLED_COLOR;
        } else {
            lerpedPositon = MathHelper.lerp(delta, THUMB_EXTENDED_X, 0);
            color = DISABLED_COLOR;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        //background and message
        DrawHelper.drawBackground(context, x, y, width, height);
        context.drawText(textRenderer, getMessage(), x + Constants.TEXT_PADDING, y + (HEIGHT - Constants.TEXT_HEIGHT) / 2, Constants.WHITE_COLOR, true);

        //the button itself
        DrawHelper.drawBackground(context, x + Constants.WIDGET_WIDTH - TRACK_WIDTH - THUMB_MARGIN, y + (HEIGHT - TRACK_HEIGHT) / 2, TRACK_WIDTH, TRACK_HEIGHT, color);
        DrawHelper.drawBackground(context, x + Constants.WIDGET_WIDTH - TRACK_WIDTH - THUMB_MARGIN + lerpedPositon - 1, y + (HEIGHT - TRACK_HEIGHT) / 2 - 1, THUMB_SIZE, THUMB_SIZE);
    }


    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        boolean newValue = !supplier.get();
        consumer.accept(newValue);
        totalTicks = 0;
        return super.mouseClicked(click, doubled);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
