package banger.audio;

import banger.gui.MainView;
import banger.util.DeviceItem;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayer {

    Bass bass;
    int stream;
    float volume = 0.05f;
    boolean muted;
    Song nowPlaying;

    private boolean played;
    private MainView mainview;

    public MusicPlayer(MainView m) {
        bass = (Bass) Native.loadLibrary("bass.dll", Bass.class);
        bass.BASS_Init(-1, 44100, 0, null, null);

        mainview = m;
    }

    public void play() {
        bass.BASS_ChannelPlay(stream, false);
    }

    public void play(Song s) {
        if (isPlaying()) stop();
        nowPlaying = s;

        String path = s.getFileLocation();

        Pointer p = new Memory(Native.WCHAR_SIZE * (path.length() + 1));
        p.setWideString(0, path);

        stream = bass.BASS_StreamCreateFile(false, p, 0, 0, Bass.BASS_UNICODE);

        System.out.println(bass.BASS_ErrorGetCode());

        setVolume(volume); // remove later

        play();
    }

    public void loop(){
        play(nowPlaying);
    }

    public void loopOnce(){
        if (!played) {
            play(nowPlaying);
            played = true;
        } else {
            mainview.skipForward();
            played = false;
        }
    }

    public void stop() {
        bass.BASS_ChannelStop(stream);
    }

    public void pause() {
        bass.BASS_ChannelPause(stream);
    }

    public void kill() {
        bass.BASS_Free();
    }

    public float getVolume() {
        Pointer b = new Memory(Native.getNativeSize(Float.TYPE));
        if (stream != 0) bass.BASS_ChannelGetAttribute(stream, Bass.BASS_ATTRIB_VOL, b);
        else return volume;
        return b.getFloat(0);
    }

    public void setVolume(float x) {
        muted = false;
        volume = x;
        bass.BASS_ChannelSetAttribute(stream, Bass.BASS_ATTRIB_VOL, x);
    }

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
        long pos = bass.BASS_ChannelGetLength(stream, Bass.BASS_POS_BYTE);
        return bass.BASS_ChannelBytes2Seconds(stream, pos);
    }

    public boolean isPlaying() {
        int r = 0;
        try {
            r = bass.BASS_ChannelIsActive(stream);
        } catch (NullPointerException e) {
            // e.printStackTrace();
        }
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
}
