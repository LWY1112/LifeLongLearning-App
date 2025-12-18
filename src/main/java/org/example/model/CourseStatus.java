package org.example.model;

/**
 * Enum representing the status of a course enrollment.
 * Week 1 - Encapsulation: Using enum to encapsulate valid course statuses.
 */
public enum CourseStatus {
    NOT_ENROLLED("Not Enrolled"),
    ENROLLED("Enrolled"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String displayName;

    CourseStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Convert string to CourseStatus enum
     * @param status String representation of status
     * @return CourseStatus enum, defaults to NOT_ENROLLED if invalid
     */
    public static CourseStatus fromString(String status) {
        if (status == null || status.isEmpty()) {
            return NOT_ENROLLED;
        }
        for (CourseStatus cs : values()) {
            if (cs.displayName.equalsIgnoreCase(status) || cs.name().equalsIgnoreCase(status)) {
                return cs;
            }
        }
        return NOT_ENROLLED;
    }
}

