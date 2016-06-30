package polygraph.zieg;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.BufferedReader;
import java.util.Enumeration;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.TooManyListenersException;

public class TwoWaySerialComm implements SerialPortEventListener, Runnable {

    PolygraphZieg master;
    PolygraphGUI gui;
    GraphPlotter plotter;
    // static Printer printer;
    byte instruction = 0;
    final byte factor = 5;
    private Enumeration ports = null;
    private SerialPort serialPort;
    BufferedReader portReader;
    private InputStream in = null;
    private OutputStream out = null;
    final static int TIMEOUT = 2000;
    final static int SPACE_ASCII = 32;
    final static int DASH_ASCII = 45;
    final static int NEW_LINE_ASCII = 10;
    public boolean setConnected = false;
    public boolean inStreaming = false;
    public boolean outStreaming = false;
    // static int counter = 0;
    public boolean fGMode = false;
    boolean pcoMode = false;
    int baudRate = 115200;
    String gotcha;

    public TwoWaySerialComm(PolygraphZieg myMaster, PolygraphGUI myGUI, GraphPlotter myPlotter) {
        super();
        this.master = myMaster;
        this.gui = myGUI;
        this.plotter = myPlotter;
        // this.printer = new Printer();
        // initiate();
    }

    void findAndConnect() throws Exception {
        ports = CommPortIdentifier.getPortIdentifiers();
        while (ports.hasMoreElements()) {
            CommPortIdentifier curPort = (CommPortIdentifier) ports.nextElement();
            if (!curPort.isCurrentlyOwned()) {
                CommPort commPort = curPort.open(this.getClass().getName(), 2000);
                if (commPort instanceof SerialPort) {
                    serialPort = (SerialPort) commPort;
                    serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    gui.log("Connected to " + curPort.getName());
                    setConnected = true;
                    return;
                }
            }
        }
        gui.log("Couldn't connect. Make sure that your USB is connected properly and not in use currently.");
    }

    void startInputStreaming() throws IOException, TooManyListenersException {
        if (setConnected && !inStreaming) {
            in = serialPort.getInputStream();
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            this.inStreaming = true;
            // gui.log("Started Streaming.");
            GraphPlotter.startMilliTime = (System.nanoTime() / 1000000);
            System.out.println(GraphPlotter.startMilliTime);
        }
        
    }

    void setPortNames() throws PortInUseException, UnsupportedCommOperationException {
        ports = CommPortIdentifier.getPortIdentifiers();
        while (ports.hasMoreElements()) {
            CommPortIdentifier curPort = (CommPortIdentifier) ports.nextElement();
            if (!curPort.isCurrentlyOwned()) {
                gui.log("Found an avaibale port: " + curPort.getName());
                gui.addPortName(curPort.getName());
            }
        }
    }

    void connectTo(String portName) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException {
        CommPortIdentifier curPort = CommPortIdentifier.getPortIdentifier(portName);
        if (!curPort.isCurrentlyOwned()) {
            CommPort commPort = curPort.open(this.getClass().getName(), 2000);
            if (commPort instanceof SerialPort) {
                serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                gui.log("Connected to " + curPort.getName());
                setConnected = true;
                return;
            }
        }
    }

    void startOutputStreaming() throws IOException, TooManyListenersException {
        if (setConnected && !outStreaming) {
            out = serialPort.getOutputStream();
            this.outStreaming = true;
            // gui.log("Started Streaming.");
        }
    }

    void disconnect() throws IOException, TooManyListenersException {
        if (setConnected) {
            serialPort.removeEventListener();
            serialPort.close();
            this.inStreaming = false;
            this.outStreaming = false;
            instruction = 6;
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            setConnected = false;
            gui.log("Disconnected successfully.");
        }
    }

    void disconnectInputStream() throws IOException {
        if (inStreaming) {
            in.close();
        }
        this.inStreaming = false;
    }

    void disconnectOutputStream() throws IOException, TooManyListenersException {
        instruction = 6;
        if (outStreaming) {
            out.close();
        }
        this.outStreaming = false;
    }

    /**
     *
     * @param spe
     */
    @Override
    public synchronized void serialEvent(SerialPortEvent spe) {
        portReader = new BufferedReader(new InputStreamReader(in));
        if (spe.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                gotcha = portReader.readLine();
                (new Thread(this)).start();
                // System.out.println(plotter.type + " " + plotter.received);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        if (gotcha.startsWith("c1")) {
            plotter.received = Float.parseFloat(gotcha.substring(2));
            plotter.received = plotter.received * Sketch.rInput1 / (1024 - plotter.received);
            if (Math.random() > 0.9) gui.setR1Text(plotter.received);
            plotter.received = Sketch.q / 6 * (plotter.received - Sketch.rLower1) / (Sketch.rUpper1 - Sketch.rLower1);
            plotter.type = "c1";
            (new Thread(plotter)).start();
        } else if (gotcha.startsWith("c2")) {
            plotter.received = Float.parseFloat(gotcha.substring(2));
            plotter.received = plotter.received * Sketch.rInput2 / (1024 - plotter.received);
            if (Math.random() > 0.9) gui.setR2Text(plotter.received);
            plotter.received = Sketch.q / 6 * (plotter.received - Sketch.rLower2) / (Sketch.rUpper2 - Sketch.rLower2);
            plotter.type = "c2";
            (new Thread(plotter)).start();
        } else if (gotcha.startsWith("p1")) {
            plotter.received = Float.parseFloat(gotcha.substring(2));
            plotter.type = "p1";
            (new Thread(plotter)).start();
        } else if (gotcha.startsWith("r1")) {
            plotter.received = Float.parseFloat(gotcha.substring(2));
            plotter.received = plotter.received * Sketch.rInput3 / (1024 - plotter.received);
            if (Math.random() > 0.9) gui.setR3Text(plotter.received);
            plotter.received = Sketch.q / 6 * (plotter.received - Sketch.rLower3) / (Sketch.rUpper3 - Sketch.rLower3);
            plotter.type = "r1";
            (new Thread(plotter)).start();
        } else if (gotcha.startsWith("r2")) {
            plotter.received = Float.parseFloat(gotcha.substring(2));
            plotter.received = plotter.received * Sketch.rInput4 / (1024 - plotter.received);
            if (Math.random() > 0.9) gui.setR4Text(plotter.received);
            plotter.received = Sketch.q / 6 * (plotter.received - Sketch.rLower4) / (Sketch.rUpper4 - Sketch.rLower4);
            plotter.type = "r2";
            (new Thread(plotter)).start();
        } else if (gotcha.startsWith("b1")) {
            plotter.received = Float.parseFloat(gotcha.substring(2));
            plotter.type = "b1";
            (new Thread(plotter)).start();
        }
    }

}
