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

    public TFRenderer(AnchorPane renderPane, DicomImage dicomImage) {

        img = dicomImage;

        this.renderPane = renderPane;

        dimensionIndex = 2;
        doScale = true;
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
        double imgMax = img.getMaxValue();

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
                for (int k = 0; k < imgDims[dimensionIndex]; k++) {

                    // terminate when max. opacity is reached
                    if (accumulatedColor.getOpacity() >= 1.0) {
                        break;
                    }

                    // choose pixel value and normalize
                    pixelSelector[dimensionIndex] = k;
                    double pixelVal = img.getValue(pixelSelector[0], pixelSelector[1], pixelSelector[2]);
                    double pixelValNorm = Math.max(0, pixelVal / imgMax);

                    Color mappedColor = TransferFunctionManager.getInstance().apply(pixelValNorm);

                    // apply over operator
                    accumulatedColor = new Color(
                            accumulatedColor.getRed() + (1 - accumulatedColor.getOpacity()) * mappedColor.getOpacity() * mappedColor.getRed(),
                            accumulatedColor.getGreen() + (1 - accumulatedColor.getOpacity()) * mappedColor.getOpacity() * mappedColor.getGreen(),
                            accumulatedColor.getBlue() + (1 - accumulatedColor.getOpacity()) * mappedColor.getOpacity() * mappedColor.getBlue(),
                            accumulatedColor.getOpacity() + (1 - accumulatedColor.getOpacity()) * mappedColor.getOpacity());
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

        renderPane.getChildren().setAll(iView);
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

}
