package renderer;

import dicom.DicomImage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import vtk.vtkImageData;

/**
 * Created by j0h1 on 04.11.2015.
 */
public class TFRenderer implements Renderer {

    private DicomImage img;
    private double imgMax;
    private int imgDims[];

    private AnchorPane renderPane;
    protected Canvas canvas;
    protected GraphicsContext gc;

    public TFRenderer(AnchorPane renderPane, DicomImage dicomImage) {

        img = dicomImage;
        this.imgDims = img.getDimensions();

        this.renderPane = renderPane;
        this.canvas = new Canvas(imgDims[0],imgDims[1]);
        this.gc = canvas.getGraphicsContext2D();
    }

    @Override
    public void render() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());

        //Offset for image centering
        //int offsetX = (int)(canvas.getWidth()*0.5-imgDims[0]*0.5);
        //int offsetY = (int)(canvas.getHeight()*0.5-imgDims[1]*0.5);
        PixelWriter pw = gc.getPixelWriter();

        for (int x = 0; x < imgDims[0]; x++) {
            for (int y = 0; y < imgDims[1]; y++) {
                double pixelVal = img.getValue(x,y,0);
                //TODO use transfer function
//                pw.setColor(x, y,new Color(curMax/imgMax,curMax/imgMax,curMax/imgMax,1));
            }
        }

        ImageView iView = RenderUtil.canvasToImageView(canvas, renderPane, true, true);

        renderPane.getChildren().setAll(iView);
    }

}
