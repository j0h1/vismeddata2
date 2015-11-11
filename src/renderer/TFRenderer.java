package renderer;

import dicom.DicomImage;
import filter.Filter;
import filter.FilterBank;
import javafx.scene.Node;
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
    protected Canvas[] canvasArr;
    protected Canvas canvas;
    protected GraphicsContext gc;

    public TFRenderer(AnchorPane renderPane, DicomImage dicomImage) {

        img = dicomImage;
        this.imgDims = img.getDimensions();

        this.renderPane = renderPane;

        //Create 3 canvases for rendering from every direction
        this.canvasArr = new Canvas[3];
        canvasArr[0] = new Canvas(imgDims[0],imgDims[1]); //XY
        canvasArr[1] = new Canvas(imgDims[0],imgDims[2]); //XZ
        canvasArr[2] = new Canvas(imgDims[1],imgDims[2]); //YZ
    }

    @Override
    public void render() {

        renderPane.getChildren().setAll();

        renderXY();
        renderXZ();
        renderYZ();

        //Create ImageView from canvas, set to pane
//        ImageView iView = RenderUtil.canvasToImageView(canvas, renderPane, true, true);
//        renderPane.getChildren().setAll(iView);
    }

    public void renderXY() {
        Node fxNode = renderSlice(0, 1, 2, 0, false, "XY");

        renderPane.setTopAnchor(fxNode, 5.0);
        renderPane.setLeftAnchor(fxNode, 5.0);
    }

    public void renderXZ() {
        Node fxNode = renderSlice(0, 2, 1, 1, true, "XZ");

        renderPane.setTopAnchor(fxNode, imgDims[0] * 0.5d - canvasArr[1].getHeight() * 0.5d);
        renderPane.setRightAnchor(fxNode, imgDims[1] * 0.5d - canvasArr[1].getWidth() * 0.5d);
    }

    public void renderYZ() {
        Node fxNode = renderSlice(1, 2, 0, 2, true, "YZ");

        renderPane.setBottomAnchor(fxNode, imgDims[0] * 0.5d - canvasArr[2].getHeight() * 0.5d);
        renderPane.setLeftAnchor(fxNode, renderPane.getWidth() * 0.5 - canvasArr[2].getWidth() * 0.5);
    }

    private Node renderSlice(int dim1, int dim2, int staticDim, int canvasIndex, boolean resizeToXY, String title) {

        //Local copies for faster rendering
        int[] imgDims = img.getDimensions();
        double imgMax = img.getMaxValue();

        gc = canvasArr[canvasIndex].getGraphicsContext2D();

        //Render
        PixelWriter pw = gc.getPixelWriter();
        for (int i = 0; i < imgDims[dim1]; i++) {
            for (int j = 0; j < imgDims[dim2]; j++) {
                Color accumulatedColor = new Color(0, 0, 0, 0);
                for (int k = 0; k < imgDims[staticDim]; k++) {
                    double pixelValNorm = getNormalizedPixelValue(i, j, k, staticDim, imgMax);

                    Color mappedColor = TransferFunctionManager.getInstance().apply(pixelValNorm);

                    // over operator
//                    accumulatedColor = new Color(
//                            accumulatedColor.getRed() + (1 - accumulatedColor.getOpacity()) * mappedColor.getOpacity() * mappedColor.getRed(),
//                            accumulatedColor.getGreen() + (1 - accumulatedColor.getOpacity()) * mappedColor.getOpacity() * mappedColor.getGreen(),
//                            accumulatedColor.getBlue() + (1 - accumulatedColor.getOpacity()) * mappedColor.getOpacity() * mappedColor.getBlue(),
//                            accumulatedColor.getOpacity() + (1 - accumulatedColor.getOpacity()) * mappedColor.getOpacity());

                    accumulatedColor = new Color(
                            mappedColor.getRed() + (1 - mappedColor.getOpacity()) * accumulatedColor.getRed(),
                            mappedColor.getGreen() + (1 - mappedColor.getOpacity()) * accumulatedColor.getGreen(),
                            mappedColor.getBlue() + (1 - mappedColor.getOpacity()) * accumulatedColor.getBlue(),
                            mappedColor.getOpacity() + (1 - mappedColor.getOpacity()) * accumulatedColor.getOpacity());
                }

                pw.setColor(i, j, accumulatedColor);
            }
        }

        //Apply filter
        Filter filter = FilterBank.getFilter();
        filter.prepare(canvasArr[canvasIndex]);
        canvasArr[canvasIndex] = filter.execute();

        ImageView iView = RenderUtil.canvasToImageView(canvasArr[canvasIndex], renderPane, false, false);

        if (resizeToXY) {
            iView.setFitWidth(imgDims[0]);
            iView.setFitHeight(imgDims[1]);
        }

        //If not rendered "for the first time" (size!=3) add node, else set new
        if (renderPane.getChildren().size() == 3) {
            renderPane.getChildren().set(canvasIndex, iView);
        } else {
            renderPane.getChildren().add(iView);
        }

        return iView;
    }

    private double getNormalizedPixelValue(int i, int j, int k, int staticDim, double imgMax) {
        switch (staticDim) {
            case 0:
                return Math.max(0, img.getValue(k, i, j) / imgMax);
            case 1:
                return Math.max(0, img.getValue(i, k, j) / imgMax);
            default:
                return Math.max(0, img.getValue(i, j, k) / imgMax);
        }
    }

}
