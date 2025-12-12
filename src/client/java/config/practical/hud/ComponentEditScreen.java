package config.practical.hud;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class ComponentEditScreen extends Screen {

    private static final Text TITLE = Text.literal("");
    private static final Text INFO_SCALE_TEXT = Text.literal("Use the mouse wheel to increase or decrease the scale");
    private static final Text INFO_RESET_TEXT = Text.literal("Press r to reset the selected component");

    private static final int RESET_WIDTH = 100;
    private static final int WIDGET_HEIGHT = 20;
    private static final int WIDGET_MARGIN = 4;

    private static final float SCALE_FACTOR = 0.02f;

    private static final ArrayList<HUDComponent> ALL_COMPONENTS = new ArrayList<>();

    private final ArrayList<HUDComponent> components;

    private final Screen parent;

    private HUDComponent selected;
    private boolean isDragging = false;

    public ComponentEditScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
        this.components = new ArrayList<>();

        ALL_COMPONENTS.forEach(component -> {
            if (component.editable()) {
                components.add(component);
            }
        });
    }

    public static void addComponent(HUDComponent component) {
        if (component == null) return;
        ALL_COMPONENTS.add(component);
    }

    @Override
    protected void init() {

        assert this.client != null;
        Window window = this.client.getWindow();
        int windowWidth = window.getScaledWidth();
        int windowHeight = window.getScaledHeight();


        ButtonWidget reset = ButtonWidget.builder(Text.literal("Reset All"),
                        (button -> components.forEach(HUDComponent::reset))
                ).position((windowWidth - RESET_WIDTH) / 2, (windowHeight - WIDGET_HEIGHT) / 2)
                .size(RESET_WIDTH, WIDGET_HEIGHT)
                .build();

        assert this.client != null;
        TextWidget infoScale = new TextWidget(INFO_SCALE_TEXT, this.client.textRenderer);
        infoScale.setPosition((windowWidth - infoScale.getWidth()) / 2, (windowHeight - WIDGET_HEIGHT) / 2 + WIDGET_HEIGHT + WIDGET_MARGIN);
        TextWidget infoReset = new TextWidget(INFO_RESET_TEXT, this.client.textRenderer);
        infoReset.setPosition((windowWidth - infoReset.getWidth()) / 2, (windowHeight - WIDGET_HEIGHT) / 2 + 2 * (WIDGET_HEIGHT + WIDGET_MARGIN));

        addDrawableChild(reset);
        addDrawableChild(infoScale);
        addDrawableChild(infoReset);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (selected != null) {
            float newScale = selected.getScale() + (float) Math.signum(verticalAmount) * SCALE_FACTOR;
            selected.setScale(newScale);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        int x = (int) click.x();
        int y = (int) click.y();

        selected = null;
        for (HUDComponent component : components) {

            if (component.inBounds(x, y)) {
                selected = component;
                isDragging = true;
                break;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (isDragging && selected != null) {
            assert client != null;
            Window window = client.getWindow();
            selected.move(offsetY / window.getScaledWidth(), offsetX / window.getScaledHeight());
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        isDragging = false;
        return super.mouseReleased(click);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.getKeycode() == GLFW.GLFW_KEY_R && selected != null) {
            selected.reset();
        }

        return super.keyPressed(input);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        if (selected != null) {
            selected.renderHighlight(context);
        }

        for (HUDComponent component : components) {
            component.renderIgnoreConditions(context);
        }
    }

    @Override
    public void close() {
        assert this.client != null;
        this.client.setScreen(parent);
    }
}
