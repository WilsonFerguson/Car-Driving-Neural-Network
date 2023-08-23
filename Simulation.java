import library.core.*;

public class Simulation extends Applet {

    MainScene mainScene;
    InformationScene informationScene;
    TrackEditorScene trackEditorScene;

    boolean switchedToTrackEditor = false;

    SoundPlayer soundPlayer;

    public void setup() {
        fullScreen();
        // size(1920, 1080);

        mainScene = new MainScene(this, "Main");
        informationScene = new InformationScene(this, "Info");
        trackEditorScene = new TrackEditorScene(this, "Track");

        Scene.loadScene("Main");

        // Song1 or Song2
        soundPlayer = new SoundPlayer("Music/Song1.wav");
        soundPlayer.playLoop();

        rectMode(CENTER);
    }

    public void draw() {
        mainScene.draw();
        informationScene.draw();
        trackEditorScene.draw();

        if (switchedToTrackEditor) {
            trackEditorScene.Reset();
            switchedToTrackEditor = false;
        }

        // displayFrameRate();
    }
}
