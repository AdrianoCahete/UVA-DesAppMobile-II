package dev.adrianocahete.mediacalc;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
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
    private Menu navigationMenu;

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
        handleIntent();
        loadInitialFragment(savedInstanceState);
        updateNavigationHeaderOnStart();
        callNotificationsEndpoint();
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
        navigationMenu = navigationView.getMenu();

        // Set default selection to Profile
        navigationView.setCheckedItem(R.id.nav_perfil);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        boolean showProfile = intent.getBooleanExtra("show_profile", false);
        boolean guestMode = intent.getBooleanExtra("guest_mode", false);

        if (showProfile) {
            // Navigate to profile fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PerfilFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_perfil);
        }
    }

    private void loadInitialFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null && !getIntent().getBooleanExtra("show_profile", false)) {
            // Default to Profile instead of Calculator
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PerfilFragment())
                    .commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        int itemId = item.getItemId();
        if (itemId == R.id.nav_perfil) {
            selectedFragment = new PerfilFragment();
        } else if (itemId == R.id.nav_materias) {
            selectedFragment = new MateriasFragment();
        } else if (itemId == R.id.nav_calculator) {
            selectedFragment = new CalculatorFragment();
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

    public void updateNavigationHeader(String name, String email) {
        View headerView = navigationView.getHeaderView(0);
        TextView textViewName = headerView.findViewById(R.id.textViewName);
        TextView textViewEmail = headerView.findViewById(R.id.textViewEmail);

        textViewName.setText(name);
        textViewName.setVisibility(View.VISIBLE);

        if (email != null && !email.isEmpty() && !name.equals(getString(R.string.nav_header_login))) {
            textViewEmail.setText(email);
            textViewEmail.setVisibility(View.VISIBLE);
        } else {
            textViewEmail.setVisibility(View.GONE);
        }

        // Update menu visibility when user data changes
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        User user = databaseHelper.getUser();
        updateMenuVisibility(user);
    }

    private void updateNavigationHeaderOnStart() {
        // Check if user exists in database and update header accordingly
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        User user = databaseHelper.getUser();

        View headerView = navigationView.getHeaderView(0);
        TextView textViewName = headerView.findViewById(R.id.textViewName);
        TextView textViewEmail = headerView.findViewById(R.id.textViewEmail);

        if (user != null) {
            // User exists - show user data
            textViewName.setText(user.getNome());
            textViewEmail.setText(user.getMatricula());
            textViewEmail.setVisibility(View.VISIBLE);
            textViewName.setVisibility(View.VISIBLE);

            // Enable Matérias menu if user has Nome, Matricula or API_KEY
            updateMenuVisibility(user);
        } else {
            // No user - hide name and email completely
            textViewName.setVisibility(View.GONE);
            textViewEmail.setVisibility(View.GONE);

            // Disable Matérias menu
            updateMenuVisibility(null);
        }
    }

    private void updateMenuVisibility(User user) {
        MenuItem materiasItem = navigationMenu.findItem(R.id.nav_materias);

        if (user != null) {
            // Enable Matérias if user has Nome, Matricula or API_KEY filled
            boolean hasRequiredData = !TextUtils.isEmpty(user.getNome()) ||
                                    !TextUtils.isEmpty(user.getMatricula()) ||
                                    !TextUtils.isEmpty(user.getApiKey());
            materiasItem.setEnabled(hasRequiredData);
            materiasItem.setVisible(hasRequiredData);
        } else {
            // No user - disable Matérias
            materiasItem.setEnabled(false);
            materiasItem.setVisible(false);
        }
    }

    private void callNotificationsEndpoint() {
        // Call notifications endpoint when user opens the app
        // Using example.com API for now
        // TODO: Replace with actual notifications API URL when provided

        // This is a placeholder for the notifications API call
        // Implementation will use HTTP client like OkHttp or Volley
        // Example: NotificationService.getNotifications("https://example.com/api/notifications");
    }

    public void onSyncButtonClicked() {
        // Called when user clicks sync from API
        callNotificationsEndpoint();
    }

    public void updateNavigationSelection(int menuItemId) {
        if (navigationView != null) {
            navigationView.setCheckedItem(menuItemId);
        }
    }
}
