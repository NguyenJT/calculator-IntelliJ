import javafx.geometry.Pos;

import java.awt.*;
import java.sql.SQLOutput;
import java.util.*;
import java.util.List;

import static java.lang.Double.NaN;
import static java.lang.Double.parseDouble;
import static java.lang.Math.exp;
import static java.lang.Math.pow;


/*
 *   A calculator for rather simple arithmetic expressions
 *
 *   This is not the program, it's a class declaration (with methods) in it's
 *   own file (which must be named Calculator.java)
 *
 *   NOTE:
 *   - No negative numbers implemented
 */
public class Calculator {

    // Here are the only allowed instance variables!
    // Error messages (more on static later)
    final static String MISSING_OPERAND = "Missing or bad operand";
    final static String DIV_BY_ZERO = "Division with 0";
    final static String MISSING_OPERATOR = "Missing operator or parenthesis";
    final static String OP_NOT_FOUND = "Operator not found";

    // Definition of operators
    final static String OPERATORS = "+-*/^";

    // Method used in REPL
    double eval(String expr) {
        if (expr.length() == 0) {
            return NaN;
        }
        List<String> tokens = tokenize(expr);
        List<String> postfix = infix2Postfix(tokens);
        return evalPostfix(postfix);
    }

    // ------  Evaluate RPN expression -------------------

    double evalPostfix(List<String> postfix) {
        Deque<String> stack = new ArrayDeque<>();

        for (int i = 0; i < postfix.size(); i++){
            //Check if number ---> push to stack
            if (Character.isDigit(postfix.get(i).charAt(0))){
                stack.push(postfix.get(i));
            }

            //Check if operator -----> Calculate the answer using the operator and push into stack
            else{
                double number1 = Double.parseDouble(stack.pop());
                double number2 = Double.parseDouble(stack.pop());
                stack.push(String.valueOf(applyOperator(postfix.get(i), number1, number2)));
            }

        }
        return Double.parseDouble(stack.pop());
    }

    double applyOperator(String op, double d1, double d2) {
        switch (op) {
            case "+":
                return d1 + d2;
            case "-":
                return d2 - d1;
            case "*":
                return d1 * d2;
            case "/":
                if (d1 == 0) {
                    throw new IllegalArgumentException(DIV_BY_ZERO);
                }
                return d2 / d1;
            case "^":
                return pow(d2, d1);
        }
        throw new RuntimeException(OP_NOT_FOUND);
    }

    // ------- Infix 2 Postfix ------------------------

    List<String> infix2Postfix(List<String> infix) {
        Deque<String> stack = new ArrayDeque<>();
        List<String> Postfix = new ArrayList<>();

        for (int i = 0; i < infix.size(); i++) {

            //----------If its a number-----------
            if (Character.isDigit(infix.get(i).charAt(0))) {
                Postfix.add(infix.get(i));
            }


            //-----------If its parentheses-------------
            else if (infix.get(i).contains("(")) {//Could be wrong, might have to do this first/put it in the top of
                stack.push(infix.get(i));
            }
            else if (infix.get(i).equals(")")) {
                while (!stack.peek().equals("(")) {
                    Postfix.add(stack.pop());
                }
                stack.pop();
            }


            //------------If its an operator----------------
            else {

                //-----Stack is empty-----
                if (stack.isEmpty()) {
                    stack.push(infix.get(i));
                }

                //----Stack is not empty and has a parentheses-------
                else if (stack.peek().equals("(")){
                    stack.push(infix.get(i));
                }

                //------Stack not empty and has a operator in it-------
                else {

                    if (getPrecedence(infix.get(i)) > getPrecedence(stack.peek())) {
                        stack.push(infix.get(i));
                    }
                    else if (getPrecedence(infix.get(i)) <= getPrecedence(stack.peek())) {
                        if (getAssociativity(infix.get(i)) == Assoc.LEFT) {
                            while (!stack.isEmpty()) {
                                Postfix.add(stack.pop());
                            }
                            stack.push(infix.get(i));
                        }
                        else {
                            stack.push(infix.get(i));
                        }
                    }
                }
            }
        }

        while(!stack.isEmpty()){
            Postfix.add(stack.pop());
        }
        System.out.println(Postfix);
        return Postfix;
    }

    int getPrecedence(String op) {
        if ("+-".contains(op)) {
            return 2;
        } else if ("*/".contains(op)) {
            return 3;
        } else if ("^".contains(op)) {
            return 4;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }

    Assoc getAssociativity(String op) {
        if ("+-*/".contains(op)) {
            return Assoc.LEFT;
        } else if ("^".contains(op)) {
            return Assoc.RIGHT;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }
    enum Assoc {
        LEFT,
        RIGHT
    }

    // ---------- Tokenize -----------------------


    // List String (not char) because numbers (with many chars)

    boolean isOperator(char a) {
        for (int i = 0; i < OPERATORS.length(); i++) {
            if (OPERATORS.charAt(i) == a) {
                return true;
            }
        }
        return false;
    }

    boolean isParenthesis(char a) {
        return (a == '(' || a == ')');
    }

    List<String> tokenize(String expr){
        StringBuilder output = new StringBuilder();
        List<String> test = new ArrayList<>();
        expr = expr.trim();

        for(int i = 0; i < expr.length(); i++) {
            if (isOperator(expr.charAt(i)) ) {
                if (i == expr.length()-1) {
                    throw new IllegalArgumentException(MISSING_OPERAND);
                }
                if (expr.charAt(i - 1) != ' ' && expr.charAt(i + 1) != ' ') {
                    output.append(' ');
                    output.append(expr.charAt(i));
                    output.append(' ');
                }
                else if (expr.charAt(i - 1) != ' ') {
                    output.append(' ');
                    output.append(expr.charAt(i));
                }
                else if (expr.charAt(i + 1) != ' ') {
                    output.append(expr.charAt(i));
                    output.append(' ');
                } else {
                    output.append(expr.charAt(i));
                }
            }
            else if (expr.charAt(i) == ')'){
                if (expr.charAt(i - 1) != ' ') {
                    output.append(' ');
                    output.append(expr.charAt(i));
                }
            }
            else if (expr.charAt(i) == '(') {
                if (expr.charAt(i+1) != ' ') {
                    output.append(expr.charAt(i));
                    output.append(' ');
                } else {
                    output.append(expr.charAt(i));
                }

            }
            else {
                output.append(expr.charAt(i));
            }
        }
        String str = String.valueOf(output);
        String [] strs = str.split("\\s+");
        for (int i = 0; i < strs.length; i++) {
            test.add(strs[i]);
        }
        int count = 0;
        int parenCount = 0;
        for (int i = 0; i < test.size(); i++) {
            for (int j = 0; j < OPERATORS.length(); j++) {
                if (test.get(i).contains(String.valueOf(OPERATORS.charAt(j)))) {
                    count++;
                }
            }
            if (isParenthesis(test.get(i).charAt(0))) {
                parenCount++;
            }
        }

        if (count == 0 || parenCount % 2 != 0) {
            throw new IllegalArgumentException(MISSING_OPERATOR);
        }
        //System.out.println(test);
        return test;

    }

}
