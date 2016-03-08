package gui;

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
		prev = new Button("<<");
		prev.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
		next = new Button(">>");
		next.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
		play = new Button(">");
		play.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
		mute = new Button("M");
		mute.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
		repeat = new Button("R");
		repeat.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
		shuffle = new Button("S");
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
