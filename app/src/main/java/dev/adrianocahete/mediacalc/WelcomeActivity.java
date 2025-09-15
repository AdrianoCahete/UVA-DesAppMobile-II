package dev.adrianocahete.mediacalc;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WelcomeActivity extends AppCompatActivity {

    private Button buttonLogin;
    private TextView textViewContinueWithoutLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupButtons();
    }

    private void initializeViews() {
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewContinueWithoutLogin = findViewById(R.id.textViewContinueWithoutLogin);
    }

    private void setupButtons() {
        buttonLogin.setOnClickListener(v -> showLoginDialog());
        textViewContinueWithoutLogin.setOnClickListener(v -> continueWithoutLogin());
    }

    private void showLoginDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.login_dialog_title))
                .setMessage(getString(R.string.login_dialog_message))
                .setPositiveButton(getString(R.string.proceed_button), (dialog, which) -> proceedToUVALogin())
                .setNegativeButton(getString(R.string.close_button), null)
                .show();
    }

    private void proceedToUVALogin() {
        setLoadingState(true);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String url = getString(R.string.uva_login_url);
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
            setLoadingState(false);
        }, 500);
    }

    private void setLoadingState(boolean isLoading) {
        buttonLogin.setEnabled(!isLoading);
        buttonLogin.setText(isLoading ? getString(R.string.loading) : getString(R.string.login_button));
        textViewContinueWithoutLogin.setEnabled(!isLoading);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setLoadingState(false);
    }

    private void continueWithoutLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("show_profile", true);
        intent.putExtra("guest_mode", true);
        startActivity(intent);
        finish();
    }
}
