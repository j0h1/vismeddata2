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

/**
 * Created by felix on 03.11.2015.
 */
public class MIPRenderer implements Renderer {


    private AnchorPane renderPane;
    protected Canvas canvas;

    private DicomImage img;

    public MIPRenderer(AnchorPane renderPane, DicomImage dicomImage) {

        this.img = dicomImage;

        this.renderPane = renderPane;
        this.canvas = new Canvas(img.getDimensions()[0],img.getDimensions()[1]);

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

                double curMax = Double.MIN_VALUE;
                for (int z = 0; z < imgDims[2]; z++) {

                    //Retrieve intensity
                    double pixelVal = img.getValue(x,y,z);

                    //Get max
                    if (pixelVal > curMax) {
                        curMax = pixelVal;
                    }
                }
                //Set color
                pw.setColor(x, y, new Color(curMax/imgMax,curMax/imgMax,curMax/imgMax,1));
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
