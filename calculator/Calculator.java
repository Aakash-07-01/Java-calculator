import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Calculator extends Application {

    @Override
    public void start(Stage primaryStage) {
        TextField display = new TextField();
        display.setEditable(false); // Make display uneditable, only updated via buttons

        GridPane gridPane = new GridPane();
        String[] buttons = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "C", "+"
        };

        // Add number and operator buttons
        for (int i = 0; i < buttons.length; i++) {
            Button button = new Button(buttons[i]);
            button.setMinSize(50, 50);  // Set button size for better layout
            button.setOnAction(e -> {
                String text = button.getText();
                if (text.equals("C")) {
                    display.clear(); // Clear the display when "C" is pressed
                } else {
                    display.setText(display.getText() + text);
                }
            });
            gridPane.add(button, i % 4, i / 4);
        }

        // Add equal button separately
        Button equalButton = new Button("=");
        equalButton.setMinSize(100, 50);  // Double-width button for "="
        equalButton.setOnAction(e -> {
            String expression = display.getText();
            try {
                // Ensure the expression is not empty or invalid
                if (!expression.isEmpty()) {
                    String result = Double.toString(eval(expression));
                    display.setText(result);  // Display result on success
                } else {
                    display.setText("Error");  // Show error for empty input
                }
            } catch (Exception ex) {
                display.setText("Error");  // Display "Error" on invalid input
            }
        });
        gridPane.add(equalButton, 2, 4, 2, 1);  // Place "=" button in the grid

        // Make sure the display is placed at the top
        gridPane.add(display, 0, 0, 4, 1);  // Span across 4 columns

        // Set up the scene and stage
        Scene scene = new Scene(gridPane, 220, 300);  // Set an appropriate scene size
        primaryStage.setScene(scene);
        primaryStage.setTitle("Calculator");

        // Ensure the window is visible
        primaryStage.setResizable(false);  // Disable resizing
        primaryStage.show();  // Show the window
    }

    // Parsing and evaluating the arithmetic expression
    private double eval(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Support addition and subtraction
            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            // Support multiplication, division
            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            // Support unary plus, minus, and numbers
            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                return x;
            }
        }.parse();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
