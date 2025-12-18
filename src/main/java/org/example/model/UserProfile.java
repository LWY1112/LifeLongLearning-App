package org.example.model;

/**
 * Value object representing user profile information.
 * Week 1 - Encapsulation: Encapsulates user profile data with validation.
 * Week 4 - Composition: Used as a component in Learner entity.
 */
public class UserProfile {
    private String fullName;
    private String phone;
    private String email;
    private String age;

    public UserProfile(String fullName, String phone, String email, String age) {
        // Allow null/empty values for backward compatibility with existing data
        this.fullName = fullName != null ? fullName.trim() : "";
        this.phone = phone != null ? phone.trim() : "";
        this.email = email != null ? email.trim() : "";
        this.age = age != null ? age.trim() : "";
    }

    private String validateAndSet(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
        return value.trim();
    }

    private String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return email.trim();
    }

    // Getters
    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAge() {
        return age;
    }

    // Setters with validation
    public void setFullName(String fullName) {
        this.fullName = validateAndSet(fullName, "Full Name");
    }

    public void setPhone(String phone) {
        this.phone = validateAndSet(phone, "Phone");
    }

    public void setEmail(String email) {
        this.email = validateEmail(email);
    }

    public void setAge(String age) {
        this.age = validateAndSet(age, "Age");
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}

