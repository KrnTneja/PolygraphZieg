package polygraph.zieg;

public class GraphPlotter implements Runnable {

    static long startMilliTime;
    static final long timeSpan = 60000;
    static int[] c1MilliSeconds;
    static int[] c2MilliSeconds;
    static int[] p1MilliSeconds;
    static int[] r1MilliSeconds;
    static int[] r2MilliSeconds;
    static int[] b1MilliSeconds;

    static float[] c1DataPoints;
    static float[] c2DataPoints;
    static float[] p1DataPoints;
    static float[] r1DataPoints;
    static float[] r2DataPoints;
    static float[] b1DataPoints;

    static int samplesPerFrame = 10000;

    static int c1FillIndex = 0;
    static int c2FillIndex = 0;
    static int p1FillIndex = 0;
    static int r1FillIndex = 0;
    static int r2FillIndex = 0;
    static int b1FillIndex = 0;

    float received = 0;
    String type;

    private final PolygraphZieg master;
    private final PolygraphGUI gui;
    private int counter = 0;

    GraphPlotter(PolygraphZieg myMaster, PolygraphGUI myGUI) {
        this.master = myMaster;
        this.gui = myGUI;
        c1DataPoints = new float[samplesPerFrame];
        c2DataPoints = new float[samplesPerFrame];
        p1DataPoints = new float[samplesPerFrame];
        r1DataPoints = new float[samplesPerFrame];
        r2DataPoints = new float[samplesPerFrame];
        b1DataPoints = new float[samplesPerFrame];

        c1MilliSeconds = new int[samplesPerFrame];
        c2MilliSeconds = new int[samplesPerFrame];
        p1MilliSeconds = new int[samplesPerFrame];
        r1MilliSeconds = new int[samplesPerFrame];
        r2MilliSeconds = new int[samplesPerFrame];
        b1MilliSeconds = new int[samplesPerFrame];

    }

    void takeData(float digit) {
        switch (this.type) {
            case "c1":
                c1FillIndex = fill(c1DataPoints, c1MilliSeconds, c1FillIndex, digit, "c1");
                break;
            case "c2":
                c2FillIndex = fill(c2DataPoints, c2MilliSeconds, c2FillIndex, digit, "c2");
                break;
            case "p1":
                p1FillIndex = fill(p1DataPoints, p1MilliSeconds, p1FillIndex, digit, "p1");
                break;
            case "r1":
                r1FillIndex = fill(r1DataPoints, r1MilliSeconds, r1FillIndex, digit, "r1");
                break;
            case "r2":
                r2FillIndex = fill(r2DataPoints, r2MilliSeconds, r2FillIndex, digit, "r2");
                break;
            case "b1":
                b1FillIndex = fill(b1DataPoints, b1MilliSeconds, b1FillIndex, digit, "b1");
                break;
        }
        // (new Thread(new Storage("c1", c1MilliSeconds, digit))).start();
    }

    @Override
    public void run() {
        this.takeData(received);
    }

    private int fill(float[] newDataPoints, int[] milliSeconds, int arrayFillIndex, float digit, String type) {
        //counter++;
        //System.out.println(counter);
        while ((System.nanoTime() / 1000000) - startMilliTime > timeSpan) {
            startMilliTime += timeSpan;
            resetVariables();
        }
        newDataPoints[arrayFillIndex] = (float) ((float) digit);
        milliSeconds[arrayFillIndex] = (int) ((System.nanoTime() / 1000000) - startMilliTime);
        Storage.takeData(type, milliSeconds[arrayFillIndex], digit);
        if (PolygraphGUI.storage) (new Thread(new Storage())).start();
        arrayFillIndex++;
        return arrayFillIndex;
    }

    private void resetVariables() {
        c1FillIndex = 0;
        c2FillIndex = 0;
        p1FillIndex = 0;
        r1FillIndex = 0;
        r2FillIndex = 0;
        b1FillIndex = 0;

        c1DataPoints = new float[samplesPerFrame];
        c2DataPoints = new float[samplesPerFrame];
        p1DataPoints = new float[samplesPerFrame];
        r1DataPoints = new float[samplesPerFrame];
        r2DataPoints = new float[samplesPerFrame];
        b1DataPoints = new float[samplesPerFrame];

        counter = 0;
    }

    void calculatePulse() {
        for (int i = 0; i < GraphPlotter.p1FillIndex - 1; i++) {
            if (GraphPlotter.p1DataPoints[i + 1] > GraphPlotter.p1DataPoints[i]
                    && GraphPlotter.p1DataPoints[i + 1] >= Sketch.trigger && GraphPlotter.p1DataPoints[i] <= Sketch.trigger) {
                Sketch.trigger2 = Sketch.trigger1;
                Sketch.trigger1 = GraphPlotter.p1MilliSeconds[i];
            }
        }
        updatePulse();
    }

    private void updatePulse() {
        if (Sketch.trigger2 - Sketch.trigger1 != 0) {
            Sketch.pulse = 60000 / (Sketch.trigger1 - Sketch.trigger2);
        }
        if (Sketch.pulse > 0) gui.changePulse();
        // System.out.println(Sketch.pulse);
    }
}
