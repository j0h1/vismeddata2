package visualizations;

import dicom.DicomImage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import renderer.OrthogonalSlicesRenderer;
import renderer.Renderer;


/**
 * Created by felix on 03.11.2015.
 */
public class OrthogonalSlicesVisualization implements Visualization {

    private OrthogonalSlicesRenderer renderer;

    public OrthogonalSlicesVisualization(AnchorPane pane, DicomImage dicomImage) {
        this.renderer = new OrthogonalSlicesRenderer(pane,dicomImage);
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

        //XY
        Slider slideZ = new Slider();
        slideZ.setOrientation(Orientation.HORIZONTAL);
        slideZ.setMax(renderer.getMaxZ());
        slideZ.setMajorTickUnit((double)renderer.getMaxZ()*0.5d);
        slideZ.setMinorTickCount((int)(renderer.getMaxZ()*0.05d));
        slideZ.setShowTickLabels(true);
        slideZ.setValue(renderer.getSelectedZ());
        Label labelZ = new Label();
        labelZ.setText("Select Z: ");
        labelZ.setLabelFor(slideZ);

        slideZ.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                renderer.selectZ(new_val.intValue());
                renderer.renderXY();
            }
        });

        //Element,column, row
        pane.add(labelZ,0,0);
        pane.add(slideZ,1,0);

        //XZ
        Slider slideY = new Slider();
        slideY.setOrientation(Orientation.HORIZONTAL);
        slideY.setMax(renderer.getMaxY());
        slideY.setMajorTickUnit((double)renderer.getMaxY()*0.5d);
        slideY.setMinorTickCount((int)(renderer.getMaxY()*0.05d));
        slideY.setShowTickLabels(true);
        slideY.setValue(renderer.getSelectedY());
        Label labelY = new Label();
        labelY.setText("Select Y: ");
        labelY.setLabelFor(slideY);

        slideY.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                renderer.selectY(new_val.intValue());
                renderer.renderXZ();
            }
        });

        pane.add(labelY, 0, 1);
        pane.add(slideY, 1, 1);

        //YZ
        Slider slideX = new Slider();
        slideX.setOrientation(Orientation.HORIZONTAL);
        slideX.setMax(renderer.getMaxX());
        slideX.setMajorTickUnit((double)renderer.getMaxX()*0.5d);
        slideX.setMinorTickCount((int)(renderer.getMaxX()*0.05d));
        slideX.setShowTickLabels(true);
        slideX.setValue(renderer.getSelectedX());
        Label labelX = new Label();
        labelX.setText("Select X: ");
        labelX.setLabelFor(slideX);

        slideX.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                renderer.selectX(new_val.intValue());
                renderer.renderYZ();
            }
        });

        pane.add(labelX,0,2);
        pane.add(slideX,1,2);

        CheckBox scaleBox = new CheckBox("Scale to XZ");
        scaleBox.setSelected(true);
        scaleBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov,
                                Boolean old_val, Boolean new_val) {
                renderer.setScaling(new_val);
                renderer.render();
            }
        });
        pane.add(scaleBox,0,3);

        CheckBox labelsBox = new CheckBox("Labels");
        labelsBox.setSelected(true);
        labelsBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov,
                                Boolean old_val, Boolean new_val) {
                renderer.setShowTitles(new_val);
                renderer.render();
            }
        });
        pane.add(labelsBox,0,4);

        //pane.getChildren().addAll(labelZ,slideZ,labelY,slideY,labelX,slideX);

        return pane;
    }
}
