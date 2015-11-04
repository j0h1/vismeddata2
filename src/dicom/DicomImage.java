package dicom;

import vtk.vtkAlgorithmOutput;
import vtk.vtkImageData;

/**
 * Created by felix on 04.11.2015.
 */
public class DicomImage {

    private vtkImageData imageData;
    private vtkAlgorithmOutput imagePort;

    public DicomImage(vtkImageData imageData, vtkAlgorithmOutput imagePort) {
        this.imageData = imageData;
        this.imagePort = imagePort;
    }

    public vtkImageData getImageData() {
        return imageData;
    }

    public void setImageData(vtkImageData imageData) {
        this.imageData = imageData;
    }

    public vtkAlgorithmOutput getImagePort() {
        return imagePort;
    }

    public void setImagePort(vtkAlgorithmOutput imagePort) {
        this.imagePort = imagePort;
    }

}
