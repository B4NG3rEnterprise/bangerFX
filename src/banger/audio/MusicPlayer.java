package banger.audio;

import banger.audio.data.Song;
import banger.audio.listeners.PlayPauseListener;
import banger.audio.listeners.QueueListener;
import banger.gui.MainView;
import banger.gui.library.Library;
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
    private Bass.BASSFlac bassflac;

    private int stream;
    private float volume;
    private boolean muted;
    private boolean shuffle;
    private boolean played;
    private Song nowPlaying;
    private RepeatState repeatState;
    private ObservableList<Song> queue;
    private int queueIndex;

    //Listeners
    private ArrayList<QueueListener> queueListeners;
    private ArrayList<PlayPauseListener> playPauseListeners;


    public enum RepeatState {
        REPEAT_OFF, REPEAT_SINGLE, REPEAT_ALL
    }

    private MainView mainview;

    public MusicPlayer(MainView m) {
        bass = (Bass) Native.loadLibrary("bass.dll", Bass.class);
        bass.BASS_PluginLoad("res/bassflac.dll", 0);
        bass.BASS_PluginLoad("res/bass_aac.dll", 0);

        bass.BASS_Init(-1, 44100, 0, null, null);

        volume = 0.05f; // 0.5?
        muted = false;
        shuffle = false;
        repeatState = RepeatState.REPEAT_OFF;
        queue = FXCollections.observableArrayList();
        queueIndex = 0;

        queueListeners = new ArrayList<QueueListener>();
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
        int error;
        if ((error = bass.BASS_ErrorGetCode()) != Bass.BASS_OK) System.out.println(error);

        if (mainview.getLibrary().getCurrentView() == Library.VIEW_LYRICS)
            mainview.getLibrary().refreshData();

        if(muted) {
            unmute();
            mute();
        } else
            setVolume(volume);

        queueIndex = queue.indexOf(nowPlaying);

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
        int i = queue.indexOf(getNowPlaying());
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
        fireQueueListeners(queue, queue.indexOf(next));
        play(next);
    }

    public void skipBackward() {
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).equals(getNowPlaying())) {
                if (i > 0) {
                    Song next = queue.get(i-1);
                    fireQueueListeners(queue, queue.indexOf(next));
                    play(next);
                } else {
                    setPosition(0);
                }
                break;
            }
        }
    }

    public void updateQueue(ObservableList<Song> q) {
        if (isShuffling()) {
            q.remove(nowPlaying);
            Collections.shuffle(q, new Random(System.nanoTime()));
            q.add(0, nowPlaying);
        }
        queueIndex = q.indexOf(nowPlaying);
        queue = q;
        fireQueueListeners(queue, queueIndex);
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

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

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

    public void fft() {
        Pointer p = new Memory(128 * Float.BYTES);
        bass.BASS_ChannelGetData(stream, p, Bass.BASS_DATA_FFT256);
        System.out.println(bass.BASS_ErrorGetCode());
        for (int i = 0; i < 128; i++)
            System.out.println(p.getFloat(i * Float.BYTES));
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

    public void addPlayPauseListener(PlayPauseListener l) {
        playPauseListeners.add(l);
    }

    
    private void fireQueueListeners(ObservableList<Song> q, int queueIndex) {
        for(QueueListener listener : queueListeners)
            listener.queueUpdated(q, queueIndex);
    }

    private void firePlayPauseListeners(boolean playing, Song now) {
        for(PlayPauseListener listener : playPauseListeners)
            listener.statusChanged(playing, now);
    }
}
