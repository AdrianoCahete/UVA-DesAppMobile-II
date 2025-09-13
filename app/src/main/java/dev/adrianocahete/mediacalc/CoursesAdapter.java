package dev.adrianocahete.mediacalc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CourseViewHolder> {

    private List<Course> courses;
    private OnCourseClickListener listener;
    private OnCourseDeleteListener deleteListener;

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    public interface OnCourseDeleteListener {
        void onCourseDelete(Course course);
    }

    public CoursesAdapter(List<Course> courses, OnCourseClickListener listener, OnCourseDeleteListener deleteListener) {
        this.courses = courses;
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.bind(course, listener, deleteListener);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void updateCourses(List<Course> newCourses) {
        this.courses = newCourses;
        notifyDataSetChanged();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewCourseName;
        private TextView textViewCourseCode;
        private TextView textViewGradeCurrent;
        private TextView textViewGradeFinal;
        private TextView textViewSource;
        private ImageButton buttonDelete;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCourseName = itemView.findViewById(R.id.textViewCourseName);
            textViewCourseCode = itemView.findViewById(R.id.textViewCourseCode);
            textViewGradeCurrent = itemView.findViewById(R.id.textViewGradeCurrent);
            textViewGradeFinal = itemView.findViewById(R.id.textViewGradeFinal);
            textViewSource = itemView.findViewById(R.id.textViewSource);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }

        public void bind(Course course, OnCourseClickListener listener, OnCourseDeleteListener deleteListener) {
            textViewCourseName.setText(course.getName());
            textViewCourseCode.setText(course.getCourseCode());

            // Show current grade if available
            if (course.hasCurrentGrade()) {
                textViewGradeCurrent.setText(String.format("Atual: %.1f", course.getGradeCurrent()));
                textViewGradeCurrent.setVisibility(View.VISIBLE);
            } else {
                textViewGradeCurrent.setVisibility(View.GONE);
            }

            // Show final grade if available
            if (course.hasFinalGrade()) {
                textViewGradeFinal.setText(String.format("Final: %.1f", course.getGradeFinal()));
                textViewGradeFinal.setVisibility(View.VISIBLE);
            } else {
                textViewGradeFinal.setVisibility(View.GONE);
            }

            // Show source indicator
            textViewSource.setText(course.isFromApi() ? "API" : "Manual");
            textViewSource.setTextColor(course.isFromApi() ?
                itemView.getContext().getColor(android.R.color.holo_blue_dark) :
                itemView.getContext().getColor(android.R.color.holo_green_dark));

            // Show/hide delete button based on source
            if ("user".equals(course.getSource())) {
                buttonDelete.setVisibility(View.VISIBLE);
                buttonDelete.setOnClickListener(v -> {
                    if (deleteListener != null) {
                        deleteListener.onCourseDelete(course);
                    }
                });
            } else {
                buttonDelete.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCourseClick(course);
                }
            });
        }
    }
}
