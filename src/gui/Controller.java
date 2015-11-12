package gui;

import dicom.DicomImage;
import filter.BlankFilter;
import filter.FilterBank;
import filter.GaussianFilter;
import filter.MedianFilter;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import dicom.DicomUtil;
import visualizations.MIPVisualization;
import visualizations.OrthogonalSlicesVisualization;
import visualizations.TFVisualization;
import visualizations.Visualization;

import java.io.File;

public class Controller {

    @FXML
    private Parent root;
    @FXML
    private Button loadButton;
    @FXML
    private Button quickButton;
    @FXML
    private Label statusLabel;
    @FXML
    private AnchorPane vtkPane;
    @FXML
    private Label dataPathLabel;
    @FXML
    private ComboBox visTypeCombo;
    @FXML
    private ComboBox filterTypeCombo;
    @FXML
    private AnchorPane settingsPane;
    @FXML
    private Slider slideWindowLower;
    @FXML
    private Slider slideWindowUpper;

    private Stage stage;
    private DicomImage dicomImage;
    private Visualization vis;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {

        //Clear status label
        statusLabel.setText("");

        ObservableList<String> visTypes = FXCollections.observableArrayList();
        visTypes.add("Orthogonal Slices");
        visTypes.add("MIP");
        visTypes.add("DVR");
        visTypeCombo.setItems(visTypes);
        visTypeCombo.setValue(visTypes.get(1));

        ObservableList<String> filterTypes = FXCollections.observableArrayList();
        filterTypes.add("None");
        filterTypes.add("Gaussian");
        filterTypes.add("Median");
        filterTypeCombo.setItems(filterTypes);
        filterTypeCombo.setValue(filterTypes.get(0));

        vtkPane.setStyle("-fx-background-color: black;");


    }

    public void loadDataSet() {

        //Get folder path
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Path to folder containing DICOM files ...");
        File defaultDirectory = new File(System.getProperty("user.dir"));
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(stage);
        String path = selectedDirectory.getPath();

        doLoad(path);

    }

    public void quickLoad() {

        String path = "data\\BRAINIX\\sT2W-FLAIR - 401";
        doLoad(path);

    }

    public void doLoad(String path) {

        updateStatus("Loading ...",null,null);
        dicomImage = DicomUtil.readDicom(path);
        int[] imgDims = dicomImage.getDimensions();
        dataPathLabel.setText(path.substring(path.lastIndexOf("\\")+1, path.length()) + " ("+imgDims[0]+"x"+imgDims[1]+"x"+imgDims[2]+")");
        updateStatus("Files loaded.", Color.DARKGREEN, 2000l);

        doVis();
    }

    public void changeFilter() {

        if (dicomImage != null) {

            if (filterTypeCombo.getValue().equals("None")) {
                FilterBank.setFilter(new BlankFilter());
            } else if (filterTypeCombo.getValue().equals("Gaussian")) {
                FilterBank.setFilter(new GaussianFilter());
            } else if (filterTypeCombo.getValue().equals("Median")) {
                FilterBank.setFilter(new MedianFilter());
            }

            vis.getRenderer().render();

        }
    }

    public void doVis() {

        if (dicomImage != null) {

            if (visTypeCombo.getValue().equals("Orthogonal Slices")) {
                vis = new OrthogonalSlicesVisualization(vtkPane, dicomImage);
            } else if (visTypeCombo.getValue().equals("MIP")) {
                vis = new MIPVisualization(vtkPane, dicomImage);
            } else if (visTypeCombo.getValue().equals("DVR")) {
                vis = new TFVisualization(vtkPane, dicomImage);
            }

            settingsPane.getChildren().setAll(vis.getVisSettings());
            vis.getRenderer().render();

            slideWindowLower.setDisable(false);
            slideWindowLower.setMin(dicomImage.getWindowLower());
            slideWindowLower.setMax(dicomImage.getWindowUpper());
            slideWindowLower.setValue(slideWindowLower.getMin());
            slideWindowLower.valueProperty().addListener((ov, old_val, new_val) -> {
                double upperVal = slideWindowUpper.getValue();
                if(new_val.doubleValue() > upperVal) {
                    slideWindowLower.setValue(upperVal);
                    dicomImage.setWindowLower(upperVal);
                } else {
                    dicomImage.setWindowLower(new_val.doubleValue());
                }
                vis.getRenderer().render();
            });

            slideWindowUpper.setDisable(false);
            slideWindowUpper.setMin(dicomImage.getWindowLower());
            slideWindowUpper.setMax(dicomImage.getWindowUpper());
            slideWindowUpper.setValue(slideWindowUpper.getMax());
            slideWindowUpper.valueProperty().addListener((ov, old_val, new_val) -> {
                double lowerVal = slideWindowLower.getValue();
                if(new_val.doubleValue() < lowerVal) {
                    slideWindowUpper.setValue(lowerVal);
                    dicomImage.setWindowUpper(lowerVal);
                } else {
                    dicomImage.setWindowUpper(new_val.doubleValue());
                }
                vis.getRenderer().render();
            });

        }

    }

    private void updateStatus(String message, Color color, Long duration) {

        if (color != null) {
            statusLabel.textFillProperty().setValue(color);
        } else {
            statusLabel.textFillProperty().setValue(Color.BLACK);
        }

        statusLabel.setText(message);

        if (duration != null) {
            FadeTransition ft = new FadeTransition(Duration.millis(duration), statusLabel);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.play();
        }

    }
}
