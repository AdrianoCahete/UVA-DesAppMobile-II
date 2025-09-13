// Criado por Adriano Cahete - <https://adrianocahete.dev> @ 2025
// Projeto MediaCalc - [UVA] Calculadora de Média
//
// Codigo fonte e git history: https://github.com/AdrianoCahete/UVA-DesAppMobile-II

package dev.adrianocahete.mediacalc;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView textViewQuestionMark;
    private ImageView imageViewHamburger;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

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
        setupNavigationDrawer();
        loadCalculatorFragment(savedInstanceState);
    }

    private void initializeViews() {
        textViewQuestionMark = findViewById(R.id.textViewQuestionMark);
        imageViewHamburger = findViewById(R.id.imageViewHamburger);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
    }

    private void setupNavbar() {
        textViewQuestionMark.setOnClickListener(v -> showDeveloperPopup());
        imageViewHamburger.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
        });
    }

    private void setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(this);

        // Set default selection
        navigationView.setCheckedItem(R.id.nav_calculator);
    }

    private void loadCalculatorFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CalculatorFragment())
                    .commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        int itemId = item.getItemId();
        if (itemId == R.id.nav_calculator) {
            selectedFragment = new CalculatorFragment();
        } else if (itemId == R.id.nav_perfil) {
            selectedFragment = new PerfilFragment();
        } else if (itemId == R.id.nav_materias) {
            selectedFragment = new MateriasFragment();
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }

        drawerLayout.closeDrawer(navigationView);
        return true;
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
