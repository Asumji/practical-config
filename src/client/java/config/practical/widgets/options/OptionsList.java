package config.practical.widgets.options;

import config.practical.utilities.Constants;
import config.practical.utilities.DrawHelper;
import config.practical.widgets.abstracts.ConfigChild;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;

import java.util.function.Consumer;

class OptionsList <T> extends ConfigChild {

    public static final int WIDTH = 80;
    public static final int ELEMENT_HEIGHT = 20;
    private final T[] options;
    private final ConfigOptions<T> parent;

    private final Consumer<T> consumer;

    public OptionsList(ConfigOptions<T> parent, T[] options, Consumer<T> consumer) {
        super(parent, WIDTH, options.length * ELEMENT_HEIGHT);
        this.parent = parent;
        this.options = options;
        this.consumer = consumer;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {

        if (!parent.displayList()) return;

        int x = getX();
        int y = getY();

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        DrawHelper.drawBackground(context, x, y, width, ELEMENT_HEIGHT* options.length);

        for (int i = 0; i < options.length; i++) {
            T option = options[i];
            context.drawText(textRenderer, option.toString(),x + Constants.TEXT_PADDING, y + ELEMENT_HEIGHT * i + ((ELEMENT_HEIGHT - Constants.TEXT_HEIGHT) / 2), 0xffffffff, true);
        }

    }

    @Override
    protected boolean showWidget() {
        return parent.displayList();
    }

    @Override
    protected void updatePosition(int x, int y) {
        this.setX(x + ConfigOptions.WIDTH - WIDTH - ConfigOptions.MARGIN);
        this.setY(y + ConfigOptions.MARGIN + ELEMENT_HEIGHT);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public void onClick(Click click, boolean doubled) {
        super.onClick(click, doubled);
        int index =(int) (click.y() - getY()) / ELEMENT_HEIGHT;
        if (index < 0 || index >= options.length) return;
        consumer.accept(options[index]);
        parent.hideList();
    }
}
