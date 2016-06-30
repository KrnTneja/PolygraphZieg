package polygraph.zieg;

import processing.core.PApplet;

public class Sketch {

    PolygraphZieg master;
    static int p, q;
    double magnification;
    GraphPlotter plotter;

    static float timeMagnification = 1;
    float[] currDataPoints;
    private int[] currTimePoints;
    private int currIndex;

// for skin conductivity
    static float rInput1 = 560000;
    static float rUpper1 = 1000000;
    static float rLower1 = 0;

    static float rInput2 = 560000;
    static float rUpper2 = 1000000;
    static float rLower2 = 0;

// for respiration rate
    static float rInput3 = 68000;
    static float rUpper3 = 100000;
    static float rLower3 = 0;

    static float rInput4 = 68000;
    static float rUpper4 = 100000;
    static float rLower4 = 0;

// for pulse rate
    static int trigger1;
    static int trigger2;
    static int trigger = 512;
    static int pulse = 72;

    void giveMaster(PolygraphZieg myMaster) {
        this.master = myMaster;
    }

    void givePlotter(GraphPlotter myPlotter) {
        this.plotter = myPlotter;
        /*if (this.plotter != null) {
         System.out.println("Masters Plotter isn't null.");
         } else {
         System.out.println("Masters plotter is null.");
         }*/
    }

    public void assignVar(int windowWidth, int windowHeight) {
        p = windowWidth;
        q = windowHeight;
        magnification = ((double) q) / 6.0 / 1024.0;
        // System.out.println("Magnification is " + magnification + "!");
    }

