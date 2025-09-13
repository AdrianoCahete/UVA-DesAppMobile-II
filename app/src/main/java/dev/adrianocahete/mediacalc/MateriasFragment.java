package dev.adrianocahete.mediacalc;

import android.app.AlertDialog;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONObject;

public class MateriasFragment extends Fragment {

    private RecyclerView recyclerViewCourses;
    private Button buttonAddCourse;
    private Button buttonResyncApi;
    private TextView textViewEmptyState;
    private DatabaseHelper databaseHelper;
    private CoursesAdapter coursesAdapter;
    private ExecutorService executor;
    private Handler mainHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_materias, container, false);

        initializeViews(view);
        setupDatabase();
        setupButtons();

        // Initialize executor for background tasks
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // Check if user has API key and call endpoint if available
        checkApiKeyAndLoadCourses();

        return view;
    }

    private void initializeViews(View view) {
        recyclerViewCourses = view.findViewById(R.id.recyclerViewCourses);
        buttonAddCourse = view.findViewById(R.id.buttonAddCourse);
        buttonResyncApi = view.findViewById(R.id.buttonResyncApi);
        textViewEmptyState = view.findViewById(R.id.textViewEmptyState);

        recyclerViewCourses.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupDatabase() {
        databaseHelper = new DatabaseHelper(getContext());
    }

    private void setupButtons() {
        buttonAddCourse.setOnClickListener(v -> showAddCourseDialog());
        buttonResyncApi.setOnClickListener(v -> resyncFromApi());

        // Show/hide resync button based on API key availability
        updateResyncButtonVisibility();
    }

    private void updateResyncButtonVisibility() {
        User user = databaseHelper.getUser();
        boolean hasApiKey = user != null && !TextUtils.isEmpty(user.getApiKey());
        buttonResyncApi.setVisibility(hasApiKey ? View.VISIBLE : View.GONE);
    }

    private void checkApiKeyAndLoadCourses() {
        User user = databaseHelper.getUser();

        if (user != null && !TextUtils.isEmpty(user.getApiKey())) {
            // User has API key - call endpoint for materias
            callMateriasEndpoint(user.getApiKey());
        } else {
            // No API key - just load existing courses
            loadCourses();
        }
    }

    private void callMateriasEndpoint(String apiKey) {
        // Show loading message
        Toast.makeText(getContext(), getString(R.string.syncing_api), Toast.LENGTH_SHORT).show();

        executor.execute(() -> {
            try {
                // Call materias endpoint using example.com for now
                // TODO: Replace with actual Canvas API URL when provided
                URL url = new URL("https://example.com/api/courses");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + apiKey);
                connection.setRequestProperty("Accept", "application/json");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Process API courses
                    mainHandler.post(() -> {
                        try {
                            processApiCoursesResponse(response.toString());
                            loadCourses(); // Refresh the list after processing API response
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Error processing courses: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            loadCourses(); // Still load existing courses
                        }
                    });
                } else {
                    mainHandler.post(() -> {
                        Toast.makeText(getContext(), "API sync failed. Loading existing courses.", Toast.LENGTH_SHORT).show();
                        loadCourses(); // Load existing courses on API failure
                    });
                }
                connection.disconnect();
            } catch (Exception e) {
                mainHandler.post(() -> {
                    Toast.makeText(getContext(), "Network error. Loading existing courses.", Toast.LENGTH_SHORT).show();
                    loadCourses(); // Load existing courses on network error
                });
            }
        });
    }

    private void loadCourses() {
        List<Course> courses = databaseHelper.getAllCourses();

        if (courses.isEmpty()) {
            showEmptyState();
        } else {
            showCoursesList(courses);
        }
    }

    private void showEmptyState() {
        textViewEmptyState.setVisibility(View.VISIBLE);
        recyclerViewCourses.setVisibility(View.GONE);

        User user = databaseHelper.getUser();
        boolean hasApiKey = user != null && !TextUtils.isEmpty(user.getApiKey());

        if (hasApiKey) {
            // User has API key but no courses found
            textViewEmptyState.setText(getString(R.string.no_courses_found));
        } else {
            // User doesn't have API key
            textViewEmptyState.setText(getString(R.string.no_courses_no_api_key));
        }
    }

    private void showCoursesList(List<Course> courses) {
        textViewEmptyState.setVisibility(View.GONE);
        recyclerViewCourses.setVisibility(View.VISIBLE);

        if (coursesAdapter == null) {
            coursesAdapter = new CoursesAdapter(courses, this::onCourseClick, this::onCourseDelete);
            recyclerViewCourses.setAdapter(coursesAdapter);
        } else {
            coursesAdapter.updateCourses(courses);
        }
    }

    private void showAddCourseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.add_course_dialog_title));

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_course, null);
        EditText editTextCourseName = dialogView.findViewById(R.id.editTextCourseName);
        EditText editTextCourseCode = dialogView.findViewById(R.id.editTextCourseCode);

        builder.setView(dialogView);
        builder.setPositiveButton(getString(R.string.add_button), (dialog, which) -> {
            String courseName = editTextCourseName.getText().toString().trim();
            String courseCode = editTextCourseCode.getText().toString().trim();

            if (TextUtils.isEmpty(courseName)) {
                Toast.makeText(getContext(), getString(R.string.course_name_required), Toast.LENGTH_SHORT).show();
                return;
            }

            Course course = new Course(courseName, courseCode);
            course.setSource("user");

            long result = databaseHelper.createCourse(course);
            if (result != -1) {
                Toast.makeText(getContext(), getString(R.string.course_added_success), Toast.LENGTH_SHORT).show();
                loadCourses();
            } else {
                Toast.makeText(getContext(), getString(R.string.course_add_error), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel_button), null);
        builder.show();
    }

    private void resyncFromApi() {
        User user = databaseHelper.getUser();
        if (user == null || TextUtils.isEmpty(user.getApiKey())) {
            Toast.makeText(getContext(), getString(R.string.api_key_not_configured), Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading message
        Toast.makeText(getContext(), getString(R.string.syncing_api), Toast.LENGTH_SHORT).show();

        // Call notifications endpoint and sync courses
        callNotificationsAndSync();

        // Perform Canvas API sync
        performCanvasApiSync(user.getApiKey());
    }

    private void callNotificationsAndSync() {
        executor.execute(() -> {
            try {
                // Call notifications endpoint using example.com
                URL url = new URL("https://example.com/api/notifications");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Process notifications response (placeholder)
                    mainHandler.post(() -> {
                        // Handle notifications response on main thread
                        processNotificationsResponse(response.toString());
                    });
                } else {
                    mainHandler.post(() -> {
                        Toast.makeText(getContext(), "Failed to fetch notifications", Toast.LENGTH_SHORT).show();
                    });
                }
                connection.disconnect();
            } catch (Exception e) {
                mainHandler.post(() -> {
                    Toast.makeText(getContext(), "Notifications endpoint error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void processNotificationsResponse(String response) {
        // Process notifications response (placeholder implementation)
        // This would handle any notifications from the API
        try {
            JSONObject jsonResponse = new JSONObject(response);
            // Process notifications as needed
        } catch (Exception e) {
            // Handle JSON parsing error
        }
    }

    private void performCanvasApiSync(String apiKey) {
        executor.execute(() -> {
            try {
                // Simulate Canvas API call for now with example.com
                // TODO: Replace with actual Canvas API URL when provided
                URL url = new URL("https://example.com/api/courses");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + apiKey);
                connection.setRequestProperty("Accept", "application/json");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Process API courses
                    mainHandler.post(() -> {
                        try {
                            processApiCoursesResponse(response.toString());
                            Toast.makeText(getContext(), getString(R.string.api_sync_success), Toast.LENGTH_SHORT).show();
                            loadCourses();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Error processing courses: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mainHandler.post(() -> {
                        Toast.makeText(getContext(), "API sync failed. Response code: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
                connection.disconnect();
            } catch (Exception e) {
                mainHandler.post(() -> {
                    Toast.makeText(getContext(), "Canvas API error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void processApiCoursesResponse(String response) throws Exception {
        JSONArray coursesArray = new JSONArray(response);

        for (int i = 0; i < coursesArray.length(); i++) {
            JSONObject courseJson = coursesArray.getJSONObject(i);

            String courseId = courseJson.optString("id", "");
            String courseName = courseJson.optString("name", "");
            String courseCode = courseJson.optString("course_code", "");
            String startsAt = courseJson.optString("start_at", "");
            int enrollmentTermId = courseJson.optInt("enrollment_term_id", 0);
            String calendarUrl = courseJson.optString("calendar", "");

            // Create course object from API data
            Course apiCourse = new Course();
            apiCourse.setName(courseName);
            apiCourse.setCourseCode(courseCode);
            apiCourse.setStartsAt(startsAt);
            apiCourse.setEnrollmentTermId(enrollmentTermId);
            apiCourse.setCalendarUrl(calendarUrl);
            apiCourse.setSource("api");

            // Check if course already exists
            Course existingCourse = findExistingCourse(courseId, courseName);

            if (existingCourse != null && "api".equals(existingCourse.getSource())) {
                // Update existing API course - DO NOT DELETE
                apiCourse.setId(existingCourse.getId());
                databaseHelper.updateCourse(apiCourse);
            } else if (existingCourse == null) {
                // Insert new course from API - DO NOT DELETE existing courses
                databaseHelper.createCourse(apiCourse);
            }
            // Never delete courses - preserve both user-created and existing API courses
        }
    }

    private Course findExistingCourse(String courseId, String courseName) {
        List<Course> existingCourses = databaseHelper.getAllCourses();

        for (Course course : existingCourses) {
            if ((course.getCourseCode() != null && course.getCourseCode().equals(courseId)) ||
                (course.getName() != null && course.getName().equals(courseName))) {
                return course;
            }
        }
        return null;
    }

    private void onCourseClick(Course course) {
        // TODO: Open course details/edit dialog
        Toast.makeText(getContext(), getString(R.string.course_details_placeholder) + " " + course.getName(), Toast.LENGTH_SHORT).show();
    }

    private void onCourseDelete(Course course) {
        // Only allow deletion of user-created courses
        if (!"user".equals(course.getSource())) {
            Toast.makeText(getContext(), getString(R.string.cannot_delete_api_course), Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.delete_course_title))
                .setMessage(getString(R.string.delete_course_message, course.getName()))
                .setPositiveButton(getString(R.string.delete_button), (dialog, which) -> {
                    databaseHelper.deleteCourse(course.getId());
                    Toast.makeText(getContext(), getString(R.string.course_deleted_success), Toast.LENGTH_SHORT).show();
                    loadCourses();
                })
                .setNegativeButton(getString(R.string.cancel_button), null)
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
