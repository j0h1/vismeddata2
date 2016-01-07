package filter;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

/**
 * Created by Felix on 04.01.2016.
 */
public interface Filter {

    void prepare(Canvas canvas);

    /**
     * Iterates through image and applies filter (which is defined in subclass)
     * @return
     */
    Canvas execute();

    /**
     * Returns the result of the last filtering operation as image
     * @return
     */
    Image lastImage();


}
