package student.inti.librarysystem.ui.login;

import student.inti.librarysystem.data.entity.Student;

public class LoginResult {
    public final boolean success;
    public final String errorMessage;
    public final Student student;

    public LoginResult(boolean success, String errorMessage, Student student) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.student = student;
    }
}