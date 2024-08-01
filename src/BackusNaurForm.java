import java.util.ArrayList;
import java.util.Scanner;

public class BackusNaurForm {
    Input input;
    Output output;

    public BackusNaurForm(Input input) {
        this.input = input;
    }

    public Token getToken() {
        return input.getCurrentToken();
    }

    public void nextToken() {
        input.next();
    }

    public BinaryTree expression() { // <expression> ::= <factor> * <expression> | <factor> / <expression> | <factor>
        BinaryTree factorTree = factor();
        Token t = getToken();
        if (t.isMultiplicative()) {
            nextToken();
            return new BinaryTree(t, factorTree, expression());
        } else {
            return factorTree;
        }
    }

    public BinaryTree factor() { // <factor> :== <term> + <factor> | <term> - <factor> | <term>
        BinaryTree termTree = term();
        Token t = getToken();
        if (t.isAdditive()) {
            nextToken();
            return new BinaryTree(t, termTree, factor());
        } else {
            return termTree;
        }
    }

    public BinaryTree term() { // <term> ::= { <expression> } | <literal>

        if (getToken().isType(Type.BRACKET)) {
            nextToken();
            BinaryTree expressionTree = expression();
            nextToken();
            return expressionTree;
        } else {
            return literal();
        }
    }

    public BinaryTree literal() { // <literal> ::= 0|1|2|3|4|5|6|7|8|9

        BinaryTree literalTree = new BinaryTree(getToken());
        nextToken();
        return literalTree;
    }

    public static void main(String[] args) { // The main method to run the program.

        Input input = new Input();
        input.open();
        input.requestVisualizationOption();
        input.displayBackusNaurForm();
        input.requestExpression();
        input.close();

        BackusNaurForm backusNaurForm = new BackusNaurForm(input);

        BinaryTree resultTree = backusNaurForm.expression();

        Output output = new Output(resultTree);
        output.printResult();
        output.printTree(input.getDisplayOption());
    }
}

class BinaryTree {
    private Token token;

    private BinaryTree left;
    private BinaryTree right;
    private BinaryTree parent;

    private int height = 1;

    public BinaryTree(Token token) {
        this.token = token;
    }

    public BinaryTree(Token token, BinaryTree left, BinaryTree right) {
        this(token);
        setLeft(left);
        setRight(right);
    }

    public Token getRoot() {
        return token;
    }

    public BinaryTree getParent() {
        return parent;
    }

    public Boolean isRoot() {
        return parent == null;
    }

    public boolean isLeft() { // Checks wheter this instance is the left child of the parent. 
        return this.parent.left == this;
    }

    public boolean isRight() { //Checks whethere this instance is the right child of the parent. 
        return this.parent.right == this;
    }

    public void setParent(BinaryTree tree) {
        this.parent = tree;
    }

    public int getHeight() {
        return height;
    }

    public boolean isLeaf() { // Checks whether this instance is a leaf. 
        return getLeft() == null && getRight() == null;
    }

    public BinaryTree getLeft() {
        return left;
    }

    public BinaryTree getRight() {
        return right;
    }

    public void setLeft(BinaryTree tree) { // Recursive method to set the left subtree and assign the parent of the tree
                                           // as this instance. Heights of each node are updated as well.
        this.left = tree;
        if (tree != null) {
            tree.setParent(this);
            updateHeight();
        }
    }

    public void setRight(BinaryTree tree) { // Recursive method to set the right subtree and assign the parent of the
                                            // tree as this instance.
        this.right = tree;
        if (tree != null) {
            tree.setParent(this);
            updateHeight();
        }
    }

    private void updateHeight() { // Reursive method to update the height of each node in the binary tree.
        height = height();
        if (parent != null) {
            parent.updateHeight();
        }
    }

    public int height() { // Evualulate the height of this instance of the binary tree. 
        return height(this);
    }

    private int height(BinaryTree tree) { // Evualte the height of the binary tree. 
        if (tree == null) {
            return 0;
        } else {
            return 1 + Math.max(height(tree.getLeft()), height(tree.getRight()));
        }
    }
}

