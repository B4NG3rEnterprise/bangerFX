package gui.statusbar;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.MainView;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;


public class StatusBar extends HBox implements EventHandler<Event> {
	private MainView mainview;
	
	private Button prev;
	private Button next;
	private ToggleButton play;
	private ToggleButton mute;
	private Button shuffle;
	private Button repeat;
	
	private Slider volume;
	private Slider songPosition;
	
	private Label currentPos;
	private Label songLength;

	private String size = "2em";
	
	public StatusBar(MainView m) {
		super(15);
		mainview = m;
		
		init();
	}
	
	public void init() {
		
		prev = new Button();
		GlyphsDude.setIcon(prev, MaterialDesignIcon.SKIP_PREVIOUS, size);
		prev.getStyleClass().add("statusbar_icon");
		prev.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
		
		next = new Button();
		GlyphsDude.setIcon(next, MaterialDesignIcon.SKIP_NEXT, size);
		next.getStyleClass().add("statusbar_icon");
		next.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
		
		play = new ToggleButton();
		GlyphsDude.setIcon(play, MaterialDesignIcon.PLAY, size);
		play.getStyleClass().add("statusbar_icon");
		play.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
		
		repeat = new Button();
		GlyphsDude.setIcon(repeat, MaterialDesignIcon.REPEAT, size);
		repeat.getStyleClass().add("statusbar_icon");
		repeat.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
		
		shuffle = new Button();
		GlyphsDude.setIcon(shuffle, MaterialDesignIcon.SHUFFLE, size);
		shuffle.getStyleClass().add("statusbar_icon");
		shuffle.addEventHandler(MouseEvent.MOUSE_CLICKED, this);

		volume = new Slider(0, 100, mainview.getMusicPlayer().getVolume()*100);
		volume.addEventHandler(MouseEvent.ANY, this);
		volume.addEventHandler(ScrollEvent.ANY, this);

        mute = new ToggleButton();
        GlyphsDude.setIcon(mute, getVolumeIcon(volume.getValue()), size);
        mute.getStyleClass().add("statusbar_icon");
        mute.addEventHandler(MouseEvent.MOUSE_CLICKED, this);

        songPosition = new Slider();
        volume.addEventHandler(MouseEvent.ANY, this);


		currentPos = new Label("00:00");
		songLength = new Label("99:99");
		
		getChildren().addAll(prev, play, next, mute, volume, currentPos, songPosition, songLength, shuffle, repeat);
	}

	public void handleClick(MouseEvent event) {
		if (event.getSource().equals(mute)) {
			if (mute.isSelected()) {
                mainview.getMusicPlayer().mute();
				GlyphsDude.setIcon(mute, MaterialDesignIcon.VOLUME_OFF, size);
			} else {
                mainview.getMusicPlayer().unmute();
				GlyphsDude.setIcon(mute, getVolumeIcon(volume.getValue()), size);
			}
		} else if (event.getSource().equals(play)) {
			if (mainview.getMusicPlayer().isPlaying()) {
				mainview.getMusicPlayer().pause();
				GlyphsDude.setIcon(play, MaterialDesignIcon.PLAY, size);
			} else {
				mainview.getMusicPlayer().play();
				GlyphsDude.setIcon(play, MaterialDesignIcon.PAUSE, size);
			}
		} else if (event.getSource().equals(volume)) {
            mainview.getMusicPlayer().setVolume((float) (volume.getValue()/100));
        }
    }

    public void handleMouse(MouseEvent event) {
        if (event.getSource().equals(volume) && event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
            if(mainview.getMusicPlayer().isMuted()) {
                GlyphsDude.setIcon(mute, MaterialDesignIcon.VOLUME_HIGH, size);
                mute.setSelected(false);
            }
            GlyphsDude.setIcon(mute, getVolumeIcon(volume.getValue()), size);

            mainview.getMusicPlayer().setVolume((float) (volume.getValue()/100));
        }
    }

    public void handleScroll(ScrollEvent event) {
        System.out.println("HIZ");
    }

	@Override
	public void handle(Event event) {
		EventType type = event.getEventType();
		
		if(type.equals(MouseEvent.MOUSE_CLICKED)) {
			handleClick((MouseEvent) event);
		} else if (type.getSuperType().equals(ScrollEvent.ANY)) {
            handleScroll((ScrollEvent) event);
		} else if (type.getSuperType().equals(MouseEvent.ANY)){
            handleMouse((MouseEvent) event);
        }
	}

    private MaterialDesignIcon getVolumeIcon(double volume) {
        MaterialDesignIcon i;
        if (volume > 60)
            i = MaterialDesignIcon.VOLUME_HIGH;
        else if (volume > 0)
            i = MaterialDesignIcon.VOLUME_MEDIUM;
        else
            i = MaterialDesignIcon.VOLUME_LOW;
        return i;
    }
	
	

}
