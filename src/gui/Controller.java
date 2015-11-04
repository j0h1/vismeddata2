package gui;

import dicom.DicomImage;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import dicom.DicomUtil;
import visualizations.MIPVisualization;
import visualizations.OrthogonalSlicesVisualization;
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
    private AnchorPane settingsPane;

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
        visTypeCombo.setItems(visTypes);
        visTypeCombo.setValue(visTypes.get(1));

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

        //DicomUtil load
        dicomImage = DicomUtil.readDicom(path);
        dataPathLabel.setText(path.substring(path.lastIndexOf("\\")+1, path.length()));
        updateStatus("Files loaded.", Color.DARKGREEN, 2000l);

        doVis();


    }

    public void quickLoad() {

        String path = "data\\BRAINIX\\sT2W-FLAIR - 401";
        dicomImage = DicomUtil.readDicom(path);
        dataPathLabel.setText(path.substring(path.lastIndexOf("\\")+1, path.length()));
        updateStatus("Files loaded.", Color.DARKGREEN, 2000l);

        doVis();

    }

    public void doVis() {

        if (dicomImage != null) {

            if (visTypeCombo.getValue().equals("Orthogonal Slices")) {
                vis = new OrthogonalSlicesVisualization(vtkPane, dicomImage);
            } else if (visTypeCombo.getValue().equals("MIP")) {
                vis = new MIPVisualization(vtkPane, dicomImage);
            }

            settingsPane.getChildren().setAll(vis.getVisSettings());
            vis.getRenderer().render();

        }

    }

    private void updateStatus(String message, Color color, long duration) {
        statusLabel.textFillProperty().setValue(color);
        statusLabel.setText(message);
        FadeTransition ft = new FadeTransition(Duration.millis(duration), statusLabel);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
    }
}
