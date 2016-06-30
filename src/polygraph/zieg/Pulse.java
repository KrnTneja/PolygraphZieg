/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package polygraph.zieg;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 *
 * @author dell
 */
class Pulse extends Sensor {
    
    // C O N T R U C T O R S
    Pulse() throws IOException {
        super();
    }
    
    Pulse(int graphSize) throws IOException {
        super(graphSize);
    }
    
    Pulse(int graphSize, File output) throws IOException {
        super(graphSize, output);
    }
}
