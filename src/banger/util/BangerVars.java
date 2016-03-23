package banger.util;

/**
 * Created by Merlin on 20.03.2016.
 */
public class BangerVars {

    public final static String FILE_EXTENSIONS[] = {".mp3", ".wav", ".wma", ".aiff", ".flac", ".m4a"};

    public enum RepeatState{
        NO_REPEAT, LOOP_SINGLE, LOOP_ONCE, LOOP_QUEUE
    }

    public enum LyricService{
        NONE, MUSIX_BOTH, SONGTEXTE_BOTH, MUSIX_TITLE, SONGTEXTE_TITLE
    }

    private BangerVars(){};

}