enum Type { // Enunmeration used to denote the type of token for parsing and evaluation of
            // the expression tree.
    INTEGER,
    MULTIPLY,
    DIVIDE,
    PLUS,
    MINUS,
    BRACKET,
    END // This type is to denote the end of the parsed input.
}

class Token {

    private String value;
    private Type type;

    public Token(String value, Type type) {
        this.value = value;
        this.type = type;
    }

    public String toString() { // Get the value of the token as a string.
        return value;
    }

    public Double toDouble() { // Get the value of the token as a double value.
        return Double.parseDouble(value);
    }

    public Type getType() {
        return type;
    }

    public boolean isType(Type type) {
        return this.getType() == type;
    }

    public boolean isMultiplicative() { // Check if the token contains a multiplication or division operator.
        return isType(Type.MULTIPLY) || isType(Type.DIVIDE);
    }

    public boolean isAdditive() { // Check if the token contains a addition or subtraction operator.
        return isType(Type.PLUS) || isType(Type.MINUS);
    }

}

enum Display {
    BASIC,
    ENHANCED
}

class Input {
    private Scanner scanner;
    private ArrayList<Token> tokens;
    private int index = 0;
    private String input;
    private Token token;
    private Display displayOption;
    private static String ansiReset = "\u001B[0m";
    private static final String ansiCyan = "\u001B[36m";
    private static final String ansiOrange = "\u001B[33m";
    private static final String ansiRed = "\u001B[1;31m";
    private static final String ansiMagenta = "\u001B[35m";

    public Input() {
        this.tokens = new ArrayList<Token>();
    }

    public void open() {
        this.scanner = new Scanner(System.in);
    }

    public void close() {
        scanner.close();
    }

    public void requestExpression() { // Method to request for the input.
        System.out.print(ansiReset + "\nENTER A VALID EXPRESSION: " + ansiCyan);
        input = scanner.nextLine();
        System.out.println(ansiReset);
        parseInput(input);
    }

    public void displayBackusNaurForm() { // Display the Backus Naur Form.
        System.out.println("BACKUS NAUR FORM:\n");
        System.out.println(ansiCyan + "<expression>  " + ansiMagenta + "::=  " + ansiCyan + "<factor>  " + ansiRed
                + "*  " + ansiCyan + "<expression>   " + ansiReset + "|   " + ansiCyan + "<factor>  " + ansiRed + "/  "
                + ansiCyan + "<expression>   " + ansiReset + "|   " + ansiCyan + "<factor>");
        System.out.println(ansiCyan + "<factor>      " + ansiMagenta + "::=  " + ansiCyan + "<term>  " + ansiRed + "+  "
                + ansiCyan + "<factor>   " + ansiReset + "|   " + ansiCyan + "<term>  " + ansiRed + "-  " + ansiCyan
                + "<factor>   " + ansiReset + "|   " + ansiCyan + "<term>");
        System.out.println(ansiCyan + "<term>        " + ansiMagenta + "::=  " + ansiRed + "{  " + ansiCyan
                + "<expression>  " + ansiRed + "}  " + ansiReset + "|   " + ansiCyan + "<literal>");
        System.out.println(ansiCyan + "<literal>     " + ansiMagenta + "::=  " + ansiRed + "0 " + ansiReset + "| "
                + ansiRed + "1 " + ansiReset + "| " + ansiRed + "2 " + ansiReset + "| " + ansiRed + "3 " + ansiReset
                + "| " + ansiRed + "4 " + ansiReset + "| " + ansiRed + "5 " + ansiReset + "| " + ansiRed + "6 "
                + ansiReset + "| " + ansiRed + "7 " + ansiReset + "| " + ansiRed + "8 " + ansiReset + "| " + ansiRed
                + "9 ");
    }

