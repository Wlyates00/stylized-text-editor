/**
 * FileOperations.java
 *
 * This class handles file-related operations for a JTextPane, including opening,
 * saving, and managing RTF and text files. It also provides undo and redo
 * functionality for text editing.
 *
 * Features:
 * - Open and save RTF (.rtf) and text (.txt) files
 * - Undo and redo text changes
 * - Key bindings for Ctrl+Z (Undo) and Ctrl+Shift+Z (Redo)
 *
 * Author: Layton Yates
 * Date: 02/4/2025
 */

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.io.*;

public class FileOperations {
    private final JTextPane textPane;
    private File currentFile; // Store the current file
    private final UndoManager undoManager;

    /**
     * Constructor that initializes the file operations for a JTextPane.
     *
     * @param textPane The JTextPane to associate with file operations.
     */
    public FileOperations(JTextPane textPane) {
        this.textPane = textPane;
        this.undoManager = new UndoManager();

        // Attach the undo manager to the document
        textPane.getDocument().addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
            }
        });

        // Add key bindings for Undo and Redo
        textPane.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "undo");
        textPane.getActionMap().put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }
        });

        textPane.getInputMap().put(KeyStroke.getKeyStroke("control shift Z"), "redo");
        textPane.getActionMap().put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
            }
        });
    }

    /**
     * Creates a new blank file by clearing the text pane.
     */
    public void newFile() {
        textPane.setText(""); // Clear the text pane
        currentFile = null;
    }

    /**
     * Opens a file using a file chooser and loads its content into the text pane.
     *
     * @param parent The parent component for file chooser dialog.
     */
    public void openFile(Component parent) {
        textPane.setText(""); // Clear the text pane
        currentFile = null;

        JFileChooser fileChooser = new JFileChooser();

        int option = fileChooser.showOpenDialog(parent);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            currentFile = file;
            try (FileInputStream fis = new FileInputStream(file)) {
                if (file.getName().endsWith(".rtf")) {
                    RTFEditorKit rtfKit = new RTFEditorKit();
                    rtfKit.read(fis, textPane.getDocument(), 0); // Read RTF content

                    // Manually apply highlight color after reading
                    applyHighlights(textPane.getStyledDocument());
                } else {
                    byte[] buffer = new byte[(int) file.length()];
                    fis.read(buffer);
                    textPane.setText(new String(buffer)); // Load text into the text pane
                }
            } catch (IOException | BadLocationException e) {
                JOptionPane.showMessageDialog(parent, "Failed to open file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Saves the current file. If no file is associated, it prompts the user to save as a new file.
     *
     * @param parent The parent component for file chooser dialog.
     */
    public void saveFile(Component parent) {
        if (currentFile != null) {
            saveFile(currentFile, parent);
        } else {
            saveFileAs(parent);
        }
    }

    /**
     * Opens a "Save As" dialog and allows the user to save the file under a new name.
     *
     * @param parent The parent component for file chooser dialog.
     */
    public void saveFileAs(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".rtf or .txt Files", "rtf", "txt");
        fileChooser.setFileFilter(filter);

        int option = fileChooser.showSaveDialog(parent);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String fileName = file.getName();
            if (!fileName.contains(".")) {
                file = new File(file.getAbsolutePath() + ".rtf");
            }
            currentFile = file;
            saveFile(file, parent);
        }
    }

    /**
     * Saves the text content to a specified file.
     *
     * @param file   The file to save the content to.
     * @param parent The parent component for error dialogs.
     */
    private void saveFile(File file, Component parent) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            if (file.getName().endsWith(".rtf")) {
                RTFEditorKit rtfKit = new RTFEditorKit();
                rtfKit.write(fos, textPane.getDocument(), 0, textPane.getDocument().getLength());
            } else if (file.getName().endsWith(".txt")) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
                writer.write(textPane.getText());
                writer.close();
            }
        } catch (IOException | BadLocationException e) {
            JOptionPane.showMessageDialog(parent, "Failed to save file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Manually apply the highlight color after loading RTF content.
     * 
     * @param doc the current styled document
     */
    private void applyHighlights(StyledDocument doc) {
        for (int i = 0; i < doc.getLength(); i++) {
            AttributeSet attributes = doc.getCharacterElement(i).getAttributes();
            Color backgroundColor = (Color) attributes.getAttribute(StyleConstants.Background);

            if (backgroundColor != null && !backgroundColor.equals(Color.BLACK)) {
                // If the highlight is not black, change it to your desired color
                MutableAttributeSet newAttributes = new SimpleAttributeSet();
                StyleConstants.setBackground(newAttributes, backgroundColor);
                doc.setCharacterAttributes(i, 1, newAttributes, false);
            } else {
                // If it's black, you can reset it or change it to another color (like yellow)
                MutableAttributeSet newAttributes = new SimpleAttributeSet();
                StyleConstants.setBackground(newAttributes, Color.WHITE); // Change this to your desired color
                doc.setCharacterAttributes(i, 1, newAttributes, false);
            }
        }
    }
}