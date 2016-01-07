package filter;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by felix on 06.11.2015.
 */
public class MedianFilterCPU extends FilterCPU {

    private HashMap<Double,Color> intensity2Colors;

    public MedianFilterCPU() {
        super();
        intensity2Colors = new HashMap<>();
    }


    @Override
    public void apply(int x, int y, PixelWriter pw) {

        double[] nbh = new double[9];
        intensity2Colors.clear();

        int counter=0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                Color c = getColor(x+i,y+j);
                nbh[counter] = (c.getRed()+c.getGreen()+c.getBlue())/3d;
                intensity2Colors.put(nbh[counter],c);
                counter++;
            }
        }

        Arrays.sort(nbh);

        pw.setColor(x,y,intensity2Colors.get(nbh[4]));
    }
}
