package visualizations;

import javafx.scene.layout.Pane;
import renderer.Renderer;


/**
 * Created by felix on 03.11.2015.
 */
public interface Visualization {

    Renderer getRenderer();

    Pane getVisSettings();

}
