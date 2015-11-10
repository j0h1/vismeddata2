package visualizations;

import dicom.DicomImage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import renderer.Renderer;
import renderer.TFRenderer;
import renderer.TransferFunctionManager;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by j0h1 on 04.11.2015.
 */
public class TFVisualization implements Visualization {

    private Stop[] colorGradientStops;
    private Stop[] opacityGradientStops;
    private TextField exactColorIntensity;
    private TextField exactOpacityIntensity;
    private Slider colorIntensitySlider;
    private Slider opacityIntensitySlider;
    private LinearGradient colorGradient;
    private LinearGradient opacityGradient;
    private Rectangle colorMapRect;
    private Rectangle opacityMapRect;

    private int currentRowIndex = 0;

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

        // initialize mappings and pass them to the TransferFunctionManager
        colorGradientStops = new Stop[] { new Stop(0, Color.BLACK), new Stop(1, Color.WHITE)};
        opacityGradientStops = new Stop[] { new Stop(0, new Color(1, 1, 1, 1)), new Stop(1, new Color(1, 1, 1, 1))};

        TransferFunctionManager.getInstance().setColorMapping(colorGradientStops);
        TransferFunctionManager.getInstance().setOpacityMapping(opacityGradientStops);

        initColorMappingControls(pane);

        initOpacityMappingControls(pane);

