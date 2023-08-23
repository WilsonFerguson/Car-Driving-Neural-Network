import library.core.*;

public class SceneButton extends Button {

    String targetScene;

    public SceneButton(Simulation sim, PVector pos, PVector dim, String targetScene) {
        super(pos, dim, targetScene);
        this.targetScene = targetScene;
    }

    public void draw() {
        super.draw();
    }

    public void clicked() {
        Scene.loadScene(targetScene);
    }

}
