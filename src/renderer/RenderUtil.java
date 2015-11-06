package renderer;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

import javax.swing.*;

/**
 * Created by felix on 04.11.2015.
 */
public class RenderUtil {

    public static ImageView canvasToImageView(Canvas canvas, Pane pane, boolean preserveRatio, boolean autoResize) {
        ImageView iView = new ImageView();
        iView.setImage(canvasToWriteableImage(canvas));
        iView.setPreserveRatio(preserveRatio);

        if (autoResize) {
            iView.fitWidthProperty().bind(pane.widthProperty());
            iView.fitHeightProperty().bind(pane.heightProperty());
        }

        return iView;
    }

    public static WritableImage canvasToWriteableImage(Canvas canvas) {
        WritableImage wImg = new WritableImage((int)canvas.getWidth(),(int)canvas.getHeight());
        return canvas.snapshot(null, wImg);
    }

    public static Canvas writableimageToCanvas(WritableImage wImg) {
        Canvas canvas = new Canvas(wImg.getWidth(),wImg.getHeight());
        canvas.getGraphicsContext2D().drawImage(wImg,0,0);
        return canvas;
    }
}
