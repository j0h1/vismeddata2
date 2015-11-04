package visualizations;

import dicom.DicomImage;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import renderer.Renderer;
import renderer.TFRenderer;

/**
 * Created by j0h1 on 04.11.2015.
 */
public class TFVisualization implements Visualization {

    private Stop[] stops;
    private Slider intensitySlider;
    private LinearGradient linearGradient;
    private Rectangle colorMapRect;

    private TFRenderer renderer;

    public TFVisualization(AnchorPane pane, DicomImage dicomImage) {
        this.renderer = new TFRenderer(pane, dicomImage);
    }

    @Override
    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public Pane getVisSettings() {
        GridPane pane = new GridPane();

        stops = new Stop[] { new Stop(0, Color.BLACK), new Stop(1, Color.WHITE)};

        colorMapRect = new Rectangle(0, 0, 200, 50);

        refreshGradientVisualization();

        pane.addRow(0, colorMapRect);

        HBox box = new HBox();
        intensitySlider = new Slider();
        intensitySlider.setPrefWidth(200);
        intensitySlider.setOrientation(Orientation.HORIZONTAL);
        intensitySlider.setMax(1);
        intensitySlider.setMajorTickUnit(0.2);
        intensitySlider.setShowTickLabels(true);
        intensitySlider.setShowTickMarks(true);

        ColorPicker colorPicker = new ColorPicker(Color.WHITE);
        colorPicker.setStyle("-fx-color-label-visible: false ;");
        colorPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                // check if stop already exist
                boolean found = false;
                for (int i = 0; i < stops.length; i++) {
                    if (stops[i].getOffset() == intensitySlider.getValue()) {
                        found = true;
                        stops[i] = new Stop(intensitySlider.getValue(), colorPicker.getValue());
                    }
                }
                // if stop doesnt exist, copy contents of old array to new stops array
                if (!found) {
                    Stop[] newStops = new Stop[stops.length + 1];
                    for (int i = 0; i < stops.length; i++) {
                        newStops[i] = stops[i];
                    }
                    newStops[stops.length] = new Stop(intensitySlider.getValue(), colorPicker.getValue());

                    stops = newStops;
                }

                refreshGradientVisualization();
            }
        });

        Button undoButton = new Button();
        undoButton.setText("X");
        undoButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if (stops.length == 2) {
                    // if user tries to remove initial Stop, reset visualization with default values
                    stops = new Stop[] { new Stop(0, Color.BLACK), new Stop(1, Color.WHITE)};
                    refreshGradientVisualization();
                    return;
                }

                // undo last added stop
                Stop[] newStops = new Stop[stops.length - 1];
                for (int i = 0; i < newStops.length; i++) {
                    newStops[i] = stops[i];
                }
                stops = newStops;

                refreshGradientVisualization();
            }
        });

        box.getChildren().addAll(intensitySlider, colorPicker, undoButton);

        pane.addRow(1, box);

        return pane;
    }

    private void refreshGradientVisualization() {
        linearGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        colorMapRect.setFill(linearGradient);
    }

}
