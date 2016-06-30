
package polygraph.zieg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Sensor {
    
    // F I E L D S
    int graphSize;
    int counter;
    short[] dataPoints;
    File output;
    BufferedWriter writer;
    
    // C O N S T R U C T O R S
    Sensor() throws IOException {
        this.graphSize = 200;
        this.output = new File((new Date()).toString() + ".txt");
        dataPoints = new short[graphSize];
        this.counter = 0;
        this.writer = new BufferedWriter(new FileWriter(output));
    }    
    
    Sensor(int graphSize) throws IOException {
        this.graphSize = graphSize;
        this.output = new File((new Date()).toString() + ".txt");
        dataPoints = new short[graphSize];
        this.counter = 0;
        this.writer = new BufferedWriter(new FileWriter(output));
    }
    
    Sensor(int graphSize, File output) throws IOException {
        this.graphSize = graphSize;
        this.output = output;
        dataPoints = new short[graphSize];
        this.counter = 0;
        this.writer = new BufferedWriter(new FileWriter(output));
    }
    
    // D A T A   H A N D L I N G
    void takeData(short data) throws IOException {
        if (counter == graphSize) {
            dataPoints = new short[graphSize];
            dataPoints[0] = data;
            counter = 1;
        } else {
            dataPoints[counter] = data;
            counter++;
        }
        storeData(data);
    }
    
    void storeData(int data) throws IOException {
        writer.write(data);
        writer.newLine();
    }
}
