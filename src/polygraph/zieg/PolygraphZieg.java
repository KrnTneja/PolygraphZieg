package polygraph.zieg;

import processing.core.PApplet;

public class PolygraphZieg extends PApplet {

    static Conductivity c1;
    static Conductivity c2;
    static Pulse p1;
    static Respiration r1;
    static Respiration r2;
    static BloodPressure b1;
    static PolygraphZieg mainPCO;
    static PolygraphGUI session;
    static TwoWaySerialComm comm;
    static GraphPlotter plotter;
    static Sketch sketch;
    final static float samplesPerSecond = 400;
    final static float framesPerSecond = 4;
    final static int windowWidth = 900;
    final static int windowHeight = windowWidth / 3 * 2;

    public static void main(String[] args) {
        /*try {
         c1 = new Conductivity();
         c2 = new Conductivity();
         p1 = new Pulse();
         r1 = new Respiration();
         r2 = new Respiration();
         b1 = new BloodPressure();
         } catch (IOException ex) {
         Logger.getLogger(PolygraphZieg.class.getName()).log(Level.SEVERE, null, ex);
         }*/
        PolygraphZieg master = new PolygraphZieg();
        PolygraphZieg.mainPCO = master;
        PolygraphZieg.session = new PolygraphGUI(master);
        PolygraphZieg.plotter = new GraphPlotter(master, PolygraphZieg.session);
        PolygraphZieg.comm = new TwoWaySerialComm(master, PolygraphZieg.session, PolygraphZieg.plotter);
        PolygraphZieg.main("polygraph.zieg.PolygraphZieg");
        PolygraphZieg.session.main();
    }

    TwoWaySerialComm getSerialCommunication() {
        return comm;
    }

    GraphPlotter getGraphPlotter() {
        return plotter;
    }

    @Override
    public void settings() {
        size(windowWidth, windowHeight + 1);
    }

    @Override
    public void setup() {
        frameRate(PolygraphZieg.framesPerSecond);
        PolygraphZieg.sketch = new Sketch();
        PolygraphZieg.sketch.assignVar(windowWidth, windowHeight);
        PolygraphZieg.sketch.giveMaster(this);
        PolygraphZieg.sketch.givePlotter(PolygraphZieg.plotter);
    }

    @Override
    public void draw() {
        this.sketch.refresh(this);
    }
}
