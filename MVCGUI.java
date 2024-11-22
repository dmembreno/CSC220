package Week14;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;

public class MVCGUI extends JFrame {
    //JFrame setup, Graphic and Inner panel instantiation
    private static final long serialVersionUID = -6167569334213042018L;
    private final int WIDTH = 512;
    private final int HEIGHT = 600;
    private GraphicPanelInner graphicsPanel;
    private ControlPanelInner controlPanel;
    private Timer timer; // Timer object for the "Start/Stop" functionality
    private boolean isTimerRunning = false; // To track the state of the timer

    public MVCGUI() {
        super();
        this.setTitle("MVC GUI");
        this.setSize(WIDTH, HEIGHT);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));
// Graphics Panel (Center area)
        graphicsPanel = new GraphicPanelInner();
        this.add(graphicsPanel, BorderLayout.CENTER);
// Control Panel (Left area)
        controlPanel = new ControlPanelInner();
        this.add(controlPanel, BorderLayout.WEST);
// Slider (South area)
        this.add(controlPanel.getSlider(), BorderLayout.SOUTH);
        this.setResizable(true);
        graphicsPanel.repaint();
        this.setVisible(true);
        graphicsPanel.setFocusable(true);
        graphicsPanel.requestFocus();
// Initialize the timer
        timer = new Timer(1000, e -> System.out.println("Tic"));
    }
    // Inner class for the graphics panel
    public class GraphicPanelInner extends JPanel implements MouseMotionListener {
        private static final long serialVersionUID = 7056793999538384084L;
        private int rows = 10; // Default rows
        private int columns = 10; // Default columns
        private boolean gridOn = true; // Whether grid lines are shown
        private boolean wrap = false; // Wrap behavior
        public GraphicPanelInner() {
            super();
            this.setBackground(Color.YELLOW);
            this.prepareActionHandlers();
            this.addMouseMotionListener(this);
        }
        private void prepareActionHandlers() {
            this.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    System.out.println("Mouse clicked at (" + event.getX() + ", " +
                            event.getY() + ")");
                }
                @Override
                public void mouseEntered(MouseEvent event) {
                    System.out.println("Mouse entered panel");
                }
                @Override
                public void mouseExited(MouseEvent event) {
                    System.out.println("Mouse exited panel");
                }
                @Override
                public void mousePressed(MouseEvent event) {
                    System.out.println("Mouse pressed at (" + event.getX() + ", " +
                            event.getY() + ")");
                }
                @Override
                public void mouseReleased(MouseEvent event) {
                    System.out.println("Mouse released at (" + event.getX() + ", "
                            + event.getY() + ")");
                    repaint();
                }
            });
        }
        @Override
        public void paint(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphicsContext = (Graphics2D) g;
            int width = this.getWidth();
            int height = this.getHeight();
            double horzSpacing = width / (double) columns;
            double vertSpacing = height / (double) rows;
// Drawing grid lines if gridOn is true
            if (gridOn) {
                graphicsContext.setColor(Color.BLACK);
                for (int i = 0; i < columns; i++) {
                    graphicsContext.drawLine((int) (i * horzSpacing), 0, (int) (i *
                            horzSpacing), height);
                }
                for (int i = 0; i < rows; i++) {
                    graphicsContext.drawLine(0, (int) (i * vertSpacing), width,
                            (int) (i * vertSpacing));
                }
            }
        }
        public void setGridSize(int newRows, int newColumns) {
            this.rows = newRows;
            this.columns = newColumns;
            repaint();
        }
        public void toggleGrid(boolean value) {
            this.gridOn = value;
            repaint();
        }
        public void toggleWrap(boolean value) {
            this.wrap = value;
        }
        @Override
        public void mouseDragged(MouseEvent e) {}
        @Override
        public void mouseMoved(MouseEvent e) {}
    }
    // Inner class for control panel
    public class ControlPanelInner extends JPanel {
        private static final long serialVersionUID = -8776438726683578403L;
        private JButton startStopButton;
        private JButton saveButton;
        private JButton loadButton;
        private JButton setSizeButton;
        private JTextField rowsTextField;
        private JTextField colsTextField;
        private JSlider slider;
        private JCheckBox gridCheckBox;
        private JCheckBox drawCellCheckBox;//I wasn't sure what this buttons
        //-functionality would be, so I added none
        private JCheckBox wrapCheckBox;
        public ControlPanelInner() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
// Row and Column text fields
            rowsTextField = new JTextField("10", 5);
            rowsTextField.setPreferredSize(new Dimension(60, 25));
            colsTextField = new JTextField("10", 5);
            colsTextField.setPreferredSize(new Dimension(60, 25));
// Add controls to panel
            this.add(new JLabel("Rows:"));
            this.add(rowsTextField);
            this.add(new JLabel("Columns:"));
            this.add(colsTextField);
// Grid On checkbox
            gridCheckBox = new JCheckBox("Grid On", true);
            gridCheckBox.addActionListener(e -> {
                System.out.println("Grid On checkbox toggled");
                graphicsPanel.toggleGrid(gridCheckBox.isSelected());
            });
            this.add(gridCheckBox);
// Draw Cells checkbox (no functionality for now)
            drawCellCheckBox = new JCheckBox("Draw Cells");
// No action listener added, so it has no effect
            this.add(drawCellCheckBox);
// Wrap checkbox
            wrapCheckBox = new JCheckBox("Wrap");
            wrapCheckBox.addActionListener(e -> {
                System.out.println("Wrap checkbox toggled");
                graphicsPanel.toggleWrap(wrapCheckBox.isSelected());
            });
            this.add(wrapCheckBox);
// Set Size button
            setSizeButton = new JButton("Set Size");
            setSizeButton.addActionListener(e -> {
                System.out.println("Set Size button clicked");
                int rows = Integer.parseInt(rowsTextField.getText());
                int cols = Integer.parseInt(colsTextField.getText());
                graphicsPanel.setGridSize(rows, cols);
            });
            this.add(setSizeButton);
// Start/Stop Animation button
            startStopButton = new JButton("Start");
            startStopButton.addActionListener(e -> {
                if (isTimerRunning) {
                    System.out.println("Timer stopped");
                    timer.stop();
                    startStopButton.setText("Start");
                } else {
                    System.out.println("Timer started");
                    timer.start();
                    startStopButton.setText("Stop");
                }
                isTimerRunning = !isTimerRunning;
            });
            this.add(startStopButton);
// Save Button
            saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                JFileChooser jfc = new JFileChooser();
                if (jfc.showDialog(null, "Save") == JFileChooser.APPROVE_OPTION) {
                    System.out.println("Save selected file: " +
                            jfc.getSelectedFile().getName());
                }
            });
            this.add(saveButton);
// Load Button
            loadButton = new JButton("Load");
            loadButton.addActionListener(e -> {
                JFileChooser jfc = new JFileChooser();
                if (jfc.showDialog(null, "Load") == JFileChooser.APPROVE_OPTION) {
                    System.out.println("Load selected file: " +
                            jfc.getSelectedFile().getName());
                }
            });
            this.add(loadButton);
// Slider for adjusting grid size
            slider = new JSlider(10, 100, 50); // Slider for grid size (10 to 100)
            slider.setMajorTickSpacing(10);
            slider.setMinorTickSpacing(1);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.addChangeListener(e -> {
                System.out.println("Slider value changed: " + slider.getValue());
                graphicsPanel.setGridSize(slider.getValue(),
                        slider.getValue()); // Adjust rows/cols
            });
        }
        public JSlider getSlider() {
            return slider;
        }
    }
    public static void main(String[] args) {
        new MVCGUI();
    }
}
