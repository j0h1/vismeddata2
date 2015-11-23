package gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

/**
 * Created by felix on 23.11.2015.
 */
public class HistogramGenerator {

    private static Image img;

    public static void setImage(Image newImg) {
        img = newImg;
    }

    public static Canvas generate2DHistogram(int bins, double w, double h, double windowLowerX, double windowUpperX) {

        Canvas canv = new Canvas(w,h);
        GraphicsContext gc = canv.getGraphicsContext2D();

        //Background
        gc.setFill(Color.WHITE);
        gc.fillRect(0,0,canv.getWidth(),canv.getHeight());

        //Window
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0,0,windowLowerX,canv.getHeight());
        gc.fillRect(windowUpperX,0,canv.getWidth()-windowUpperX,canv.getHeight());

        //Histo
        if (img != null) {

            //construct histogram
            PixelReader pr = img.getPixelReader();
            int[] histogram = new int[bins];
            for (int x = 0; x < (int) img.getWidth(); x++) {
                for (int y = 0; y < (int) img.getHeight(); y++) {
                    Color c = pr.getColor(x, y);
                    double intensity = (c.getRed() + c.getBlue() + c.getGreen()) / 3d;
                    int binIndex = (int) Math.round((bins - 1) * intensity / 1d);
                    histogram[binIndex]++;
                }
            }

            //draw
            double range = windowUpperX-windowLowerX;
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(range / histogram.length);
            double scaleVal = img.getWidth()*img.getHeight()*0.15d;
            for (int i = 0; i < histogram.length; i++) {

                double binHeight = Math.round(canv.getHeight() * (histogram[i] / scaleVal));
                double posx = windowLowerX + gc.getLineWidth() * 0.5d + gc.getLineWidth() * i;

                if (binHeight != 0d) {
                    gc.strokeLine(posx, canv.getHeight(), posx, canv.getHeight() - binHeight);
                }
            }
        } else {
            System.out.println("Histogram img was null. :(");
        }

        //Border
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(0,0,canv.getWidth(),canv.getHeight());

        return canv;
    }
}
