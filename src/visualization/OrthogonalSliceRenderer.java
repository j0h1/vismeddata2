package visualization;

import javafx.scene.image.PixelWriter;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import vtk.vtkImageData;

/**
 * Created by felix on 03.11.2015.
 */
public class OrthogonalSliceRenderer extends Renderer {

    private vtkImageData img;
    private Mode mode;

    public OrthogonalSliceRenderer(Pane renderPane) {
        super(renderPane);
        img = null;
        mode = Mode.AXIAL;

    }

    public OrthogonalSliceRenderer(Pane renderPane, vtkImageData img) {
        this(renderPane);
        this.img = img;
    }

    public void render() {
        super.render();

        if (img == null) {
            return;
        }

        PixelWriter pw = gc.getPixelWriter();
        int[] dims = img.GetDimensions();
        System.out.println(dims);
        for (int x = 0; x < dims[0];x++) {
            for (int y = 0; y < dims[1];y++) {
                double pixelVal = img.GetScalarComponentAsDouble(x,y,0,0);
                //System.out.println(pixelVal);
                pw.setColor(x,y,new Color((double)pixelVal/1024d,(double)pixelVal/1024d,(double)pixelVal/1024d,1));
            }
        }

    }

    public enum Mode {
        AXIAL,
        CORONAL,
        SAGITAL;
    }


}
