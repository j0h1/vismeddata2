package filter;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.util.Arrays;

/**
 * Created by felix on 06.11.2015.
 */
public class MedianFilter extends Filter {

    @Override
    public void apply(int x, int y, PixelWriter pw) {

        double[] neighbourhood = new double[9];
        int counter=0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                neighbourhood[counter] = getColor(x+i,y+j).getRed();
                counter++;
            }
        }

        Arrays.sort(neighbourhood);

        double medianIntensity = neighbourhood[4];

        pw.setColor(x,y,new Color(medianIntensity,medianIntensity,medianIntensity,1.0));
    }
}
