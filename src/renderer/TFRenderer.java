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
import java.util.ArrayList;

/**
 * Created by j0h1 on 04.11.2015.
 */
public class TFRenderer implements Renderer {

    private DicomImage img;

    private AnchorPane renderPane;
    protected Canvas canvas;
    protected GraphicsContext gc;

    private int dimensionIndex;
    private boolean doScale;
    private boolean frontToBack;

    public TFRenderer(AnchorPane renderPane, DicomImage dicomImage) {

        img = dicomImage;

        this.renderPane = renderPane;

        dimensionIndex = 2;
        doScale = true;
        frontToBack = true;

    }

    @Override
    public void render() {

        renderPane.getChildren().setAll();

        ArrayList<Integer> allDims = new ArrayList<Integer>() {{
            add(0);
            add(1);
            add(2);
        }};
        allDims.remove(dimensionIndex);

        // init image dimensions and max value
        int[] imgDims = img.getDimensions();

        canvas = new Canvas(imgDims[allDims.get(0)], imgDims[allDims.get(1)]);
        gc = canvas.getGraphicsContext2D();

        //Render
        PixelWriter pw = canvas.getGraphicsContext2D().getPixelWriter();
        int[] pixelSelector = new int[3];

        for (int i = 0; i < imgDims[allDims.get(0)]; i++) {
            pixelSelector[allDims.get(0)] = i;
            for (int j = 0; j < imgDims[allDims.get(1)]; j++) {
                pixelSelector[allDims.get(1)] = j;
                Color accumulatedColor = new Color(0, 0, 0, 0);
                if (frontToBack) {
                    for (int k = 0; k < imgDims[dimensionIndex]; k++) {
                        // terminate when max. opacity is reached
                        if (accumulatedColor.getOpacity() >= 1.0) {
                            break;
                        }
                        accumulatedColor = accumulateColor(accumulatedColor, pixelSelector, k);
                    }
                } else {
                    for (int k = imgDims[dimensionIndex] - 1; k >= 0; k--) {
                        if (accumulatedColor.getOpacity() >= 1.0) {
                            break;
                        }
                        accumulatedColor = accumulateColor(accumulatedColor, pixelSelector, k);
                    }
                }
                pw.setColor(i, j, accumulatedColor);
            }
        }

        // apply chosen filter
        Filter filter = FilterBank.getFilter();
        filter.prepare(canvas);
        canvas = filter.execute();

        ImageView iView;
        if (doScale) {
            iView = RenderUtil.canvasToImageView(canvas, renderPane, false, true);
        } else {
            iView = RenderUtil.canvasToImageView(canvas, renderPane, true, false);
            renderPane.setTopAnchor(iView, renderPane.getHeight() * 0.5 - canvas.getHeight() * 0.5);
            renderPane.setLeftAnchor(iView, renderPane.getWidth() * 0.5 - canvas.getWidth() * 0.5);
        }
        RenderUtil.enableInteractivity(iView);

        renderPane.getChildren().setAll(iView);
    }

    private Color accumulateColor(Color accumulatedColor, int[] pixelSelector, int k) {

        // choose pixel value and normalize
        pixelSelector[dimensionIndex] = k;
        double pixelValNorm = img.getRelativeWindowedValue(pixelSelector[0], pixelSelector[1], pixelSelector[2]);

        Color mappedColor = TransferFunctionManager.getInstance().apply(pixelValNorm);

        // apply under operator
        return new Color(
                (1 - accumulatedColor.getOpacity()) * mappedColor.getRed() + accumulatedColor.getOpacity() * accumulatedColor.getRed(),
                (1 - accumulatedColor.getOpacity()) * mappedColor.getGreen() + accumulatedColor.getOpacity() * accumulatedColor.getGreen(),
                (1 - accumulatedColor.getOpacity()) * mappedColor.getBlue() + accumulatedColor.getOpacity() * accumulatedColor.getBlue(),
                (1 - accumulatedColor.getOpacity()) * mappedColor.getOpacity() + accumulatedColor.getOpacity());

    }

    public boolean isScaling() {
        return doScale;
    }

    public void setScaling(boolean doScale) {
        this.doScale = doScale;
    }

    public int getViewingDimension() {
        return dimensionIndex;
    }

    public void setViewingDimension(int dimensionIndex) {
        this.dimensionIndex = dimensionIndex;
    }

    public boolean isFrontToBack() {
        return frontToBack;
    }

    public void setFrontToBack(boolean frontToBack) {
        this.frontToBack = frontToBack;
    }
}
