import static jouvieje.bass.Bass.*;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import jouvieje.bass.Bass;
import jouvieje.bass.BassInit;
import jouvieje.bass.defines.BASS_ACTIVE;
import jouvieje.bass.defines.BASS_ATTRIB;
import jouvieje.bass.defines.BASS_DATA;
import jouvieje.bass.defines.BASS_POS;
import jouvieje.bass.structures.HSTREAM;
import jouvieje.bass.utils.BufferUtils;
import jouvieje.bass.utils.ObjectPointer;
import jouvieje.bass.utils.Pointer;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.*;
import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class MusicPlayer {

    HSTREAM stream;
    ObjectPointer p;

    public MusicPlayer() {
        BassInit.loadLibraries();
        Bass.BASS_Init(-1, 44100, 0, null, null);

        String path = JOptionPane.showInputDialog("File path");

        stream = BASS_StreamCreateFile(false, path, 0, 0, 0);

        System.out.println(Bass.BASS_ErrorGetCode());

        setVolume(0.05f);
    }

    public void play() {
        Bass.BASS_ChannelPlay(stream.asInt(), false);
    }

    public void pause() {
        Bass.BASS_ChannelPause(stream.asInt());
    }

    public void stop() {
        Bass.BASS_Free();
    }

    public float getVolume() {
        FloatBuffer b = BufferUtils.newFloatBuffer(1);
        Bass.BASS_ChannelGetAttribute(stream.asInt(), BASS_ATTRIB.BASS_ATTRIB_VOL, b);
        return b.get(0);
    }

    public void setVolume(float x) {
        Bass.BASS_ChannelSetAttribute(stream.asInt(), BASS_ATTRIB.BASS_ATTRIB_VOL, x);
    }

    public synchronized double getPosition() {
        long pos = Bass.BASS_ChannelGetPosition(stream.asInt(), BASS_POS.BASS_POS_BYTE);
        return Bass.BASS_ChannelBytes2Seconds(stream.asInt(), pos);
    }

    public synchronized void setPosition(double x) {
        Bass.BASS_ChannelSetPosition(stream.asInt(), Bass.BASS_ChannelSeconds2Bytes(stream.asInt(), x), BASS_POS.BASS_POS_BYTE);
    }

    public double getLength() {
        long pos = Bass.BASS_ChannelGetLength(stream.asInt(), BASS_POS.BASS_POS_BYTE);
        return Bass.BASS_ChannelBytes2Seconds(stream.asInt(), pos);
    }

    public boolean isPlaying() {
        int r = Bass.BASS_ChannelIsActive(stream.asInt());

        if(!(r == BASS_ACTIVE.BASS_ACTIVE_PLAYING))
            return false;
        else
            return true;
    }


}
