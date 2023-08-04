package com.github.frensi.notchess;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Helper class to build the notchess program
 * 
 * @author Frensi Angjo
 * @version Spring 2023
 */

public class ThreadGraphicsController implements Runnable {

    // The panel that stores the game
    protected JPanel panel;
    // The label of the window
    protected String windowLabel;
    // The size of the window
    protected Dimension windowSize;

    // A copy of the "this" reference
    protected ThreadGraphicsController thisTGC;

    /**
     * Constructor to build the window
     * 
     * @param label The label of the window
     * @param size  The size of the window
     */
    public ThreadGraphicsController(String label, int width, int height) {

        windowLabel = label;
        windowSize = new Dimension(width, height);
        thisTGC = this;
    }

    /**
     * The run method to set up the graphical user interface
     */
    @Override
    public void run() {

        // Create the frame
		JFrame frame = new JFrame(windowLabel);
		frame.setPreferredSize(windowSize);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create panel to store the game
        panel = new JPanel(new BorderLayout());

        // Build GUI from notchess
        buildGUI(frame, panel);

        // Show frame
		frame.pack();
		frame.setVisible(true);
    }

    /**
     * Method to add listeners to buttons on the frame
     * 
     * @param b The button to add the listener
     */
    protected void addListeners(JButton b) {

    }

    /**
     * Method that is called by notchess to create the GUI
     * 
     * @param frame the JFrame where everything is shown
     * @param panel the JPanel that shows the game
     */
    protected void buildGUI(JFrame frame, JPanel panel) {

        frame.add(panel);
    }

}
