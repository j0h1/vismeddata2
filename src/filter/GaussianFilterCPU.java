package filter;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

/**
 * Created by felix on 06.11.2015.
 */
public class GaussianFilterCPU extends FilterCPU {

    @Override
    public void apply(int x, int y, PixelWriter pw) {

        Color[] nbh = new Color[9];
        int counter=0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                nbh[counter] = getColor(x + i, y + j);
                counter++;
            }
        }

        // 2D-3x3 Gauss Kernel:
        // 1 2 1
        // 2 4 2
        // 1 2 1 * 1/16
        double newRed = (1.0*nbh[0].getRed() + 2*nbh[1].getRed() + 1*nbh[2].getRed() + 2*nbh[3].getRed() + 4*nbh[4].getRed() + 2*nbh[5].getRed() + 1*nbh[6].getRed() + 2*nbh[7].getRed() + 1*nbh[8].getRed())/16.0;
        double newGreen = (1.0*nbh[0].getGreen() + 2*nbh[1].getGreen() + 1*nbh[2].getGreen() + 2*nbh[3].getGreen() + 4*nbh[4].getGreen() + 2*nbh[5].getGreen() + 1*nbh[6].getGreen() + 2*nbh[7].getGreen() + 1*nbh[8].getGreen())/16.0;
        double newBlue = (1.0*nbh[0].getBlue() + 2*nbh[1].getBlue() + 1*nbh[2].getBlue() + 2*nbh[3].getBlue() + 4*nbh[4].getBlue() + 2*nbh[5].getBlue() + 1*nbh[6].getBlue() + 2*nbh[7].getBlue() + 1*nbh[8].getBlue())/16.0;

        pw.setColor(x,y,new Color(newRed, newGreen, newBlue , 1.0));

    }
}
