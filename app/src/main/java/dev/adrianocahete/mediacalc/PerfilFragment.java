package dev.adrianocahete.mediacalc;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class PerfilFragment extends Fragment {

    private TextInputEditText editTextName;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextApiKey;
    private Button buttonSave;

    private DatabaseHelper databaseHelper;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        initializeViews(view);
        setupDatabase();
        loadUserData();
        setupButtons();

        return view;
    }

    private void initializeViews(View view) {
        editTextName = view.findViewById(R.id.editTextName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextApiKey = view.findViewById(R.id.editTextApiKey);
        buttonSave = view.findViewById(R.id.buttonSave);
    }

    private void setupDatabase() {
        databaseHelper = new DatabaseHelper(getContext());
    }

    private void loadUserData() {
        currentUser = databaseHelper.getUser();

        if (currentUser != null) {
            editTextName.setText(currentUser.getNome());
            editTextEmail.setText(currentUser.getMatricula());
            editTextApiKey.setText(currentUser.getApiKey());

            if (currentUser.isDataFromApi()) {
                editTextName.setEnabled(false);
                editTextEmail.setEnabled(false);
            }
        }
    }

    private void setupButtons() {
        buttonSave.setOnClickListener(v -> saveUserData());
    }

    private void saveUserData() {
        String nome = editTextName.getText().toString().trim();
        String matricula = editTextEmail.getText().toString().trim();
        String apiKey = editTextApiKey.getText().toString().trim();

        if (!validateInput(nome, matricula)) {
            return;
        }

        if (currentUser != null) {
            // Update existing user - preserve API data restrictions
            String changedBy = currentUser.isDataFromApi() ? "api" : "user";
            int result = databaseHelper.updateUser(currentUser.getId(), nome, matricula,
                    currentUser.getAvatarUrl(), apiKey, changedBy);
            if (result > 0) {
                currentUser.setNome(nome);
                currentUser.setMatricula(matricula);
                currentUser.setApiKey(apiKey);
                Toast.makeText(getContext(), getString(R.string.profile_saved_success), Toast.LENGTH_SHORT).show();
                updateNavigationHeader(nome, matricula);

                // Navigate to Materias after saving
                navigateToMaterias();
            } else {
                Toast.makeText(getContext(), getString(R.string.profile_save_error), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Create new user
            long result = databaseHelper.createUser(nome, matricula, apiKey);
            if (result != -1) {
                currentUser = databaseHelper.getUser();
                Toast.makeText(getContext(), getString(R.string.profile_created_success), Toast.LENGTH_SHORT).show();
                updateNavigationHeader(nome, matricula);

                // Navigate to Materias after creating profile
                navigateToMaterias();
            } else {
                Toast.makeText(getContext(), getString(R.string.profile_create_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInput(String nome, String matricula) {
        if (TextUtils.isEmpty(nome)) {
            editTextName.setError(getString(R.string.name_required));
            editTextName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(matricula)) {
            editTextEmail.setError(getString(R.string.email_required));
            editTextEmail.requestFocus();
            return false;
        }

        return true;
    }

    private void updateNavigationHeader(String name, String email) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateNavigationHeader(name, email);
        }
    }

    private void navigateToMaterias() {
        if (getActivity() != null) {
            // Switch to Materias fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MateriasFragment())
                    .commit();

            // Update navigation drawer selection
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateNavigationSelection(R.id.nav_materias);
            }
        }
    }
}
