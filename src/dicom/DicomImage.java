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

    private double windowCenter;
    private double windowWidth;
    private double maxVal;
    private double windowLower;
    private double windowUpper;
    private double windowSpan;
    private WindowMode wmode;

    public DicomImage(vtkImageData imageData, vtkAlgorithmOutput imagePort) {

        //Extract dimension from vtk for independence
        dimensions = imageData.GetDimensions();

        //stack variables for faster pixel locating on getValue/setValue
        dimensionX = dimensions[0];
        dimensionY = dimensions[1];
        dimensionZ = dimensions[2];

        //Create 1D-java image array and locate max intensity value
        vtkDataArray scalars =  imageData.GetPointData().GetScalars();
        imgArr = new double[scalars.GetSize()];
        maxVal = Double.MIN_VALUE;
        for (int i = 0; i < scalars.GetSize(); i++) {
            imgArr[i] = scalars.GetTuple1(i);
            if (scalars.GetTuple1(i) > maxVal) {
                maxVal = scalars.GetTuple1(i);
            }
        }

        windowCenter = maxVal*0.5d;
        windowWidth = maxVal*0.5d;
        updateWindow();

        wmode = new WindowMode1();

        //Reporting prints
        System.out.println("DICOM LOADED\n\tdimX: "+dimensions[0]+" dimY: "+dimensions[1]+" dim Z: "+dimensions[2]);
    }

    public double getMaxVal() {
        return maxVal;
    }

    public int getSampleCount() {
        return imgArr.length;
    }

    public double getWindowedValue(int x, int y, int z) {
        return wmode.getWindowedValue(getValue(x, y, z));
    }

    public double getRelativeWindowedValue(int x, int y, int z) {
        return (getWindowedValue(x, y, z)-windowLower)/windowSpan;
    }

    public void setWindowCenter(double center) {
        if (center < 0) {
            return;
        }
        windowCenter = center;
        updateWindow();
    }

    public void setWindowWidth(double width) {
        windowWidth = width;
        updateWindow();
    }


    public double getWindowLower() {
        return windowLower;
    }


    public double getWindowUpper() {
        return windowUpper;
    }


    private void updateWindow() {

        windowLower = Math.max(0,windowCenter-windowWidth);
        windowUpper = windowCenter+windowWidth;
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

    //WINDOWING MODES FOR DYNAMIC BINDING

    private interface WindowMode {
        double getWindowedValue(double val);
    }

    private class WindowMode1 implements WindowMode {

        @Override
        public double getWindowedValue(double val) {
            return Math.max(Math.min(val, windowUpper), windowLower);
        }

    }

    private class WindowMode2 implements WindowMode {

        @Override
        public double getWindowedValue(double val) {
            if ( val > windowUpper) {
                return windowLower;
            }
            return Math.max(val,windowLower);
        }
    }


}
