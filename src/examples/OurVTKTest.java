package examples;

import vtk.*;

/**
 * Created by felix on 02.11.2015.
 */
public class OurVTKTest {

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

    public static void main (String args[]) {

        String path = "data\\BRAINIX\\sT2W-FLAIR - 401\\";

        vtkDICOMImageReader dicomReader = new vtkDICOMImageReader();
        dicomReader.SetDirectoryName(path);  //only recognized thing!!!
        //dicomReader.SetFileName(path);
        dicomReader.Update();
        vtkImageData fullImage = dicomReader.GetOutput();

        vtkImageViewer2 imgView = new vtkImageViewer2();
        imgView.SetInputConnection(dicomReader.GetOutputPort());
        vtkRenderWindowInteractor renderWindowInteractor = new vtkRenderWindowInteractor();
        //renderWindowInteractor.SetInteractorStyle(new vtkInteractorStyleRubberBand2D());
        imgView.SetupInteractor(renderWindowInteractor);
        imgView.Render();
        imgView.GetRenderer().ResetCamera();
        imgView.Render();

        renderWindowInteractor.Start();

    }
}
