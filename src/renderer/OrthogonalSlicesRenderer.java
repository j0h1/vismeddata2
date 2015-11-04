package renderer;

import dicom.DicomImage;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import vtk.vtkImageData;
import vtk.vtkImageReslice;

/**
 * Created by felix on 03.11.2015.
 */
public class OrthogonalSlicesRenderer implements Renderer {

    private AnchorPane renderPane;
    protected Canvas[] canvasArr;

    private vtkImageData img;
    private double imgMax;

    private int selectedX;
    private int selectedY;
    private int selectedZ;
    private int[] imgDims;

    public OrthogonalSlicesRenderer(AnchorPane renderPane, DicomImage dicomImage) {

        System.out.println("OrthogonalSlicesRenderer init.");
        int[] tempDims = dicomImage.getImageData().GetDimensions();
        double[] spacing = dicomImage.getImageData().GetSpacing();
        System.out.println("\tSpacing: "+spacing[0] +", "+spacing[1]+", "+spacing[2]);

        /*double newZSpacing = spacing[2]*(spacing[2]/spacing[0]);
        System.out.println("\tNew estimate for spacingZ: "+newZSpacing);
        vtkImageReslice reslicer = new vtkImageReslice();
        reslicer.SetInputConnection(dicomImage.getImagePort());
        reslicer.SetOutputSpacing(spacing[0],spacing[0],newZSpacing);
        reslicer.SetInterpolationModeToCubic();
        reslicer.Update();

        this.img = reslicer.GetOutput();*/

        this.img = dicomImage.getImageData();
        imgDims = img.GetDimensions();
        System.out.println("\tDimensions: "+imgDims[0] +", "+imgDims[1]+", "+imgDims[2]);

        this.renderPane = renderPane;
        this.canvasArr = new Canvas[3];

        canvasArr[0] = new Canvas(imgDims[0],imgDims[1]); //XY
        canvasArr[1] = new Canvas(imgDims[0],imgDims[2]); //XZ
        canvasArr[2] = new Canvas(imgDims[1],imgDims[2]); //YZ

        imgMax = Double.MIN_VALUE;
        for (int x = 0; x < imgDims[0]; x++) {
            for (int y = 0; y < imgDims[1]; y++) {
                for (int z = 0; z < imgDims[2]; z++) {
                    double pixelVal = img.GetScalarComponentAsDouble(x,y,z,0);

                    //Get min
                    if (pixelVal > imgMax) {
                        imgMax = pixelVal;
                    }
                }
            }
        }

        selectedX = 0;
        selectedY = 0;
        selectedZ = 0;

    }

    public void selectX(int x) {
        if (x > getMaxX()) {
            return;
        }
        this.selectedX = Math.max(x-1,0);
    }

    public int getMaxX() {
        return imgDims[0];
    }

    public void selectY(int y) {
        if (y > getMaxY()) {
            return;
        }
        this.selectedY = Math.max(y-1,0);
    }

    public int getMaxY() {
        return imgDims[1];
    }

    public void selectZ(int z) {
        if (z > getMaxZ()) {
            return;
        }
        this.selectedZ = Math.max(z-1,0);
    }

    public int getMaxZ() {
        return imgDims[2];
    }

    public synchronized void render() {

        for (int i = 0; i < 3; i++) {

            //Fill background
            canvasArr[i].getGraphicsContext2D().setFill(Color.BLACK);
            canvasArr[i].getGraphicsContext2D().fillRect(0, 0, canvasArr[i].getWidth(), canvasArr[i].getHeight());

        }

        //XY
        PixelWriter pw = canvasArr[0].getGraphicsContext2D().getPixelWriter();
        for (int x = 0; x < imgDims[0];x++) {
            for (int y = 0; y < imgDims[1];y++) {
                double pixelVal = img.GetScalarComponentAsDouble(x,y,selectedZ,0);
                pw.setColor(x,y,new Color(pixelVal/imgMax,pixelVal/imgMax,pixelVal/imgMax,1));
            }
        }

        //XZ
        PixelWriter pw2 = canvasArr[1].getGraphicsContext2D().getPixelWriter();
        for (int x = 0; x < imgDims[0];x++) {
            for (int z = 0; z < imgDims[2];z++) {
                double pixelVal = img.GetScalarComponentAsDouble(x,selectedY,z,0);
                pw2.setColor(x,z,new Color(pixelVal/1024d,pixelVal/1024d,pixelVal/1024d,1));
            }
        }

        //YZ
        PixelWriter pw3 = canvasArr[2].getGraphicsContext2D().getPixelWriter();
        for (int y = 0; y < imgDims[1];y++) {
            for (int z = 0; z < imgDims[2];z++) {
                double pixelVal = img.GetScalarComponentAsDouble(selectedX,y,z,0);
                pw3.setColor(y,z,new Color(pixelVal/1024d,pixelVal/1024d,pixelVal/1024d,1));
            }
        }

        //Set vis
        renderPane.setTopAnchor(canvasArr[0],0.0);
        renderPane.setLeftAnchor(canvasArr[0], 0.0);
        renderPane.setTopAnchor(canvasArr[1], 0.0);
        renderPane.setRightAnchor(canvasArr[1], 0.0);
        renderPane.setBottomAnchor(canvasArr[2],0.0);
        renderPane.setLeftAnchor(canvasArr[2],0.0);
        renderPane.getChildren().setAll(canvasArr[0], canvasArr[1], canvasArr[2]);



    }


}
