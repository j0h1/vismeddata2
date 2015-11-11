package renderer;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import javax.swing.*;

/**
 * Created by felix on 04.11.2015.
 */
public class RenderUtil {

    private static double orgSceneX, orgSceneY, orgTranslateX, orgTranslateY;

    public static ImageView canvasToImageView(Canvas canvas, Pane pane, boolean preserveRatio, boolean autoResize) {
        ImageView iView = new ImageView();
        iView.setImage(canvasToWriteableImage(canvas));
        iView.setPreserveRatio(preserveRatio);

        if (autoResize) {
            iView.fitWidthProperty().bind(pane.widthProperty());
            iView.fitHeightProperty().bind(pane.heightProperty());
        }

        iView.setCursor(Cursor.OPEN_HAND);

        iView.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            ImageView iV = (ImageView)event.getSource();
            iV.setCursor(Cursor.CLOSED_HAND);

            orgSceneX  = event.getSceneX();
            orgSceneY = event.getSceneY();
            orgTranslateX = ((ImageView)event.getSource()).getTranslateX();
            orgTranslateY = ((ImageView)event.getSource()).getTranslateX();
        });

        iView.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            ImageView iV = (ImageView)event.getSource();
            iV.setCursor(Cursor.OPEN_HAND);
        });

        iView.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {

            ImageView iV = (ImageView)event.getSource();
            iV.fitWidthProperty().unbind();
            iV.fitHeightProperty().unbind();

            double offsetX = event.getSceneX() - orgSceneX;
            double offsetY = event.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;

            if (event.isPrimaryButtonDown() && event.isSecondaryButtonDown()) { //Left and Right Button -> scale
                iV.setFitWidth(iV.getFitWidth()+offsetY*0.5);
                iV.setFitHeight(iV.getFitHeight()+offsetY*0.5);
            } else if (event.isPrimaryButtonDown()) { //Left Button -> Drag and Drop
                iV.setTranslateX(newTranslateX);
                iV.setTranslateY(newTranslateY);
            } else if (event.isSecondaryButtonDown()) { //Right Button -> Window function

            }


        });

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
