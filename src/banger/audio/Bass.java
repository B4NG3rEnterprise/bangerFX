package banger.audio;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public interface Bass extends Library {
    int BASS_UNICODE = 0x80000000;

    //region Error codes returned by BASS_ErrorGetCode
    final int BASS_OK               = 0;    // all is OK
    final int BASS_ERROR_MEM        = 1;    // memory error
    final int BASS_ERROR_FILEOPEN   = 2;	// can't open the file
    final int BASS_ERROR_DRIVER     = 3;	// can't find a free/valid driver
    final int BASS_ERROR_BUFLOST    = 4;	// the sample buffer was lost
    final int BASS_ERROR_HANDLE     = 5;	// invalid handle
    final int BASS_ERROR_FORMAT     = 6;	// unsupported sample format
    final int BASS_ERROR_POSITION   = 7;	// invalid position
    final int BASS_ERROR_INIT       = 8;    // BASS_Init has not been successfully called
    final int BASS_ERROR_START      = 9;    // BASS_Start has not been successfully called
    final int BASS_ERROR_SSL        = 10;   // SSL/HTTPS support isn't available
    final int BASS_ERROR_ALREADY    = 14;	// already initialized/paused/whatever
    final int BASS_ERROR_NOCHAN     = 18;	// can't get a free channel
    final int BASS_ERROR_ILLTYPE    = 19;	// an illegal type was specified
    final int BASS_ERROR_ILLPARAM   = 20;	// an illegal parameter was specified
    final int BASS_ERROR_NO3D       = 21;   // no 3D support
    final int BASS_ERROR_NOEAX      = 22;	// no EAX support
    final int BASS_ERROR_DEVICE     = 23;	// illegal device number
    final int BASS_ERROR_NOPLAY     = 24;	// not playing
    final int BASS_ERROR_FREQ       = 25;   // illegal sample rate
    final int BASS_ERROR_NOTFILE    = 27;	// the stream is not a file stream
    final int BASS_ERROR_NOHW       = 29;   // no hardware voices available
    final int BASS_ERROR_EMPTY      = 31;	// the MOD music has no sequence data
    final int BASS_ERROR_NONET      = 32;	// no internet connection could be opened
    final int BASS_ERROR_CREATE     = 33;	// couldn't create the file
    final int BASS_ERROR_NOFX       = 34;   // effects are not available
    final int BASS_ERROR_NOTAVAIL   = 37;	// requested data is not available
    final int BASS_ERROR_DECODE     = 38;	// the channel is/isn't a "decoding channel"
    final int BASS_ERROR_DX         = 39;	// a sufficient DirectX version is not installed
    final int BASS_ERROR_TIMEOUT    = 40;	// connection timedout
    final int BASS_ERROR_FILEFORM   = 41;	// unsupported file format
    final int BASS_ERROR_SPEAKER    = 42;	// unavailable speaker
    final int BASS_ERROR_VERSION    = 43;	// invalid BASS version (used by add-ons)
    final int BASS_ERROR_CODEC      = 44;	// codec is not available/supported
    final int BASS_ERROR_ENDED      = 45;	// the channel/file has ended
    final int BASS_ERROR_BUSY       = 46;	// the device is busy
    final int BASS_ERROR_UNKNOWN    = -1;	// some other mystery problem
    //endregion

    //region Plugins
    int BASS_PluginLoad(String file, int flags);
    boolean BASS_PluginFree(int handle);
    BASS_PLUGININFO BASS_PluginGetInfo(int handle);

    class BASS_PLUGINFORM extends Structure {
        public int ctype;
        public String name;
        public String exts;

        protected List getFieldOrder() {
            return Arrays.asList(new String[] {"ctype", "name", "exts"});
        }
    }

    class BASS_PLUGININFO extends Structure {
        public int version;
        public int formatc;
        public BASS_PLUGINFORM formats;

        protected List getFieldOrder() {
            return Arrays.asList(new String[] {"version", "formatc", "formats"});
        }
    }
    //endregion

    //region Initialization, info, etc
    int BASS_ErrorGetCode();
    boolean BASS_Free();
    float BASS_GetCPU();
    int BASS_GetDevice();
    boolean BASS_GetDeviceInfo(int device, BASS_DEVICEINFO info);
    Pointer BASS_GetDSoundObject(int object);
    boolean BASS_GetInfo(BASS_INFO info);
    int BASS_GetVersion();
    float BASS_GetVolume();
    boolean BASS_Init(int device, int freq, int flags, Pointer win, Pointer clsid);
    boolean BASS_Pause();
    boolean BASS_SetDevice(int device);
    boolean BASS_SetVolume(float volume);
    boolean BASS_Start();
    boolean BASS_Stop();
    boolean BASS_Update(int length);

    class BASS_DEVICEINFO extends Structure {
        public String name;
        public String driver;
        public int flags;

        protected List getFieldOrder() {
            return Arrays.asList(new String[] {"name", "driver", "flags"});
        }
    }

    class BASS_INFO extends Structure {
        public int flags;
        public int hwsize;
        public int hwfree;
        public int freesam;
        public int minrate;
        public int maxrate;
        public boolean eax;
        public int minbuf;
        public int dsver;
        public int latency;
        public int initflags;
        public int speakers;
        public int freq;

        protected List getFieldOrder() {
            return Arrays.asList(new String[] {"flags", "hwsize", "hwfree", "freesam", "free3d", "minrate", "maxrate", "eax", "minbuf", "dsver", "latency", "initflags", "speakers", "freq"});
        }
    }
    //endregion

    //region 3D & EAX
    //Structures
    class BASS_3DVECTOR extends Structure {
        public float x;
        public float y;
        public float z;

        protected List getFieldOrder() {
            return Arrays.asList(new String[] {"x", "y", "z"});
        }
    }
    //endregion

    //region Streams
    int BASS_StreamCreate(int freq, int chans, int flags, STREAMPROC proc, Pointer user);
    int BASS_StreamCreateFile(boolean mem, Pointer file, long offset, long length, int flags);
    int BASS_StreamCreateFileUser(int system, int flags, BASS_FILEPROCS procs, Pointer user);
    int BASS_StreamCreateURL(String url, int offset, int flags, DOWNLOADPROC proc, Pointer user);
    boolean BASS_StreamFree(int handle);
    long BASS_StreamGetFilePosittion(int handle, int mode);
    int BASS_StreamPutData(int handle, Pointer buffer, int length);
    int BASS_StreamPutFileData(int handle, Pointer buffer, int length);

    //Callbacks
    interface DOWNLOADPROC extends Callback {
        void DOWNLOADPROC(Pointer buffer, int length, Pointer user);
    }
    interface FILECLOSEPROC extends Callback {
        void FILECLOSEPROC(Pointer user);
    }
    interface FILELENPROC extends Callback {
        void FILELENPROC(Pointer user);
    }
    interface FILEREADPROC extends Callback {
        void FILEREADPROC(Pointer bufer, int length, Pointer user);
    }
    interface FILESEEKPROC extends Callback {
        void FILESEEKPROC(long offset, Pointer user);
    }
    interface STREAMPROC extends Callback {
        void STREAMPROC(int handle, Pointer buffer, int length, Pointer user);
    }

    //Structures
    class BASS_FILEPROCS extends Structure {
        public FILECLOSEPROC close;
        public FILELENPROC len;
        public FILEREADPROC read;
        public FILESEEKPROC seek;

        protected List getFieldOrder() {
            return Arrays.asList(new String[] {"close", "len", "read", "seek"});
        }
    }
    //endregion

    //region Channels
    double BASS_ChannelBytes2Seconds(int handle, long pos);
    int BASS_ChannelFlags(int handle, int flags, int mask);
    boolean BASS_ChannelGet3DAttributes(int handle, Pointer mode_int, Pointer min_float, Pointer max_float, Pointer iangle_int, Pointer oangle_int, Pointer outvol_float);
    boolean BASS_ChannelGet3DPosition(int handle, BASS_3DVECTOR pos, BASS_3DVECTOR orient, BASS_3DVECTOR vel);
    boolean BASS_ChannelGetAttribute(int handle, int attrib, Pointer value_float);
    int BASS_ChannelGetAttributeEx(int handle, int attrib, Pointer value, int size);
    int BASS_ChannelGetData(int handle, Pointer buffer, int length);
    int BASS_ChannelGetDevice(int handle);
    boolean BASS_ChannelGetInfo(int handle, BASS_CHANNELINFO info);
    int BASS_ChannelGetLength(int handle, int mode);
    int BASS_ChannelGetLevel(int handle);
    boolean BASS_ChannelGetLevelEx(int handle, Pointer levels_float, float length, int flags);
    long BASS_ChannelGetPosition(int handle, int mode);
    String BASS_ChannelGetTags(int handle, int tags);
    int BASS_ChannelIsActive(int handle);
    boolean BASS_ChannelIsSliding(int handle, int attrib);
    boolean BASS_ChannelLock(int handle, boolean lock);
    boolean BASS_ChannelPause(int handle);
    boolean BASS_ChannelPlay(int handle, boolean restart);
    boolean BASS_ChannelRemoveDSP(int handle, int dsp);
    boolean BASS_ChannelRemoveFX(int handle, int fx);
    boolean BASS_ChannelRemoveLink(int handle, int chan);
    boolean BASS_ChannelRemoveSync(int handle, int sync);
    long BASS_ChannelSeconds2Bytes(int handle, double pos);
    boolean BASS_ChannelSet3DAttributes(int handle, int mode, float min, float max, int iangle, int oangle, float outvol);
    boolean BASS_ChannelSet3DPosition(int handle, BASS_3DVECTOR pos, BASS_3DVECTOR orient, BASS_3DVECTOR vel);
    boolean BASS_ChannelSetAttribute(int handle, int attrib, float value);
    boolean BASS_ChannelSetAttributeEx(int handle, int attrib, Pointer value, int size);
    boolean BASS_ChannelSetDevice(int handle, int device);
    int BASS_ChannelSetDSP(int handle, DSPPROC proc, Pointer user, int priority);
    int BASS_ChannelSetFX(int handle, int type, int priority);
    boolean BASS_ChannelSetLink(int handle, int chan);
    boolean BASS_ChannelSetPosition(int handle, long pos, int mode);
    int BASS_ChannelSetSync(int handle, int type, long param, SYNCPROC proc, Pointer user);
    boolean BASS_ChannelSlideAttribute(int handle, int attrib, float value, int time);
    boolean BASS_ChannelStop(int handle);
    boolean BASS_ChannelUpdate(int handle, int length);

    //region BASS_ChannelIsActive return values
    final int BASS_ACTIVE_STOPPED = 0;
    final int BASS_ACTIVE_PLAYING = 1;
    final int BASS_ACTIVE_STALLED = 2;
    final int BASS_ACTIVE_PAUSED  = 3;
    //endregion
    
    //region Channel Attributes
    final int BASS_ATTRIB_FREQ              = 1;
    final int BASS_ATTRIB_VOL               = 2;
    final int BASS_ATTRIB_PAN               = 3;
    final int BASS_ATTRIB_EAXMIX            = 4;
    final int BASS_ATTRIB_NOBUFFER          = 5;
    final int BASS_ATTRIB_VBR               = 6;
    final int BASS_ATTRIB_CPU               = 7;
    final int BASS_ATTRIB_SRC               = 8;
    final int BASS_ATTRIB_NET_RESUME        = 9;
    final int BASS_ATTRIB_SCANINFO          = 10;
    final int BASS_ATTRIB_NORAMP            = 11;
    final int BASS_ATTRIB_BITRATE           = 12;
    final int BASS_ATTRIB_MUSIC_AMPLIFY     = 0x100;
    final int BASS_ATTRIB_MUSIC_PANSEP      = 0x101;
    final int BASS_ATTRIB_MUSIC_PSCALER     = 0x102;
    final int BASS_ATTRIB_MUSIC_BPM         = 0x103;
    final int BASS_ATTRIB_MUSIC_SPEED       = 0x104;
    final int BASS_ATTRIB_MUSIC_VOL_GLOBAL  = 0x105;
    final int BASS_ATTRIB_MUSIC_ACTIVE      = 0x106;
    final int BASS_ATTRIB_MUSIC_VOL_CHAN    = 0x200;
    final int BASS_ATTRIB_MUSIC_VOL_INST    = 0x300;
    //endregion

    //region BASS_ChannelGetData flags
    final int BASS_DATA_AVAILABLE       = 0;			// query how much data is buffered
    final int BASS_DATA_FIXED           = 0x20000000;	// flag: return 8.24 fixed-point data
    final int BASS_DATA_FLOAT           = 0x40000000;	// flag: return floating-point sample data
    final int BASS_DATA_FFT256          = 0x80000000;	// 256 sample FFT
    final int BASS_DATA_FFT512          = 0x80000001;	// 512 FFT
    final int BASS_DATA_FFT1024         = 0x80000002;	// 1024 FFT
    final int BASS_DATA_FFT2048         = 0x80000003;	// 2048 FFT
    final int BASS_DATA_FFT4096         = 0x80000004;	// 4096 FFT
    final int BASS_DATA_FFT8192         = 0x80000005;	// 8192 FFT
    final int BASS_DATA_FFT16384        = 0x80000006;	// 16384 FFT
    final int BASS_DATA_FFT32768        = 0x80000007;	// 32768 FFT
    final int BASS_DATA_FFT_INDIVIDUAL  = 0x10;	        // FFT flag: FFT for each channel, else all combined
    final int BASS_DATA_FFT_NOWINDOW    = 0x20;	        // FFT flag: no Hanning window
    final int BASS_DATA_FFT_REMOVEDC    = 0x40;	        // FFT flag: pre-remove DC bias
    final int BASS_DATA_FFT_COMPLEX     = 0x80;	        // FFT flag: return complex data
    //endregion

    //region BASS_ChannelGetLevelEx flags
    final int BASS_LEVEL_MONO   = 1;
    final int BASS_LEVEL_STEREO = 2;
    final int BASS_LEVEL_RMS    = 4;
    //endregion

    //region BASS_ChannelGetTags types: what's returned
    final int BASS_TAG_ID3              = 0;	    // ID3v1 tags : TAG_ID3 structure
    final int BASS_TAG_ID3V2            = 1;	    // ID3v2 tags : variable length block
    final int BASS_TAG_OGG              = 2;	    // OGG comments : series of null-terminated UTF-8 strings
    final int BASS_TAG_HTTP             = 3;	    // HTTP headers : series of null-terminated ANSI strings
    final int BASS_TAG_ICY              = 4;	    // ICY headers : series of null-terminated ANSI strings
    final int BASS_TAG_META             = 5;	    // ICY metadata : ANSI string
    final int BASS_TAG_APE              = 6;	    // APE tags : series of null-terminated UTF-8 strings
    final int BASS_TAG_MP4              = 7;	    // MP4/iTunes metadata : series of null-terminated UTF-8 strings
    final int BASS_TAG_WMA              = 8;	    // WMA tags : series of null-terminated UTF-8 strings
    final int BASS_TAG_VENDOR           = 9;	    // OGG encoder : UTF-8 string
    final int BASS_TAG_LYRICS3          = 10;	    // Lyric3v2 tag : ASCII string
    final int BASS_TAG_CA_CODEC         = 11;	    // CoreAudio codec info : TAG_CA_CODEC structure
    final int BASS_TAG_MF               = 13;	    // Media Foundation tags : series of null-terminated UTF-8 strings
    final int BASS_TAG_WAVEFORMAT       = 14;	    // WAVE format : WAVEFORMATEEX structure
    final int BASS_TAG_RIFF_INFO        = 0x100;    // RIFF "INFO" tags : series of null-terminated ANSI strings
    final int BASS_TAG_RIFF_BEXT        = 0x101;    // RIFF/BWF "bext" tags : TAG_BEXT structure
    final int BASS_TAG_RIFF_CART        = 0x102;    // RIFF/BWF "cart" tags : TAG_CART structure
    final int BASS_TAG_RIFF_DISP        = 0x103;    // RIFF "DISP" text tag : ANSI string
    final int BASS_TAG_APE_BINARY       = 0x1000;	// + index #, binary APE tag : TAG_APE_BINARY structure
    final int BASS_TAG_MUSIC_NAME       = 0x10000;	// MOD music name : ANSI string
    final int BASS_TAG_MUSIC_MESSAGE    = 0x10001;	// MOD message : ANSI string
    final int BASS_TAG_MUSIC_ORDERS     = 0x10002;	// MOD order list : BYTE array of pattern numbers
    final int BASS_TAG_MUSIC_AUTH       = 0x10003;	// MOD author : UTF-8 string
    final int BASS_TAG_MUSIC_INST       = 0x10100;	// + instrument #, MOD instrument name : ANSI string
    final int BASS_TAG_MUSIC_SAMPLE     = 0x10300;	// + sample #, MOD sample name : ANSI string
    //endregion

    //region BASS_ChannelGetLength/GetPosition/SetPosition modes
    final int BASS_POS_BYTE         = 0;		  // byte position
    final int BASS_POS_MUSIC_ORDER  = 1;		  // order.row position, MAKELONG(order,row)
    final int BASS_POS_OGG          = 3;		  // OGG bitstream number
    final int BASS_POS_INEXACT      = 0x8000000;  // flag: allow seeking to inexact position
    final int BASS_POS_DECODE       = 0x10000000; // flag: get the decoding (not playing) position
    final int BASS_POS_DECODETO     = 0x20000000; // flag: decode to the position instead of seeking
    final int BASS_POS_SCAN         = 0x40000000; // flag: scan to the position
    //endregion
    
    //region DX8 effect types, use with BASS_ChannelSetFX
    interface BASSFXType {
        int BASS_FX_DX8_CHORUS = 0;
        int BASS_FX_DX8_COMPRESSOR = 1;
        int BASS_FX_DX8_DISTORTION = 2;
        int BASS_FX_DX8_ECHO = 3;
        int BASS_FX_DX8_FLANGER = 4;
        int BASS_FX_DX8_GARGLE = 5;
        int BASS_FX_DX8_I3DL2REVERB = 6;
        int BASS_FX_DX8_PARAMEQ = 7;
        int BASS_FX_DX8_REVERB = 8;
    }
    //endregion

    //Callbacks
    interface DSPPROC extends Callback {
        void DSPPROC(int handle, int channel, Pointer buffer, int length, Pointer user);
    }
    interface SYNCPROC extends Callback {
        void SYNCPROC(int handle, int channel, int data, Pointer user);
    }

    //Structures
    class BASS_CHANNELINFO extends Structure {
        public int freq;
        public int chans;
        public int flags;
        public int ctype;
        public int origres;
        public int plugin;
        public int sample;
        public String filename;

        protected List getFieldOrder() {
            return Arrays.asList(new String[] {"freq", "chans", "flags", "ctype", "origres", "plugin", "sample", "filename"});
        }
    }
    class TAG_APE_BINARY extends Structure {
        public String key;
        public Pointer data;
        public int length;

        protected List getFieldOrder() {
            return Arrays.asList(new String[] {"key", "data", "length"});
        }
    }
    class TAG_BEXT extends Structure {
        public byte[] Description = new byte[256];
        public byte[] Originator = new byte[32];
        public byte[] OriginatorReference = new byte[32];
        public byte[] OriginationDate = new byte[10];
        public byte[] OriginationTime = new byte[8];
        public long TimeReference;
        public short Version;
        public byte[] UMID = new byte[64];
        public byte[] Reserved = new byte[190];
        public byte[] CodingHistory;

        protected List getFieldOrder() {
            return Arrays.asList(new String[] {"Description", "Originator", "OriginatorReference", "OriginationDate", "OriginationTime", "TimeReference", "Version", "UMID", "Reserved", "CodingHistory"});
        }
    }
    class TAG_ID3 extends Structure {
        public byte[] id = new byte[3];
        public byte[] title = new byte[30];
        public byte[] artist = new byte[30];
        public byte[] album = new byte[30];
        public byte[] year = new byte[4];
        public byte[] comment = new byte[30];
        public byte genre;

        protected List getFieldOrder() {
            return Arrays.asList(new String[] {"id", "title", "artist", "album", "year", "comment", "genre"});
        }
    }
    //endregion

    //region Effects
    boolean BASS_FXGetParameters(int handle, Pointer params);
    boolean BASS_FXReset(int handle);
    boolean BASS_FXSetParameters(int handle, Pointer params);
    boolean BASS_FXSetPriority(int handle, int priority);

    //Structures
    class BASS_DX8_PARAMEQ extends Structure {
        public float fCenter;
        public float fBandwidth;
        public float fGain;

        protected List getFieldOrder() {
            return Arrays.asList("fCenter", "fBandwidth", "fGain");
        }
    }

    class BASS_DX8_CHORUS extends Structure {
        public float fWetDryMix;
        public float fDepth;
        public float fFeedback;
        public float fFrequency;
        public int lWaveform;	// 0=triangle, 1=sine
        public float fDelay;
        public int lPhase;		// BASS_DX8_PHASE_xxx

        public BASS_DX8_CHORUS() {
            super();
        }

        public BASS_DX8_CHORUS(Pointer p) {
            useMemory(p);
        }

        protected List getFieldOrder() {
            return Arrays.asList("fWetDryMix", "fDepth", "fFeedback", "fFrequency", "lWaveform", "fDelay", "lPhase");
        }
    }
    //endregion
}
