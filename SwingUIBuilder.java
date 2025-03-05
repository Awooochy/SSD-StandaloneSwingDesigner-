import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.*;

public class SwingUIBuilder {
    private JFrame frame;
    private JPanel workspace;
    private JButton exportButton, saveButton, loadButton;
    private List<JComponent> components;

    private JTextField titleField, widthField, heightField;
    private String currentTitle = "Generated UI";
    private int currentWidth = 800;
    private int currentHeight = 600;

    //Line coordinates view
    private JLabel positionLabel;


    //variables gridline
    private List<GridLine> gridLines = new ArrayList<>();
    private GridLine selectedGridLine = null;
    private Point dragStart = null;
    private int snapRange = 5;


    public SwingUIBuilder() {
        frame = new JFrame("AWT GUI Builder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLayout(new BorderLayout());

        components = new ArrayList<>();


        // Create left sidebar panel
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(150, 0));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add components to left panel
        JButton addButton = new JButton("Add Button");
        JButton addCheckbox = new JButton("Add Checkbox");
        JButton addTextArea = new JButton("Add Text Area");
        JButton addLabel = new JButton("Add Label");
        JButton addComboBox = new JButton("Add Combo Box");
        JButton addList = new JButton("Add List");
        JButton addTextField = new JButton("Add Text Field");
        JButton addPasswordField = new JButton("Add Password Field");
        JButton addSlider = new JButton("Add Slider");
        JButton addTable = new JButton("Add Table");
        JButton addSeparator = new JButton("Add Separator");


        leftPanel.add(addButton);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(addCheckbox);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(addTextArea);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(addLabel);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(addComboBox);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(addList);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(addTextField);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(addPasswordField);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(addSlider);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(addTable);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(addSeparator);





        // Create top toolbar
        JPanel toolbar = new JPanel();
        exportButton = new JButton("Export Code");
        saveButton = new JButton("Save Layout");
        loadButton = new JButton("Load Layout");


        // Add toolbar buttons
        JButton addVerticalLine = new JButton("Add Vertical Line");
        JButton addHorizontalLine = new JButton("Add Horizontal Line");
        toolbar.add(exportButton);
        toolbar.add(saveButton);
        toolbar.add(loadButton);
        toolbar.add(addVerticalLine);
        toolbar.add(addHorizontalLine);



        JPanel settingsPanel = new JPanel();
        titleField = new JTextField(currentTitle, 15);
        widthField = new JTextField(String.valueOf(currentWidth), 5);
        heightField = new JTextField(String.valueOf(currentHeight), 5);
        JButton resizeButton = new JButton("Resize");
        JTextField snapRangeField = new JTextField("5", 5);
        JButton applySnapButton = new JButton("Apply Snap");
        JButton lockAllButton = new JButton("Lock All");


        //settings panel section
        settingsPanel.add(new JLabel("Title:"));
        settingsPanel.add(titleField);
        settingsPanel.add(new JLabel("Width:"));
        settingsPanel.add(widthField);
        settingsPanel.add(new JLabel("Height:"));
        settingsPanel.add(heightField);
        settingsPanel.add(resizeButton);
        settingsPanel.add(new JLabel("Snap Range:"));
        settingsPanel.add(snapRangeField);
        settingsPanel.add(applySnapButton);
        settingsPanel.add(lockAllButton);




        workspace = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                // Draw dashed border
                g2d.setColor(Color.GRAY);
                float[] dash = {5f, 5f};
                Stroke dashedStroke = new BasicStroke(1, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 1, dash, 0);
                g2d.setStroke(dashedStroke);
                g2d.drawRect(0, 0, currentWidth - 1, currentHeight - 1);

                // Draw grid lines
                g2d.setColor(new Color(0, 0, 255, 100)); // Semi-transparent blue
                for (GridLine line : gridLines) {
                    if (line.getOrientation() == GridLine.VERTICAL) {
                        int x = line.getPosition();
                        g2d.drawLine(x, 0, x, currentHeight);
                    } else {
                        int y = line.getPosition();
                        g2d.drawLine(0, y, currentWidth, y);
                    }
                }
                g2d.dispose();
            }
        };


        //lock all gridlines button
        lockAllButton.addActionListener(e -> {
            for (GridLine line : gridLines) {
                line.setLocked(true);
            }
            workspace.repaint();
        });


        workspace.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    // Check if clicking on a grid line
                    for (GridLine line : gridLines) {
                        if (isOverLine(line, e.getPoint())) {
                            showGridLineMenu(line, e.getPoint());
                            break;
                        }
                    }
                } else {
                    // Check for grid line selection
                    for (GridLine line : gridLines) {
                        if (isOverLine(line, e.getPoint()) && !line.isLocked()) {
                            selectedGridLine = line;
                            dragStart = e.getPoint();

                            // Create position label
                            positionLabel = new JLabel();
                            positionLabel.setOpaque(true);
                            positionLabel.setBackground(new Color(255, 255, 225, 200));
                            positionLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                            positionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
                            workspace.add(positionLabel);
                            updatePositionLabel(e.getPoint());
                            break;
                        }
                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
                // Remove position label
                if (positionLabel != null) {
                    workspace.remove(positionLabel);
                    positionLabel = null;
                    workspace.repaint();
                }
                selectedGridLine = null;
                dragStart = null;
            }
        });

        workspace.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (selectedGridLine != null && dragStart != null && !selectedGridLine.isLocked()) {
                    // Existing drag handling
                    int delta = (selectedGridLine.getOrientation() == GridLine.VERTICAL) ?
                            e.getX() - dragStart.x : e.getY() - dragStart.y;
                    selectedGridLine.setPosition(selectedGridLine.getPosition() + delta);
                    dragStart = e.getPoint();

                    // Update position label
                    updatePositionLabel(e.getPoint());
                    workspace.repaint();
                }
            }
        });



        // Add action listener for Apply button
        applySnapButton.addActionListener(e -> {
            try {
                snapRange = Integer.parseInt(snapRangeField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid snap range!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });



        // Add action listeners
        addVerticalLine.addActionListener(e -> {
            gridLines.add(new GridLine(GridLine.VERTICAL, 50));
            workspace.repaint();
        });
        addHorizontalLine.addActionListener(e -> {
            gridLines.add(new GridLine(GridLine.HORIZONTAL, 50));
            workspace.repaint();
        });




        workspace.setBackground(Color.LIGHT_GRAY);
        workspace.setPreferredSize(new Dimension(currentWidth, currentHeight));
        JScrollPane scrollPane = new JScrollPane(workspace);


        // Add main ui panels
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(settingsPanel, BorderLayout.SOUTH);
        frame.add(toolbar, BorderLayout.NORTH);
        frame.add(leftPanel, BorderLayout.WEST);



        // Event listeners
        addButton.addActionListener(e -> setupComponent(new JButton("Button")));
        addCheckbox.addActionListener(e -> setupComponent(new JCheckBox("Checkbox")));
        addTextArea.addActionListener(e -> {
            JTextArea ta = new JTextArea("Text Area");
            ta.setLineWrap(true);
            setupComponent(ta);
        });
        addLabel.addActionListener(e -> setupComponent(new JLabel("Label")));
        addComboBox.addActionListener(e -> {
            JComboBox<String> combo = new JComboBox<>(new String[]{"Item 1", "Item 2"});
            setupComponent(combo);
        });
        addList.addActionListener(e -> {
            DefaultListModel<String> model = new DefaultListModel<>();
            model.addElement("Item 1");
            model.addElement("Item 2");
            JList<String> list = new JList<>(model);
            setupComponent(list);
        });
        addTextField.addActionListener(e -> setupComponent(new JTextField("TextField")));
        addPasswordField.addActionListener(e -> {
            JPasswordField pf = new JPasswordField();
            pf.setEchoChar('*');
            setupComponent(pf);
        });
        addSlider.addActionListener(e -> {
            JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
            slider.setPaintTicks(true);
            slider.setMajorTickSpacing(20);
            setupComponent(slider);
        });
        addTable.addActionListener(e -> {
            JTable table = new JTable(new Object[][]{{"Data1", "Data2"}, {"Data3", "Data4"}}, new String[]{"Column 1", "Column 2"});
            setupComponent(table);
        });
        addSeparator.addActionListener(e -> setupComponent(new JSeparator(SwingConstants.HORIZONTAL)));



        //ui buttons header
        exportButton.addActionListener(e -> exportCode());
        saveButton.addActionListener(e -> saveLayout());
        loadButton.addActionListener(e -> loadLayout());
        resizeButton.addActionListener(e -> resizeWorkspace());

        frame.setVisible(true);
    }



    private void setupComponent(JComponent comp) {
        setupComponent(comp, true);
    }

    private void setupComponent(JComponent comp, boolean applyDefaults) {
        if (applyDefaults) {
            if (comp instanceof JButton) {
                comp.setBounds(50, 50, 100, 25);
            } else if (comp instanceof JCheckBox) {
                comp.setBounds(50, 50, 120, 25);
            } else if (comp instanceof JTextArea) {
                comp.setBounds(50, 50, 200, 100);
            } else if (comp instanceof JLabel) {
                comp.setBounds(50, 50, 100, 25);
            } else if (comp instanceof JComboBox) {
                comp.setBounds(50, 50, 120, 25);
            } else if (comp instanceof JList) {
                comp.setBounds(50, 50, 150, 100);
            } else if (comp instanceof JTextField) {
                comp.setBounds(50, 50, 120, 25);
            } else if (comp instanceof JPasswordField) {
                comp.setBounds(50, 50, 120, 25);
            } else if (comp instanceof JSlider) {
                comp.setBounds(50, 50, 200, 50);
            } else if (comp instanceof JTable) {
                comp.setBounds(50, 50, 300, 150);
            } else if (comp instanceof JSeparator) {
                comp.setBounds(50, 50, 100, 2);
            }
        }

        addDragFunctionality(comp);
        addResizeHandles(comp);
        addRightClickMenu(comp);
        workspace.add(comp);
        components.add(comp);
        workspace.repaint();
    }




    private void resizeWorkspace() {
        try {
            currentTitle = titleField.getText();
            currentWidth = Integer.parseInt(widthField.getText());
            currentHeight = Integer.parseInt(heightField.getText());
            workspace.setPreferredSize(new Dimension(currentWidth, currentHeight));
            workspace.revalidate();
            workspace.repaint();  // Force redraw of stencil
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Invalid width or height value", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private int snapEdgeToGrid(int edgePosition, int orientation) {
        int closest = edgePosition;
        int minDelta = snapRange + 1;
        for (GridLine line : gridLines) {
            if (line.getOrientation() == orientation) {
                int delta = Math.abs(edgePosition - line.getPosition());
                if (delta < minDelta) {
                    minDelta = delta;
                    closest = line.getPosition();
                }
            }
        }
        return (minDelta <= snapRange) ? closest : edgePosition;
    }

    private int snapComponentX(JComponent component, int newX) {
        int bestX = newX;
        int minDelta = snapRange + 1;
        int leftEdge = newX;
        int rightEdge = newX + component.getWidth();

        for (GridLine line : gridLines) {
            if (line.getOrientation() == GridLine.VERTICAL) {
                int lineX = line.getPosition();
                int deltaLeft = Math.abs(leftEdge - lineX);
                int deltaRight = Math.abs(rightEdge - lineX);

                if (deltaLeft <= snapRange && deltaLeft < minDelta) {
                    minDelta = deltaLeft;
                    bestX = lineX;
                }
                if (deltaRight <= snapRange && deltaRight < minDelta) {
                    minDelta = deltaRight;
                    bestX = lineX - component.getWidth();
                }
            }
        }
        return bestX;
    }

    private int snapComponentY(JComponent component, int newY) {
        int bestY = newY;
        int minDelta = snapRange + 1;
        int topEdge = newY;
        int bottomEdge = newY + component.getHeight();

        for (GridLine line : gridLines) {
            if (line.getOrientation() == GridLine.HORIZONTAL) {
                int lineY = line.getPosition();
                int deltaTop = Math.abs(topEdge - lineY);
                int deltaBottom = Math.abs(bottomEdge - lineY);

                if (deltaTop <= snapRange && deltaTop < minDelta) {
                    minDelta = deltaTop;
                    bestY = lineY;
                }
                if (deltaBottom <= snapRange && deltaBottom < minDelta) {
                    minDelta = deltaBottom;
                    bestY = lineY - component.getHeight();
                }
            }
        }
        return bestY;
    }


    private void addDragFunctionality(JComponent component) {
        component.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                component.putClientProperty("dragOrigin", e.getPoint());
            }
        });

        component.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point start = (Point) component.getClientProperty("dragOrigin");
                if (start != null) {
                    int newX = component.getX() + e.getX() - start.x;
                    int newY = component.getY() + e.getY() - start.y;

                    // Enhanced snapping for all edges
                    newX = snapComponentX(component, newX);
                    newY = snapComponentY(component, newY);

                    component.setLocation(newX, newY);

                    JPanel resizeHandle = (JPanel) component.getClientProperty("resizeHandle");
                    if (resizeHandle != null) {
                        resizeHandle.setBounds(
                                component.getX() + component.getWidth() - 5,
                                component.getY() + component.getHeight() - 5,
                                10, 10
                        );
                    }
                    workspace.repaint();
                }
            }
        });
    }



    private int snapToGrid(int value, int orientation) {
        for (GridLine line : gridLines) {
            if (line.getOrientation() == orientation && Math.abs(value - line.getPosition()) <= snapRange) {
                return line.getPosition();
            }
        }
        return value;
    }

    private void addResizeHandles(JComponent component) {
        JPanel resizeHandle = new JPanel();
        resizeHandle.setSize(10, 10);
        resizeHandle.setBackground(Color.RED);
        resizeHandle.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));

        workspace.add(resizeHandle);
        resizeHandle.setBounds(component.getX() + component.getWidth() - 5, component.getY() + component.getHeight() - 5, 10, 10);

        component.putClientProperty("resizeHandle", resizeHandle);

        resizeHandle.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                resizeHandle.putClientProperty("dragOrigin", e.getPoint());
            }
        });

        resizeHandle.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point start = (Point) resizeHandle.getClientProperty("dragOrigin");
                if (start != null) {
                    int dx = e.getX() - start.x;
                    int dy = e.getY() - start.y;

                    int newWidth = component.getWidth() + dx;
                    int newHeight = component.getHeight() + dy;

                    // Snap right edge
                    int rightEdge = component.getX() + newWidth;
                    int snappedRight = snapEdgeToGrid(rightEdge, GridLine.VERTICAL);
                    newWidth = snappedRight - component.getX();

                    // Snap bottom edge
                    int bottomEdge = component.getY() + newHeight;
                    int snappedBottom = snapEdgeToGrid(bottomEdge, GridLine.HORIZONTAL);
                    newHeight = snappedBottom - component.getY();

                    component.setSize(Math.max(1, newWidth), Math.max(1, newHeight));
                    resizeHandle.setBounds(
                            component.getX() + component.getWidth() - 5,
                            component.getY() + component.getHeight() - 5,
                            10, 10
                    );
                    component.repaint();
                    workspace.repaint();
                }
            }
        });
    }

    private void addRightClickMenu(JComponent component) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem duplicate = new JMenuItem("Duplicate");
        JMenuItem delete = new JMenuItem("Delete");

        duplicate.addActionListener(e -> duplicateComponent(component));
        delete.addActionListener(e -> deleteComponent(component));

        menu.add(duplicate);
        menu.add(delete);

        component.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    menu.show(component, e.getX(), e.getY());
                }
            }
        });
    }


    private void duplicateComponent(JComponent original) {
        JComponent duplicate;
        if (original instanceof JButton) {
            duplicate = new JButton(((JButton) original).getText());
        } else if (original instanceof JCheckBox) {
            duplicate = new JCheckBox(((JCheckBox) original).getText());
        } else if (original instanceof JTextArea) {
            JTextArea ta = new JTextArea(((JTextArea) original).getText());
            ta.setLineWrap(((JTextArea) original).getLineWrap());
            duplicate = ta;
        } else if (original instanceof JLabel) {
            duplicate = new JLabel(((JLabel) original).getText());
        } else if (original instanceof JComboBox) {
            JComboBox<?> originalCombo = (JComboBox<?>) original;
            JComboBox<Object> combo = new JComboBox<>();
            for (int i = 0; i < originalCombo.getItemCount(); i++) {
                combo.addItem(originalCombo.getItemAt(i));
            }
            duplicate = combo;
        } else if (original instanceof JList) {
            JList<?> originalList = (JList<?>) original;
            DefaultListModel<Object> model = new DefaultListModel<>();
            ListModel<?> originalModel = originalList.getModel();
            for (int i = 0; i < originalModel.getSize(); i++) {
                model.addElement(originalModel.getElementAt(i));
            }
            duplicate = new JList<>(model);
        } else if (original instanceof JTextField) {
            duplicate = new JTextField(((JTextField) original).getText());
        } else if (original instanceof JPasswordField) {
            JPasswordField pf = new JPasswordField();
            pf.setText(new String(((JPasswordField) original).getPassword()));
            pf.setEchoChar(((JPasswordField) original).getEchoChar());
            duplicate = pf;
        } else if (original instanceof JSlider) {
            JSlider originalSlider = (JSlider) original;
            JSlider slider = new JSlider(originalSlider.getMinimum(), originalSlider.getMaximum(), originalSlider.getValue());
            slider.setOrientation(originalSlider.getOrientation());
            slider.setPaintTicks(originalSlider.getPaintTicks());
            slider.setMajorTickSpacing(originalSlider.getMajorTickSpacing());
            duplicate = slider;
        } else if (original instanceof JTable) {
            JTable originalTable = (JTable) original;
            TableModel originalModel = originalTable.getModel();
            DefaultTableModel model = new DefaultTableModel();
            for (int i = 0; i < originalModel.getColumnCount(); i++) {
                model.addColumn(originalModel.getColumnName(i));
            }
            for (int i = 0; i < originalModel.getRowCount(); i++) {
                Object[] row = new Object[originalModel.getColumnCount()];
                for (int j = 0; j < originalModel.getColumnCount(); j++) {
                    row[j] = originalModel.getValueAt(i, j);
                }
                model.addRow(row);
            }
            duplicate = new JTable(model);
        } else if (original instanceof JSeparator) {
            duplicate = new JSeparator(((JSeparator) original).getOrientation());
        } else {
            return;
        }
        duplicate.setBounds(original.getX() + 10, original.getY() + 10, original.getWidth(), original.getHeight());
        setupComponent(duplicate, false); // Pass false to avoid applying default bounds
        workspace.repaint();
    }



    private void deleteComponent(JComponent component) {
        JPanel resizeHandle = (JPanel) component.getClientProperty("resizeHandle");
        if (resizeHandle != null) {
            workspace.remove(resizeHandle);
        }
        workspace.remove(component);
        components.remove(component);
        workspace.repaint();
    }


    //This is for line coordinate view
    private void updatePositionLabel(Point mousePos) {
        if (positionLabel == null || selectedGridLine == null) return;

        int pos = selectedGridLine.getPosition();
        if (selectedGridLine.getOrientation() == GridLine.VERTICAL) {
            positionLabel.setText(" X: " + pos + " ");
            positionLabel.setBounds(
                    pos + 10,
                    mousePos.y - 15,
                    positionLabel.getPreferredSize().width,
                    20
            );
        } else {
            positionLabel.setText(" Y: " + pos + " ");
            positionLabel.setBounds(
                    mousePos.x + 10,
                    pos - 10,
                    positionLabel.getPreferredSize().width,
                    20
            );
        }
    }


    private void exportCode() {
        StringBuilder code = new StringBuilder("import javax.swing.*;\nimport java.awt.*;\nimport javax.swing.table.*;\n\npublic class GeneratedUI extends JFrame {\n");
        code.append("    public GeneratedUI() {\n        setTitle(\"")
                .append(escapeString(currentTitle)).append("\");\n        setSize(")
                .append(currentWidth).append(", ").append(currentHeight)
                .append(");\n        setLayout(null);\n        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);\n");

        for (JComponent comp : components) {
            String type = comp.getClass().getSimpleName();
            String varName = type.toLowerCase() + components.indexOf(comp);
            code.append("        ").append(type).append(" ").append(varName).append(" = new ").append(type).append("();\n");

            if (comp instanceof JComboBox) {
                JComboBox<?> combo = (JComboBox<?>) comp;
                for (int i = 0; i < combo.getItemCount(); i++) {
                    code.append("        ").append(varName).append(".addItem(\"")
                            .append(escapeString(combo.getItemAt(i).toString())).append("\");\n");
                }
            } else if (comp instanceof JList) {
                JList<?> list = (JList<?>) comp;
                ListModel<?> model = list.getModel();
                code.append("        DefaultListModel<Object> ").append(varName).append("Model = new DefaultListModel<>();\n");
                for (int i = 0; i < model.getSize(); i++) {
                    code.append("        ").append(varName).append("Model.addElement(\"")
                            .append(escapeString(model.getElementAt(i).toString())).append("\");\n");
                }
                code.append("        ").append(varName).append(".setModel(").append(varName).append("Model);\n");
            } else if (comp instanceof JTable) {
                JTable table = (JTable) comp;
                TableModel model = table.getModel();
                code.append("        DefaultTableModel ").append(varName).append("Model = new DefaultTableModel();\n");
                for (int i = 0; i < model.getColumnCount(); i++) {
                    code.append("        ").append(varName).append("Model.addColumn(\"")
                            .append(escapeString(model.getColumnName(i))).append("\");\n");
                }
                for (int i = 0; i < model.getRowCount(); i++) {
                    code.append("        ").append(varName).append("Model.addRow(new Object[]{");
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object value = model.getValueAt(i, j);
                        code.append("\"").append(escapeString(value.toString())).append("\"");
                        if (j < model.getColumnCount() - 1) code.append(", ");
                    }
                    code.append("});\n");
                }
                code.append("        ").append(varName).append(".setModel(").append(varName).append("Model);\n");
            } else if (comp instanceof JSlider) {
                JSlider slider = (JSlider) comp;
                code.append("        ").append(varName).append(".setMinimum(").append(slider.getMinimum()).append(");\n");
                code.append("        ").append(varName).append(".setMaximum(").append(slider.getMaximum()).append(");\n");
                code.append("        ").append(varName).append(".setValue(").append(slider.getValue()).append(");\n");
                code.append("        ").append(varName).append(".setPaintTicks(").append(slider.getPaintTicks()).append(");\n");
                code.append("        ").append(varName).append(".setMajorTickSpacing(").append(slider.getMajorTickSpacing()).append(");\n");
            } else if (comp instanceof JSeparator) {
                JSeparator sep = (JSeparator) comp;
                code.append("        ").append(varName).append(".setOrientation(SwingConstants.")
                        .append(sep.getOrientation() == SwingConstants.HORIZONTAL ? "HORIZONTAL" : "VERTICAL").append(");\n");
            } else {
                String text = getComponentText(comp);
                if (!text.isEmpty()) {
                    code.append("        ").append(varName).append(".setText(\"")
                            .append(escapeString(text)).append("\");\n");
                }
            }

            code.append("        ").append(varName).append(".setBounds(")
                    .append(comp.getX()).append(", ").append(comp.getY()).append(", ")
                    .append(comp.getWidth()).append(", ").append(comp.getHeight()).append(");\n");

            if (comp instanceof JTextArea) {
                code.append("        ").append(varName).append(".setLineWrap(true);\n");
            } else if (comp instanceof JPasswordField) {
                code.append("        ").append(varName).append(".setEchoChar('*');\n");
            }

            code.append("        add(").append(varName).append(");\n");
        }

        code.append("        setVisible(true);\n    }\n\n    public static void main(String[] args) {\n        new GeneratedUI();\n    }\n}");

        try (FileWriter writer = new FileWriter("GeneratedUI.java")) {
            writer.write(code.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String escapeString(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    private String getComponentText(JComponent comp) {
        if (comp instanceof JButton) return ((JButton) comp).getText();
        if (comp instanceof JCheckBox) return ((JCheckBox) comp).getText();
        if (comp instanceof JTextArea) return ((JTextArea) comp).getText();
        return "";
    }



    private void saveLayout() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("layout.dat"))) {
            out.writeObject(components);
            out.writeObject(currentTitle);
            out.writeInt(currentWidth);
            out.writeInt(currentHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLayout() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("layout.dat"))) {
            components = (List<JComponent>) in.readObject();
            currentTitle = (String) in.readObject();
            currentWidth = in.readInt();
            currentHeight = in.readInt();

            titleField.setText(currentTitle);
            widthField.setText(String.valueOf(currentWidth));
            heightField.setText(String.valueOf(currentHeight));
            workspace.setPreferredSize(new Dimension(currentWidth, currentHeight));

            workspace.removeAll();
            for (JComponent comp : components) {
                workspace.add(comp);
                addDragFunctionality(comp); // Reattach drag
                addResizeHandles(comp);     // Recreate handles
                addRightClickMenu(comp);    // Reattach menu
            }
            workspace.revalidate();
            workspace.repaint();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }







    class GridLine {
        public static final int VERTICAL = 0;
        public static final int HORIZONTAL = 1;

        private int orientation;
        private int position;
        private boolean locked;

        public GridLine(int orientation, int position) {
            this.orientation = orientation;
            this.position = position;
            this.locked = false;
        }

        // Getters and Setters
        public int getOrientation() { return orientation; }
        public int getPosition() { return position; }
        public boolean isLocked() { return locked; }
        public void setPosition(int pos) { position = pos; }
        public void setLocked(boolean locked) { this.locked = locked; }
    }



    private boolean isOverLine(GridLine line, Point p) {
        int tolerance = 5;
        if (line.getOrientation() == GridLine.VERTICAL) {
            return Math.abs(p.x - line.getPosition()) <= tolerance;
        } else {
            return Math.abs(p.y - line.getPosition()) <= tolerance;
        }
    }

    private void showGridLineMenu(GridLine line, Point p) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem lockItem = new JMenuItem(line.isLocked() ? "Unlock" : "Lock");
        lockItem.addActionListener(evt -> {
            line.setLocked(!line.isLocked());
            workspace.repaint();
        });
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(evt -> {
            gridLines.remove(line);
            workspace.repaint();
        });
        menu.add(lockItem);
        menu.add(deleteItem);
        menu.show(workspace, p.x, p.y);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SwingUIBuilder::new);
    }
}
