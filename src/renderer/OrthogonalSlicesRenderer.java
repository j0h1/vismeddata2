package renderer;

import dicom.DicomImage;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

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

    private boolean showTitles;
    private boolean doScale;

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

        //Per defaults titles are shown, but no scaling
        showTitles = true;
        doScale = true;

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

    public boolean showTitles() {
        return showTitles;
    }

    public void setShowTitles(boolean showTitles) {
        this.showTitles = showTitles;
    }

    public boolean isScaling() {
        return doScale;
    }

    public void setScaling(boolean doScale) {
        this.doScale = doScale;
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

        Node fxNode = renderSlice(0,1,2,selectedZ,0,false,"XY");

        renderPane.setTopAnchor(fxNode, 5.0);
        renderPane.setLeftAnchor(fxNode, 5.0);
    }

    public void renderXZ() {

        Node fxNode = renderSlice(0,2,1,selectedY,1,doScale,"XZ");

        if (doScale) {
            renderPane.setTopAnchor(fxNode, 5.0);
            renderPane.setRightAnchor(fxNode, 5.0);
        } else {
            renderPane.setTopAnchor(fxNode,imgDims[0]*0.5d-canvasArr[1].getHeight()*0.5d);
            renderPane.setRightAnchor(fxNode,imgDims[1]*0.5d-canvasArr[1].getWidth()*0.5d);
        }


    }

    public void renderYZ() {

        Node fxNode = renderSlice(1,2,0,selectedX,2,doScale,"YZ");

        if (doScale) {
            renderPane.setBottomAnchor(fxNode, 5.0);
            renderPane.setLeftAnchor(fxNode, renderPane.getWidth() * 0.5 - fxNode.getBoundsInParent().getWidth()*0.5d);
        } else {
            renderPane.setBottomAnchor(fxNode, imgDims[0] * 0.5d - canvasArr[2].getHeight() * 0.5d);
            renderPane.setLeftAnchor(fxNode, renderPane.getWidth() * 0.5 - canvasArr[2].getWidth() * 0.5);
        }


    }

    /**
     * Renders a single slice
     * @param dim1 The first "variable" dimension e.g. 0 for "x"
     * @param dim2 The second "variable" dimension e.g. 1 for "y"
     * @param staticDim The static dimension which is not varied, but selected (via GUI) e.g. 3 for "z"
     * @param selectedSlice The slice of the static dimension that is selected  e.g. 15 for slice 15
     * @param canvasIndex The 0-2 index of the canvas to draw on
     * @param resizeToXY Should the output ImageView be resized to have the same dimensions as the XY-view?
     * @param title title of the view
     * @return ImageView containing the rendered image
     */
    private Node renderSlice(int dim1, int dim2, int staticDim, int selectedSlice, int canvasIndex, boolean resizeToXY, String title) {

        //Pixel selector, used to pass params in the correct order to img.getValue()
        int[] pixelSelector = new int[3];
        pixelSelector[staticDim] = selectedSlice;

        //Draw on canvas, create a local copy of maxValue on stack
        GraphicsContext gc = canvasArr[canvasIndex].getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();
        double imgMax = img.getMaxValue();

        for (int i = 0; i < imgDims[dim1];i++) {
            for (int j = 0; j < imgDims[dim2];j++) {
                pixelSelector[dim1] = i;
                pixelSelector[dim2] = j; //title offset
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

        Node fxNode;
        if (showTitles) {
            StackPane stackPane = new StackPane();
            stackPane.setPrefHeight(iView.getFitHeight());
            stackPane.setPrefWidth(iView.getFitWidth());
            Text titleLabel = new Text(title + " slice: "+(selectedSlice+1));
            titleLabel.setFont(Font.font("Verdana", 10));
            titleLabel.setFill(Color.LIGHTGREEN);
            stackPane.getChildren().add(iView);
            stackPane.setAlignment(Pos.TOP_LEFT);
            stackPane.getChildren().add(titleLabel);
            fxNode = stackPane;
        } else {
            fxNode = iView;
        }

        //If not rendered "for the first time" (size!=3) add view, else set new
        if (renderPane.getChildren().size() == 3) {
            renderPane.getChildren().set(canvasIndex,fxNode);
        } else {
            renderPane.getChildren().add(fxNode);
        }

        return fxNode;

    }


}
