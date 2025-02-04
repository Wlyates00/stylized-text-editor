/**
 * TextEditor.java
 *
 * A simple styled text editor that allows users to create, open, edit, and save
 * text files with rich text formatting. It provides basic text styling options
 * such as bold, underline, bullet points, font selection, and symbol insertion.
 *
 * Features:
 * - File operations (New, Open, Save, Save As)
 * - Rich text formatting (Bold, Underline, Bullets)
 * - Font customization (Family, Size)
 * - Symbol insertion (Math, Greek, Arrows, Weather, Chess, Miscellaneous)
 * - Color selection for text and highlighting
 *
 * This class extends JFrame and implements ActionListener to handle UI interactions.
 * It integrates FileOperations for file management and StylingOperations for text styling.
 *
 * Author: Layton Yates
 * Date: 02/4/2025
 */

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

// Main class for the text editor, extending JFrame and implementing ActionListener for button events
public class TextEditor extends JFrame implements ActionListener {
    // Components for the text editor
    private final JTextPane textPane; // Text area for editing styled text
    private final FileOperations fileOperations; // Handles file operations like open and save
    private final StylingOperations stylingOperations; // Handles text styling like bold, underline, and bullets


    /**
     * Constructing the GUI and adding listeners.
     */
    public TextEditor() {
        // Set up the JFrame (main window)
        setTitle("Style-able Text Editor"); // Window title
        setSize(800, 600); // Window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the application when the window is closed

        ImageIcon icon = new ImageIcon(getClass().getResource("ico.png")); // Replace with your icon path
        setIconImage(icon.getImage());

        // Create a JTextPane for styled text editing
        textPane = new JTextPane();
        // Initialize FileOperations and StylingOperations
        fileOperations = new FileOperations(textPane); // Handles file operations
        stylingOperations = new StylingOperations(textPane); // Handles text styling
        // ScrollPane for the text area
        JScrollPane scrollPane = new JScrollPane(textPane); // Add scroll bars to the text pane
        add(scrollPane, BorderLayout.CENTER); // Add the scroll pane to the center of the window

        // Create a toolbar for styling buttons
        JToolBar toolBar = new JToolBar();
        add(toolBar, BorderLayout.NORTH); // Add the toolbar to the top of the window

        // Bold, Underline, ... buttons
        {
            // Add Bold button to the toolbar
            // Button to toggle bold style
            JButton boldButton = new JButton("Bold");
            boldButton.addActionListener(this); // Register this class as the action listener
            toolBar.add(boldButton);

            // Add Underline button to the toolbar
            // Button to toggle underline style
            JButton underlineButton = new JButton("Underline");
            underlineButton.addActionListener(this); // Register this class as the action listener
            toolBar.add(underlineButton);

            // Add Bullet button to the toolbar
            // Button to toggle bullet points
            JButton bulletButton = new JButton("Bullet");
            bulletButton.addActionListener(this); // Register this class as the action listener
            toolBar.add(bulletButton);
        }

        // Create a menu bar for file and font options
        JMenuBar menuBar = new JMenuBar();

        // File Operation buttons
        {
            setJMenuBar(menuBar); // Add the menu bar to the window

            // Create a "File" menu
            JMenu fileMenu = new JMenu("File");
            menuBar.add(fileMenu); // Add the File menu to the menu bar

            // Add "Open" menu item to the File menu
            JMenuItem newMenuItem = new JMenuItem("New File");
            newMenuItem.addActionListener(this); // Register this class as the action listener
            fileMenu.add(newMenuItem);

            // Add "Open" menu item to the File menu
            JMenuItem openMenuItem = new JMenuItem("Open");
            openMenuItem.addActionListener(this); // Register this class as the action listener
            fileMenu.add(openMenuItem);

            // Add "Save" menu item to the File menu
            JMenuItem saveMenuItem = new JMenuItem("Save");
            saveMenuItem.addActionListener(this); // Register this class as the action listener
            fileMenu.add(saveMenuItem);

            // Add "Save" menu item to the File menu
            JMenuItem saveAsMenuItem = new JMenuItem("Save As");
            saveAsMenuItem.addActionListener(this); // Register this class as the action listener
            fileMenu.add(saveAsMenuItem);

            // Add "Exit" menu item to the File menu
            JMenuItem exitMenuItem = new JMenuItem("Exit");
            exitMenuItem.addActionListener(this); // Register this class as the action listener
            fileMenu.add(exitMenuItem);
        }

        // Create a "Font" menu
        JMenu fontMenu = new JMenu("Font");

        // Font Style Operations
        {
            menuBar.add(fontMenu); // Add the Font menu to the menu bar

            // Add Font Family submenu to the Font menu
            JMenu fontFamilyMenu = new JMenu("Font Family");
            fontMenu.add(fontFamilyMenu);

            // Add common font families to the Font Family submenu
            String[] fontFamilies = {"Arial", "Times New Roman", "Courier New", "Verdana"};
            for (String fontFamily : fontFamilies) {
                JMenuItem fontItem = new JMenuItem(fontFamily);
                fontItem.addActionListener(e -> stylingOperations.applyFontFamily(fontFamily)); // Apply the selected font family
                fontFamilyMenu.add(fontItem);
            }

            // Add Font Size submenu to the Font menu
            JMenu fontSizeMenu = new JMenu("Font Size");
            fontMenu.add(fontSizeMenu);

            // Add common font sizes to the Font Size submenu
            int[] fontSizes = {8, 12, 16, 20, 24, 28, 32, 36, 44, 48};
            for (int fontSize : fontSizes) {
                JMenuItem sizeItem = new JMenuItem(String.valueOf(fontSize));
                sizeItem.addActionListener(e -> stylingOperations.applyFontSize(fontSize)); // Apply the selected font size
                fontSizeMenu.add(sizeItem);
            }
        }

        // Create a "Symbols" menu
        JMenu symbolsMenu = new JMenu("Symbols");
        menuBar.add(symbolsMenu); // Add the Symbols menu to the menu bar

        // Create submenus for different symbol categories
        JMenu mathSymbolsMenu = new JMenu("Math Symbols");
        JMenu greekLettersMenu = new JMenu("Greek Letters");
        JMenu arrowsMenu = new JMenu("Arrows");
        JMenu weatherMenu = new JMenu("Weather");
        JMenu chessMenu = new JMenu("Chess");
        JMenu miscMenu = new JMenu("Misc.");



        // Add submenus to the Symbols menu
        symbolsMenu.add(mathSymbolsMenu);
        symbolsMenu.add(greekLettersMenu);
        symbolsMenu.add(arrowsMenu);
        symbolsMenu.add(weatherMenu);
        symbolsMenu.add(chessMenu);
        symbolsMenu.add(miscMenu);


        // Define and add Math Symbols
        String[] mathSymbols = {"≥", "≤", "≠", "∑", "π", "Θ", "∀", "Ǝ", "∞", "∫", "≈", "∈", "⊆", "∪", "∩", "∅"};
        for (String symbol : mathSymbols) {
            JMenuItem item = new JMenuItem(symbol);
            item.addActionListener(e -> insertSymbol(symbol));
            mathSymbolsMenu.add(item);
        }

        // Define and add Greek Letters
        // Array of Greek symbols (excluding π)
        String[] greekSymbols = {"α", "β", "γ", "δ", "ε", "ζ", "η", "θ",
                "ι", "κ", "λ", "μ", "ν", "ξ", "ο", "ρ",
                "σ", "τ", "υ", "φ", "χ", "ψ", "ω"};
        for (String letter : greekSymbols) {
            JMenuItem item = new JMenuItem(letter);
            item.addActionListener(e -> insertSymbol(letter));
            greekLettersMenu.add(item);
        }

        // Define and add Arrows
        String[] arrows = {"←", "→", "↑", "↓", "↔", "↕"};
        for (String arrow : arrows) {
            JMenuItem item = new JMenuItem(arrow);
            item.addActionListener(e -> insertSymbol(arrow));
            arrowsMenu.add(item);
        }

        String[] weatherSymbols = {"☼", "☀", "☁", "☂", "☃", "☄", "☾", "☽", "❄", "☇", "☈", "⊙", "☉", "℃", "℉", "°", "-"};
        for (String symbol : weatherSymbols) {
            JMenuItem item = new JMenuItem(symbol);
            item.addActionListener(e -> insertSymbol(symbol));
            weatherMenu.add(item);
        }

        // Chess symbols submenu
        String[] chessSymbols = {"♚", "♛", "♜", "♝", "♞", "♟", "♔", "♕", "♖", "♗", "♘", "♙", "-"};
        for (String symbol : chessSymbols) {
            JMenuItem item = new JMenuItem(symbol);
            item.addActionListener(e -> insertSymbol(symbol));
            chessMenu.add(item);
        }

        // Hearts symbols submenu
        String[] heartsSymbols = {"♥", "♡", "★", "☆", "✡", "✦", "✧", "⌑", "✩", "✪", "⍟", "❂",
                "✫", "✬", "✭", "✮", "✯", "✰", "☪", "⚝", "⛤", "⛥",
                "⛦", "⛧",};
        for (String symbol : heartsSymbols) {
            JMenuItem item = new JMenuItem(symbol);
            item.addActionListener(e -> insertSymbol(symbol));
            miscMenu.add(item);
        }

        // Create a toolbar for styling buttons
        JToolBar colorSwatches = new JToolBar();
        add(colorSwatches, BorderLayout.SOUTH); // Add the toolbar to the top of the window

        // Create a panel for color swatches
        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new FlowLayout());

