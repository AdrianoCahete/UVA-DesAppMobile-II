package dev.adrianocahete.mediacalc;

public class Course {
    private int id;
    private String name;
    private String courseCode;
    private String startsAt;
    private int enrollmentTermId;
    private String type;
    private String calendarUrl;
    private double gradeCurrent;
    private double gradeFinal;
    private String source; // 'user' or 'api'

    public Course() {
    }

    public Course(String name, String courseCode) {
        this.name = name;
        this.courseCode = courseCode;
        this.source = "user"; // Default to user input
        this.gradeCurrent = 0.0;
        this.gradeFinal = 0.0;
    }

    public Course(String name, String courseCode, String startsAt, int enrollmentTermId,
                  String type, String calendarUrl, double gradeCurrent, double gradeFinal, String source) {
        this.name = name;
        this.courseCode = courseCode;
        this.startsAt = startsAt;
        this.enrollmentTermId = enrollmentTermId;
        this.type = type;
        this.calendarUrl = calendarUrl;
        this.gradeCurrent = gradeCurrent;
        this.gradeFinal = gradeFinal;
        this.source = source;
    }

    // Getters
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getCourseCode() {
        return courseCode;
    }
    public String getStartsAt() {
        return startsAt;
    }
    public int getEnrollmentTermId() {
        return enrollmentTermId;
    }
    public String getType() {
        return type;
    }
    public String getCalendarUrl() {
        return calendarUrl;
    }
    public double getGradeCurrent() {
        return gradeCurrent;
    }
    public double getGradeFinal() {
        return gradeFinal;
    }
    public String getSource() {
        return source;
    }


    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
    public void setStartsAt(String startsAt) {
        this.startsAt = startsAt;
    }
    public void setEnrollmentTermId(int enrollmentTermId) {
        this.enrollmentTermId = enrollmentTermId;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setCalendarUrl(String calendarUrl) {
        this.calendarUrl = calendarUrl;
    }
    public void setGradeCurrent(double gradeCurrent) {
        this.gradeCurrent = gradeCurrent;
    }
    public void setGradeFinal(double gradeFinal) {
        this.gradeFinal = gradeFinal;
    }
    public void setSource(String source) {
        this.source = source;
    }

    public boolean isFromApi() {
        return "api".equals(source);
    }
    public boolean isFromUser() {
        return "user".equals(source);
    }
    public boolean hasCurrentGrade() {
        return gradeCurrent > 0.0;
    }
    public boolean hasFinalGrade() {
        return gradeFinal > 0.0;
    }
}
