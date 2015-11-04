package renderer;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

/**
 * Created by felix on 04.11.2015.
 */
public class RenderUtil {

    public static ImageView canvasToImageView(Canvas canvas, double fitWidth, double fitHeight, boolean preserveRatio) {
        WritableImage wImg = new WritableImage((int)canvas.getWidth(),(int)canvas.getHeight());
        canvas.snapshot(null, wImg);
        ImageView iView = new ImageView();
        iView.setImage(wImg);
        iView.setFitWidth(fitWidth);
        iView.setFitHeight(fitHeight);
        iView.setPreserveRatio(preserveRatio);
        return iView;
    }
}
