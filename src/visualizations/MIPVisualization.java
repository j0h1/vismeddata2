package visualizations;

import dicom.DicomImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import renderer.MIPRenderer;
import renderer.Renderer;
import vtk.vtkImageData;

/**
 * Created by felix on 03.11.2015.
 */
public class MIPVisualization implements Visualization {

    private MIPRenderer renderer;

    public MIPVisualization(AnchorPane pane, DicomImage dicomImage) {
        this.renderer = new MIPRenderer(pane,dicomImage);
    }

    @Override
    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public Pane getVisSettings() {
        return new Pane();
    }
}
