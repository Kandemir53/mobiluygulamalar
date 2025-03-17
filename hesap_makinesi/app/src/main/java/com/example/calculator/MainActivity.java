package com.example.calculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TextView textOperation;
    private String currentExpression = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textOperation = findViewById(R.id.text_operation);

        int[] numberButtons = {
                R.id.button_num1, R.id.button_num2, R.id.button_num3,
                R.id.button_num4, R.id.button_num5, R.id.button_num6,
                R.id.button_num7, R.id.button_num8, R.id.button_num9
        };

        for (int id : numberButtons) {
            Button button = findViewById(id);
            if (button != null) {
                button.setOnClickListener(v -> {
                    String buttonText = button.getText().toString();
                    currentExpression += buttonText;
                    textOperation.setText(currentExpression);
                });
            }
        }

        findViewById(R.id.button_plus).setOnClickListener(v -> appendOperator("+"));
        findViewById(R.id.button_minus).setOnClickListener(v -> appendOperator("-"));
        findViewById(R.id.button_cross).setOnClickListener(v -> appendOperator("*"));
        findViewById(R.id.button_slash).setOnClickListener(v -> appendOperator("/"));

        findViewById(R.id.button_result).setOnClickListener(v -> calculateResult());
        findViewById(R.id.button_clear).setOnClickListener(v -> clearOperation());
    }
    private void appendOperator(String operator) {
        if (!currentExpression.isEmpty() && !isLastCharOperator()) {
            currentExpression += operator;
            textOperation.setText(currentExpression);
        }
    }
    private boolean isLastCharOperator() {
        char lastChar = currentExpression.charAt(currentExpression.length() - 1);
        return lastChar == '+' || lastChar == '-' || lastChar == '*' || lastChar == '/';
    }

    private void calculateResult() {
        if (currentExpression.isEmpty() || isLastCharOperator()) return;

        List<Double> numbers = new ArrayList<>();
        List<Character> operators = new ArrayList<>();

        String number = "";
        for (char ch : currentExpression.toCharArray()) {
            if (Character.isDigit(ch) || ch == '.') {
                number += ch;
            } else {
                numbers.add(Double.parseDouble(number));
                operators.add(ch);
                number = "";
            }
        }
        numbers.add(Double.parseDouble(number));

        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i) == '*' || operators.get(i) == '/') {
                double num1 = numbers.get(i);
                double num2 = numbers.get(i + 1);
                double result = (operators.get(i) == '*') ? (num1 * num2) : (num1 / num2);

                numbers.set(i, result);
                numbers.remove(i + 1);
                operators.remove(i);
                i--;
            }
        }

        double finalResult = numbers.get(0);
        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i) == '+') {
                finalResult += numbers.get(i + 1);
            } else {
                finalResult -= numbers.get(i + 1);
            }
        }

        textOperation.setText(String.valueOf(finalResult));
        currentExpression = String.valueOf(finalResult);
    }

    private void clearOperation() {
        currentExpression = "";
        textOperation.setText("");
    }

}
