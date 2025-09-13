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
        try {
            double value = Double.parseDouble(text);
            return value >= 0.0 && value <= 10.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void calculateGrade() {
        String a1Text = editTextA1.getText().toString().trim();
        String a2Text = editTextA2.getText().toString().trim();
        String a3Text = editTextA3.getText().toString().trim();

        if (TextUtils.isEmpty(a1Text)) {
            Toast.makeText(getContext(), getString(R.string.a1_required), Toast.LENGTH_SHORT).show();
            editTextA1.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(a2Text) && TextUtils.isEmpty(a3Text)) {
            Toast.makeText(getContext(), getString(R.string.a2_or_a3_required), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double a1 = Double.parseDouble(a1Text);
            double a2 = TextUtils.isEmpty(a2Text) ? 0.0 : Double.parseDouble(a2Text);
            double a3 = TextUtils.isEmpty(a3Text) ? 0.0 : Double.parseDouble(a3Text);

            if (!isValidGrade(a1) || (!TextUtils.isEmpty(a2Text) && !isValidGrade(a2)) ||
                    (!TextUtils.isEmpty(a3Text) && !isValidGrade(a3))) {
                Toast.makeText(getContext(), getString(R.string.invalid_grade_range), Toast.LENGTH_SHORT).show();
                return;
            }

            double media = (a1 * 4 + a2 * 3 + a3 * 3) / 10.0;

            textViewResult.setText(String.format(Locale.getDefault(), "%.2f", media));
            textViewResult.setVisibility(View.VISIBLE);

            if (media >= 7.0) {
                textViewApproved.setText(getString(R.string.approved));
                textViewApproved.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                textViewApproved.setText(getString(R.string.not_approved));
                textViewApproved.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
            textViewApproved.setVisibility(View.VISIBLE);

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), getString(R.string.invalid_number_format), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidGrade(double grade) {
        return grade >= 0.0 && grade <= 10.0;
    }

    private void showFormulaDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.formula_dialog_title))
                .setMessage(getString(R.string.formula_dialog_message))
                .setPositiveButton(getString(R.string.ok_button), null)
                .show();
    }
}
