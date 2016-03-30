package banger.gui.statusbar;

import banger.audio.MusicPlayer;
import banger.gui.MainView;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;


public class StatusBar extends HBox {
	private MainView mainview;
	
	private Button prev;
	private Button next;
	private ToggleButton play;
	private ToggleButton mute;
	private ToggleButton shuffle;
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

	private boolean isShuffle;

	private Thread time;
	
	public StatusBar(MainView m) {
		super(15);
        mainview = m;

		this.getStyleClass().add("statusbar");
		getStylesheets().add("banger/gui/statusbar/statusbar.css");
        this.setAlignment(Pos.CENTER);
		init();
	}

    public void setCustomBackground(Paint p) {
        setBackground(new Background(new BackgroundFill(p, null, null)));
    }
	
	private void init() {
		prev = new Button();
		GlyphsDude.setIcon(prev, MaterialDesignIcon.SKIP_PREVIOUS, size);
		prev.getStyleClass().add("statusbar_icon");
		prev.setId("prev_button");
		prev.setOnMouseEntered(event -> {
			prev.setEffect(new Glow(1));
		});
		prev.setOnMouseExited(event -> {
			prev.setEffect(null);
		});
		prev.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> handleSkipButtons(e));
		
		next = new Button();
		GlyphsDude.setIcon(next, MaterialDesignIcon.SKIP_NEXT, size);
		next.getStyleClass().add("statusbar_icon");
		next.setId("next_button");
		next.setOnMouseEntered(event -> {
			next.setEffect(new Glow(1));
		});
		next.setOnMouseExited(event -> {
			next.setEffect(null);
		});
		next.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> handleSkipButtons(e));

		play = new ToggleButton();
		GlyphsDude.setIcon(play, MaterialDesignIcon.PLAY, size);
		play.getStyleClass().add("statusbar_icon");
		play.setId("play_button");
        play.setOnMouseEntered(event -> {
            play.setEffect(new Glow(1));
        });
        play.setOnMouseExited(event -> {
            play.setEffect(null);
        });
		play.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> handlePlayButton(e));

		repeat = new Button();
		GlyphsDude.setIcon(repeat, MaterialDesignIcon.REPEAT_OFF, size);
		repeat.getStyleClass().add("statusbar_icon");
		repeat.setId("repeat_button");
		repeat.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> handleRepeatButton(e));
		
		shuffle = new ToggleButton();
		GlyphsDude.setIcon(shuffle, MaterialDesignIcon.SHUFFLE, size);
		shuffle.getStyleClass().add("statusbar_icon");
		shuffle.setId("shuffle_button");
		shuffle.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> handleShuffleButton(e));

		//region Volume Slider
		
		volumePosition = new Slider(0, 100, mainview.getMusicPlayer().getVolume()*100);
		volumePosition.addEventHandler(MouseEvent.ANY, e -> handleVolumeSlider(e));
		volumePosition.addEventHandler(ScrollEvent.SCROLL, e -> handleVolumeSlider(e));
		volumePosition.getStyleClass().add("statusbar_slider");
		volumePosition.getStyleClass().add("progress_slider");
		volumePosition.setId("volumePosition_slider");
        volumePosition.setMaxWidth(90);

		volumeIndicator = new ProgressBar();
		volumePosition.valueProperty().addListener((ov, old_val, new_val) -> {
			volumeIndicator.setProgress(new_val.doubleValue()/100);
		});
		volumeIndicator.getStyleClass().add("progress_indicator");
		volumeIndicator.setId("volumeIndicator_bar");
		volumeIndicator.setProgress(volumePosition.getValue()/100);

		volume = new StackPane(volumeIndicator, volumePosition);

		volumeIndicator.minWidthProperty().bind(volume.widthProperty());
		volumeIndicator.maxWidthProperty().bind(volume.widthProperty());

        mute = new ToggleButton();
        updateVolumeIcon(false);
		mute.setId("mute_button");
        mute.getStyleClass().add("statusbar_icon");
        mute.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> handleMuteButton(e));

		//endregion

		//region ProgressBar
        songPosition = new Slider(0,100,0);
        HBox.setHgrow(songPosition, Priority.ALWAYS);
        songPosition.addEventHandler(MouseEvent.ANY, e -> handleProgressSlider(e));
        songPosition.getStyleClass().add("statusbar_slider");
        songPosition.getStyleClass().add("progress_slider");
		songPosition.setId("songPosition_slider");
        songPosition.setMinWidth(400);

        songIndicator = new ProgressBar();
        songPosition.valueProperty().addListener((ov, old_val, new_val) -> {
            songIndicator.setProgress(new_val.doubleValue()/songPosition.getMax());
        });
        songIndicator.getStyleClass().add("progress_indicator");
		songIndicator.setId("songIndicator_bar");
        songIndicator.setProgress(0);

        progress = new StackPane(songIndicator, songPosition);

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
					while (System.currentTimeMillis() - now < 333) {
						Thread.yield();
					}
					double pos = mainview.getMusicPlayer().getPosition();
					Platform.runLater(() -> {
						currentPos.setText(asMinutes(pos));
						songPosition.setValue(pos);
					});
					if (pos >= mainview.getMusicPlayer().getLength()) {
						now = System.currentTimeMillis();
						while (System.currentTimeMillis() - now < 1000) {
							Thread.yield();
						}
						Platform.runLater(() -> mainview.getMusicPlayer().skipForward());
					}
				}
			}
		});
		time.start();

		getChildren().addAll(prev, play, next, mute, volume, currentPos, progress, songLength, shuffle, repeat);
	}

	private void handleSkipButtons(MouseEvent event) {
		if (event.getSource().equals(next)) {
			mainview.getMusicPlayer().skipForward();
		} else if (event.getSource().equals(prev)) {
			if (songPosition.getValue() < 20)
				mainview.getMusicPlayer().skipBackward();
			else {
				songPosition.setValue(0);
				mainview.getMusicPlayer().setPosition(0);
				updateCurrentPosLabel();
			}
		}
	}

	private void handlePlayButton(MouseEvent event) {
		if (play.isSelected())
			mainview.getMusicPlayer().pause();
		else
			mainview.getMusicPlayer().play();
	}

	private void handleMuteButton(MouseEvent event) {
		if(mainview.getMusicPlayer().isMuted()) {
			mainview.getMusicPlayer().unmute();
			updateVolumeIcon(false);
		} else {
			mainview.getMusicPlayer().mute();
			updateVolumeIcon(true);
		}
	}

	private void handleVolumeSlider(Event event) {
		EventType type = event.getEventType();
		if (type == MouseEvent.MOUSE_DRAGGED || type == MouseEvent.MOUSE_PRESSED) {
			if(mainview.getMusicPlayer().isMuted())
				mute.setSelected(false);
			mainview.getMusicPlayer().setVolume((float) (volumePosition.getValue()/100));
			updateVolumeIcon(false);
		}
		else if (type == ScrollEvent.SCROLL) {
			double d = ((ScrollEvent) event).getDeltaY();
			if(d > 0)
				volumePosition.setValue(volumePosition.getValue() + 2);
			else
				volumePosition.setValue(volumePosition.getValue() - 2);
			mainview.getMusicPlayer().setVolume((float) volumePosition.getValue()/100);
			if(mainview.getMusicPlayer().isMuted())
				mute.setSelected(false);
			updateVolumeIcon(false);
		}
	}

	private void handleProgressSlider(MouseEvent event) {
		if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
			mainview.getMusicPlayer().pause();
			mainview.getMusicPlayer().setPosition(songPosition.getValue());
		} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
			mainview.getMusicPlayer().setPosition(songPosition.getValue());
			if (!play.isSelected())
				mainview.getMusicPlayer().play();
		}
		updateCurrentPosLabel();
	}

	private void handleShuffleButton(MouseEvent event) {
		if (shuffle.isSelected()) {
			mainview.getMusicPlayer().setShuffle(true);
			shuffle.setEffect(new Glow(2));
		} else {
			mainview.getMusicPlayer().setShuffle(false);
			shuffle.setEffect(null);
		}
		mainview.getLibrary().updateQueue(mainview.getLibrary().getSelectedItem());
	}

	private void handleRepeatButton(MouseEvent event) {
		switch (mainview.getMusicPlayer().getRepeatState()) {
			case REPEAT_OFF:
				mainview.getMusicPlayer().setRepeatState(MusicPlayer.RepeatState.REPEAT_SINGLE);
				GlyphsDude.setIcon(repeat, MaterialDesignIcon.REPEAT_ONCE, size);
				break;
			case REPEAT_SINGLE:
				mainview.getMusicPlayer().setRepeatState(MusicPlayer.RepeatState.REPEAT_ALL);
				GlyphsDude.setIcon(repeat, MaterialDesignIcon.REPEAT, size);
				break;
			case REPEAT_ALL:
				mainview.getMusicPlayer().setRepeatState(MusicPlayer.RepeatState.REPEAT_OFF);
				GlyphsDude.setIcon(repeat, MaterialDesignIcon.REPEAT_OFF, size);
				break;
		}
	}

	public void play() {
		double len = mainview.getMusicPlayer().getLength();
		songPosition.setMax(len);
		updateCurrentPosLabel();
		songLength.setText(asMinutes(len));
		play.setSelected(true);
		updatePlayIcon();
	}

	public void pause() {
		play.setSelected(false);
		updatePlayIcon();
	}

	private void updateCurrentPosLabel() {
		currentPos.setText(asMinutes(songPosition.getValue()));
	}

	private void updatePlayIcon() {
		if (play.isSelected())
			GlyphsDude.setIcon(play, MaterialDesignIcon.PAUSE, size);
		else
			GlyphsDude.setIcon(play, MaterialDesignIcon.PLAY, size);
	}

	private void updateVolumeIcon(boolean muted) {
		if (muted) {
			GlyphsDude.setIcon(mute, MaterialDesignIcon.VOLUME_OFF, size);
		} else {
			MaterialDesignIcon i;
			double volume = volumePosition.getValue();
			if (volume > 60)
				i = MaterialDesignIcon.VOLUME_HIGH;
			else if (volume > 0)
				i = MaterialDesignIcon.VOLUME_MEDIUM;
			else
				i = MaterialDesignIcon.VOLUME_LOW;
			GlyphsDude.setIcon(mute, i, size);
		}
	}

	private String asMinutes(double val) {
		int min = 0;
		int sec = 0;

		min = (int) val/60;
		sec = (int) (val) % 60;

		return String.format("%d:%02d", min, sec);
	}
}
