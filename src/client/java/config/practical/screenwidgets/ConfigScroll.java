package config.practical.screenwidgets;

import config.practical.widgets.abstracts.ConfigChild;
import config.practical.widgets.abstracts.ConfigParent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConfigScroll extends ContainerWidget {

    private static final int ITEM_MARGIN = 4;
    private static final int SLIDER_X_OFFSET = 24;

    private final ArrayList<ClickableWidget> children;
    private final ArrayList<ClickableWidget> childWidgets;
    private int contentHeight;
    private final int maxItemWidth;

    public ConfigScroll(int x, int y, int width, int height, int maxItemWidth) {
        super(x, y, width, height, Text.empty());
        this.children = new ArrayList<>();
        this.childWidgets = new ArrayList<>();
        this.contentHeight = 0;
        this.maxItemWidth = maxItemWidth;
        update();
    }

    @Override
    public List<? extends Element> children() {
        return children;
    }

    public void add(ClickableWidget widget) {
        if (widget == null) return;
        children.add(widget);

        if (widget instanceof ConfigChild child) {
            childWidgets.add(child);
        }
    }

    @Override
    public void setFocused(@Nullable Element focused) {
        super.setFocused(focused);
        hideChildComponents();

    }

    @Override
    protected int getContentsHeightWithPadding() {
        return contentHeight;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return 10;
    }

    @Override
    protected int getScrollbarX() {
        return (width + maxItemWidth) / 2 + SLIDER_X_OFFSET;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.drawScrollbar(context, mouseX, mouseY);

        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();


        context.enableScissor(x, y, x + width, y + height);
        for (ClickableWidget widget : children) {
            if (widget instanceof ConfigChild) continue;
            widget.render(context, mouseX, mouseY, deltaTicks);
        }
        for (ClickableWidget widget : childWidgets) {
            widget.render(context, mouseX, mouseY, deltaTicks);
        }
        context.disableScissor();
    }

    @Override
    public void setScrollY(double scrollY) {
        super.setScrollY(scrollY);
        update();
    }

    public void update() {
        int x = getX();
        int y = getY();

        int currY = y - (int) getScrollY();

        int halfWidth = width / 2;

        for (ClickableWidget widget : children) {

            if (widget instanceof ConfigChild) continue;

            widget.setPosition(x + halfWidth - widget.getWidth() / 2, currY);
            currY += widget.getHeight() + ITEM_MARGIN;

            if (widget instanceof ConfigParent parent) {
                parent.update();
            }

            if (widget instanceof ConfigSection section) {
                section.hideChildComponents();
            }
        }

        //contentHeight = currY - y + (int) getScrollY();

        int highestY = 0;
        for (ClickableWidget widget : children) {
            highestY = Math.max(highestY, (widget.getY() + widget.getHeight()));
        }

        //to fix ConfigChild components being out of bounds
        contentHeight = highestY + (int) getScrollY();
    }

    public void hideChildComponents() {
        for (ClickableWidget widget : children) {
            if (widget.isFocused()) continue;

            if (widget instanceof ConfigParent parent) {
                if (parent.hasSelectedComponent()) continue;
                parent.hideAll();
                parent.update();
            }

            if (widget instanceof ConfigSection section) {
                section.hideChildComponents();
            }
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
