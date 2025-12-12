package config.practical.widgets.sliders;

import config.practical.utilities.Constants;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigInt extends Slider {

    private final int step, min, max;
    private final Supplier<Integer> supplier;
    private final Consumer<Integer> consumer;

    /**
     * @param message  the text that will be displayed
     * @param supplier a supplier to get the current value
     * @param consumer a consumer to set the current value
     * @param step     the steps inbetween each value
     * @param min      the min value
     * @param max      the max value
     */
    public ConfigInt(Text message, Supplier<Integer> supplier, Consumer<Integer> consumer, int step, int min, int max) {
        super(message);
        this.supplier = supplier;
        this.consumer = consumer;
        this.step = step;
        this.min = min;
        this.max = max;
        updateThumbPosWithDelta((supplier.get() - min) / (float) (max - min));
    }

    @Override
    String getCurrValue() {
        return supplier.get().toString();
    }

    @Override
    void updateValue(float delta) {
        int accurateValue = (int) ((max - min) * delta) + min;
        accurateValue -= accurateValue % step;
        consumer.accept(accurateValue);
    }

    @Override
    public boolean keyPressed(KeyInput input) {

        if (input.getKeycode() == GLFW.GLFW_KEY_LEFT) {
            consumer.accept(Math.max(supplier.get() - step, min));
            updateThumbPosWithDelta((supplier.get() - min) / (float) (max - min));
            return true;
        } else if (input.getKeycode() == GLFW.GLFW_KEY_RIGHT) {
            consumer.accept(Math.min(supplier.get() + step, max));
            updateThumbPosWithDelta((supplier.get() - min) / (float) (max - min));
            return true;
        }

        return super.keyPressed(input);
    }

}
