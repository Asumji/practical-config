package config.practical.widgets.color;

import config.practical.utilities.Constants;
import config.practical.utilities.DrawHelper;
import config.practical.widgets.abstracts.ConfigChild;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.util.Identifier;

class AlphaSlider extends ConfigChild {

    private static final Identifier ALPHA_SLIDER = Identifier.of(Constants.NAMESPACE, "alpha-sprite");
    private static final int THUMB_DIAMETER = Constants.CORNER_RADIUS * 2;

    private final ConfigColor parent;
    private int thumbPosition = 0;

    public AlphaSlider(ConfigColor parent, int alphaValue) {
        super(parent, ConfigColor.CHILD_WIDTH, ConfigColor.SLIDER_HEIGHT);
        this.parent = parent;

        setAlphaValue(alphaValue);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (!parent.displayColorSelector()) return;

        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ALPHA_SLIDER, x, y, width, height);
        DrawHelper.drawBackground(context, x + thumbPosition, y, THUMB_DIAMETER, THUMB_DIAMETER);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    protected void onDrag(Click click, double offsetX, double offsetY) {
        super.onDrag(click, offsetX, offsetY);
        setThumbPosition(click.x());
    }

    @Override
    public void onClick(Click click, boolean doubled) {
        super.onClick(click, doubled);
        setThumbPosition(click.x());
    }

    public void setAlphaValue(int alphaValue) {
        thumbPosition = (int)((getNormalWidth()  - THUMB_DIAMETER) * alphaValue * (1 / 255.0));
    }

    private void setThumbPosition(double mouseX) {
        int maxWidth = (getNormalWidth()  - THUMB_DIAMETER);
        thumbPosition = Math.clamp((int) mouseX - getX() - THUMB_DIAMETER / 2, 0, maxWidth);
        parent.setAlphaValue((int)(thumbPosition / (float)maxWidth * 255));
    }

    @Override
    protected boolean showWidget() {
        return parent.displayColorSelector();
    }

    @Override
    protected void updatePosition(int x, int y) {
        this.setX(x + ConfigColor.WIDTH + ConfigColor.CHILD_OFFSET);
        this.setY(y + SBSelector.SIZE + ConfigColor.SLIDER_HEIGHT * 2);
    }
}
