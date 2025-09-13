// Criado por Adriano Cahete - <https://adrianocahete.dev> @ 2025
// Projeto MediaCalc - [UVA] Calculadora de Média
//
// Codigo fonte e git history: https://github.com/AdrianoCahete/UVA-DesAppMobile-II

package dev.adrianocahete.mediacalc;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class CalculatorFragment extends Fragment {

    private EditText editTextA1, editTextA2, editTextA3;
    private TextView textViewResult, textViewApproved, textViewTooltip, textViewValidation;
    private Button buttonCalculate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);
        initializeViews(view);
        setupCalculateButton();
        setupTooltip();
        setupTextWatchers();
        return view;
    }

    private void initializeViews(View view) {
        editTextA1 = view.findViewById(R.id.editTextA1);
        editTextA2 = view.findViewById(R.id.editTextA2);
        editTextA3 = view.findViewById(R.id.editTextA3);
        textViewResult = view.findViewById(R.id.textViewResult);
        textViewApproved = view.findViewById(R.id.textViewApproved);
        textViewTooltip = view.findViewById(R.id.textViewTooltip);
        textViewValidation = view.findViewById(R.id.textViewValidation);
        buttonCalculate = view.findViewById(R.id.buttonCalculate);
    }

    private void setupCalculateButton() {
        buttonCalculate.setOnClickListener(v -> calculateGrade());
    }

    private void setupTooltip() {
        textViewTooltip.setOnClickListener(v -> showFormulaDialog());
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

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

        if (!isA1Valid && (TextUtils.isEmpty(a2Text) && TextUtils.isEmpty(a3Text))) {
            textViewValidation.setText(getString(R.string.validation_message_all));
            textViewValidation.setVisibility(View.VISIBLE);
        } else if (!isA1Valid) {
            textViewValidation.setText(getString(R.string.validation_message_a1));
            textViewValidation.setVisibility(View.VISIBLE);
        } else if (TextUtils.isEmpty(a2Text) && TextUtils.isEmpty(a3Text)) {
            textViewValidation.setText(getString(R.string.validation_message_a2_a3));
            textViewValidation.setVisibility(View.VISIBLE);
        } else {
            textViewValidation.setVisibility(View.GONE);
        }

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
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.formula_dialog_title))
                .setMessage(getString(R.string.formula_dialog_content))
                .setNeutralButton(getString(R.string.formula_source_title), (dialog, which) -> openFormulaSource())
                .setPositiveButton(getString(R.string.ok), null)
                .show();
    }

    private void openFormulaSource() {
        String url = getString(R.string.formula_source_url);

        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
        intent.setData(android.net.Uri.parse(url));
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void calculateGrade() {
        String a1Text = editTextA1.getText().toString().trim().replace(",", ".");
        if (TextUtils.isEmpty(a1Text)) {
            Toast.makeText(getContext(), getString(R.string.a1_required), Toast.LENGTH_SHORT).show();
            return;
        }

        double a1;
        try {
            a1 = Double.parseDouble(a1Text);
            if (a1 < 0 || a1 > 10) {
                Toast.makeText(getContext(), getString(R.string.a1_range_error), Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), getString(R.string.a1_invalid_number), Toast.LENGTH_SHORT).show();
            return;
        }

        String a2Text = editTextA2.getText().toString().trim().replace(",", ".");
        String a3Text = editTextA3.getText().toString().trim().replace(",", ".");

        double a2 = 0, a3 = 0;
        boolean hasA2 = false, hasA3 = false;

        if (!TextUtils.isEmpty(a2Text)) {
            try {
                a2 = Double.parseDouble(a2Text);
                if (a2 < 0 || a2 > 10) {
                    Toast.makeText(getContext(), getString(R.string.a2_range_error), Toast.LENGTH_SHORT).show();
                    return;
                }
                hasA2 = true;
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), getString(R.string.a2_invalid_number), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!TextUtils.isEmpty(a3Text)) {
            try {
                a3 = Double.parseDouble(a3Text);
                if (a3 < 0 || a3 > 10) {
                    Toast.makeText(getContext(), getString(R.string.a3_range_error), Toast.LENGTH_SHORT).show();
                    return;
                }
                hasA3 = true;
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), getString(R.string.a3_invalid_number), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!hasA2 && !hasA3) {
            Toast.makeText(getContext(), getString(R.string.validation_message_a2_a3), Toast.LENGTH_SHORT).show();
            return;
        }

        double secondGrade = hasA2 && hasA3 ? Math.max(a2, a3) : (hasA2 ? a2 : a3);
        double nfp = (a1 * 0.4) + (secondGrade * 0.6);

        textViewResult.setText(String.format(Locale.getDefault(), "%.2f", nfp));

        if (a1 == 0) {
            textViewApproved.setText(getString(R.string.failed));
            textViewApproved.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if (nfp >= 6.0) {
            textViewApproved.setText(getString(R.string.approved));
            textViewApproved.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            textViewApproved.setText(getString(R.string.failed));
            textViewApproved.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }
}
