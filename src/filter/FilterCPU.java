package filter;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import renderer.RenderUtil;

/**
 * Created by felix on 06.11.2015.
 */
public abstract class FilterCPU implements Filter {

    protected WritableImage wImg;
    private PixelReader pr;
    private PixelWriter pw;

    private int[] dimensions;

    /**
     * Prepares canvas for filtering.
     * @param canvas
     */
    public void prepare(Canvas canvas) {

        WritableImage wImgInput = RenderUtil.canvasToWriteableImage(canvas);//input
        wImg = new WritableImage((int)wImgInput.getWidth(),(int)wImgInput.getHeight());

        pr = wImgInput.getPixelReader();
        pw = wImg.getPixelWriter();

        dimensions = new int[2];
        dimensions[0] = (int)wImg.getWidth();
        dimensions[1] = (int)wImg.getHeight();

    }

    /**
     * Iterates through image and applies filter (which is defined in subclass)
     * @return
     */
    public Canvas execute() {

        for (int x = 0; x < dimensions[0];x++) {
            for (int y = 0; y < dimensions[1]; y++) {
                apply(x,y,pw);
            }
        }

        return RenderUtil.writableimageToCanvas(wImg);
    }

    /**
     * Returns the result of the last filtering operation as image
     * @return
     */
    public Image lastImage() {
        return wImg;
    }

    /**
     * Returns image color at x/y position. Consideres image bounds, returns "Black" in case of outofbounds
     * @param x
     * @param y
     * @return
     */
    protected Color getColor(int x, int y) {
        if (x < 0 || y < 0 || x >= dimensions[0] || y >= dimensions[1]) {
            return Color.BLACK;
        }
        return pr.getColor(x,y);
    }

    /**
     * Returns image color at x/y position as ARGB. Consideres image bounds, returns "0" in case of outofbounds
     * @param x
     * @param y
     * @return
     */
    protected int getARgb(int x, int y) {
        if (x < 0 || y < 0 || x >= dimensions[0] || y >= dimensions[1]) {
            return 0;
        }
        return pr.getArgb(x,y);
    }

    /**
     * Apply filter for image-position x/y. use printwriter to write pixeldata to image.
     * @param x
     * @param y
     * @param pw
     */
    public abstract void apply(int x, int y, PixelWriter pw);

}