    public void refresh(PApplet pApplet) {
        pApplet.background(0);

        //pApplet.text("Y axis (V/div) " + m1Voltage/magnification + " V", 0, 590);
        //pApplet.text("X axis (ms/div) " + (PolygraphZieg.minTimePerDiv/timeMagnification * 1000) + " ms", 305, 590);

// the lines
        pApplet.stroke(0, 255, 200);
        pApplet.strokeWeight(0.25f);
        for (int i = 0; i < 59; i++) {
            pApplet.line(0, q / 60 * (i + 1), p, q / 60 * (i + 1));
        }
        for (int i = 0; i < 59; i++) {
            pApplet.line(p / 60 * (i + 1), 0, p / 60 * (i + 1), q);
        }
        pApplet.strokeWeight(0.5f);
        for (int i = 0; i < 11; i++) {
            pApplet.line(p / 12 * (i + 1), 0, p / 12 * (i + 1), q);
        }

        pApplet.strokeWeight(1.0f);
        pApplet.stroke(255);
        for (int i = 0; i < 6; i++) {
            pApplet.line(0, q / 6 * (i + 1), p, q / 6 * (i + 1));
        }
        
        pApplet.stroke(0, 255, 0);
        pApplet.strokeWeight(1.0f);
        
// F O R   S K I N   C O N D U C T I V I T Y - C 1        
        currDataPoints = GraphPlotter.c1DataPoints;
        currTimePoints = GraphPlotter.c1MilliSeconds;
        currIndex = GraphPlotter.c1FillIndex;
        for (int i = 0; i < currIndex - 2; i++) {
            //System.out.println(currDataPoints[i] + ", " + x[i]);
            if(currDataPoints[i] < q / 6 && currDataPoints[i + 1] < q / 6) 
                pApplet.line((float) (p * 1.0 * currTimePoints[i] / GraphPlotter.timeSpan), (float) (-(currDataPoints[i]) + q / 6),
                    (float) (p * 1.0 * currTimePoints[i + 1] / GraphPlotter.timeSpan), (float) (-(currDataPoints[i + 1]) + q / 6));
        }
        
//        pApplet.stroke(255, 0, 0);
        
// F O R   S K I N   C O N D U C T I V I T Y - C 2
        currDataPoints = GraphPlotter.c2DataPoints;
        currTimePoints = GraphPlotter.c2MilliSeconds;
        currIndex = GraphPlotter.c2FillIndex;
        for (int i = 0; i < currIndex - 2; i++) {
            //System.out.println(currDataPoints[i] + ", " + x[i]);
            if(currDataPoints[i] < q / 6 && currDataPoints[i + 1] < q / 6) 
                pApplet.line((float) (p * 1.0 * currTimePoints[i] / GraphPlotter.timeSpan), (float) (-(currDataPoints[i]) + q / 3),
                    (float) (p * 1.0 * currTimePoints[i + 1] / GraphPlotter.timeSpan), (float) (-(currDataPoints[i + 1]) + q / 3));
        }
        
//        pApplet.stroke(255, 255, 0);

// F O R   P U L S E   M E A S U R E M E N T - P 1
        plotter.calculatePulse();
        currDataPoints = GraphPlotter.p1DataPoints;
        currTimePoints = GraphPlotter.p1MilliSeconds;
        currIndex = GraphPlotter.p1FillIndex;
        for (int i = 0; i < currIndex - 2; i++) {
            //System.out.println(currDataPoints[i] + ", " + x[i]);
            pApplet.line((float) (p * 1.0 * currTimePoints[i] / GraphPlotter.timeSpan), (float) (-(currDataPoints[i]) * magnification + q / 2),
                    (float) (p * 1.0 * currTimePoints[i + 1] / GraphPlotter.timeSpan), (float) (-(currDataPoints[i + 1]) * magnification + q / 2));
        }
        pApplet.stroke(255, 0, 0);
        pApplet.line(0, (float) (-(trigger) * magnification + q / 2), p, (float) (-(trigger) * magnification + q / 2));
        
        pApplet.stroke(0, 255, 0);

// F O R   R E S P I R A T I O N   R A T E - R 1
        currDataPoints = GraphPlotter.r1DataPoints;
        currTimePoints = GraphPlotter.r1MilliSeconds;
        currIndex = GraphPlotter.r1FillIndex;
        for (int i = 0; i < currIndex - 2; i++) {
            //System.out.println(currDataPoints[i] + ", " + x[i]);
            if(currDataPoints[i] < q / 6 && currDataPoints[i + 1] < q / 6) 
                pApplet.line((float) (p * 1.0 * currTimePoints[i] / GraphPlotter.timeSpan), (float) (-(currDataPoints[i]) + 2 * q / 3),
                    (float) (p * 1.0 * currTimePoints[i + 1] / GraphPlotter.timeSpan), (float) (-(currDataPoints[i + 1]) + 2 * q / 3));
        }
        
//        pApplet.stroke(255, 0, 0);

// F O R   R E S P I R A T I O N   R A T E - R 2
        currDataPoints = GraphPlotter.r2DataPoints;
        currTimePoints = GraphPlotter.r2MilliSeconds;
        currIndex = GraphPlotter.r2FillIndex;
        for (int i = 0; i < currIndex - 2; i++) {
            //System.out.println(currDataPoints[i] + ", " + x[i]);
            if(currDataPoints[i] < q / 6 && currDataPoints[i + 1] < q / 6) 
                pApplet.line((float) (p * 1.0 * currTimePoints[i] / GraphPlotter.timeSpan), (float) (-(currDataPoints[i]) + 5 * q / 6),
                    (float) (p * 1.0 * currTimePoints[i + 1] / GraphPlotter.timeSpan), (float) (-(currDataPoints[i + 1]) + 5 * q / 6));
        }
        
//        pApplet.stroke(255, 255, 0);

// F O R   B L O O D   P R E S S U R E - B 1
        currDataPoints = GraphPlotter.b1DataPoints;
        currTimePoints = GraphPlotter.b1MilliSeconds;
        currIndex = GraphPlotter.b1FillIndex;
        for (int i = 0; i < currIndex - 2; i++) {
            //System.out.println(currDataPoints[i] + ", " + x[i]);
            pApplet.line((float) (p * 1.0 * currTimePoints[i] / GraphPlotter.timeSpan), (float) (-(currDataPoints[i]) * magnification + q),
                    (float) (p * 1.0 * currTimePoints[i + 1] / GraphPlotter.timeSpan), (float) (-(currDataPoints[i + 1]) * magnification + q));
        }

    }
}
