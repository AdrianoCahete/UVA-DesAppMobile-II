package dev.adrianocahete.mediacalc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mediacalc.db";
    private static final int DATABASE_VERSION = 1; // Back to version 1 since app never released

    // User
    private static final String TABLE_USER = "user";
    private static final String USER_ID = "id";
    private static final String USER_NOME = "nome";
    private static final String USER_MATRICULA = "matricula";
    private static final String USER_AVATAR_URL = "avatar_url";
    private static final String USER_API_KEY = "api_key";
    private static final String USER_CREATED_AT = "created_at";
    private static final String USER_CHANGED_BY = "changed_by";

    // Matéira
    private static final String TABLE_COURSE = "course";
    private static final String COURSE_ID = "id";
    private static final String COURSE_NAME = "name";
    private static final String COURSE_CODE = "course_code";
    private static final String COURSE_STARTS_AT = "starts_at";
    private static final String COURSE_ENROLLMENT_TERM_ID = "enrollment_term_id";
    private static final String COURSE_TYPE = "type";
    private static final String COURSE_CALENDAR_URL = "calendar_url";
    private static final String COURSE_GRADE_CURRENT = "grade_current";
    private static final String COURSE_GRADE_FINAL = "grade_final";
    private static final String COURSE_SOURCE = "source";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create User table
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + USER_NOME + " TEXT,"
                + USER_MATRICULA + " TEXT,"
                + USER_AVATAR_URL + " TEXT,"
                + USER_API_KEY + " TEXT,"
                + USER_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + USER_CHANGED_BY + " TEXT DEFAULT 'user'"
                + ")";

        // Create Course table
        String CREATE_COURSE_TABLE = "CREATE TABLE " + TABLE_COURSE + "("
                + COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COURSE_NAME + " TEXT NOT NULL,"
                + COURSE_CODE + " TEXT,"
                + COURSE_STARTS_AT + " DATETIME,"
                + COURSE_ENROLLMENT_TERM_ID + " INTEGER,"
                + COURSE_TYPE + " TEXT,"
                + COURSE_CALENDAR_URL + " TEXT,"
                + COURSE_GRADE_CURRENT + " REAL DEFAULT 0.0,"
                + COURSE_GRADE_FINAL + " REAL DEFAULT 0.0,"
                + COURSE_SOURCE + " TEXT DEFAULT 'user'"
                + ")";

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_COURSE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Since app was never released, this should never be called
        // But keeping it for safety
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // USER METHODS
    public long createUser(String nome, String matricula, String apiKey) {
        return createUser(nome, matricula, null, apiKey, "user");
    }

    public long createUser(String nome, String matricula, String avatarUrl, String apiKey, String changedBy) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_NOME, nome);
        values.put(USER_MATRICULA, matricula);
        values.put(USER_AVATAR_URL, avatarUrl);
        values.put(USER_API_KEY, apiKey);
        values.put(USER_CHANGED_BY, changedBy);

        long id = db.insert(TABLE_USER, null, values);
        db.close();
        return id;
    }

    public User getUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String selectQuery = "SELECT * FROM " + TABLE_USER + " ORDER BY " + USER_ID + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(USER_ID)));
            user.setNome(cursor.getString(cursor.getColumnIndexOrThrow(USER_NOME)));
            user.setMatricula(cursor.getString(cursor.getColumnIndexOrThrow(USER_MATRICULA)));
            user.setAvatarUrl(cursor.getString(cursor.getColumnIndexOrThrow(USER_AVATAR_URL)));
            user.setApiKey(cursor.getString(cursor.getColumnIndexOrThrow(USER_API_KEY)));
            user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(USER_CREATED_AT)));
            user.setChangedBy(cursor.getString(cursor.getColumnIndexOrThrow(USER_CHANGED_BY)));
        }

        cursor.close();
        db.close();
        return user;
    }

    public int updateUser(int id, String nome, String matricula, String apiKey) {
        return updateUser(id, nome, matricula, null, apiKey, "user");
    }

    public int updateUser(int id, String nome, String matricula, String avatarUrl, String apiKey, String changedBy) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_NOME, nome);
        values.put(USER_MATRICULA, matricula);
        values.put(USER_AVATAR_URL, avatarUrl);
        values.put(USER_API_KEY, apiKey);
        values.put(USER_CHANGED_BY, changedBy);

        int rowsAffected = db.update(TABLE_USER, values, USER_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();
        return rowsAffected;
    }

    public boolean hasUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_USER;
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count > 0;
    }

    // matérias
    public long createCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COURSE_NAME, course.getName());
        values.put(COURSE_CODE, course.getCourseCode());
        values.put(COURSE_STARTS_AT, course.getStartsAt());
        values.put(COURSE_ENROLLMENT_TERM_ID, course.getEnrollmentTermId());
        values.put(COURSE_TYPE, course.getType());
        values.put(COURSE_CALENDAR_URL, course.getCalendarUrl());
        values.put(COURSE_GRADE_CURRENT, course.getGradeCurrent());
        values.put(COURSE_GRADE_FINAL, course.getGradeFinal());
        values.put(COURSE_SOURCE, course.getSource());

        long id = db.insert(TABLE_COURSE, null, values);
        db.close();
        return id;
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_COURSE + " ORDER BY " + COURSE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Course course = new Course();
                course.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COURSE_ID)));
                course.setName(cursor.getString(cursor.getColumnIndexOrThrow(COURSE_NAME)));
                course.setCourseCode(cursor.getString(cursor.getColumnIndexOrThrow(COURSE_CODE)));
                course.setStartsAt(cursor.getString(cursor.getColumnIndexOrThrow(COURSE_STARTS_AT)));
                course.setEnrollmentTermId(cursor.getInt(cursor.getColumnIndexOrThrow(COURSE_ENROLLMENT_TERM_ID)));
                course.setType(cursor.getString(cursor.getColumnIndexOrThrow(COURSE_TYPE)));
                course.setCalendarUrl(cursor.getString(cursor.getColumnIndexOrThrow(COURSE_CALENDAR_URL)));
                course.setGradeCurrent(cursor.getDouble(cursor.getColumnIndexOrThrow(COURSE_GRADE_CURRENT)));
                course.setGradeFinal(cursor.getDouble(cursor.getColumnIndexOrThrow(COURSE_GRADE_FINAL)));
                course.setSource(cursor.getString(cursor.getColumnIndexOrThrow(COURSE_SOURCE)));
                courses.add(course);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return courses;
    }

    public Course getCourse(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Course course = null;

        String selectQuery = "SELECT * FROM " + TABLE_COURSE + " WHERE " + COURSE_ID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            course = new Course();
            course.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COURSE_ID)));
            course.setName(cursor.getString(cursor.getColumnIndexOrThrow(COURSE_NAME)));
            course.setCourseCode(cursor.getString(cursor.getColumnIndexOrThrow(COURSE_CODE)));
            course.setStartsAt(cursor.getString(cursor.getColumnIndexOrThrow(COURSE_STARTS_AT)));
            course.setEnrollmentTermId(cursor.getInt(cursor.getColumnIndexOrThrow(COURSE_ENROLLMENT_TERM_ID)));
            course.setType(cursor.getString(cursor.getColumnIndexOrThrow(COURSE_TYPE)));
            course.setCalendarUrl(cursor.getString(cursor.getColumnIndexOrThrow(COURSE_CALENDAR_URL)));
            course.setGradeCurrent(cursor.getDouble(cursor.getColumnIndexOrThrow(COURSE_GRADE_CURRENT)));
            course.setGradeFinal(cursor.getDouble(cursor.getColumnIndexOrThrow(COURSE_GRADE_FINAL)));
            course.setSource(cursor.getString(cursor.getColumnIndexOrThrow(COURSE_SOURCE)));
        }

        cursor.close();
        db.close();
        return course;
    }

    public int updateCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COURSE_NAME, course.getName());
        values.put(COURSE_CODE, course.getCourseCode());
        values.put(COURSE_STARTS_AT, course.getStartsAt());
        values.put(COURSE_ENROLLMENT_TERM_ID, course.getEnrollmentTermId());
        values.put(COURSE_TYPE, course.getType());
        values.put(COURSE_CALENDAR_URL, course.getCalendarUrl());
        values.put(COURSE_GRADE_CURRENT, course.getGradeCurrent());
        values.put(COURSE_GRADE_FINAL, course.getGradeFinal());
        values.put(COURSE_SOURCE, course.getSource());

        int rowsAffected = db.update(TABLE_COURSE, values, COURSE_ID + " = ?",
                new String[]{String.valueOf(course.getId())});

        db.close();
        return rowsAffected;
    }

    public void deleteCourse(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COURSE, COURSE_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteAllApiCourses() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COURSE, COURSE_SOURCE + " = ?", new String[]{"api"});
        db.close();
    }
}
