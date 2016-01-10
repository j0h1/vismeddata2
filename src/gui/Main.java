package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vtk.vtkNativeLibrary;

public class Main extends Application {

    /* Load VTK shared librarires (.dll) on startup, print message if not found */
    static
    {
        if (!vtkNativeLibrary.LoadAllNativeLibraries())
        {
            for (vtkNativeLibrary lib : vtkNativeLibrary.values())
            {
                if (!lib.IsLoaded())
                    System.out.println(lib.GetLibraryName() + " not loaded");
            }

            System.out.println("Make sure the search path is correct: ");
            System.out.println(System.getProperty("java.library.path"));
        }
        //vtkNativeLibrary.DisableOutputWindow(null);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("LOOKATVOLUMEDATA");
        primaryStage.setScene(new Scene(root, 1366, 768));


        Controller controller = loader.getController();
        controller.setStage(primaryStage);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
