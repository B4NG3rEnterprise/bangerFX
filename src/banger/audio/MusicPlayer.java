package banger.audio;

import banger.audio.data.Song;
import banger.audio.listeners.PlayPauseListener;
import banger.audio.listeners.QueueListener;
import banger.gui.MainView;
import banger.gui.options.Options;
import banger.util.DeviceItem;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MusicPlayer {

    private Bass bass;
    private int bassflacHandle;
    private int bassaacHandle;

    private int stream;
    private float volume;
    private boolean muted;
    private boolean shuffle;
    private BooleanProperty eqState;
    private Song nowPlaying;
    private RepeatState repeatState;
    private ObservableList<Song> queue;
    private ObservableList<Song> queueBackup;
    private int queueIndex;
    private Task fader;

    private int error;

    //Listeners
    private ArrayList<QueueListener> queueListeners;
    private ArrayList<PlayPauseListener> playPauseListeners;

    //Equalizer
    int[] fxEQ = new int[10];
    float[] freq = {80, 150, 300, 500, 1000, 2000, 4000, 8000, 12000, 14000};
    FloatProperty[] gain = new FloatProperty[10];

    public enum RepeatState {
        REPEAT_OFF, REPEAT_SINGLE, REPEAT_ALL
    }

    private MainView mainview;

    public MusicPlayer(MainView m) {
        bass = (Bass) Native.loadLibrary("bass.dll", Bass.class);

        bassflacHandle = bass.BASS_PluginLoad("res/bassflac.dll", 0);
        if ((error = bass.BASS_ErrorGetCode()) != Bass.BASS_OK) System.out.println(error);
        bassaacHandle = bass.BASS_PluginLoad("res/bass_aac.dll", 0);
        if ((error = bass.BASS_ErrorGetCode()) != Bass.BASS_OK) System.out.println(error);

        bass.BASS_Init(-1, 44100, 0, null, null);

        volume = 0.05f; // 0.5?
        muted = false;
        shuffle = false;
        repeatState = RepeatState.REPEAT_OFF;
        queue = FXCollections.observableArrayList();
        queueIndex = 0;

        //EQ
        eqState = new SimpleBooleanProperty(false);
        eqState.addListener((observable, oldValue, newValue) -> {
            if(newValue)
                initEQ();
            else
                clearEQ();
        });
        for (int i = 0; i < gain.length; i++) {
            gain[i] = new SimpleFloatProperty(0.0f);
            gain[i].addListener((observable, oldValue, newValue) -> updateEQ());
        }

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
        if ((error = bass.BASS_ErrorGetCode()) != Bass.BASS_OK) System.out.println("ReplayError: " + error);

        if (muted)
            bass.BASS_ChannelSetAttribute(stream, Bass.BASS_ATTRIB_VOL, 0);
        else
            setVolume(volume);

        if (eqState.getValue())
            initEQ();

        queueIndex = queue.indexOf(nowPlaying);

        play();
    }

    public void crossfade() {
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

        final int old = stream;
        nowPlaying = next;

        String path = next.getFileLocation();
        Pointer p = new Memory(Native.WCHAR_SIZE * (path.length() + 1));
        p.setWideString(0, path);

        stream = bass.BASS_StreamCreateFile(false, p, 0, 0, Bass.BASS_UNICODE);
        if ((error = bass.BASS_ErrorGetCode()) != Bass.BASS_OK) System.out.println(error);

        if (muted)
            bass.BASS_ChannelSetAttribute(stream, Bass.BASS_ATTRIB_VOL, 0);
        else
            setVolume(volume);


        queueIndex = queue.indexOf(nowPlaying);

        //Set start volume to 0
        bass.BASS_ChannelSetAttribute(stream, Bass.BASS_ATTRIB_VOL, 0);
        play();

        fader = new Task<Void>() {
            @Override
            public Void call() {
                int delay = 100;
                float stepCount = Options.crossfade * 1000 / delay;
                float stepSize = volume / stepCount;
                float curVol = 0;
                for (int i = 0; i < stepCount; i++) {
                    long now = System.currentTimeMillis();
                    while (System.currentTimeMillis() - now < 100) Thread.yield();

                    //Linearer Crossfade
                    curVol += stepSize;
                    //System.out.println(curVol);
                    bass.BASS_ChannelSetAttribute(old, Bass.BASS_ATTRIB_VOL, volume - curVol);
                    bass.BASS_ChannelSetAttribute(stream, Bass.BASS_ATTRIB_VOL,  curVol);
                }
                bass.BASS_ChannelStop(old);
                return null;
            }
        };
        Thread t = new Thread(fader);
        t.setDaemon(true);
        t.start();
    }

    //region Equalizer
    public void initEQ() {
        Bass.BASS_DX8_PARAMEQ eq = new Bass.BASS_DX8_PARAMEQ();

        for (int i = 0; i < fxEQ.length; i++) {
            fxEQ[i] = bass.BASS_ChannelSetFX(stream, Bass.BASSFXType.BASS_FX_DX8_PARAMEQ, 0);
            if ((error = bass.BASS_ErrorGetCode()) != Bass.BASS_OK)
                System.err.println("ERROR: " + error + " at 0." + i);
        }

        for (int i = 0; i < fxEQ.length; i++) {
            eq.fBandwidth = 12;
            eq.fCenter = freq[i];
            eq.fGain = gain[i].getValue();
            eq.write();

            bass.BASS_FXSetParameters(fxEQ[i], eq.getPointer());
            if ((error = bass.BASS_ErrorGetCode()) != Bass.BASS_OK) System.err.println("ERROR: " + error + " at 2." + i);
        }

        eqState.setValue(true);
    }

    public void clearEQ() {
        Bass.BASS_DX8_PARAMEQ eq = new Bass.BASS_DX8_PARAMEQ();
        for (int i = 0; i < fxEQ.length; i++) {
            eq.fBandwidth = 12;
            eq.fCenter = freq[i];
            eq.fGain = 0;
            eq.write();

            bass.BASS_FXSetParameters(fxEQ[i], eq.getPointer());
            if ((error = bass.BASS_ErrorGetCode()) != Bass.BASS_OK) System.err.println("ERROR: " + error + " at " + i);

            bass.BASS_ChannelRemoveFX(fxEQ[i], Bass.BASSFXType.BASS_FX_DX8_PARAMEQ);
        }

        eqState.setValue(false);
    }

    public void updateEQ() {
        if(eqState.getValue()) {
            Bass.BASS_DX8_PARAMEQ eq = new Bass.BASS_DX8_PARAMEQ();

            for (int i = 0; i < fxEQ.length; i++) {
                if (bass.BASS_FXGetParameters(fxEQ[i], eq.getPointer())) {
                    eq.read();
                    eq.fGain = gain[i].getValue();
                    eq.write();

                    bass.BASS_FXSetParameters(fxEQ[i], eq.getPointer());
                    if ((error = bass.BASS_ErrorGetCode()) != Bass.BASS_OK)
                        System.err.println("ERROR: " + error + " at " + i);
                }
            }
        }
    }

    public void updateEQ(int band, float gain) {
        if (eqState.getValue()) {
            Bass.BASS_DX8_PARAMEQ eq = new Bass.BASS_DX8_PARAMEQ();

            if (bass.BASS_FXGetParameters(fxEQ[band], eq.getPointer())) {
                eq.read();
                this.gain[band].setValue(gain);
                eq.fGain = gain;
                eq.write();

                bass.BASS_FXSetParameters(fxEQ[band], eq.getPointer());
                if ((error = bass.BASS_ErrorGetCode()) != Bass.BASS_OK)
                    System.err.println("ERROR: " + error + " at " + band);
            }
        }
    }

    public BooleanProperty isEQActive() {
        return eqState;
    }

    public FloatProperty getEQBandGain(int band) {
        return gain[band];
    }

    public float[] getEQFrequencies() {
        return freq;
    }
    //endregion

    public void setRepeatState(RepeatState repeatState) {
        this.repeatState = repeatState;
    }

    public RepeatState getRepeatState() { return repeatState; }



    public void stop() {
        bass.BASS_ChannelStop(stream);
        bass.BASS_StreamFree(stream);
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
            queueBackup = FXCollections.observableArrayList(q);
            q.remove(nowPlaying);
            Collections.shuffle(q, new Random(System.nanoTime()));
            q.add(0, nowPlaying);
        } else {
            queueBackup = null;
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
        updateQueue(shuffle ? queue : queueBackup);
    }

    public boolean isShuffling() { return shuffle; }

    public void mute() {
        float v = getVolume();
        setVolume(0);
        volume = v;
        muted = true;
    }

    public void unmute() {
        muted = false;
        setVolume(volume);
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
        bass.BASS_PluginFree(bassflacHandle);
        bass.BASS_PluginFree(bassaacHandle);
        //bass.BASS_PluginFree(bassHandle);

        for (DeviceItem d : getDevices()) {
            setOutputDevice(d.getDeviceInt());
            bass.BASS_Free();
        }
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