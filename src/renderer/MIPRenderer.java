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
 * Created by felix on 03.11.2015.
 */
public class MIPRenderer implements Renderer {


    private AnchorPane renderPane;
    protected Canvas canvas;

    private DicomImage img;
    private int dimensionIndex;
    private boolean doScale;

    public MIPRenderer(AnchorPane renderPane, DicomImage dicomImage) {

        this.img = dicomImage;
        this.renderPane = renderPane;

        dimensionIndex = 2;
        doScale = true;

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

    @Override
    public void render() {

        renderPane.getChildren().setAll();
        ArrayList<Integer> allDims = new ArrayList<Integer>() {{ add(0); add(1); add(2); }};
        allDims.remove(dimensionIndex);

        //Local copies for faster rendering
        int[] imgDims = img.getDimensions();
        double imgMax = img.getMaxValue();

        canvas = new Canvas(imgDims[allDims.get(0)],imgDims[allDims.get(1)]);

        //Render
        PixelWriter pw = canvas.getGraphicsContext2D().getPixelWriter();
        int[] pixelSelector = new int[3];
        for (int i = 0; i < imgDims[allDims.get(0)]; i++) {

            pixelSelector[allDims.get(0)] = i;

            for (int j = 0; j < imgDims[allDims.get(1)]; j++) {

                pixelSelector[allDims.get(1)] = j;

                double curMax = Double.MIN_VALUE;
                for (int k = 0; k < imgDims[dimensionIndex]; k++) {

                    pixelSelector[dimensionIndex] = k;

                    //Retrieve intensity
                    double pixelVal = img.getValue(pixelSelector[0],pixelSelector[1],pixelSelector[2]);

                    //Get max
                    if (pixelVal > curMax) {
                        curMax = pixelVal;
                    }
                }
                //Set color
<<<<<<< HEAD
                pw.setColor(x, y, new Color(curMax/imgMax,curMax/imgMax,curMax/imgMax,1));
=======
                pw.setColor(i, j,new Color(curMax/imgMax,curMax/imgMax,curMax/imgMax,1));
>>>>>>> 8236feac0a298137338b2c21879b0b564c81a427
            }
        }

        //Apply filter
        Filter filter = FilterBank.getFilter();
        filter.prepare(canvas);
        canvas = filter.execute();

        //Create ImageView from canvas
        ImageView iView;
        if (doScale) {
            iView = RenderUtil.canvasToImageView(canvas, renderPane, false, true);
        } else {
            iView = RenderUtil.canvasToImageView(canvas, renderPane, true, false);
            renderPane.setTopAnchor(iView, renderPane.getHeight()*0.5-canvas.getHeight()*0.5);
            renderPane.setLeftAnchor(iView, renderPane.getWidth()*0.5-canvas.getWidth()*0.5);
        }

        renderPane.getChildren().setAll(iView);

    }
}
