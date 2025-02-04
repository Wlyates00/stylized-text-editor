import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class StylingOperations {
    // Define an array of colors for the swatches
    public final Color[] colors = {
            Color.BLACK, Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE,
            Color.CYAN, Color.MAGENTA, Color.PINK, Color.YELLOW, Color.GRAY, Color.WHITE
    };
    private boolean bolding = false;

    private boolean underlining = false;


    private int fontSize = 12;

    private String fontFamily = "Arial";

    JTextPane textPane;

    /**
     * Constructor for StylingOperations.
     *
     * @param tp The JTextPane to apply styles to.
     */
    public StylingOperations(JTextPane tp){
        textPane = tp;

        // Add KeyListener to listen for Ctrl + B to toggle bold style
        textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Check if Ctrl + B is pressed
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_B) {
                    toggleStyle(StyleConstants.Bold); // Toggle bold style
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_U) {
                    toggleStyle(StyleConstants.Underline); // Toggle bold style
                }
            }
        });
    }

    /**
     * Apply a font family to the selected text.
     *
     * @param fontFamily The font family to apply (e.g., "Arial", "Times New Roman").
     */
    public void applyFontFamily(String fontFamily) {
        setFontFamily(fontFamily);
        StyledDocument doc = textPane.getStyledDocument(); // Get the document model
        int start = textPane.getSelectionStart(); // Get the start of the selection
        int end = textPane.getSelectionEnd(); // Get the end of the selection

        if (start == end) {
            // No text selected, apply to the caret position
            start = textPane.getCaretPosition();
            end = start; // Ensure the end is the same as the start
        }

        // Create a new attribute set and set the font family
        MutableAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attr, fontFamily);
        doc.setCharacterAttributes(start, end - start, attr, false); // Apply the font family
    }

    /**
     * Apply a font size to the selected text.
     *
     * @param fontSize The font size to apply (e.g., 12, 16).
     */
    public void applyFontSize(int fontSize) {
        setFontSize(fontSize);
        StyledDocument doc = textPane.getStyledDocument(); // Get the document model
        int start = textPane.getSelectionStart(); // Get the start of the selection
        int end = textPane.getSelectionEnd(); // Get the end of the selection

        if (start == end) {
            // No text selected, apply to the caret position
            start = textPane.getCaretPosition();
            end = start; // Ensure the end is the same as the start
        }

        // Create a new attribute set and set the font size
        MutableAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setFontSize(attr, fontSize);
        doc.setCharacterAttributes(start, end - start, attr, false); // Apply the font size
    }

    /**
     * Toggle a text style (Bold or Underline) for selected text.
     *
     * @param style The style to toggle (StyleConstants.Bold or StyleConstants.Underline).
     */
    public void toggleStyle(Object style) {
        StyledDocument doc = textPane.getStyledDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();

        if (start == end) {
            // No text selected, apply to the caret position
            start = textPane.getCaretPosition();
            end = start; // Ensure the end is the same as the start
        }

        // Get the element at the start position and retrieve its attributes
        Element element = doc.getCharacterElement(start);
        AttributeSet attr = element.getAttributes();

        // Check if the style is already applied
        boolean isStyleApplied = false;
        if (style.equals(StyleConstants.Bold)) {
            isStyleApplied = StyleConstants.isBold(attr);
        } else if (style.equals(StyleConstants.Underline)) {
            isStyleApplied = StyleConstants.isUnderline(attr);
        }

        // Toggle the style
        MutableAttributeSet newAttr = new SimpleAttributeSet();
        if (style.equals(StyleConstants.Bold)) {
            StyleConstants.setBold(newAttr, !isStyleApplied);
            // Explicitly update the bolding state
            setBolding(!isBolding());
        } else if (style.equals(StyleConstants.Underline)) {
            StyleConstants.setUnderline(newAttr, !isStyleApplied);
            // Explicitly update the bolding state
            setUnderlining(!isUnderlining());
        }

        // Apply the new attributes to the selected text
        doc.setCharacterAttributes(start, end - start, newAttr, false);
    }

    /**
     * Add bullet points right at the beginning of the selection of each line. (Amount of lines that the selection spans across = # of bullet points)
     */
    public void toggleBulletPoints() {
        StyledDocument doc = textPane.getStyledDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();

        try {
            // Get the selected text
            String selectedText = textPane.getSelectedText();
            if (selectedText == null || selectedText.isEmpty()) {
                return; // No text selected
            }

            // Check if the text already has bullet points
            boolean hasBullets = selectedText.startsWith("• ");

            String newText;
            if (hasBullets) {
                // Remove bullet points
                String[] lines = selectedText.split("\n");
                StringBuilder plainText = new StringBuilder();
                for (String line : lines) {
                    plainText.append(line.substring(2)).append("\n"); // Remove "• "
                }
                newText = plainText.toString();
            } else {
                // Add bullet points
                String[] lines = selectedText.split("\n");
                StringBuilder bulletedText = new StringBuilder();
                for (String line : lines) {
                    bulletedText.append("• ").append(line);
                }
                newText = bulletedText.toString();
            }

            // Replace the selected text with the new text
            doc.remove(start, end - start);
            doc.insertString(start, newText, null);

            // Apply indentation style
            MutableAttributeSet attr = new SimpleAttributeSet();
            doc.setParagraphAttributes(start, newText.length(), attr, false);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Change the selected text color.
     *
     * @param color The color to apply.
     */
    public void changeTextColor(Color color) {
        StyledDocument doc = textPane.getStyledDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();

        if (start != end) { // If some text is selected
            SimpleAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setForeground(attr, color);
            doc.setCharacterAttributes(start, end - start, attr, false);
        }
    }

    /**
     * Highlight the selected text with a given color.
     *
     * @param color The background color to apply.
     */
    public void highlightText(Color color) {
        StyledDocument doc = textPane.getStyledDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();

        if (start != end) { // If some text is selected
            SimpleAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setBackground(attr, color);
            doc.setCharacterAttributes(start, end - start, attr, false);
        }
    }

    /**
     * Resets the caret style to the default style.
     * This ensures that newly typed text uses the correct styles (E.g. Bold, FontFamily, ...).
     */
    public void resetCaretStyle() {

        MutableAttributeSet defaultAttr = new SimpleAttributeSet();


        StyleConstants.setFontSize(defaultAttr, getFontSize()); // Set default font size
        StyleConstants.setFontFamily(defaultAttr, getFontFamily());
        StyleConstants.setBold(defaultAttr, isBolding()); // Toggle bold state
        StyleConstants.setUnderline(defaultAttr, isUnderlining()); // Set no underline by default
        StyleConstants.setForeground(defaultAttr, Color.BLACK); // Set default text color

        textPane.setCharacterAttributes(defaultAttr, true); // Apply the default style
    }


    public boolean isBolding() {
        return bolding;
    }

    public void setBolding(boolean bolding) {
        this.bolding = bolding;
    }

    public boolean isUnderlining() {
        return underlining;
    }

    public void setUnderlining(boolean underlining) {
        this.underlining = underlining;
    }
    public int getFontSize() {
        return fontSize;
    }
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
    public String getFontFamily() {
        return fontFamily;
    }
    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }
}
