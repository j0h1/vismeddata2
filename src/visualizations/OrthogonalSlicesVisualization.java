package visualizations;

import dicom.DicomImage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
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

        //XY
        Slider slideZ = new Slider();
        slideZ.setOrientation(Orientation.HORIZONTAL);
        slideZ.setMax(renderer.getMaxZ());
        slideZ.setMajorTickUnit((double)renderer.getMaxZ()/10d);
        slideZ.setMinorTickCount(1);
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
        slideY.setMajorTickUnit((double)renderer.getMaxY()/10d);
        slideY.setMinorTickCount(1);
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
        slideX.setMajorTickUnit((double)renderer.getMaxX()/10d);
        slideX.setMinorTickCount(1);
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

        //pane.getChildren().addAll(labelZ,slideZ,labelY,slideY,labelX,slideX);

        return pane;
    }
}
