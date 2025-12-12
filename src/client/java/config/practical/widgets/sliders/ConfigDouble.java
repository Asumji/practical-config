package config.practical.widgets.sliders;

import config.practical.utilities.Constants;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigDouble extends Slider {
    private final double step, min, max;
    private final Supplier<Double> supplier;
    private final Consumer<Double> consumer;

    /**
     * @param message  the text that will be displayed
     * @param supplier a supplier to get the current value
     * @param consumer a consumer to set the current value
     * @param step     the steps inbetween each value
     * @param min      the min value
     * @param max      the max value
     */
    public ConfigDouble(Text message, Supplier<Double> supplier, Consumer<Double> consumer, double step, double min, double max) {
        super(message);
        this.supplier = supplier;
        this.consumer = consumer;
        this.step = step;
        this.min = min;
        this.max = max;
        this.updateThumbPosWithDelta((float) (supplier.get() - min) / (float) (max - min));
    }

    @Override
    String getCurrValue() {
        return Constants.DECIMAL_FORMAT.format(supplier.get());
    }


    @Override
    void updateValue(float delta) {
        double accurateValue = ((max - min) * delta) + min;
        accurateValue -= accurateValue % step;
        consumer.accept(accurateValue);
    }

    @Override
    public boolean keyPressed(KeyInput input) {

        if (input.getKeycode() == GLFW.GLFW_KEY_LEFT) {
            consumer.accept(Math.max(supplier.get() - step, min));
            updateThumbPosWithDelta((float)((supplier.get() - min) / (max - min)));
            return true;
        } else if (input.getKeycode() == GLFW.GLFW_KEY_RIGHT) {
            consumer.accept(Math.min(supplier.get() + step, max));
            updateThumbPosWithDelta((float)((supplier.get() - min) / (max - min)));
            return true;
        }

        return super.keyPressed(input);
    }

}
