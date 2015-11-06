package filter;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

/**
 * Created by felix on 06.11.2015.
 */
public class GaussianFilter extends Filter {

    @Override
    public void apply(int x, int y, PixelWriter pw) {

        double[] nbh = new double[9];
        int counter=0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                nbh[counter] = getColor(x+i,y+j).getRed();
                counter++;
            }
        }

        // 2D-3x3 Gauss Kernel:
        // 1 2 1
        // 2 4 2
        // 1 2 1 * 1/16
        double newIntensity = (1.0*nbh[0] + 2*nbh[1] + 1*nbh[2] + 2*nbh[3] + 4*nbh[4] + 2*nbh[5] + 1*nbh[6] + 2*nbh[7] + 1*nbh[8])/16.0;

        pw.setColor(x,y,new Color(newIntensity, newIntensity, newIntensity, 1.0));

    }
}
