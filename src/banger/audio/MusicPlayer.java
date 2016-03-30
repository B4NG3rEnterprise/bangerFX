package banger.audio;

import banger.audio.data.Song;
import banger.audio.listeners.PlayPauseListener;
import banger.audio.listeners.QueueListener;
import banger.audio.listeners.SkipListener;
import banger.gui.MainView;
import banger.util.DeviceItem;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MusicPlayer {

    private Bass bass;
    private int stream;
    private float volume;
    private boolean muted;
    private boolean shuffle;
    private boolean played;
    private Song nowPlaying;
    private RepeatState repeatState;
    private ArrayList<Song> queue;

    //Listeners
    private ArrayList<QueueListener> queueListeners;
    private ArrayList<SkipListener> skipListeners;
    private ArrayList<PlayPauseListener> playPauseListeners;


    public enum RepeatState {
        REPEAT_OFF, REPEAT_SINGLE, REPEAT_ALL
    }

    private MainView mainview;

    public MusicPlayer(MainView m) {
        bass = (Bass) Native.loadLibrary("bass.dll", Bass.class);
        bass.BASS_Init(-1, 44100, 0, null, null);

        volume = 0.05f; // 0.5?
        muted = false;
        shuffle = false;
        repeatState = RepeatState.REPEAT_OFF;
        queue = new ArrayList<Song>();

        queueListeners = new ArrayList<QueueListener>();
        skipListeners = new ArrayList<SkipListener>();
        playPauseListeners = new ArrayList<PlayPauseListener>();

        mainview = m;
    }

    public void play() {
        bass.BASS_ChannelPlay(stream, false);
        firePlayPauseListeners(true, nowPlaying);
    }

    public void play(Song s) {
        if (isPlaying()) stop();
        nowPlaying = s;

        String path = s.getFileLocation();
        Pointer p = new Memory(Native.WCHAR_SIZE * (path.length() + 1));
        p.setWideString(0, path);

        stream = bass.BASS_StreamCreateFile(false, p, 0, 0, Bass.BASS_UNICODE);
        System.out.println(bass.BASS_ErrorGetCode());

        if(muted) {
            unmute();
            mute();
        } else
            setVolume(volume);

        play();
    }

    public void setRepeatState(RepeatState repeatState) {
        this.repeatState = repeatState;
    }

    public RepeatState getRepeatState() { return repeatState; }

    public void stop() {
        bass.BASS_ChannelStop(stream);
        firePlayPauseListeners(false, null);
    }

    public void pause() {
        bass.BASS_ChannelPause(stream);
        firePlayPauseListeners(false, nowPlaying);
    }

    public void skipForward() {
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).equals(getNowPlaying())) {
                Song next = null;
                switch (repeatState) {
                    case REPEAT_SINGLE:
                        next = queue.get(i);
                        break;
                    case REPEAT_ALL:
                        if (!(i+1 < queue.size())) {
                            next = queue.get(0);
                            break;
                        }
                    case REPEAT_OFF:
                        if (i+1 < queue.size() && queue.get(i+1) != null) {
                            next = queue.get(i + 1);
                        }
                        break;
                }
                fireSkipListeners(SkipListener.Skip.FORWARD, next);
                play(next);
                break;
            }
        }
    }

    public void skipBackward() {
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).equals(getNowPlaying())) {
                if (i > 0) {
                    Song next = queue.get(i-1);
                    fireSkipListeners(SkipListener.Skip.BACKWARD, next);
                    play(next);
                } else {
                    setPosition(0);
                }
                break;
            }
        }
    }

    public void updateQueue(ArrayList<Song> q) {
        if (shuffle) Collections.shuffle(q, new Random(System.nanoTime()));
        queue = q;
        fireQueueListeners(queue);
    }

    public float getVolume() {
        Pointer b = new Memory(Native.getNativeSize(Float.TYPE));
        if (stream != 0) bass.BASS_ChannelGetAttribute(stream, Bass.BASS_ATTRIB_VOL, b);
        else b.setFloat(0, volume);
        return b.getFloat(0);
    }

    public void setVolume(float x) {
        muted = false;
        volume = x;
        bass.BASS_ChannelSetAttribute(stream, Bass.BASS_ATTRIB_VOL, x);
    }

    public void setShuffle(boolean shuffle) { this.shuffle = shuffle; }

    public boolean isShuffling() { return shuffle; }

    public void mute() {
        float v = getVolume();
        setVolume(0);
        volume = v;
        muted = true;
    }

    public void unmute() {
        if (muted) {
            muted = false;
            setVolume(volume);
        }
    }

    public boolean isMuted() {
        return muted;
    }

    public synchronized double getPosition() {
        long pos = bass.BASS_ChannelGetPosition(stream, Bass.BASS_POS_BYTE);
        return bass.BASS_ChannelBytes2Seconds(stream, pos);
    }

    public synchronized void setPosition(double x) {
        bass.BASS_ChannelSetPosition(stream, bass.BASS_ChannelSeconds2Bytes(stream, x), Bass.BASS_POS_BYTE);
    }

    public synchronized double getLength() {
        long len = bass.BASS_ChannelGetLength(stream, Bass.BASS_POS_BYTE);
        return bass.BASS_ChannelBytes2Seconds(stream, len);
    }

    public boolean isPlaying() {
        int r = 0;
        if (stream != 0) r = bass.BASS_ChannelIsActive(stream);
        return r == Bass.BASS_ACTIVE_PLAYING;
    }

    public Song getNowPlaying() {
        return nowPlaying;
    }

    public ObservableList<DeviceItem> getDevices() {
        List<DeviceItem> list = new ArrayList<>();
        ObservableList<DeviceItem> devices = FXCollections.observableList(list);
        Bass.BASS_DEVICEINFO info = new Bass.BASS_DEVICEINFO();

        for(int c = 1; bass.BASS_GetDeviceInfo(c, info); c++) {
            String name = info.name;
            devices.add(new DeviceItem(name, c));
        }

        return devices;
    }

    public void setOutputDevice(int device){
        bass.BASS_Init(device, 44100, 0, null, null);
        bass.BASS_ChannelSetDevice(stream, device);
        bass.BASS_SetDevice(device);
        System.out.println(device);
    }

    public void kill() {
        bass.BASS_Free();
    }

    //Listeners
    public void addQueueListener(QueueListener l) {
        queueListeners.add(l);
    }

    public void addSkipListener(SkipListener l) {
        skipListeners.add(l);
    }

    public void addPlayPauseListener(PlayPauseListener l) {
        playPauseListeners.add(l);
    }

    
    private void fireQueueListeners(ArrayList<Song> q) {
        for(QueueListener listener : queueListeners)
            listener.queueUpdated(q);
    }

    private void fireSkipListeners(SkipListener.Skip dir, Song next) {
        for(SkipListener listener : skipListeners)
            listener.skipped(dir, next);
    }

    private void firePlayPauseListeners(boolean playing, Song now) {
        for(PlayPauseListener listener : playPauseListeners)
            listener.statusChanged(playing, now);
    }
}
