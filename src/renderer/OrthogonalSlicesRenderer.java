package renderer;

import dicom.DicomImage;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import vtk.vtkDataArray;
import vtk.vtkImageData;
import vtk.vtkImageReslice;

/**
 * Created by felix on 03.11.2015.
 */
public class OrthogonalSlicesRenderer implements Renderer {

    private AnchorPane renderPane;
    protected Canvas[] canvasArr;

    private DicomImage img;

    private int[] imgDims;
    private int selectedX;
    private int selectedY;
    private int selectedZ;

    public OrthogonalSlicesRenderer(AnchorPane renderPane, DicomImage dicomImage) {

        this.img = dicomImage;
        imgDims = dicomImage.getDimensions();
        this.renderPane = renderPane;

        //Create 3 canvases for rendering from every direction
        this.canvasArr = new Canvas[3];
        canvasArr[0] = new Canvas(imgDims[0],imgDims[1]); //XY
        canvasArr[1] = new Canvas(imgDims[0],imgDims[2]); //XZ
        canvasArr[2] = new Canvas(imgDims[1],imgDims[2]); //YZ

        //Init slice selection with "mid slices"
        selectedX = (int)(imgDims[0]*0.5d); //Mid X-slice
        selectedY = (int)(imgDims[1]*0.5d); //Mid Y-slice
        selectedZ = (int)(imgDims[2]*0.5d); //Mid Z-slice

    }

    public int getSelectedX() {
        return selectedX;
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

    public int getSelectedY() {
        return selectedY;
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

    public int getSelectedZ() {
        return selectedZ;
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

        //Clear vtkPane
        renderPane.getChildren().setAll();

        //Render
        renderXY();
        renderXZ();
        renderYZ();

    }

    public void renderXY() {

        ImageView iView = renderSlice(0,1,2,selectedZ,0,false);

        renderPane.setTopAnchor(iView, 0.0);
        renderPane.setLeftAnchor(iView, 0.0);
    }

    public void renderXZ() {

        ImageView iView = renderSlice(0,2,1,selectedY,1,true);

        renderPane.setTopAnchor(iView, 0.0);
        renderPane.setRightAnchor(iView, 0.0);

    }

    public void renderYZ() {

        ImageView iView = renderSlice(1,2,0,selectedX,2,true);

        renderPane.setBottomAnchor(iView, 0.0);
        renderPane.setLeftAnchor(iView, renderPane.getWidth()*0.5-iView.getFitWidth()*0.5);

    }

    /**
     * Renders a single slice
     * @param dim1 The first "variable" dimension e.g. 0 for "x"
     * @param dim2 The second "variable" dimension e.g. 1 for "y"
     * @param staticDim The static dimension which is not varied, but selected (via GUI) e.g. 3 for "z"
     * @param selectedSlice The slice of the static dimension that is selected  e.g. 15 for slice 15
     * @param canvasIndex The 0-2 index of the canvas to draw on
     * @param resizeToXY Should the output ImageView be resized to have the same dimensions as the XY-view?
     * @return ImageView containing the rendered image
     */
    private ImageView renderSlice(int dim1, int dim2, int staticDim, int selectedSlice, int canvasIndex, boolean resizeToXY) {

        //Pixel selector, used to pass params in the correct order to img.getValue()
        int[] pixelSelector = new int[3];
        pixelSelector[staticDim] = selectedSlice;

        //Draw on canvas, create a local copy of maxValue on stack
        PixelWriter pw = canvasArr[canvasIndex].getGraphicsContext2D().getPixelWriter();
        double imgMax = img.getMaxValue();

        for (int i = 0; i < imgDims[dim1];i++) {
            for (int j = 0; j < imgDims[dim2];j++) {
                pixelSelector[dim1] = i;
                pixelSelector[dim2] = j;
                double pixelVal = img.getValue(pixelSelector[0],pixelSelector[1],pixelSelector[2]);
                pw.setColor(i, j, new Color(pixelVal / imgMax, pixelVal / imgMax, pixelVal / imgMax, 1));
            }
        }

        //Create imageView from canvas
        ImageView iView = RenderUtil.canvasToImageView(canvasArr[canvasIndex],renderPane,false,false);

        //Resize image to dimension of XY-view, if wanted
        if (resizeToXY) {
            iView.setFitWidth(imgDims[0]);
            iView.setFitHeight(imgDims[1]);
        }

        //If not rendered "for the first time" (size!=3) add view, else set new
        if (renderPane.getChildren().size() == 3) {
            renderPane.getChildren().set(canvasIndex,iView);
        } else {
            renderPane.getChildren().add(iView);
        }

        return iView;

    }


}
