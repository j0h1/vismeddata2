package visualizations;

import javafx.scene.layout.Pane;
import renderer.BlankRenderer;
import renderer.Renderer;

/**
 * Created by felix on 03.11.2015.
 */
public class BlankVisualization implements Visualization {

    private BlankRenderer renderer;

    public BlankVisualization(Pane pane) {
        this.renderer = new BlankRenderer(pane);
    }

    @Override
    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public Pane getVisSettings() {
        return new Pane();
    }
}
