package dicom;

import vtk.vtkAlgorithmOutput;
import vtk.vtkDataArray;
import vtk.vtkImageData;

/**
 * Created by felix on 04.11.2015.
 */
public class DicomImage {

    private double maxValue;
    private double[] imgArr;
    private int[] dimensions;

    private int dimensionX;
    private int dimensionY;
    private int dimensionZ;

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
        maxValue = Double.MIN_VALUE;
        for (int i = 0; i < scalars.GetSize(); i++) {
            imgArr[i] = scalars.GetTuple1(i);
            if (scalars.GetTuple1(i) > maxValue) {
                maxValue = scalars.GetTuple1(i);
            }
        }

        //Reporting prints
        System.out.println("DICOM LOADED\n\tdimX: "+dimensions[0]+" dimY: "+dimensions[1]+" dim Z: "+dimensions[2]);
        System.out.println("\tMax-estimate: "+maxValue+" (VTK: "+maxValue+")");
    }

    public double getMaxValue() {
        return maxValue;
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
