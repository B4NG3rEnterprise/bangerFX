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
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;


public class StatusBar extends HBox implements EventHandler<Event> {
	
	private MainView mainview;
	
	private Button prev;
	private Button next;
	private Button play;
	private Button mute;
	private Button shuffle;
	private Button repeat;
	
	private Slider volume;
	private Slider songPosition;
	
	private Label currentPos;
	private Label songLength;
	
	
	public StatusBar() {
		super(15);
		
		
		init();
	}
	
	public void init() {
		
		prev = new Button();
		GlyphsDude.setIcon(prev, MaterialDesignIcon.SKIP_PREVIOUS, "3em");
		prev.getStyleClass().add("statusbar_icon");
		prev.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
		
		next = new Button();
		GlyphsDude.setIcon(next, MaterialDesignIcon.SKIP_NEXT, "3em");
		next.getStyleClass().add("statusbar_icon");
		next.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
		
		play = new Button();
		GlyphsDude.setIcon(play, MaterialDesignIcon.PLAY, "3em");
		play.getStyleClass().add("statusbar_icon");
		play.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
		
		mute = new Button();
		GlyphsDude.setIcon(mute, MaterialDesignIcon.VOLUME_HIGH, "3em");
		mute.getStyleClass().add("statusbar_icon");
		mute.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
		
		repeat = new Button();
		GlyphsDude.setIcon(repeat, MaterialDesignIcon.REPEAT, "3em");
		repeat.getStyleClass().add("statusbar_icon");
		repeat.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
		
		shuffle = new Button();
		GlyphsDude.setIcon(shuffle, MaterialDesignIcon.SHUFFLE, "3em");
		shuffle.getStyleClass().add("statusbar_icon");
		shuffle.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
		
		
		volume = new Slider(0, 1, 0);
		volume.addEventHandler(MouseEvent.ANY, this);
		volume.addEventHandler(ScrollEvent.ANY, this);
		songPosition = new Slider();
		volume.addEventHandler(MouseEvent.ANY, this);
		
		currentPos = new Label("00:00");
		songLength = new Label("99:99");
		
		getChildren().addAll(prev, play, next, mute, volume, currentPos, songPosition, songLength, shuffle, repeat);
	}

	public void handle(MouseEvent event) {
		
		System.out.println("OK");
	}
	
	@Override
	public void handle(Event event) {
		EventType type = event.getEventType();
		
		if(type.equals(MouseEvent.MOUSE_CLICKED)) {
			handle((MouseEvent) event);
		} else {
		
		System.out.println(type);
		System.out.println(type.getClass());
		System.out.println(type.getName());
		}
	}
	
	

}
