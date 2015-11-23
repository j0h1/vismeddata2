package visualizations;

import dicom.DicomImage;
import gui.Controller;
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
        labelZ.setText("Select Z (XY): ");
        labelZ.setLabelFor(slideZ);

        slideZ.valueProperty().addListener((ov, old_val, new_val) -> {
            renderer.selectZ(new_val.intValue());
            if (renderer.showDecoration()) {
                renderer.render();
            } else {
                renderer.renderXY();
            }
            Controller.getInstance().updateHistogram();
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
        labelY.setText("Select Y (XZ): ");
        labelY.setLabelFor(slideY);

        slideY.valueProperty().addListener((ov, old_val, new_val) -> {
            renderer.selectY(new_val.intValue());
            if (renderer.showDecoration()) {
                renderer.render();
            } else {
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
        labelX.setText("Select X (YZ): ");
        labelX.setLabelFor(slideX);

        slideX.valueProperty().addListener((ov, old_val, new_val) -> {
            renderer.selectX(new_val.intValue());
            if (renderer.showDecoration()) {
                renderer.render();
            } else {
                renderer.renderYZ();
            }
        });

        pane.add(labelX,0,2);
        pane.add(slideX,1,2);

        //Scale to XZ checkbox
        CheckBox scaleBox = new CheckBox("Scale to XZ");
        scaleBox.setSelected(true);
        scaleBox.selectedProperty().addListener((ov, old_val, new_val) -> {
            renderer.setScaling(new_val);
            renderer.render();
        });
        pane.add(scaleBox,0,3);

        //Decoration checkbox & alpha slider
        CheckBox labelsBox = new CheckBox("Decoration");
        labelsBox.setSelected(true);
        Slider slideDecoAlpha = new Slider();
        Label slideDecoAlphaLabel = new Label();
        labelsBox.selectedProperty().addListener((ov, old_val, new_val) -> {
            if (new_val == false) {
                slideDecoAlpha.setDisable(true);
                slideDecoAlphaLabel.setDisable(true);
            } else {
                slideDecoAlpha.setDisable(false);
                slideDecoAlphaLabel.setDisable(false);
            }
            renderer.setDecoration(new_val);
            renderer.render();
        });
        pane.add(labelsBox,0,4);

        slideDecoAlpha.setOrientation(Orientation.HORIZONTAL);
        slideDecoAlpha.setMax(1.0d);
        slideDecoAlpha.setMajorTickUnit(0.5d);
        slideDecoAlpha.setValue(1.0d);
        slideDecoAlphaLabel.setText("Decoration alpha: ");
        slideDecoAlphaLabel.setLabelFor(slideDecoAlpha);
        slideDecoAlpha.valueProperty().addListener((ov, old_val, new_val) -> {
            renderer.setDecorationAlpha(new_val.doubleValue());
            renderer.render();
        });
        pane.add(slideDecoAlphaLabel,0,5);
        pane.add(slideDecoAlpha,1,5);

        return pane;
    }
}
