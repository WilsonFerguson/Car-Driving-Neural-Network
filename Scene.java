import java.util.ArrayList;
import library.core.*;

public class Scene extends PComponent {

    public static ArrayList<Scene> scenes = new ArrayList<Scene>();
    public static ArrayList<String> sceneNames = new ArrayList<String>();
    private static int currentScene = 0;

    Simulation sim;

    public String name;

    public boolean active = false;

    public static long lastSwitch = 0;

    public Scene(Simulation sim, String name) {
        this.sim = sim;
        this.name = name;
        scenes.add(this);
        sceneNames.add(name);
    }

    public void draw() {
    }

    public void setup() {

    }

    public static void loadScene(String name) {
        if (System.currentTimeMillis() - lastSwitch < 300)
            return;
        for (int i = 0; i < scenes.size(); i++) {
            if (!scenes.get(i).name.equals(name)) {
                scenes.get(i).active = false;
                continue;
            }

            scenes.get(i).active = true;
            scenes.get(i).setup();
            currentScene = i;
        }

        lastSwitch = System.currentTimeMillis();
    }

    public static Scene getScene(String name) {
        for (Scene scene : scenes) {
            if (scene.name.equals(name))
                return scene;
        }

        return null;
    }

    public static Scene getCurrentScene() {
        return scenes.get(currentScene);
    }

}
