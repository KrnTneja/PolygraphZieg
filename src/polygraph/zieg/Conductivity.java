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
class Conductivity extends Sensor {
    
    // C O N T R U C T O R S
    Conductivity() throws IOException {
        super();
    }
    
    Conductivity(int graphSize) throws IOException {
        super(graphSize);
    }
    
    Conductivity(int graphSize, File output) throws IOException {
        super(graphSize, output);
    }
}
