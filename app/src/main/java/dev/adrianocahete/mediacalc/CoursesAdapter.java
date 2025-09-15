package dev.adrianocahete.mediacalc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class CoursesAdapter extends BaseAdapter {

    private List<Course> courses;
    private OnCourseClickListener listener;
    private OnCourseDeleteListener deleteListener;
    private LayoutInflater inflater;

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

    @Override
    public int getCount() {
        return courses.size();
    }

    @Override
    public Course getItem(int position) {
        return courses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return courses.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            if (inflater == null) {
                inflater = LayoutInflater.from(parent.getContext());
            }
            convertView = inflater.inflate(R.layout.item_course, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Course course = getItem(position);
        holder.bind(course, listener, deleteListener);

        return convertView;
    }

    public void updateCourses(List<Course> newCourses) {
        this.courses = newCourses;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        private TextView textViewCourseName;
        private TextView textViewCourseCode;
        private TextView textViewStartsAt;
        private TextView textViewPeriodo;
        private TextView textViewGradeCurrent;
        private TextView textViewGradeFinal;
        private TextView textViewSource;
        private ImageButton buttonDelete;
        private View itemView;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
            textViewCourseName = itemView.findViewById(R.id.textViewCourseName);
            textViewCourseCode = itemView.findViewById(R.id.textViewCourseCode);
            textViewStartsAt = itemView.findViewById(R.id.textViewStartsAt);
            textViewPeriodo = itemView.findViewById(R.id.textViewPeriodo);
            textViewGradeCurrent = itemView.findViewById(R.id.textViewGradeCurrent);
            textViewGradeFinal = itemView.findViewById(R.id.textViewGradeFinal);
            textViewSource = itemView.findViewById(R.id.textViewSource);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }

        public void bind(Course course, OnCourseClickListener listener, OnCourseDeleteListener deleteListener) {
            textViewCourseName.setText(course.getName());

            // Show course code if available
            if (course.getCourseCode() != null && !course.getCourseCode().trim().isEmpty()) {
                textViewCourseCode.setText(course.getCourseCode());
                textViewCourseCode.setVisibility(View.VISIBLE);
            } else {
                textViewCourseCode.setVisibility(View.GONE);
            }

            // Show start date if available
            if (course.getStartsAt() != null && !course.getStartsAt().trim().isEmpty()) {
                textViewStartsAt.setText("Início: " + course.getStartsAt());
                textViewStartsAt.setVisibility(View.VISIBLE);
            } else {
                textViewStartsAt.setVisibility(View.GONE);
            }

            // Show period (stored in type field) if available
            if (course.getType() != null && !course.getType().trim().isEmpty()) {
                textViewPeriodo.setText("Período: " + course.getType());
                textViewPeriodo.setVisibility(View.VISIBLE);
            } else {
                textViewPeriodo.setVisibility(View.GONE);
            }

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
