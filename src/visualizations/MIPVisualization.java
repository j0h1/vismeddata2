package visualizations;

import dicom.DicomImage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
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

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(10,0,0,0));
        pane.setHgap(5);
        pane.setVgap(5);

        //Dimension selection box
        ComboBox<String> dimCombo = new ComboBox<>();
        ObservableList<String> dimensions = FXCollections.observableArrayList();
        dimensions.add("X");
        dimensions.add("Y");
        dimensions.add("Z");
        dimCombo.setItems(dimensions);
        dimCombo.setValue(dimensions.get(2));
        dimCombo.valueProperty().addListener((ov, oldStr, newStr) -> {
            if (newStr.equals("X")) {
                renderer.setViewingDimension(0);
            } else if (newStr.equals("Y")) {
                renderer.setViewingDimension(1);
            } else {
                renderer.setViewingDimension(2);
            }
            renderer.render();
        });

        Label dimLabel = new Label();
        dimLabel.setText("Projection dimension: ");
        dimLabel.setLabelFor(dimCombo);

        pane.add(dimLabel,0,0);
        pane.add(dimCombo,1,0);

        //Scale checkbox
        CheckBox scaleBox = new CheckBox("Scale: ");
        scaleBox.setSelected(true);
        scaleBox.selectedProperty().addListener((ov, old_val, new_val) -> {
            renderer.setScaling(new_val);
            renderer.render();
        });
        pane.add(scaleBox,0,1);

        return pane;

    }
}
