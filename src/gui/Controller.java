package gui;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.DicomUtil;
import visualization.OrthogonalSliceRenderer;
import visualization.Renderer;
import vtk.vtkImageData;
import javafx.scene.canvas.Canvas;

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

    private Stage stage;
    private vtkImageData img;
    private Renderer renderer;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {

        //Clear status label
        statusLabel.setText("");

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
        img = DicomUtil.readDicom(path);
        dataPathLabel.setText(path.substring(path.lastIndexOf("\\")+1, path.length()));
        updateStatus("Files loaded.", Color.DARKGREEN, 2000l);

        renderer = new Renderer(vtkPane);
        renderer.render();


    }

    public void quickLoad() {

        String path = "data\\BRAINIX\\sT2W-FLAIR - 401";
        img = DicomUtil.readDicom(path);
        dataPathLabel.setText(path.substring(path.lastIndexOf("\\")+1,path.length()));
        updateStatus("Files loaded.", Color.DARKGREEN, 2000l);


        renderer = new OrthogonalSliceRenderer(vtkPane,img);
        renderer.render();

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
