package project;
import java.util.Scanner;
/**This program for solve jetbrain's internship task without simplification
 * @author Nurullokhon Gulomkodirov
 * @version 11.0
 */
class ConvertString {
    private final String line;
    private final char[] arithSymbols = {'+', '-', '*'};
    private final char[] boolSymbols = {'&', '|' , '=', '>', '<'};

    private String currMap = "";
    private String currFilter = "";


    ConvertString(String line){
        this.line = line;
    }

    /**
     * Method convert the string
     */
    String getConvertedString(){
        String[] arrayOfChains = line.split("%>%");
        try {
            for(String item : arrayOfChains)
                checkAndConvertChain(item);
        }
        catch (IllegalArgumentException e){
            return e.getMessage();
        }

        if(currMap.equals(""))
            currMap = "element";

        return "filter{" + currFilter + "}%>%map{" + currMap + "}";
    }

    /**
     * Method convert the string
     * @param chain - <map-call> or <filter-call>
     */
    void checkAndConvertChain(String chain) {
        int index = chain.indexOf('{') + 1;
        if (index == 0)
            throw new IllegalArgumentException("SYNTAX ERROR");

        String filterOrMap = chain.substring(0, index) + chain.charAt(chain.length() - 1);
        String expression = chain.substring(index, chain.length() - 1);

        if(filterOrMap.equals("filter{}")){
            checkExpression(expression, boolSymbols);
            changeCurrFilter(expression);
        }
        else if(filterOrMap.equals("map{}")){
            checkExpression(expression, arithSymbols);
            changeCurrMap(expression);
        }
        else
            throw new IllegalArgumentException("SYNTAX ERROR");
    }

    /**
     * Method check syntax and type errors of expression
     * @param exp - “element” | <constant-expression> | <binary-expression>
     * @param symbols - allowed characters
     */
    void checkExpression(String exp, char[] symbols){
        if(!exp.equals("") && exp.charAt(0) == '(' && exp.charAt(exp.length() - 1) == ')'){
            String binary = exp.substring(1, exp.length() - 1);
            // expression
            if(binary.contains("(") || binary.contains(")")){
                if (!checkParentheses(binary))
                    throw new IllegalArgumentException("SYNTAX ERROR");

                int index = findOperator(binary);
                if (index == 0 || index == binary.length() - 1 )
                    throw new IllegalArgumentException("SYNTAX ERROR");
                if (!containsChar(binary.charAt(index), symbols))
                    throw new IllegalArgumentException("TYPE ERROR");

                checkExpression(binary.substring(0, index), symbols);
                checkExpression(binary.substring(index + 1), symbols);
            }
            //binary-expression
            else {
                String[] parts = binary.split("[\\W]");
                if(parts.length != 2)
                    throw new IllegalArgumentException("SYNTAX ERROR");

                int index = binary.indexOf(parts[1]);
                if(index > 0  && !containsChar(binary.charAt(index - 1), symbols))
                    throw new IllegalArgumentException("TYPE ERROR");

                checkExpression(parts[0], symbols);
                checkExpression(parts[1], symbols);
            }
        }
        //  “element” or constant-expression
        else if(!(exp.equals("element") || tryParseLong(exp)))
            throw new IllegalArgumentException("SYNTAX ERROR");
    }

    /**
     * Method check the sequence of parentheses
     * @param expr - “element” | <constant-expression> | <binary-expression>
     */
    private boolean checkParentheses(String expr){
        int count = 0;
        for(int i = 0; i < expr.length(); i++){
            if (expr.charAt(i) == '(')
                count++;
            else if (expr.charAt(i) == ')')
                count--;
        }
        return count == 0;
    }

    /**
     * Method find necessary operator
     * @param expr - “element” | <constant-expression> | <binary-expression>
     * @return index of binary operator
     */
    private int findOperator(String expr){
        if(expr.charAt(0) != '(')
            return expr.indexOf('(') - 1;

        int count = 1;
        int index = 0;
        for(int i = 1; i < expr.length(); i++){
            if(count == 0){
                index = i;
                break;
            }
            if (expr.charAt(i) == '(')
                count++;
            else if (expr.charAt(i) == ')')
                count--;
        }
        return index;
    }

    /**
     * Helper method for converting string
     * @param expr - “element” | <constant-expression> | <binary-expression>
     */
    private void changeCurrMap(String expr){
        if (currMap.equals(""))
            currMap = expr;
        else
            currMap = expr.replace("element", currMap);
    }

    /**
     * Helper method for converting string
     * @param expr - “element” | <constant-expression> | <binary-expression>
     */
    private void changeCurrFilter(String expr){
        if (!currMap.equals(""))
            expr = expr.replace("element", currMap);

        if(currFilter.equals(""))
            currFilter = expr;
        else
            currFilter = "(" + currFilter + "&" + expr + ")";
    }

    /**
     * Method for checking string value to long
     * @param value - “-” <number> | <number>
     */
    boolean tryParseLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Method for checking if a character belongs to an array
     * @param symbol - char
     * @param array - character array
     */
    boolean containsChar(char symbol, char[] array){
        boolean flag = false;
        for (char item : array) {
            if (symbol == item) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();

        ConvertString convert = new ConvertString(line);
        System.out.println(convert.getConvertedString());
    }
}
