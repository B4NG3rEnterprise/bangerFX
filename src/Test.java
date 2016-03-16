import audio.MusicPlayer;
import gui.MainView;

public class Test {

    private static MusicPlayer p;

    public static void main(String... args) {
        p = new MusicPlayer();
    	MainView.launch(MainView.class, args);
		
		
        //PlayerInterface.launch(PlayerInterface.class, args);
    }

    public static MusicPlayer getMusicPlayer() {
        return p;
    }
}
