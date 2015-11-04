package dicom;

import org.itk.simple.Image;
import org.itk.simple.ImageSeriesReader;
import org.itk.simple.VectorString;
import vtk.vtkDICOMImageReader;

/**
 * Created by felix on 02.11.2015.
 */
public class DicomUtil {

    public static DicomImage readDicom(String path) {
        vtkDICOMImageReader dicomReader = new vtkDICOMImageReader();
        dicomReader.SetDirectoryName(path);  //only recognized thing!!!
        dicomReader.Update();
        return new DicomImage(dicomReader.GetOutput(),dicomReader.GetOutputPort());
    }

    public static Image readDicomSITK(String path) {
        ImageSeriesReader dicomReader = new ImageSeriesReader();
        VectorString fileNames = ImageSeriesReader.getGDCMSeriesFileNames(path);
        dicomReader.setFileNames(fileNames);
        return dicomReader.execute();
    }

}
