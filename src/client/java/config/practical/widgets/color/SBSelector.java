package config.practical.widgets.color;

import config.practical.utilities.Constants;
import config.practical.widgets.abstracts.ConfigChild;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import java.awt.Color;

class SBSelector extends ConfigChild {

    private static final Identifier EMPTY_SB_MASK = Identifier.of(Constants.NAMESPACE, "empty-sb-mask");
    public static final int SIZE = ConfigColor.CHILD_WIDTH;
    public static final int SPRITE_SIZE = SIZE - Constants.LINE_THICKNESS * 2;

    private final ConfigColor parent;
    private final Identifier sbMask;

    public SBSelector(ConfigColor parent, String identifier, float hue) {
        super(parent, SIZE, SIZE);
        this.parent = parent;

        sbMask = Identifier.of(Constants.NAMESPACE, identifier);
        makeSBMask(hue);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (!parent.displayColorSelector()) return;

        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        context.drawTexture(RenderPipelines.GUI_TEXTURED, sbMask, x + Constants.LINE_THICKNESS, y + Constants.LINE_THICKNESS, 0, 0, SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE, Constants.WHITE_COLOR);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, EMPTY_SB_MASK, x, y, width, height);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    protected boolean showWidget() {
        return parent.displayColorSelector();
    }

    @Override
    protected void updatePosition(int x, int y) {
        this.setX(x + ConfigColor.WIDTH + ConfigColor.CHILD_OFFSET);
        this.setY(y + ConfigColor.SLIDER_HEIGHT);
    }

    @Override
    public void onClick(Click click, boolean doubled) {
        super.onClick(click, doubled);

        float saturation = Math.clamp((float) (click.x() - getX() - Constants.LINE_THICKNESS) / (float) SPRITE_SIZE, 0f, 1f);
        float brightness = 1 - Math.clamp((float) (click.y() - getY() - Constants.LINE_THICKNESS) / (float) SPRITE_SIZE, 0f, 1f);

        parent.setSBValue(saturation, brightness);
    }

    public void makeSBMask(float hue) {
        try (NativeImage image = new NativeImage(SPRITE_SIZE, SPRITE_SIZE, true)) {

            float saturationFraction = (float) (1.0 / (SPRITE_SIZE - 1));
            float brightnessFraction = (float) (1.0 / (SPRITE_SIZE - 1));

            for (int i = 0; i < SPRITE_SIZE; i++) {
                for (int j = 0; j < SPRITE_SIZE; j++) {

                    float saturation = Math.min(i * saturationFraction, 1);
                    float brightness = Math.min((SPRITE_SIZE - 1 - j) * brightnessFraction, 1);

                    int argb = Color.HSBtoRGB(hue, saturation, brightness) | (0xff << 24);

                    //so for some reason the default format is supposed to be argb
                    //however it's all coded to use abgr for some reason so have to
                    //use this function instead...
                    image.setColorArgb(i, j, argb);
                }
            }

            TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
            NativeImageBackedTexture texture = new NativeImageBackedTexture(() -> "TODO: change this", image);

            texture.upload();
            textureManager.destroyTexture(sbMask);
            textureManager.registerTexture(sbMask, texture);
        }
    }

}
