package renderer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Created by felix on 03.11.2015.
 */
public class BlankRenderer implements Renderer {

    private Pane renderPane;
    protected Canvas canvas;
    protected GraphicsContext gc;

    public BlankRenderer(Pane renderPane) {
        this.renderPane = renderPane;
        this.canvas = new Canvas(renderPane.getWidth(),renderPane.getHeight());
        this.gc = canvas.getGraphicsContext2D();
    }

    public void render() {

        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());

        renderPane.getChildren().setAll(canvas);
    }

}
