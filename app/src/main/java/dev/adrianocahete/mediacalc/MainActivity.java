// Criado por Adriano Cahete - <https://adrianocahete.dev> @ 2025
// Projeto MediaCalc - [UVA] Calculadora de Média
//
// Codigo fonte e git history: https://github.com/AdrianoCahete/UVA-DesAppMobile-II

package dev.adrianocahete.mediacalc;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView textViewQuestionMark;

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
        setupNavbar();
        loadCalculatorFragment(savedInstanceState);
    }

    private void initializeViews() {
        textViewQuestionMark = findViewById(R.id.textViewQuestionMark);
    }

    private void setupNavbar() {
        textViewQuestionMark.setOnClickListener(v -> showDeveloperPopup());
    }

    private void loadCalculatorFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CalculatorFragment())
                    .commit();
        }
    }

    private void showDeveloperPopup() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.navbar_popup_title))
                .setMessage(getString(R.string.navbar_popup_message))
                .setPositiveButton(getString(R.string.visit_button), (dialog, which) -> openWebsite())
                .setNegativeButton(getString(R.string.close_button), null)
                .show();
    }

    private void openWebsite() {
        String url = "https://adrianocahete.dev";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
