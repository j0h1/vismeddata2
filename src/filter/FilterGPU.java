package filter;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

/**
 * Created by Felix on 04.01.2016.
 */
public abstract class FilterGPU implements Filter {

    @Override
    public void prepare(Canvas canvas) {

    }

    @Override
    public Canvas execute() {
        return null;
    }

    @Override
    public Image lastImage() {
        return null;
    }
}
