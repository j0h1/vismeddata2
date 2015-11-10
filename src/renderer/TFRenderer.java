package renderer;

import dicom.DicomImage;
import filter.Filter;
import filter.FilterBank;
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

        renderPane.getChildren().setAll();

        //Local copies for faster rendering
        int[] imgDims = img.getDimensions();
        double imgMax = img.getMaxValue();

        //Render
        PixelWriter pw = canvas.getGraphicsContext2D().getPixelWriter();
        for (int x = 0; x < imgDims[0]; x++) {
            for (int y = 0; y < imgDims[1]; y++) {
                Color accumulatedColor = new Color(0, 0, 0, 0);
                for (int z = 0; z < imgDims[2]; z++) {
                    // retrieve intensity
                    double pixelVal = img.getValue(x, y, z);
                    double pixelValNorm = Math.max(0, pixelVal / imgMax);

                    // find color with transfer function
                    Color mappedColor = TransferFunctionManager.getInstance().apply(pixelValNorm);

                    accumulatedColor = new Color(
                            Math.min(1, accumulatedColor.getRed() + (1 - accumulatedColor.getOpacity()) * mappedColor.getRed()),
                            Math.min(1, accumulatedColor.getGreen() + (1 - accumulatedColor.getOpacity()) * mappedColor.getGreen()),
                            Math.min(1, accumulatedColor.getBlue() + (1 - accumulatedColor.getOpacity()) * mappedColor.getBlue()),
                            Math.min(1, accumulatedColor.getOpacity() + (1 - accumulatedColor.getOpacity()) * mappedColor.getOpacity()));
                }

                pw.setColor(x, y, accumulatedColor);
            }
        }

        //Apply filter
        Filter filter = FilterBank.getFilter();
        filter.prepare(canvas);
        canvas = filter.execute();

        //Create ImageView from canvas, set to pane
        ImageView iView = RenderUtil.canvasToImageView(canvas, renderPane, true, true);
        renderPane.getChildren().setAll(iView);
    }

}
