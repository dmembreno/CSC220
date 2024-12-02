package MVCGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class MVCGUI3 extends JFrame {
    private static final long serialVersionUID = -6167569334213042018L;
    private final int WIDTH = 512;
    private final int HEIGHT = 600;
    private GraphicPanelInner graphicsPanel;
    private ControlPanelInner controlPanel;
    private Timer timer;
    private boolean isTimerRunning = false;
    private int ticCounter = 1;
    private Model1 model; // Reference to the Model1 class

    public MVCGUI3() {
        super();
        this.setTitle("MVC GUI");
        this.setSize(WIDTH, HEIGHT);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        model = new Model1(10, 10); // Initializing Model1 with 10x10 grid

        graphicsPanel = new GraphicPanelInner();
        this.add(graphicsPanel, BorderLayout.CENTER);

        controlPanel = new ControlPanelInner();
        this.add(controlPanel, BorderLayout.WEST);

        this.add(controlPanel.getSlider(), BorderLayout.SOUTH);
        this.setResizable(true);
        graphicsPanel.repaint();
        this.setVisible(true);
        graphicsPanel.setFocusable(true);
        graphicsPanel.requestFocus();

        // Initialize the timer
        int delay = 1000;
        timer = new Timer(delay, e -> {
            System.out.println("tic " + ticCounter);
            ticCounter++;
            model.updateGrid(); // Update the grid based on the Game of Life rules
            graphicsPanel.repaint(); // Repaint the grid
        });
    }

    // Inner class for the graphics panel
    public class GraphicPanelInner extends JPanel implements MouseMotionListener {
        private static final long serialVersionUID = 7056793999538384084L;
        private boolean gridOn = true;
        private boolean drawCells = false;

        public GraphicPanelInner() {
            super();
            this.setBackground(Color.YELLOW);
            this.addMouseMotionListener(this);
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    System.out.println("Mouse clicked at (" + e.getX() + ", " +
                            e.getY() + ")");
                    handleMouseClick(e.getX(), e.getY());
                }
            });
        }

        private void handleMouseClick(int x, int y) {
            int row = y / (getHeight() / model.getRows());
            int col = x / (getWidth() / model.getColumns());
            if (row < model.getRows() && col < model.getColumns()) {
                if (drawCells) {
                    model.setCell(row, col, 1); // Draw cell (alive)
                } else {
                    model.setCell(row, col, 0); // Erase cell (dead)
                }
                repaint();
            }
        }

        @Override
        public void paint(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphicsContext = (Graphics2D) g;
            int width = this.getWidth();
            int height = this.getHeight();
            double horzSpacing = width / (double) model.getColumns();
            double vertSpacing = height / (double) model.getRows();

            // Drawing grid lines if gridOn is true
            if (gridOn) {
                graphicsContext.setColor(Color.BLACK);
                for (int i = 0; i < model.getColumns(); i++) {
                    graphicsContext.drawLine((int) (i * horzSpacing), 0, (int) (i * horzSpacing), height);
                }
                for (int i = 0; i < model.getRows(); i++) {
                    graphicsContext.drawLine(0, (int) (i * vertSpacing), width, (int) (i * vertSpacing));
                }
            }

            // Drawing the filled oval (cell) based on the grid data
            graphicsContext.setColor(Color.BLACK);
            for (int i = 0; i < model.getRows(); i++) {
                for (int j = 0; j < model.getColumns(); j++) {
                    if (model.getGrid()[i][j] == 1) {
                        int x = (int) (j * horzSpacing);
                        int y = (int) (i * vertSpacing);
                        int ovalWidth = (int) horzSpacing;
                        int ovalHeight = (int) vertSpacing;
                        graphicsContext.fillOval(x, y, ovalWidth, ovalHeight);
                    }
                }
            }
        }

        public void setGridSize(int newRows, int newColumns) {
            model = new Model1(newRows, newColumns); // Reinitialize the model with new grid size
            repaint();
        }

        public void toggleGrid(boolean value) {
            gridOn = value;
            repaint();
        }

        public void toggleWrap(boolean value) {
            model.toggleWrap(value); // Enable/disable wrap functionality in Model1
        }

        public void toggleDrawCells(boolean value) {
            drawCells = value;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            handleMouseClick(e.getX(), e.getY());
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }

    // Inner class for control panel
    public class ControlPanelInner extends JPanel {
        private static final long serialVersionUID = -8776438726683578403L;
        private JButton startStopButton;
        private JTextField rowsTextField;
        private JTextField colsTextField;
        private JSlider slider;
        private JButton saveButton;
        private JButton loadButton;
        private JCheckBox gridCheckBox;
        private JCheckBox drawCellCheckBox;
        private JCheckBox wrapCheckBox;

        public ControlPanelInner() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            rowsTextField = new JTextField("10", 5);
            rowsTextField.setPreferredSize(new Dimension(60, 25));
            colsTextField = new JTextField("10", 5);
            colsTextField.setPreferredSize(new Dimension(60, 25));

            this.add(new JLabel("Rows:"));
            this.add(rowsTextField);
            this.add(new JLabel("Columns:"));
            this.add(colsTextField);

            // Grid checkbox
            gridCheckBox = new JCheckBox("Grid On", true);
            gridCheckBox.addActionListener(e -> {
                graphicsPanel.toggleGrid(gridCheckBox.isSelected());
            });
            this.add(gridCheckBox);

            // Draw Cells checkbox
            drawCellCheckBox = new JCheckBox("Draw Cells");
            drawCellCheckBox.addActionListener(e -> {
                graphicsPanel.toggleDrawCells(drawCellCheckBox.isSelected());
            });
            this.add(drawCellCheckBox);

            // Wrap checkbox
            wrapCheckBox = new JCheckBox("Wrap");
            wrapCheckBox.addActionListener(e -> {
                graphicsPanel.toggleWrap(wrapCheckBox.isSelected());

            });
            this.add(wrapCheckBox);

            // Set Size button
            JButton setSizeButton = new JButton("Set Size");
            setSizeButton.addActionListener(e -> {
                try {
                    // Parse the text fields to integers
                    int rows = Integer.parseInt(rowsTextField.getText());
                    int cols = Integer.parseInt(colsTextField.getText());

                    // Check for negative values
                    if (rows < 1 || cols < 1) {
                        throw new IllegalArgumentException("Rows and columns must be positive integers.");
                    }

                    // Set the grid size if valid
                    graphicsPanel.setGridSize(rows, cols);

                } catch (NumberFormatException ex) {
                    // Handle invalid input (non-numeric)
                    JOptionPane.showMessageDialog(this, "Please enter valid numbers for rows and columns.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    // Handle invalid values (negative numbers)
                    JOptionPane.showMessageDialog(this, ex.getMessage(),
                            "Invalid Value", JOptionPane.ERROR_MESSAGE);
                }
            });
            this.add(setSizeButton);

            // Start/Stop button
            startStopButton = new JButton("Start");
            startStopButton.addActionListener(e -> {
                if (isTimerRunning) {
                    timer.stop();
                    startStopButton.setText("Start");
                } else {
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
                jfc.setDialogTitle("Save Grid");
                if (jfc.showSaveDialog(MVCGUI3.this) ==
                        JFileChooser.APPROVE_OPTION) {
                    // Get the selected file from the JFileChooser
                    File selectedFile = jfc.getSelectedFile();

                    // Create an ObjectOutputStream to serialize the model
                    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(selectedFile))) {
                        out.writeObject(model); // Serialize and save the model object
                        JOptionPane.showMessageDialog(this, "Grid saved successfully!");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Error saving the grid: " + ex.getMessage(),
                                "Save Error", JOptionPane.ERROR_MESSAGE);
                    }
// Save logic
                }
            });
            this.add(saveButton);
