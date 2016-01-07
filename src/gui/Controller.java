package gui;

import dicom.DicomImage;
import filter.BlankFilter;
import filter.FilterBank;
import filter.GaussianFilterCPU;
import filter.MedianFilterCPU;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import dicom.DicomUtil;
import visualizations.*;

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
    private HBox histoBox;
    @FXML
    private Slider slideWindowCenter;
    @FXML
    private Slider slideWindowWidth;

    private Stage stage;
    private DicomImage dicomImage;
    private Visualization vis;
    private static Controller instance;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initialize() {

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
        vis = new BlankVisualization(settingsPane);

        instance = this;


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

        //Init window function
        slideWindowCenter.setDisable(false);
        slideWindowCenter.setMin(dicomImage.getWindowLower());
        slideWindowCenter.setMax(dicomImage.getWindowUpper());
        slideWindowCenter.setValue(dicomImage.getWindowUpper()*0.5d);
        slideWindowCenter.valueProperty().addListener((ov, old_val, new_val) -> {
            dicomImage.setWindowCenter(new_val.doubleValue());
            updateHistogram();
            vis.getRenderer().render();
        });
        slideWindowWidth.setDisable(false);
        slideWindowWidth.setMin(0);
        slideWindowWidth.setMax(dicomImage.getWindowUpper()*0.5d);
        slideWindowWidth.setValue(slideWindowWidth.getMax());
        slideWindowWidth.valueProperty().addListener((ov, old_val, new_val) -> {
            dicomImage.setWindowWidth(new_val.doubleValue());
            updateHistogram();
            vis.getRenderer().render();
        });

        doVis();
    }

    public void changeFilter() {

        if (dicomImage != null) {

            if (filterTypeCombo.getValue().equals("None")) {
                FilterBank.setFilter(new BlankFilter());
            } else if (filterTypeCombo.getValue().equals("Gaussian")) {
                FilterBank.setFilter(new GaussianFilterCPU());
            } else if (filterTypeCombo.getValue().equals("Median")) {
                FilterBank.setFilter(new MedianFilterCPU());
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
            updateHistogram();

        }

    }

    public void updateStatus(String message, Color color, Long duration) {

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

    public void updateHistogram() {

        double lowerWindowX = Math.round(dicomImage.getWindowLower()/dicomImage.getMaxVal()*histoBox.getWidth());
        double upperWindowX = Math.round(dicomImage.getWindowUpper()/dicomImage.getMaxVal()*histoBox.getWidth());

        Canvas histo = HistogramGenerator.generate2DHistogram(64,histoBox.getWidth(),histoBox.getHeight(),lowerWindowX,upperWindowX);
        histoBox.getChildren().setAll(histo);
    }

    public static Controller getInstance() {
        return instance;
    }


}


