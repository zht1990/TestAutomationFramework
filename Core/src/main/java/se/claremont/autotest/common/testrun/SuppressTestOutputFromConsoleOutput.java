package se.claremont.autotest.common.testrun;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Created by jordam on 2017-03-06.
 */
public class SuppressTestOutputFromConsoleOutput {
    static PrintStream originalOutputChannel;
    static ByteArrayOutputStream testOutputChannel;

    public static void restoreOutputChannel(){
        System.setOut(originalOutputChannel);
    }

    private static void rememberOriginalOutputChannel(){
        if(originalOutputChannel == null) originalOutputChannel = System.out;
    }

    public static void redirectOutputChannel(){
        rememberOriginalOutputChannel();
        testOutputChannel = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutputChannel));
    }

}