// Load Button
            loadButton = new JButton("Load");
            loadButton.addActionListener(e -> {
                JFileChooser jfc = new JFileChooser();
                jfc.setDialogTitle("Load Grid");
                if (jfc.showOpenDialog(MVCGUI3.this) ==
                        JFileChooser.APPROVE_OPTION) {
// Load logic
                    // Get the selected file from the JFileChooser
                    File selectedFile = jfc.getSelectedFile();

                    // Create an ObjectInputStream to deserialize the model
                    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(selectedFile))) {
                        model = (Model1) in.readObject(); // Deserialize the model object
                        graphicsPanel.repaint(); // Repaint the graphics panel to display the loaded grid
                        JOptionPane.showMessageDialog(this, "Grid loaded successfully!");
                    } catch (IOException | ClassNotFoundException ex) {
                        JOptionPane.showMessageDialog(this, "Error loading the grid: " + ex.getMessage(),
                                "Load Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            this.add(loadButton);
            // Slider for controlling the speed of the simulation
            slider = new JSlider(0, 100, 50);
            slider.setMajorTickSpacing(20);
            slider.setMinorTickSpacing(1);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.addChangeListener(e -> {
                int value = slider.getValue();
                int delay = 50 + (value * 19);
                timer.setDelay(delay);
                System.out.println("Slider value changed: " + value + ", Delay: " +
                        delay + "ms");
            });
            this.add(slider);
        }

        public JSlider getSlider() {
            return slider;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MVCGUI3());
    }
}
