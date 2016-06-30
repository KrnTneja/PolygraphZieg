package polygraph.zieg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


/**
 *
 * @author dell
 */
public class Storage implements Runnable {

    static int timeStamp;
    static String type;
    static float value;
    static String fileName = "D:\\testdata.txt";
    static File polyFile = new File(Storage.fileName);
    static BufferedWriter writer;
    static boolean setup = false;

    
    static void takeData(String type, int milliSecond, float digit) {        
        Storage.timeStamp = milliSecond;
        Storage.value = digit;
        Storage.type = type;
    }

    static void resetFile(String name) throws IOException {
        Storage.fileName = name;
        Storage.polyFile = new File(name);
        setup = false;
        setup();
    }
    @Override
    public void run() {
        try {
            if (!setup) { setup(); }
            writer.append(type + " " + timeStamp + " " + value + "\r"
                    + "\n");
            writer.flush();
            // System.out.println(type + " " + timeStamp + " " + value);
            // writer.newLine();
            // writer.close()
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static private void setup() throws IOException {
        if (!polyFile.exists()) { polyFile.createNewFile(); }
        writer = new BufferedWriter(new FileWriter(polyFile));
        setup = true;
    }

}

