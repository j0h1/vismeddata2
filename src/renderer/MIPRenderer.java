package renderer;

import dicom.DicomImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import vtk.vtkImageData;

/**
 * Created by felix on 03.11.2015.
 */
public class MIPRenderer implements Renderer {

    private vtkImageData img;
    private double imgMax;
    private int imgDims[];

    private Pane renderPane;
    protected Canvas canvas;
    protected GraphicsContext gc;

    public MIPRenderer(Pane renderPane, DicomImage dicomImage) {

        this.img = dicomImage.getImageData();
        this.imgDims = img.GetDimensions();

        imgMax = Double.MIN_VALUE;
        for (int x = 0; x < imgDims[0]; x++) {
            for (int y = 0; y < imgDims[1]; y++) {
                for (int z = 0; z < imgDims[2]; z++) {
                    double pixelVal = img.GetScalarComponentAsDouble(x,y,z,0);


                    //Get max
                    if (pixelVal > imgMax) {
                        imgMax = pixelVal;
                    }
                }
            }
        }

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

                double curMax = Double.MIN_VALUE;
                for (int z = 0; z < imgDims[2]; z++) {
                    double pixelVal = img.GetScalarComponentAsDouble(x,y,z,0);

                    //Get max
                    if (pixelVal > curMax) {
                        curMax = pixelVal;
                    }
                }
                pw.setColor(x, y,new Color(curMax/imgMax,curMax/imgMax,curMax/imgMax,1));
            }
        }

        ImageView iView = RenderUtil.canvasToImageView(canvas, canvas.getWidth(), canvas.getHeight(), true);

        renderPane.getChildren().setAll(iView);

    }
}
