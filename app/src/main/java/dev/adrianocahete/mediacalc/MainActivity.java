package dev.adrianocahete.mediacalc;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editTextA1, editTextA2, editTextA3;
    private TextView textViewResult, textViewApproved, textViewTooltip;
    private Button buttonCalculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initializeViews();
        setupCalculateButton();
        setupTooltip();
        setupTextWatchers();
    }

    private void initializeViews() {
        editTextA1 = findViewById(R.id.editTextA1);
        editTextA2 = findViewById(R.id.editTextA2);
        editTextA3 = findViewById(R.id.editTextA3);
        textViewResult = findViewById(R.id.textViewResult);
        textViewApproved = findViewById(R.id.textViewApproved);
        textViewTooltip = findViewById(R.id.textViewTooltip);
        buttonCalculate = findViewById(R.id.buttonCalculate);
    }

    private void setupCalculateButton() {
        buttonCalculate.setOnClickListener(v -> calculateNFp());
    }

    private void setupTooltip() {
        textViewTooltip.setOnClickListener(v -> showFormulaDialog());
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateFields();
            }
        };

        editTextA1.addTextChangedListener(textWatcher);
        editTextA2.addTextChangedListener(textWatcher);
        editTextA3.addTextChangedListener(textWatcher);
    }

    private void validateFields() {
        String a1Text = editTextA1.getText().toString().trim();
        String a2Text = editTextA2.getText().toString().trim();
        String a3Text = editTextA3.getText().toString().trim();

        boolean isA1Valid = !TextUtils.isEmpty(a1Text) && isValidNumber(a1Text);
        boolean hasValidA2OrA3 = (!TextUtils.isEmpty(a2Text) && isValidNumber(a2Text)) ||
                                (!TextUtils.isEmpty(a3Text) && isValidNumber(a3Text));

        buttonCalculate.setEnabled(isA1Valid && hasValidA2OrA3);
    }

    private boolean isValidNumber(String text) {
        if (TextUtils.isEmpty(text)) return false;

        try {
            String normalizedText = text.replace(",", ".");
            double value = Double.parseDouble(normalizedText);
            return value >= 0 && value <= 10;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showFormulaDialog() {
        // TODO: Mover pra strings
        String formulaText =
            "• Se A1 > 0 E (A2 ou A3) ≥ 5,0:\n" +
            "  NFp = (A1 × 0,4) + (A2 ou A3 × 0,6)\n\n" +
            "• Se A1 = 0 OU (A2 e A3) < 5,0:\n" +
            "  NFp = (A1 × 0,4) + (A2 ou A3 × 0,6) ÷ 2\n\n" +
            "Aprovação:\n" +
            "• Se A1 igual à 0: Reprovado automaticamente\n" +
            "• Se NFp maior OU igual à 6,0: Aprovado\n" +
            "• Se NFp menor que 6,0: Reprovado\n\n" +
            "Quando A2 e A3 estão preenchidos, usa-se a maior nota.";

        new AlertDialog.Builder(this)
            .setTitle("Fórmula de Cálculo")
            .setMessage(formulaText)
            .setPositiveButton("OK", null)
            .show();
    }

    private void calculateNFp() {
        String a1Text = editTextA1.getText().toString().trim().replace(",", ".");
        if (TextUtils.isEmpty(a1Text)) {
            Toast.makeText(this, "A1 é obrigatório!", Toast.LENGTH_SHORT).show();
            return;
        }

        double a1;
        try {
            a1 = Double.parseDouble(a1Text);
            if (a1 < 0 || a1 > 10) {
                Toast.makeText(this, "A1 deve estar entre 0 e 10!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "A1 deve ser um número válido!", Toast.LENGTH_SHORT).show();
            return;
        }

        String a2Text = editTextA2.getText().toString().trim().replace(",", ".");
        String a3Text = editTextA3.getText().toString().trim().replace(",", ".");

        double a2 = 0, a3 = 0;
        boolean hasA2 = false, hasA3 = false;
        if (!TextUtils.isEmpty(a2Text)) {
            try {
                a2 = Double.parseDouble(a2Text);
                hasA2 = true;
                if (a2 < 0 || a2 > 10) {
                    Toast.makeText(this, "A2 deve estar entre 0 e 10!", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "A2 deve ser um número válido!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!TextUtils.isEmpty(a3Text)) {
            try {
                a3 = Double.parseDouble(a3Text);
                if (a3 < 0 || a3 > 10) {
                    Toast.makeText(this, "A3 deve estar entre 0 e 10!", Toast.LENGTH_SHORT).show();
                    return;
                }
                hasA3 = true;
            } catch (NumberFormatException e) {
                Toast.makeText(this, "A3 deve ser um número válido!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        double higherGrade;
        if (hasA2 && hasA3) {
            higherGrade = Math.max(a2, a3);
        } else if (hasA2) {
            higherGrade = a2;
        } else {
            higherGrade = a3;
        }

        // Fonte: https://d15k2d11r6t6rl.cloudfront.net/public/users/Integrators/BeeProAgency/404306_383891/PDF/manual_ead.pdf
        double nfp;
        if (a1 > 0.0 && higherGrade >= 5.0) {
            // A1 > 0 e (A2 ou A3) >= 5: NFp = (A1 x 0.4) + (A2 or A3 x 0.6)
            nfp = (a1 * 0.4) + (higherGrade * 0.6);
        } else {
            // A1 = 0 e/ou (A2 e A3) < 5: NFp = (A1 x 0.4) + (A2 or A3 x 0.6)/2
            nfp = (a1 * 0.4) + ((higherGrade * 0.6) / 2);
        }

        boolean isApproved;
        if (a1 == 0.0) {
            isApproved = false;
        } else {
            isApproved = nfp >= 6.0;
        }

        textViewResult.setText(String.format(Locale.getDefault(), "%.2f", nfp));

        if (isApproved) {
            textViewApproved.setText(getString(R.string.approved));
            textViewApproved.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            textViewApproved.setText(getString(R.string.failed));
            textViewApproved.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }
}
