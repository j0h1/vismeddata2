package dicom;

import vtk.vtkAlgorithmOutput;
import vtk.vtkDataArray;
import vtk.vtkImageData;

/**
 * Created by felix on 04.11.2015.
 */
public class DicomImage {

    private double[] imgArr;
    private int[] dimensions;

    private int dimensionX;
    private int dimensionY;
    private int dimensionZ;
    private double windowUpper;
    private double windowLower;
    private double windowSpan;

    public DicomImage(vtkImageData imageData, vtkAlgorithmOutput imagePort) {

        //Extract dimension from vtk for independence
        dimensions = imageData.GetDimensions();

        //stack variables for faster pixel locating on getValue/setValue
        dimensionX = dimensions[0];
        dimensionY = dimensions[0];
        dimensionZ = dimensions[0];

        //Create 1D-java image array and locate max intensity value
        vtkDataArray scalars =  imageData.GetPointData().GetScalars();
        imgArr = new double[scalars.GetSize()];
        windowUpper = Double.MIN_VALUE;
        for (int i = 0; i < scalars.GetSize(); i++) {
            imgArr[i] = scalars.GetTuple1(i);
            if (scalars.GetTuple1(i) > windowUpper) {
                windowUpper = scalars.GetTuple1(i);
            }
        }

        windowLower = 0;
        windowSpan = windowUpper;

        //Reporting prints
        System.out.println("DICOM LOADED\n\tdimX: "+dimensions[0]+" dimY: "+dimensions[1]+" dim Z: "+dimensions[2]);
        System.out.println("\tWindow value (from max): "+windowUpper);
    }

    public double getWindowedValue(int x, int y, int z) {
        return Math.max(Math.min(getValue(x, y, z), windowUpper), windowLower);
    }

    public double getRelativeWindowedValue(int x, int y, int z) {
        return (getWindowedValue(x, y, z)-windowLower)/windowSpan;
    }

    public double getWindowLower() {
        return windowLower;
    }

    public void setWindowLower(double window) {
        if (window > windowUpper) {
            return;
        }
        this.windowLower = window;
        updateWindowSpan();
    }

    public double getWindowUpper() {
        return windowUpper;
    }

    public void setWindowUpper(double window) {
        if (window < 0) {
            return;
        }
        this.windowUpper = window;
        updateWindowSpan();
    }

    private void updateWindowSpan() {
        windowSpan = windowUpper - windowLower;
    }


    public int[] getDimensions() {
        return dimensions;
    }

    public double setValue(int x, int y, int z, double value) {
        return imgArr[dimensionX*dimensionY*z+dimensionX*y+x] = value;
    }

    public double getValue(int x, int y, int z) {
        return imgArr[dimensionX*dimensionY*z+dimensionX*y+x];
    }

}