        // Add color swatches as buttons
        for (Color color : stylingOperations.colors) {
            JButton colorButton = createColorButton(color);
            colorPanel.add(colorButton);
        }

        add(colorPanel, BorderLayout.SOUTH); // Add color toolbar at the bottom

        // Add a KeyListener to reset the caret style to default when typing
        textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) == 0) {
                    stylingOperations.resetCaretStyle(); // Reset the caret style to default
                }
            }
        });

        // Make the window visible
        setVisible(true);
    }

    /**
     * Performs an operation depending on the command.
     *
     * @param e The event that occurred (E.g. JMenuItems).
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand(); // Get the action command (e.g., "Open", "Save", "Bold")
        switch (command) {
            case "New File":
                fileOperations.newFile(); // Open a file
                break;
            case "Open":
                fileOperations.openFile(this); // Open a file
                break;
            case "Save":
                fileOperations.saveFile(this); // Save the current file
                break;
            case "Save As":
                fileOperations.saveFileAs(this); // Save the current file
                break;
            case "Exit":
                System.exit(0); // Exit the application
                break;
            case "Bold":
                stylingOperations.toggleStyle(StyleConstants.Bold); // Toggle bold style
                break;
            case "Underline":
                stylingOperations.toggleStyle(StyleConstants.Underline); // Toggle underline style
                break;
            case "Bullet":
                stylingOperations.toggleBulletPoints(); // Toggle bullet points
                break;
        }
    }

    /**
     * Inserts a symbol at the current caret position in the text pane.
     *
     * @param symbol The symbol to insert.
     */
    private void insertSymbol(String symbol) {
        textPane.replaceSelection(symbol); // Insert the selected symbol at the caret position
    }

    /**
     * Creates a color button that sets text color on left-click and highlights on right-click.
     *
     * @param color The color value to set to a swatch
     */
    private JButton createColorButton(Color color) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(15, 15)); // Square buttons
        button.setBackground(color);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Mouse listener for left-click (text color) and right-click (highlight)
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    stylingOperations.changeTextColor(color);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    stylingOperations.highlightText(color);
                }
            }
        });

        return button;
    }

    // Main method to start the text editor
    public static void main(String[] args) {
        new TextEditor();
    }
}
