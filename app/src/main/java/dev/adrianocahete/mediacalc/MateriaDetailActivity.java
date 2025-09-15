package dev.adrianocahete.mediacalc;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class MateriaDetailActivity extends AppCompatActivity {

    private TextInputEditText editTextCourseName;
    private TextInputEditText editTextCourseCode;
    private TextInputEditText editTextStartsAt;
    private TextInputEditText editTextType;
    private TextInputEditText editTextGradeCurrent;
    private TextInputEditText editTextGradeFinal;
    private Button buttonSave;
    private ProgressBar progressBar;

    private DatabaseHelper databaseHelper;
    private Course course;
    private int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materia_detail);

        initializeViews();
        setupDatabase();
        getCourseFromIntent();
        loadCourseData();
        setupSaveButton();
    }

    private void initializeViews() {
        editTextCourseName = findViewById(R.id.editTextCourseName);
        editTextCourseCode = findViewById(R.id.editTextCourseCode);
        editTextStartsAt = findViewById(R.id.editTextStartsAt);
        editTextType = findViewById(R.id.editTextType);
        editTextGradeCurrent = findViewById(R.id.editTextGradeCurrent);
        editTextGradeFinal = findViewById(R.id.editTextGradeFinal);
        buttonSave = findViewById(R.id.buttonSave);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void getCourseFromIntent() {
        courseId = getIntent().getIntExtra("course_id", -1);
        if (courseId == -1) {
            Toast.makeText(this, "Erro: ID do curso não encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadCourseData() {
        showLoading(true);

        course = databaseHelper.getCourse(courseId);

        if (course == null) {
            Toast.makeText(this, "Curso não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        populateFields();
        showLoading(false);
    }

    private void populateFields() {
        editTextCourseName.setText(course.getName());
        editTextCourseCode.setText(course.getCourseCode());
        editTextStartsAt.setText(course.getStartsAt());
        editTextType.setText(course.getType());

        if (course.getGradeCurrent() > 0) {
            editTextGradeCurrent.setText(String.valueOf(course.getGradeCurrent()));
        }

        if (course.getGradeFinal() > 0) {
            editTextGradeFinal.setText(String.valueOf(course.getGradeFinal()));
        }

        // Disable editing for API-sourced courses except for grades
        boolean isApiCourse = "api".equals(course.getSource());
        editTextCourseName.setEnabled(!isApiCourse);
        editTextCourseCode.setEnabled(!isApiCourse);
        editTextStartsAt.setEnabled(!isApiCourse);
        editTextType.setEnabled(!isApiCourse);
    }

    private void setupSaveButton() {
        buttonSave.setOnClickListener(v -> saveCourse());
    }

    private void saveCourse() {
        if (!validateInput()) {
            return;
        }

        showLoading(true);

        updateCourseFromFields();

        long result = databaseHelper.updateCourse(course);

        showLoading(false);

        if (result > 0) {
            Toast.makeText(this, "Curso atualizado com sucesso", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Erro ao atualizar curso", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput() {
        String courseName = editTextCourseName.getText().toString().trim();

        if (TextUtils.isEmpty(courseName)) {
            editTextCourseName.setError("Nome da matéria é obrigatório");
            editTextCourseName.requestFocus();
            return false;
        }

        return true;
    }

    private void updateCourseFromFields() {
        course.setName(editTextCourseName.getText().toString().trim());
        course.setCourseCode(editTextCourseCode.getText().toString().trim());
        course.setStartsAt(editTextStartsAt.getText().toString().trim());
        course.setType(editTextType.getText().toString().trim());

        String gradeCurrentStr = editTextGradeCurrent.getText().toString().trim();
        if (!TextUtils.isEmpty(gradeCurrentStr)) {
            try {
                course.setGradeCurrent(Double.parseDouble(gradeCurrentStr));
            } catch (NumberFormatException e) {
                course.setGradeCurrent(0.0);
            }
        } else {
            course.setGradeCurrent(0.0);
        }

        String gradeFinalStr = editTextGradeFinal.getText().toString().trim();
        if (!TextUtils.isEmpty(gradeFinalStr)) {
            try {
                course.setGradeFinal(Double.parseDouble(gradeFinalStr));
            } catch (NumberFormatException e) {
                course.setGradeFinal(0.0);
            }
        } else {
            course.setGradeFinal(0.0);
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        buttonSave.setEnabled(!show);
    }
}
