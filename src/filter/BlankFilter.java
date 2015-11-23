package filter;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import renderer.RenderUtil;

/**
 * Created by felix on 06.11.2015.
 *
 * Blank filter class that overrides EVERY method of the super class so that no unnecessary computations are done.
 */
public class BlankFilter extends Filter {

    private Canvas canvas;

    @Override
    public void prepare(Canvas canvas) {

        this.canvas = canvas;
        super.wImg = RenderUtil.canvasToWriteableImage(canvas);

    }

    @Override
    public Canvas execute() {
        return canvas;
    }

    @Override
    public void apply(int x, int y, PixelWriter pw) {

    }
}
