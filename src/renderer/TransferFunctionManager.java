package renderer;

import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;

/**
 * Created by j0h1 on 09.11.2015.
 */
public class TransferFunctionManager {

    private Stop[] colorMapping;
    private Stop[] opacityMapping;
    private static boolean changedSinceInitialization = false;

    private static TransferFunctionManager transferFunctionManager;

    private TransferFunctionManager() { }

    public static TransferFunctionManager getInstance() {
        if (transferFunctionManager == null) {
            transferFunctionManager = new TransferFunctionManager();
        }
        return transferFunctionManager;
    }

    public Color apply(double intensity) {
        // find interval of given intensity in mappings
        Stop[] colorInterpolationStops = findInterpolationInterval(colorMapping, intensity);
        Stop[] opacityInterpolationStops = findInterpolationInterval(opacityMapping, intensity);

        // calculate color ratios to interpolate in between the lower and higher boundaries of the interval
        double colorRatio = (colorInterpolationStops[1].getOffset() - intensity) /
                (colorInterpolationStops[1].getOffset() - colorInterpolationStops[0].getOffset());
        double inverseColorRatio = 1.0 - colorRatio;

        double opacityRatio = (opacityInterpolationStops[1].getOffset() - intensity) /
                (opacityInterpolationStops[1].getOffset() - opacityInterpolationStops[0].getOffset());
        double inverseOpacityRatio = 1.0 - opacityRatio;

        Color interpolated = new Color(
                colorInterpolationStops[0].getColor().getRed() * colorRatio + colorInterpolationStops[1].getColor().getRed() * inverseColorRatio,
                colorInterpolationStops[0].getColor().getGreen() * colorRatio + colorInterpolationStops[1].getColor().getGreen() * inverseColorRatio,
                colorInterpolationStops[0].getColor().getBlue() * colorRatio + colorInterpolationStops[1].getColor().getBlue() * inverseColorRatio,
                opacityInterpolationStops[0].getColor().getOpacity() * opacityRatio + opacityInterpolationStops[1].getColor().getOpacity() * inverseOpacityRatio);

        return interpolated;
    }

    private Stop[] findInterpolationInterval(Stop[] mapping, double intensity) {
        if (intensity == 1.0) {
            // if intensity is 1.0, choose highest and second highest mapping
            return new Stop[] { mapping[mapping.length - 2], mapping[mapping.length - 1] };
        }

        Stop[] interpolationStops = new Stop[2];

        for (int i = 0; i < mapping.length; i++) {
            // in order for this to work, the mappings are pre-sorted by intensity
            // look for a mapping that is > than intensity and return neighboring mappings for interpolation
            if (mapping[i].getOffset() > intensity) {
                interpolationStops[0] = mapping[i - 1];    // lower interpolation border
                interpolationStops[1] = mapping[i];        // higher interpolation border
                break;
            }
        }

        return interpolationStops;
    }

    public boolean isChangedSinceInitialization() {
        return changedSinceInitialization;
    }

    public void setChangedSinceInitialization(boolean changedSinceInitialization) {
        this.changedSinceInitialization = changedSinceInitialization;
    }

    public Stop[] getColorMapping() {
        return colorMapping;
    }

    public void setColorMapping(Stop[] colorMapping) {
        this.colorMapping = colorMapping;
    }

    public Stop[] getOpacityMapping() {
        return opacityMapping;
    }

    public void setOpacityMapping(Stop[] opacityMapping) {
        this.opacityMapping = opacityMapping;
    }
}
