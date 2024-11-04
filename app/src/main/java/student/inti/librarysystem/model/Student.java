package student.inti.librarysystem.model;

import com.google.firebase.firestore.PropertyName;

public class Student {
    private String studentId; // Remove @DocumentId annotation
    private String name;
    private String email;
    private String profileImageUrl;
    private String salt;
    private String hashedPassword;

    // Required empty constructor for Firestore
    public Student() {}

    public Student(String studentId, String name, String email) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
    }

    // Getters and Setters with PropertyName annotations
    @PropertyName("studentId")
    public String getStudentId() {
        return studentId;
    }

    @PropertyName("studentId")
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("email")
    public String getEmail() {
        return email;
    }

    @PropertyName("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @PropertyName("profileImageUrl")
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    @PropertyName("profileImageUrl")
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    @PropertyName("salt")
    public String getSalt() {
        return salt;
    }

    @PropertyName("salt")
    public void setSalt(String salt) {
        this.salt = salt;
    }

    @PropertyName("hashedPassword")
    public String getHashedPassword() {
        return hashedPassword;
    }

    @PropertyName("hashedPassword")
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
}