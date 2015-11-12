package renderer;

import dicom.DicomImage;
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

    private static double orgSceneX, orgSceneY, orgTranslateX, orgTranslateY, lastY;

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

    public static void enableInteractivity(ImageView iView) {

        iView.setCursor(Cursor.OPEN_HAND);

        iView.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            ImageView iV = (ImageView)event.getSource();
            iV.setCursor(Cursor.CLOSED_HAND);

            orgSceneX  = event.getSceneX();
            orgSceneY = event.getSceneY();
            orgTranslateX = ((ImageView)event.getSource()).getTranslateX();
            orgTranslateY = ((ImageView)event.getSource()).getTranslateX();
            lastY = 0d;
        });

        iView.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            ImageView iV = (ImageView)event.getSource();
            iV.setCursor(Cursor.OPEN_HAND);
        });

        iView.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {

            ImageView iV = (ImageView)event.getSource();
            iV.fitWidthProperty().unbind();
            iV.fitHeightProperty().unbind();

            //Translation
            double dx = event.getSceneX() - orgSceneX;
            double dy = event.getSceneY() - orgSceneY;

            //Mouse moved up or down?
            double trend = event.getY()-lastY;
            if (trend > 0) {
                trend = 1d;
            } else {
                trend = -1d;
            }

            if (event.isPrimaryButtonDown() && event.isSecondaryButtonDown()) { //Left and Right Button -> scale

                //Rescale and translate
                double height = iV.getFitHeight();
                double width = iV.getFitWidth();
                iV.setFitHeight(height+height*0.025d*trend);
                iV.setFitWidth(width+width*0.025d*trend);
                iV.setTranslateX(orgTranslateX + width*0.025d*trend*-1d);
                iV.setTranslateY(orgTranslateY + height*0.025d*trend*-1d);

            } else if (event.isPrimaryButtonDown()) { //Left Button -> Drag and Drop

                //Translate
                iV.setTranslateX(orgTranslateX + dx);
                iV.setTranslateY(orgTranslateY + dy);

            } 

            lastY = event.getY();
        });


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