        return pane;
    }

    private void initColorMappingControls(GridPane parent) {

        // set label
        Label colorMappingTitle = new Label();
        colorMappingTitle.setText("Color Mapping:");
        parent.addRow(currentRowIndex++, colorMappingTitle);

        HBox gradientBox = new HBox();

        // init gradient
        colorMapRect = new Rectangle(0, 0, 200, 25);
        refreshGradientVisualization(false);

        // init color picker
        ColorPicker colorPicker = new ColorPicker(Color.WHITE);
        colorPicker.setStyle("-fx-color-label-visible: false ;");
        colorPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                // check if stop already exist
                boolean found = false;
                for (int i = 0; i < colorGradientStops.length; i++) {
                    if (colorGradientStops[i].getOffset() == colorIntensitySlider.getValue()) {
                        found = true;
                        colorGradientStops[i] = new Stop(colorIntensitySlider.getValue(), colorPicker.getValue());
                    }
                }
                // if stop doesnt exist, copy contents of old array to new stops array
                if (!found) {
                    Stop[] newStops = new Stop[colorGradientStops.length + 1];
                    for (int i = 0; i < colorGradientStops.length; i++) {
                        newStops[i] = colorGradientStops[i];
                    }
                    newStops[colorGradientStops.length] = new Stop(colorIntensitySlider.getValue(), colorPicker.getValue());

                    colorGradientStops = newStops;
                }

                refreshGradientVisualization(true);
            }
        });

        // init undo button
        Button resetButton = new Button();
        resetButton.setText("X");
        resetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                // reset visualization with default values
                colorGradientStops = new Stop[]{new Stop(0, Color.BLACK), new Stop(1, Color.WHITE)};
                refreshGradientVisualization(false);
            }
        });

        // add gradient, color picker and undo button to hbox (and hbox to pane)
        gradientBox.getChildren().addAll(colorMapRect, colorPicker, resetButton);
        parent.addRow(currentRowIndex++, gradientBox);


        HBox sliderBox = new HBox();

        // init intensity slider
        colorIntensitySlider = new Slider();
        colorIntensitySlider.setPrefWidth(200);
        colorIntensitySlider.setOrientation(Orientation.HORIZONTAL);
        colorIntensitySlider.setMax(1);
        colorIntensitySlider.setMajorTickUnit(0.2);
        colorIntensitySlider.setShowTickLabels(true);
        colorIntensitySlider.setShowTickMarks(true);
        colorIntensitySlider.setSnapToTicks(true);

        colorIntensitySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                exactColorIntensity.setText(String.format("%.2f", newValue));
            }
        });

        // init exact intensity TextField
        exactColorIntensity = new TextField();
        exactColorIntensity.setText("0,00");
        exactColorIntensity.setMaxWidth(70);
        exactColorIntensity.setAlignment(Pos.CENTER);
        exactColorIntensity.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    Double intensity = Double.parseDouble(newValue.replace(",", "."));

                    if (intensity >= 0 && intensity <= 1) {
                        colorIntensitySlider.setValue(intensity);
                    }
                } catch (NumberFormatException e) {
                    // do nothing
                }
            }
        });

        // add intensity slider and text field for exact intensity to hbox (and hobx to pane)
        sliderBox.getChildren().addAll(colorIntensitySlider, exactColorIntensity);
        parent.addRow(currentRowIndex++, sliderBox);
    }

    private void refreshGradientVisualization(boolean changedSinceInitialization) {
        // tell the TransferFunctionManager if it changed since the initial setup
        TransferFunctionManager.getInstance().setChangedSinceInitialization(changedSinceInitialization);

        // pre-sort color mappings by intensity and pass them to the TransferFunctionManager
        sortColorMappingByIntensity();
        TransferFunctionManager.getInstance().setColorMapping(colorGradientStops);

        // refresh gradient visualization in settings
        colorGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, colorGradientStops);
        colorMapRect.setFill(colorGradient);

        renderer.render();
    }

    private void initOpacityMappingControls(GridPane parent) {

        // set label
        Label opacityMappingTitle = new Label();
        opacityMappingTitle.setText("Opacity Mapping:");
        parent.addRow(currentRowIndex++, opacityMappingTitle);

        HBox gradientBox = new HBox();

        // init gradient
        opacityMapRect = new Rectangle(0, 0, 200, 25);
        refreshOpacityMappingVisualization(false);

        // TODO init TextField
        Slider opacitySettingSlider = new Slider();
        opacitySettingSlider.setPrefWidth(200);
        opacitySettingSlider.setOrientation(Orientation.HORIZONTAL);
        opacitySettingSlider.setMax(1);
        opacitySettingSlider.setMajorTickUnit(0.2);
        opacitySettingSlider.setShowTickLabels(true);
        opacitySettingSlider.setShowTickMarks(true);
        opacitySettingSlider.setSnapToTicks(true);

        Stage dialogStage = new Stage();

        Button applyOpacityButton = new Button("apply");
        applyOpacityButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // check if stop already exist
                boolean found = false;
                for (int i = 0; i < opacityGradientStops.length; i++) {
                    if (opacityGradientStops[i].getOffset() == opacityIntensitySlider.getValue()) {
                        found = true;
                        opacityGradientStops[i] = new Stop(opacityIntensitySlider.getValue(), new Color(0, 0, 0, opacitySettingSlider.getValue()));
                    }
                }
                // if stop doesnt exist, copy contents of old array to new stops array
                if (!found) {
                    Stop[] newStops = new Stop[opacityGradientStops.length + 1];
                    for (int i = 0; i < opacityGradientStops.length; i++) {
                        newStops[i] = opacityGradientStops[i];
                    }
                    newStops[opacityGradientStops.length] = new Stop(opacityIntensitySlider.getValue(), new Color(0, 0, 0, opacitySettingSlider.getValue()));

                    opacityGradientStops = newStops;
                }

                refreshOpacityMappingVisualization(true);
                dialogStage.close();
            }
        });

        VBox dialogBox = new VBox();
        dialogBox.setAlignment(Pos.CENTER);
        dialogBox.setPadding(new Insets(10, 10, 10, 10));
        dialogBox.getChildren().addAll(opacitySettingSlider, applyOpacityButton);

        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setScene(new Scene(dialogBox));

        Button setOpacityButton = new Button("set");
        setOpacityButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dialogStage.setX(event.getScreenX());
                dialogStage.setY(event.getScreenY());
                dialogStage.show();
            }
        });

        // init undo button
        Button resetButton = new Button();
        resetButton.setText("X");
        resetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                // reset visualization with default values
                opacityGradientStops = new Stop[] { new Stop(0, new Color(1, 1, 1, 0)), new Stop(1, new Color(1, 1, 1, 0))};
                refreshOpacityMappingVisualization(false);
            }
        });

        // add gradient, color picker and undo button to hbox (and hbox to pane)
        gradientBox.getChildren().addAll(opacityMapRect, setOpacityButton, resetButton);
        parent.addRow(currentRowIndex++, gradientBox);


        HBox sliderBox = new HBox();

        // init intensity slider
        HBox box = new HBox();
        opacityIntensitySlider = new Slider();
        opacityIntensitySlider.setPrefWidth(200);
        opacityIntensitySlider.setOrientation(Orientation.HORIZONTAL);
        opacityIntensitySlider.setMax(1);
        opacityIntensitySlider.setMajorTickUnit(0.2);
        opacityIntensitySlider.setShowTickLabels(true);
        opacityIntensitySlider.setShowTickMarks(true);
        opacityIntensitySlider.setSnapToTicks(true);

        opacityIntensitySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                exactOpacityIntensity.setText(String.format("%.2f", newValue));
            }
        });

        // init exact intensity TextField
        exactOpacityIntensity = new TextField();
        exactOpacityIntensity.setText("0,00");
        exactOpacityIntensity.setMaxWidth(70);
        exactOpacityIntensity.setAlignment(Pos.CENTER);
        exactOpacityIntensity.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    Double intensity = Double.parseDouble(newValue.replace(",", "."));

                    if (intensity >= 0 && intensity <= 1) {
                        opacityIntensitySlider.setValue(intensity);
                    }
                } catch (NumberFormatException e) {
                    // do nothing
                }
            }
        });

        // add intensity slider and text field for exact intensity to hbox (and hobx to pane)
        sliderBox.getChildren().addAll(opacityIntensitySlider, exactOpacityIntensity);
        parent.addRow(currentRowIndex++, sliderBox);
    }

    private void refreshOpacityMappingVisualization(boolean changedSinceInitialization) {
        // tell the TransferFunctionManager if it changed since the initial setup
        TransferFunctionManager.getInstance().setChangedSinceInitialization(changedSinceInitialization);

        // pre-sort opacity mappings by intensity and pass them to the TransferFunctionManager
        sortOpacityMappingByIntensity();
        TransferFunctionManager.getInstance().setOpacityMapping(opacityGradientStops);

        // refresh gradient visualization in settings
        opacityGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, opacityGradientStops);
        opacityMapRect.setFill(opacityGradient);

        renderer.render();
    }

    private void sortColorMappingByIntensity() {
        Arrays.sort(colorGradientStops, new Comparator<Stop>() {
            @Override
            public int compare(Stop o1, Stop o2) {
                return ((Integer) ((int) (o1.getOffset() * 100))).compareTo((int) (o2.getOffset() * 100));    // sort with 2 digits precision
            }
        });
    }

    private void sortOpacityMappingByIntensity() {
        Arrays.sort(opacityGradientStops, new Comparator<Stop>() {
            @Override
            public int compare(Stop o1, Stop o2) {
                return ((Integer) ((int) (o1.getOffset() * 100))).compareTo((int) (o2.getOffset() * 100));
            }
        });
    }

}
