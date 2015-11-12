package renderer;

import dicom.DicomImage;
import filter.Filter;
import filter.FilterBank;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Created by felix on 03.11.2015.
 */
public class OrthogonalSlicesRenderer implements Renderer {

    private AnchorPane renderPane;
    protected Canvas[] canvasArr;
    private DicomImage img;

    private int[] imgDims;
    private Color[] viewColors;
    private int[] selectedSlice;
    private int[] lastSelectedSlice;

    private boolean decoration;
    private double decorationAlpha;
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
        selectedSlice = new int[3];
        selectedSlice[0] = (int)(imgDims[0]*0.5d); //Mid X-slice
        selectedSlice[1] = (int)(imgDims[1]*0.5d); //Mid Y-slice
        selectedSlice[2] = (int)(imgDims[2]*0.5d); //Mid Z-slice

        //Init colors
        viewColors = new Color[3];
        viewColors[0] = Color.BLUE; //x-slice
        viewColors[1] = Color.GREEN; //y-slice
        viewColors[2] = Color.RED; //z-slice

        //Per defaults titles are shown, but no scaling
        decoration = true;
        decorationAlpha = 1.0d;
        doScale = true;

        //To avoid unnecessary re-rendering
        lastSelectedSlice = new int[3];
        lastSelectedSlice[0] = selectedSlice[0];
        lastSelectedSlice[1] = selectedSlice[1];
        lastSelectedSlice[2] = selectedSlice[2];

    }

    public int getSelectedX() {
        return  selectedSlice[0];
    }

    public void selectX(int x) {
        if (x > getMaxX()) {
            return;
        }
        selectedSlice[0] = Math.max(x-1,0);
    }

    public int getMaxX() {
        return imgDims[0];
    }

    public int getSelectedY() {
        return  selectedSlice[1];
    }

    public void selectY(int y) {
        if (y > getMaxY()) {
            return;
        }
        selectedSlice[1] = Math.max(y-1,0);
    }

    public int getMaxY() {
        return imgDims[1];
    }

    public int getSelectedZ() {
        return  selectedSlice[2];
    }

    public void selectZ(int z) {
        if (z > getMaxZ()) {
            return;
        }
        selectedSlice[2] = Math.max(z-1,0);
    }

    public int getMaxZ() {
        return imgDims[2];
    }

    public boolean showDecoration() {
        return decoration;
    }

    public void setDecoration(boolean decoration) {
        this.decoration = decoration;
    }

    public double getDecorationAlpha() {
        return decorationAlpha;
    }

    public void setDecorationAlpha(double decorationAlpha) {
        this.decorationAlpha = decorationAlpha;
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

        Node fxNode = renderSlice(0,1,2,0,false,"XY");

        renderPane.setTopAnchor(fxNode, 5.0);
        renderPane.setLeftAnchor(fxNode, 5.0);
    }

    public void renderXZ() {

        Node fxNode = renderSlice(0,2,1,1,doScale,"XZ");

        if (doScale) {
            renderPane.setTopAnchor(fxNode, 5.0);
            renderPane.setRightAnchor(fxNode, 5.0);
        } else {
            renderPane.setTopAnchor(fxNode,imgDims[0]*0.5d-canvasArr[1].getHeight()*0.5d);
            renderPane.setRightAnchor(fxNode,imgDims[1]*0.5d-canvasArr[1].getWidth()*0.5d);
        }


    }

    public void renderYZ() {

        Node fxNode = renderSlice(1,2,0,2,doScale,"YZ");

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
     * @param canvasIndex The 0-2 index of the canvas to draw on
     * @param resizeToXY Should the output ImageView be resized to have the same dimensions as the XY-view?
     * @param title title of the view
     * @return ImageView containing the rendered image
     */
    private Node renderSlice(int dim1, int dim2, int staticDim, int canvasIndex, boolean resizeToXY, String title) {


        //Pixel selector, used to pass params in the correct order to img.getValue()
        int[] pixelSelector = new int[3];
        pixelSelector[staticDim] = selectedSlice[staticDim];

        //Draw on canvas, create a local copy of maxValue on stack
        GraphicsContext gc = canvasArr[canvasIndex].getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();

        for (int i = 0; i < imgDims[dim1];i++) {
            for (int j = 0; j < imgDims[dim2];j++) {
                pixelSelector[dim1] = i;
                pixelSelector[dim2] = j; //title offset
                double pixelVal = img.getRelativeWindowedValue(pixelSelector[0], pixelSelector[1], pixelSelector[2]);
                pw.setColor(i, j, new Color(pixelVal, pixelVal, pixelVal, 1));
            }
        }

        //Filter
        Filter filter = FilterBank.getFilter();
        filter.prepare(canvasArr[canvasIndex]);
        canvasArr[canvasIndex] = filter.execute();
        gc = canvasArr[canvasIndex].getGraphicsContext2D();

        //Draw link-lines on canvas
        if (decoration) {

            gc.setGlobalAlpha(decorationAlpha);
            gc.setLineWidth(1.0d);

            gc.setStroke(viewColors[dim1]);
            gc.strokeLine(selectedSlice[dim1],0,selectedSlice[dim1],imgDims[dim2]);

            gc.setStroke(viewColors[dim2]);
            gc.strokeLine(0,selectedSlice[dim2],imgDims[dim1],selectedSlice[dim2]);

        }

        //Create imageView from canvas
        ImageView iView = RenderUtil.canvasToImageView(canvasArr[canvasIndex],renderPane,false,false);

        //Resize image to dimension of XY-view, if wanted
        if (resizeToXY) {
            iView.setFitWidth(imgDims[0]);
            iView.setFitHeight(imgDims[1]);
        }

        Node fxNode;
        if (decoration) {

            //Create stack pane to stack decoration canvas on imageview
            StackPane stackPane = new StackPane();
            stackPane.setPrefWidth(iView.getBoundsInParent().getWidth()+1);
            stackPane.setPrefHeight(iView.getBoundsInParent().getHeight()+1);

            //Draw text and rectangle box
            Canvas decoCanv = new Canvas(iView.getBoundsInParent().getWidth()+1, iView.getBoundsInParent().getHeight()+1);
            GraphicsContext decoGc = decoCanv.getGraphicsContext2D();
            decoGc.setGlobalAlpha(decorationAlpha);
            decoGc.setStroke(viewColors[staticDim]);
            decoGc.setLineWidth(1.0d);
            decoGc.strokeRect(0, 0, decoCanv.getWidth(), decoCanv.getHeight());
            decoGc.setFill(Color.WHITE);
            decoGc.setFont(Font.font("Verdana", 10));
            decoGc.fillText(title + " slice: " + (selectedSlice[staticDim]+1),2,10);

            //stack
            stackPane.getChildren().add(iView);
            stackPane.getChildren().add(decoCanv);
            fxNode = stackPane;

        } else {
            fxNode = iView;
            RenderUtil.enableInteractivity(iView);
        }

        //If not rendered "for the first time" (size!=3) add node, else set new
        if (renderPane.getChildren().size() == 3) {
            renderPane.getChildren().set(canvasIndex,fxNode);
        } else {
            renderPane.getChildren().add(fxNode);
        }

        return fxNode;

    }


}
