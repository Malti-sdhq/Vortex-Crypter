package rip.marie.util;

import com.formdev.flatlaf.FlatDarkLaf;
import rip.marie.Packer;
import rip.marie.guis.packer.PackerFrame;
import rip.marie.guis.packer.PackerPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

public class PackerUtil {
    private static File inputFile;
    private static JLabel statusLabel;
    private static JLabel fileLabel;
    private static Point mouseOffset;

    public static void setupFrame(PackerFrame frame) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        frame.setUndecorated(true);
        frame.setBackground(new Color(0, 0, 0, 0));
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new PackerPanel());
        frame.setVisible(true);
    }

    public static void setupPanel(PackerPanel panel) {
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(28, 28, 36));

        // Custom Title Bar
        JPanel titleBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(40, 40, 50));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        titleBar.setPreferredSize(new Dimension(100, 40));

        // Title
        JLabel title = new JLabel("  Vortex Crypter ~ Free");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(Color.WHITE);

        // Close Button
        JButton closeBtn = new JButton("×");
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.addActionListener(e -> System.exit(0));

        // Dragging
        titleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseOffset = e.getPoint();
            }
        });
        titleBar.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point curr = e.getLocationOnScreen();
                PackerFrame frame = (PackerFrame) SwingUtilities.getWindowAncestor(titleBar);
                frame.setLocation(curr.x - mouseOffset.x, curr.y - mouseOffset.y);
            }
        });

        titleBar.add(title, BorderLayout.WEST);
        titleBar.add(closeBtn, BorderLayout.EAST);

        // Main Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(30, 40, 40, 40));
        content.setBackground(new Color(28, 28, 36));

        // Drop Zone
        JPanel dropZone = new JPanel();
        dropZone.setLayout(new BoxLayout(dropZone, BoxLayout.Y_AXIS));
        dropZone.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 80), 2),
                BorderFactory.createEmptyBorder(40, 20, 40, 20)
        ));
        dropZone.setBackground(new Color(35, 35, 45));
        dropZone.setAlignmentX(Component.CENTER_ALIGNMENT);
        dropZone.setMaximumSize(new Dimension(Short.MAX_VALUE, 200));

        JLabel dropLabel = new JLabel("DROP JAR FILE HERE");
        dropLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dropLabel.setForeground(new Color(220, 220, 240));
        dropLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel browseLabel = new JLabel("or click to browse");
        browseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        browseLabel.setForeground(new Color(160, 160, 180));
        browseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        dropZone.add(dropLabel);
        dropZone.add(Box.createRigidArea(new Dimension(0, 5)));
        dropZone.add(browseLabel);

        // File Path Label
        fileLabel = new JLabel(" ");
        fileLabel.setForeground(new Color(150, 220, 150));
        fileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Process Button
        JButton processBtn = new JButton("PROCESS JAR");
        processBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        processBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        processBtn.setForeground(Color.WHITE);
        processBtn.setBackground(new Color(0, 140, 200));
        processBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        processBtn.setFocusPainted(false);
        processBtn.setEnabled(false);
        processBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        processBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                processBtn.setBackground(new Color(0, 160, 220));
            }
            public void mouseExited(MouseEvent e) {
                processBtn.setBackground(new Color(0, 140, 200));
            }
        });

        // Status Label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(200, 200, 220));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components
        content.add(dropZone);
        content.add(Box.createRigidArea(new Dimension(0, 15)));
        content.add(fileLabel);
        content.add(Box.createRigidArea(new Dimension(0, 15)));
        content.add(processBtn);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(statusLabel);

        // Drag and Drop functionality - Fixed implementation
        dropZone.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    List<File> files = (List<File>) support.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);
                    if (!files.isEmpty() && files.get(0).getName().endsWith(".jar")) {
                        inputFile = files.get(0);
                        fileLabel.setText(inputFile.getName());
                        processBtn.setEnabled(true);
                        return true;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        });

        // File Chooser
        dropZone.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    public boolean accept(File f) {
                        return f.getName().toLowerCase().endsWith(".jar") || f.isDirectory();
                    }
                    public String getDescription() {
                        return "JAR Files (*.jar)";
                    }
                });

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    inputFile = chooser.getSelectedFile();
                    fileLabel.setText(inputFile.getName());
                    processBtn.setEnabled(true);
                }
            }
        });

        // Process Action
        processBtn.addActionListener(e -> {
            if (inputFile == null) return;

            processBtn.setEnabled(false);
            statusLabel.setText("Processing...");
            statusLabel.setForeground(new Color(200, 200, 220));

            new Thread(() -> {
                try {
                    String outputPath = Packer.pack(inputFile);
                    SwingUtilities.invokeLater(() -> {
                        if (outputPath != null) {
                            statusLabel.setText("Saved to: " + outputPath);
                            statusLabel.setForeground(new Color(100, 220, 100)); // Green color
                        } else {
                            statusLabel.setText("Processing failed");
                            statusLabel.setForeground(new Color(220, 100, 100));
                        }
                        processBtn.setEnabled(true);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Error: " + ex.getMessage());
                        statusLabel.setForeground(new Color(220, 100, 100));
                        processBtn.setEnabled(true);
                    });
                }
            }).start();
        });

        panel.add(titleBar, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
    }
}