    public void requestVisualizationOption() { // Method to request what type of format of the tree representation
                                               // depending on user preference.
        System.out.println("\nSELECT A BINARY TREE VISUALIZATION OPTION:\n");
        System.out.println(ansiRed + "[ 1 ] " + ansiCyan + "Basic");
        System.out.println(ansiRed + "[ 2 ] " + ansiCyan + "Enhanced");
        System.out.println(ansiOrange + "\nWARNING: " + ansiReset
                + "Enhanced visualization requires a large terminal window for large binary trees. If window is not large enough, the binary tree will not be printed correctly.");
        System.out.print("\nENTER AN OPTION NUMBER: " + ansiCyan);
        int dO = scanner.nextInt();
        System.out.println(ansiReset);
        scanner.nextLine(); // Consume the newline character.

        displayOption = getType(dO);
    }

    public String toString() {
        return input;
    }

    private void parseInput(String input) { // Parse the input into seperate elements in an array while ignoring any
                                            // possible spaces.
        for (char c : input.toCharArray()) {
            if (!Character.isWhitespace(c)) {
                tokens.add(new Token(String.valueOf(c), getType(c)));
            }
        }
    }

    private Type getType(char c) { // Evaluate the type of token based on the String value of the token.
        switch (c) {
            case '*':
                return Type.MULTIPLY;
            case '/':
                return Type.DIVIDE;
            case '+':
                return Type.PLUS;
            case '-':
                return Type.MINUS;
            case '{':
                return Type.BRACKET;
            case '}':
                return Type.BRACKET;
            default:
                return Type.INTEGER;
        }
    }

    private Display getType(int displayOption) { // Evaluate the type of display for the binary tree.
        switch (displayOption) {
            case 1:
                return Display.BASIC;
            case 2:
                return Display.ENHANCED;
            default:
                throw new IllegalArgumentException();
        }
    }

    public Display getDisplayOption() {
        return displayOption;
    }

    public Token getToken() {
        return token;
    }

    public Token getCurrentToken() {
        return (index < tokens.size()) ? tokens.get(index) : new Token("", Type.END); // Ternary operator to return a
                                                                                      // new Token to signify the end of
                                                                                      // the parsed input, avoid
                                                                                      // NullPointerException, and end
                                                                                      // the recursion.
    }

    public void next() { // Increment the index to select the next token to be evaluated.
        index++;
    }

}

class Output {
    private BinaryTree bnf;
    private double result;
    private String[][] treeMatrix;
    private int height;
    private int width;
    private int arcLength;

    private static final String ansiReset = "\u001B[0m";
    private static final String ansiRed = "\u001B[1;31m";
    private static final String ansiOrange = "\u001B[33m";
    private static final String ansiCyan = "\u001B[36m";
    private static final String ansiMagenta = "\u001B[35m";
    private static final String arcLine = ansiRed + "o" + ansiReset;

    public Output(BinaryTree bnf) {
        this.bnf = bnf;
        this.result = evaluate();
    }

    private int scaleArcLength(int height) {
        return (int) Math.pow(2, height) / 4;
    }

    private void generateTreeMatrix() { // Using height to determine arcLength and width of the matrix for any binary
                                        // tree. General method to call to generate the tree matrix for the Ehanced
                                        // visualization option.
        this.height = bnf.getHeight();
        this.width = (int) Math.pow(2, height) - 1;
        this.arcLength = scaleArcLength(height);
        this.treeMatrix = new String[arcLength * 2][width];
        generateTreeMatrix(bnf, 0, width / 2, arcLength);
    }

    private void generateTreeMatrix(BinaryTree tree, int y, int x, int arc) { // Implemented Professor's pseudocode for
                                                                              // drawing a tree,
        // but the output is not preferred as it is effected by
        // the window size of the terminal, especially for larger binary trees that
        // require a larger arc length.
        if (tree != null) {
            String node = tree.getRoot().toString();
            treeMatrix[y][x] = (node.matches("[+\\-*/]") ? ansiCyan : ansiOrange) +
                    node + ansiReset;

            if (tree.getLeft() != null) {
                for (int i = 1; i <= arc; i++) {
                    treeMatrix[y + i][x - i] = arcLine;
                }
                generateTreeMatrix(tree.getLeft(), y + arc, x - arc, arc / 2);
            }
            if (tree.getRight() != null) {
                for (int i = 1; i <= arc; i++) {
                    treeMatrix[y + i][x + i] = arcLine;
                }
                generateTreeMatrix(tree.getRight(), y + arc, x + arc, arc / 2);
            }
        }
    }

