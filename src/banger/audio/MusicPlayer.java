package banger.audio;

import banger.gui.MainView;
import banger.util.DeviceItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jouvieje.bass.Bass;
import jouvieje.bass.BassInit;
import jouvieje.bass.defines.BASS_ACTIVE;
import jouvieje.bass.defines.BASS_ATTRIB;
import jouvieje.bass.defines.BASS_POS;
import jouvieje.bass.structures.BASS_DEVICEINFO;
import jouvieje.bass.structures.HSTREAM;
import jouvieje.bass.utils.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static jouvieje.bass.Bass.*;

public class MusicPlayer {

    HSTREAM stream;
    float volume;
    boolean muted;

    private MainView mainview;

    public MusicPlayer(MainView m) {
        BassInit.loadLibraries();
        Bass.BASS_Init(-1, 44100, 0, null, null);

        mainview = m;
    }

    public void play() {
        Bass.BASS_ChannelPlay(stream.asInt(), false);
    }

    public void play(String path) {
        if (isPlaying()) stop();

        stream = BASS_StreamCreateFile(false, path, 0, 0, 0);
        System.out.println(Bass.BASS_ErrorGetCode());

        setVolume(0.05f); // remove later

        play();
    }

    public void stop() {
        Bass.BASS_ChannelStop(stream.asInt());
    }

    public void pause() {
        Bass.BASS_ChannelPause(stream.asInt());
    }

    public void kill() {
        Bass.BASS_Free();
    }

    public float getVolume() {
        FloatBuffer b = BufferUtils.newFloatBuffer(1);
        if (stream != null) Bass.BASS_ChannelGetAttribute(stream.asInt(), BASS_ATTRIB.BASS_ATTRIB_VOL, b);
        else b.put(0.05f);
        return b.get(0);
    }

    public void setVolume(float x) {
        muted = false;
        Bass.BASS_ChannelSetAttribute(stream.asInt(), BASS_ATTRIB.BASS_ATTRIB_VOL, x);
    }

    public void mute() {
        volume = getVolume();
        setVolume(0);
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
        long pos = Bass.BASS_ChannelGetPosition(stream.asInt(), BASS_POS.BASS_POS_BYTE);
        return Bass.BASS_ChannelBytes2Seconds(stream.asInt(), pos);
    }

    public synchronized void setPosition(double x) {
        Bass.BASS_ChannelSetPosition(stream.asInt(), Bass.BASS_ChannelSeconds2Bytes(stream.asInt(), x), BASS_POS.BASS_POS_BYTE);
    }

    public synchronized double getLength() {
        long pos = Bass.BASS_ChannelGetLength(stream.asInt(), BASS_POS.BASS_POS_BYTE);
        return Bass.BASS_ChannelBytes2Seconds(stream.asInt(), pos);
    }

    public boolean isPlaying() {
        int r = 0;
        try {
            r = Bass.BASS_ChannelIsActive(stream.asInt());
        } catch (NullPointerException e) {
            // e.printStackTrace();
        }
        return r == BASS_ACTIVE.BASS_ACTIVE_PLAYING;
    }

    public ObservableList<DeviceItem> getDevices() {
        List<DeviceItem> list = new ArrayList<>();
        ObservableList<DeviceItem> devices = FXCollections.observableList(list);
        BASS_DEVICEINFO info = BASS_DEVICEINFO.allocate();
        for(int c = 1; BASS_GetDeviceInfo(c, info); c++) {
            String name = info.getName();
            devices.add(new DeviceItem(name, c));
        }
        info.release();

        return devices;
    }

    public void setOutputDevice(int device){
        BASS_Init(device, 44100, 0, null, null);
        BASS_ChannelSetDevice(stream.asInt(), device);
        System.out.println(device);
    }
}
