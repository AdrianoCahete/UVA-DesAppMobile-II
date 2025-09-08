package dev.adrianocahete.mediacalc;

import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private EditText editTextA1, editTextA2, editTextA3;
    private TextView textViewResult, textViewApproved;
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
    }

    private void initializeViews() {
        editTextA1 = findViewById(R.id.editTextA1);
        editTextA2 = findViewById(R.id.editTextA2);
        editTextA3 = findViewById(R.id.editTextA3);
        textViewResult = findViewById(R.id.textViewResult);
        textViewApproved = findViewById(R.id.textViewApproved);
        buttonCalculate = findViewById(R.id.buttonCalculate);
    }

    private void setupCalculateButton() {
        buttonCalculate.setOnClickListener(v -> calculateNFp());
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
                // TODO: Limitar o campo?
                Toast.makeText(this, "A1 deve estar entre 0 e 10!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            // TODO: Limitar o campo?
            Toast.makeText(this, "A1 deve ser um número válido!", Toast.LENGTH_SHORT).show();
            return;
        }

        String a2Text = editTextA2.getText().toString().trim().replace(",", ".");
        String a3Text = editTextA3.getText().toString().trim().replace(",", ".");

        if (TextUtils.isEmpty(a2Text) && TextUtils.isEmpty(a3Text)) {
            Toast.makeText(this, "É necessário preencher A2 ou A3!", Toast.LENGTH_SHORT).show();
            return;
        }

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

        double a2OrA3; // TODO: Melhorar esse nome de variavel
        if (hasA2 && hasA3) {
            a2OrA3 = Math.max(a2, a3);
        } else if (hasA2) {
            a2OrA3 = a2;
        } else {
            a2OrA3 = a3;
        }

        // Fonte: https://d15k2d11r6t6rl.cloudfront.net/public/users/Integrators/BeeProAgency/404306_383891/PDF/manual_ead.pdf
        double nfp;
        if (a1 > 0.0 && a2OrA3 >= 5.0) {
            // A1 > 0 e (A2 ou A3) >= 5: NFp = (A1 x 0.4) + (A2 or A3 x 0.6)
            nfp = (a1 * 0.4) + (a2OrA3 * 0.6);
        } else {
            // A1 = 0 e/ou (A2 e A3) < 5: NFp = (A1 x 0.4) + (A2 or A3 x 0.6)/2
            nfp = (a1 * 0.4) + ((a2OrA3 * 0.6) / 2);
        }

        boolean isApproved;
        if (a1 == 0.0) {
            isApproved = false;
        } else {
            isApproved = nfp >= 6.0;
        }

        textViewResult.setText(String.format("%.2f", nfp));

        if (isApproved) {
            // TODO: Usar cores do tema
            textViewApproved.setText("Aprovado");
            textViewApproved.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            textViewApproved.setText("Reprovado");
            textViewApproved.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }
}
