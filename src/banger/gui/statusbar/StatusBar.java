package banger.gui.statusbar;

import banger.gui.MainView;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;


public class StatusBar extends HBox implements EventHandler<Event> {
	private MainView mainview;
	
	private Button prev;
	private Button next;
	private ToggleButton play;
	private ToggleButton mute;
	private Button shuffle;
	private Button repeat;

	private StackPane volume;
	private Slider volumePosition;
	private ProgressBar volumeIndicator;

    private StackPane progress;
	private Slider songPosition;
    private ProgressBar songIndicator;
	
	private Label currentPos;
	private Label songLength;

	private String size = "2em";

	private Thread time;
	
	public StatusBar(MainView m) {
		super(15);
        mainview = m;
		this.getStyleClass().add("statusbar");
        this.setAlignment(Pos.CENTER);
		init();
	}

    public void setCustomBackground(Paint p) {
        setBackground(new Background(new BackgroundFill(p, null, null)));
    }
	
	public void init() {
		prev = new Button();
		GlyphsDude.setIcon(prev, MaterialDesignIcon.SKIP_PREVIOUS, size);
		prev.getStyleClass().add("statusbar_icon");
		prev.setOnMouseEntered(event -> {
			prev.setEffect(new Glow(1));
		});
		prev.setOnMouseExited(event -> {
			prev.setEffect(null);
		});
		prev.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
		
		next = new Button();
		GlyphsDude.setIcon(next, MaterialDesignIcon.SKIP_NEXT, size);
		next.getStyleClass().add("statusbar_icon");
		next.setOnMouseEntered(event -> {
			next.setEffect(new Glow(1));
		});
		next.setOnMouseExited(event -> {
			next.setEffect(null);
		});
		next.addEventHandler(MouseEvent.MOUSE_CLICKED, this);

		play = new ToggleButton();
		GlyphsDude.setIcon(play, MaterialDesignIcon.PLAY, size);
		play.getStyleClass().add("statusbar_icon");
        play.setOnMouseEntered(event -> {
            play.setEffect(new Glow(1));
        });
        play.setOnMouseExited(event -> {
            play.setEffect(null);
        });
		play.addEventHandler(EventType.ROOT, e -> handlePlayBtn(e));


		
		repeat = new Button();
		GlyphsDude.setIcon(repeat, MaterialDesignIcon.REPEAT, size);
		repeat.getStyleClass().add("statusbar_icon");
		repeat.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
		
		shuffle = new Button();
		GlyphsDude.setIcon(shuffle, MaterialDesignIcon.SHUFFLE, size);
		shuffle.getStyleClass().add("statusbar_icon");
		shuffle.addEventHandler(MouseEvent.MOUSE_CLICKED, this);

		//region Volume Slider
		
		volumePosition = new Slider(0, 100, mainview.getMusicPlayer().getVolume()*100);
		volumePosition.addEventHandler(MouseEvent.ANY, this);
		volumePosition.addEventHandler(ScrollEvent.ANY, this);
		volumePosition.getStyleClass().add("statusbar_slider");
		volumePosition.getStyleClass().add("progress_slider");
        volumePosition.setMaxWidth(90);

		volumeIndicator = new ProgressBar();
		volumePosition.valueProperty().addListener((ov, old_val, new_val) -> {
			volumeIndicator.setProgress(new_val.doubleValue()/100);
		});
		volumeIndicator.getStyleClass().add("progress_indicator");
		volumeIndicator.setProgress(volumePosition.getValue()/100);

		volume = new StackPane(volumeIndicator, volumePosition);

		volumeIndicator.minWidthProperty().bind(volume.widthProperty());
		volumeIndicator.maxWidthProperty().bind(volume.widthProperty());

        mute = new ToggleButton();
        GlyphsDude.setIcon(mute, getVolumeIcon(volumePosition.getValue()), size);
        mute.getStyleClass().add("statusbar_icon");
        mute.addEventHandler(EventType.ROOT, e -> handleMuteBtn(e));

		//endregion

		//region ProgressBar
        songPosition = new Slider(0,100,0);
        HBox.setHgrow(songPosition, Priority.ALWAYS);
        songPosition.addEventHandler(MouseEvent.ANY, this);
        songPosition.getStyleClass().add("statusbar_slider");
        songPosition.getStyleClass().add("progress_slider");
        songPosition.setMinWidth(400);

        songIndicator = new ProgressBar();
        songPosition.valueProperty().addListener((ov, old_val, new_val) -> {
            songIndicator.setProgress(new_val.doubleValue()/songPosition.getMax());
        });
        songIndicator.getStyleClass().add("progress_indicator");
        songIndicator.setProgress(0);

        progress = new StackPane(songIndicator, songPosition);
        HBox.setHgrow(progress, Priority.NEVER);

        songIndicator.minWidthProperty().bind(progress.widthProperty());
        songIndicator.maxWidthProperty().bind(progress.widthProperty());
		//endregion

		currentPos = new Label("--:--");
		songLength = new Label("--:--");

		time = new Thread(() -> {
			while(true) {
				if(!mainview.getMusicPlayer().isPlaying()) {
					Thread.yield();
				} else {
					long now = System.currentTimeMillis();
					while (System.currentTimeMillis() - now < 300) {
						Thread.yield();
					}
					double pos = mainview.getMusicPlayer().getPosition();
					Platform.runLater(() -> {
						currentPos.setText(asMinutes(pos));
						songPosition.setValue(pos);
					});
					if (pos == mainview.getMusicPlayer().getLength()) {
						System.out.println("STOP");
					}
				}
			}
		});
		time.start();

		getChildren().addAll(prev, play, next, mute, volume, currentPos, progress, songLength, shuffle, repeat);
	}

