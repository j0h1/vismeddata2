package utils;

import vtk.vtkDICOMImageReader;
import vtk.vtkImageData;

/**
 * Created by felix on 02.11.2015.
 */
public class DicomUtil {

    public static vtkImageData readDicom(String path) {
        vtkDICOMImageReader dicomReader = new vtkDICOMImageReader();
        dicomReader.SetDirectoryName(path);  //only recognized thing!!!
        dicomReader.Update();
        return dicomReader.GetOutput();
    }
}