    private void printEnhancedTree() { // Method to print the binary tree to terminal for the Enhanced option.
        generateTreeMatrix();
        for (String[] row : treeMatrix) {
            for (String value : row) {
                System.out.print(value != null ? value : ".");
                System.out.print(""); // Adjust the x spacing of the grid.
            }
            System.out.println(""); // Adjust the y spacing of the grid.
        }
    }

    private void printBasicTree() {
        printBasicTree(bnf, "");
    }

    private void printBasicTree(BinaryTree tree, String prefix) { // A more basic/compact visualization of a binary tree
                                                                  // that is not limited by the size of the binary tree
                                                                  // or window size of the terminal. This method prints
                                                                  // directly to the terminal instead of using a matrix.
        if (tree != null) {
            String node = tree.getRoot().toString();
            String coloredNode = (node.matches("[+\\-*/]") ? ansiCyan : ansiMagenta) + node + ansiReset;
            String drawLeft = ansiRed + "└── " + ansiReset;
            String drawRight = ansiRed + "│" + ansiOrange + "└── " + ansiReset;
            if (tree.isRoot()) { // If the current tree is the root, print only the colored node without a
                                 // prefix.
                System.out.println(coloredNode);
            } else {
                System.out.println(prefix + (tree.isLeft() ? drawLeft : drawRight) + coloredNode);
            }

            if (tree.getRight() != null) {
                printBasicTree(tree.getRight(),
                        ansiRed + (tree.isRoot() ? prefix : prefix + (tree.isLeft() ? "    " : "│    "))); // If current
                                                                                                           // tree is
                                                                                                           // root, do
                                                                                                           // not change
                                                                                                           // the index.
                                                                                                           // If the
                                                                                                           // current
                                                                                                           // tree is
                                                                                                           // not the
                                                                                                           // root
                                                                                                           // adjust the
                                                                                                           // prefix
                                                                                                           // based on
                                                                                                           // if the
                                                                                                           // current
                                                                                                           // tree is
                                                                                                           // the left
                                                                                                           // or right
                                                                                                           // child.
            }

            if (tree.getLeft() != null) {
                printBasicTree(tree.getLeft(),
                        ansiRed + (tree.isRoot() ? prefix : prefix + (tree.isRight() ? "│    " : "    "))); // This is
                                                                                                            // similar
                                                                                                            // to the
            }

        }
    }

    public void printTree(Display displayOption) {
        System.out.println("\nEXPRESSION BINARY TREE:\n");
        switch (displayOption) {
            case BASIC:
                printBasicTree();
                System.out.println("\nLEGEND:\t" + ansiCyan + "Operator   " + ansiRed + "Left   " + ansiOrange
                        + "Right   " + ansiMagenta + "Integer\n" + ansiReset);
                break;
            case ENHANCED:
                printEnhancedTree();
                System.out.println();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void printResult() {
        System.out.println("RESULT: " + ansiRed + result + ansiReset);
    }

    private double evaluate() { // Evualute this instance's binary tree.
        return evaluate(bnf);
    }

    private double evaluate(BinaryTree tree) { // Evaluate the expression tree.
        if (tree.isLeaf()) {
            return tree.getRoot().toDouble();
        } else {
            switch (tree.getRoot().getType()) {
                case PLUS:
                    return evaluate(tree.getLeft()) + evaluate(tree.getRight());
                case MINUS:
                    return evaluate(tree.getLeft()) - evaluate(tree.getRight());
                case MULTIPLY:
                    return evaluate(tree.getLeft()) * evaluate(tree.getRight());
                case DIVIDE:
                    return evaluate(tree.getLeft()) / evaluate(tree.getRight());
                default:
                    throw new IllegalArgumentException();
            }
        }
    }
}