    public void handleMouse(MouseEvent event) {
        if (event.getSource().equals(volumePosition) &&
				(event.getEventType().equals(MouseEvent.MOUSE_DRAGGED) || event.getEventType().equals(MouseEvent.MOUSE_CLICKED))) {
            if(mainview.getMusicPlayer().isMuted()) {
                mute.setSelected(false);
            }
			GlyphsDude.setIcon(mute, getVolumeIcon(volumePosition.getValue()), size);
			mainview.getMusicPlayer().setVolume((float) (volumePosition.getValue()/100));
        }
		else if(event.getSource().equals(songPosition)) {
			if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
				mainview.getMusicPlayer().pause();
				mainview.getMusicPlayer().setPosition(songPosition.getValue());
				currentPos.setText(asMinutes(songPosition.getValue()));
			}
			else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
				mainview.getMusicPlayer().setPosition(songPosition.getValue());
				currentPos.setText(asMinutes(songPosition.getValue()));
				if (!play.isSelected())
					mainview.getMusicPlayer().play();
			}
			else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
				currentPos.setText(asMinutes(songPosition.getValue()));
			}
		}
    }

    public void handleScroll(ScrollEvent event) {
		if(event.getSource().equals(volumePosition)) {
			double d = event.getDeltaY();
			if(d > 0) {
				volumePosition.setValue(volumePosition.getValue() + 2);
				mainview.getMusicPlayer().setVolume((float) volumePosition.getValue()/100);
			} else {
				volumePosition.setValue(volumePosition.getValue() - 2);
				mainview.getMusicPlayer().setVolume((float) volumePosition.getValue()/100);
			}
			if(mainview.getMusicPlayer().isMuted()) {
				GlyphsDude.setIcon(mute, MaterialDesignIcon.VOLUME_HIGH, size);
				mute.setSelected(false);
			}
			GlyphsDude.setIcon(mute, getVolumeIcon(volumePosition.getValue()), size);
		}
    }

	public void handlePlayBtn(Event e) {
		EventType type = e.getEventType();
		if (type.equals(MouseEvent.MOUSE_PRESSED)) {
			if (mainview.getMusicPlayer().isPlaying()) {
				mainview.pause();
			} else {
				mainview.play();
			}
		} else if(type.equals(ActionEvent.ACTION)) {
			if(!play.isSelected()) {
				GlyphsDude.setIcon(play, MaterialDesignIcon.PAUSE, size);
			} else {
				GlyphsDude.setIcon(play, MaterialDesignIcon.PLAY, size);
			}
		}


	}

	public void handleMuteBtn(Event e){
		EventType type = e.getEventType();
		if (type.equals(MouseEvent.MOUSE_CLICKED)) {
			if(mainview.getMusicPlayer().isMuted()) {
				mainview.getMusicPlayer().unmute();
				GlyphsDude.setIcon(mute, getVolumeIcon(volumePosition.getValue()), size);
			} else {
				mainview.getMusicPlayer().mute();
				GlyphsDude.setIcon(mute, MaterialDesignIcon.VOLUME_OFF, size);
			}
		}
	}

	@Override
	public void handle(Event event) {
		EventType type = event.getEventType();
		
		if (type.getSuperType().equals(ScrollEvent.ANY)) {
            handleScroll((ScrollEvent) event);
		} else if (type.getSuperType().equals(MouseEvent.ANY)){
            handleMouse((MouseEvent) event);
        }
	}

	public void play() {
		double len = mainview.getMusicPlayer().getLength();
		songPosition.setMax(len);
		currentPos.setText(asMinutes(songPosition.getValue()));
		songLength.setText(asMinutes(len));
		play.setSelected(true);
		play.fire();
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

	private void setCurrentTimeLabel(String l) {
		currentPos.setText(l);
	}

	private String asMinutes(double val) {
		int min = 0;
		int sec = 0;

		min = (int) val/60;
		sec = (int) (val) % 60;

		return String.format("%d:%02d", min, sec);
	}
}
