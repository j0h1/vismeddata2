package renderer;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

/**
 * Created by felix on 04.11.2015.
 */
public class RenderUtil {

    public static ImageView canvasToImageView(Canvas canvas, Pane pane, boolean autoresize) {
        WritableImage wImg = new WritableImage((int)canvas.getWidth(),(int)canvas.getHeight());
        canvas.snapshot(null, wImg);
        ImageView iView = new ImageView();
        iView.setImage(wImg);
        iView.setPreserveRatio(true);

        if (autoresize) {
            iView.fitWidthProperty().bind(pane.widthProperty());
            iView.fitHeightProperty().bind(pane.heightProperty());
        }

        return iView;
    }
}
