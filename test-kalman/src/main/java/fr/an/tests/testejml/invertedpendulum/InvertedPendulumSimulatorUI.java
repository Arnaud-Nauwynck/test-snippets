package fr.an.tests.testejml.invertedpendulum;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * code inspired from http://www.cs.colostate.edu/~anderson/code/Pole.java
 * split into params + model + ui + noise simulator + kalman filter + ...
 * 
 */
public class InvertedPendulumSimulatorUI {
    
    private JPanel panel;
    
    private long repaintPeriodMillis = 100;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private Future<?> periodicRepaintTimer;

    private InvertedPendulumParams modelParams;
    private InvertedPendulumModel model;
    private AbstractInvertedPendulumKalmanModel kalmanFilterModel;
    
    // ------------------------------------------------------------------------

    public InvertedPendulumSimulatorUI(InvertedPendulumModel model, AbstractInvertedPendulumKalmanModel kalmanFilter) {
        this.model = model;
        this.modelParams = model.getParams();
        this.kalmanFilterModel = kalmanFilter;
        createUI();
    }

    public static void main(String[] args) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                InvertedPendulumParams modelParams = new InvertedPendulumParams();
                InvertedPendulumModel model = new InvertedPendulumModel(modelParams);

                InvertedPendulumModelMeasureSimulator modelMeasureSimulator = new InvertedPendulumModelMeasureSimulator(model);
                AbstractInvertedPendulumKalmanModel kalmanFilterModel = new SimpleInvertedPendulumKalmanFilter(modelParams, modelMeasureSimulator);
                
                InvertedPendulumSimulatorUI ui = new InvertedPendulumSimulatorUI(model, kalmanFilterModel);
                JFrame frame = new JFrame();
                frame.getContentPane().add(ui.panel);
                frame.pack();
                frame.setVisible(true);
                
                model.start();
                ui.start();
                kalmanFilterModel.start();
            });
        } catch(Exception ex) {
            System.out.println("Failed ..exiting");
            ex.printStackTrace(System.out);
        }
    }
    // ------------------------------------------------------------------------

    protected double getModelForceIncr() {
        return modelParams.getParamForceIncrMag();
    }
    private void createUI() {
        panel = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            public void paint(Graphics g) {
                paintPendulum(g);
            }
        };
        panel.setPreferredSize(new Dimension(500, 200));
        
        // Event handlers
        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
                    model.incrControlForce(-getModelForceIncr());
                } else if ((e.getModifiers() & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK) {
                    model.setControlForce(0);
                    // model.resetPole();
                } else if ((e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
                    model.incrControlForce(getModelForceIncr());
                }
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                // System.out.println("keycode is " + e.getKeyCode() + " id " +
                // e.getID());
                if (e.getKeyCode() == KeyEvent.VK_LEFT)
                    model.incrControlForce(-getModelForceIncr());
                else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                    model.incrControlForce(getModelForceIncr());
                else if (e.getKeyCode() == KeyEvent.VK_DOWN)
                    model.setControlForce(0);
                else if (e.getKeyChar() == 'r') {
                    model.setControlForce(0);
                    model.resetPole();
                }
            }
        });
    }
    
    public void start() {
        if (periodicRepaintTimer == null) {
            periodicRepaintTimer = executorService.scheduleWithFixedDelay(() -> asyncRepaint(), 
                0, repaintPeriodMillis, TimeUnit.MILLISECONDS);
        }
    }

    public void stop() {
        if (periodicRepaintTimer != null) {
            periodicRepaintTimer.cancel(false);
            periodicRepaintTimer = null;
        }
    }

    public void asyncRepaint() {
        SwingUtilities.invokeLater(() -> panel.repaint()); 
    }


    public void paintPendulum(Graphics g2d) {
        Dimension d = panel.getSize();
        Color cartColor = new Color(0, 20, 255);
        Color estimatedCartColor = new Color(0, 255, 20);
        Color arrowColor = new Color(255, 255, 0);
        Color trackColor = new Color(100, 100, 50);

        // Erase the previous image.
        g2d.setColor(panel.getBackground());
        g2d.fillRect(0, 0, d.width, d.height);

        // Draw Track.
        double xs[] = { -2.5, 2.5, 2.5, 2.3, 2.3, -2.3, -2.3, -2.5 };
        double ys[] = { -0.4, -0.4, 0., 0., -0.2, -0.2, 0, 0 };
        int pixxs[] = new int[8], pixys[] = new int[8];
        for (int i = 0; i < 8; i++) {
            pixxs[i] = pixX(d, xs[i]);
            pixys[i] = pixY(d, ys[i]);
        }
        g2d.setColor(trackColor);
        g2d.fillPolygon(pixxs, pixys, 8);

        // Draw message
        String msg = "Left Mouse Button: push left    Right Mouse Button: push right     Middle Button: release";
        g2d.drawString(msg, 20, d.height - 20);

        double pos = model.getPos();
        double angle = model.getAngle();
        
        double poleLength = modelParams.paramPoleLength();
        
        // Draw cart.
        g2d.setColor(cartColor);
        g2d.fillRect(pixX(d, pos - 0.2), pixY(d, 0), pixDX(d, 0.4), pixDY(d, -0.2));

        // Draw pole.
        // offGraphics.setColor(cartColor);
        g2d.drawLine(pixX(d, pos), pixY(d, 0), pixX(d, pos + Math.sin(angle) * poleLength), pixY(d, poleLength * Math.cos(angle)));

        // Draw action arrow.
        double controlForce = model.getControlForce() / modelParams.getParamForceIncrMag();
        if (controlForce != 0) {
            int signAction = (controlForce > 0 ? 1 : (controlForce < 0) ? -1 : 0);
            int tipx = pixX(d, pos + 0.2 * controlForce);
            int tipy = pixY(d, -0.1);
            g2d.setColor(arrowColor);
            g2d.drawLine(pixX(d, pos), pixY(d, -0.1), tipx, tipy);
            g2d.drawLine(tipx, tipy, tipx - 4 * signAction, tipy + 4);
            g2d.drawLine(tipx, tipy, tipx - 4 * signAction, tipy - 4);
        }

        // draw from estimated kalman filter
        double estimatedPos = kalmanFilterModel.getEstimatedPos();
        double estimatedAngle = kalmanFilterModel.getEstimatedAngle();

        // Draw cart.
        g2d.setColor(estimatedCartColor);
        g2d.fillRect(pixX(d, estimatedPos - 0.2), pixY(d, 0), pixDX(d, 0.4), pixDY(d, -0.2));
        // Draw pole.
        g2d.drawLine(pixX(d, estimatedPos), pixY(d, 0), 
            pixX(d, estimatedPos + Math.sin(estimatedAngle) * poleLength), 
            pixY(d, poleLength * Math.cos(estimatedAngle)));

    }

    public int pixX(Dimension d, double v) {
        return (int) Math.round((v + 2.5) / 5.0 * d.width);
    }

    public int pixY(Dimension d, double v) {
        return (int) Math.round(d.height - (v + 2.5) / 5.0 * d.height);
    }

    public int pixDX(Dimension d, double v) {
        return (int) Math.round(v / 5.0 * d.width);
    }

    public int pixDY(Dimension d, double v) {
        return (int) Math.round(-v / 5.0 * d.height);
    }

}
