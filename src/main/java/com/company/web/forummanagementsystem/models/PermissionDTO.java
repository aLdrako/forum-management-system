package com.company.web.forummanagementsystem.models;

public class PermissionDTO {
    private boolean isAdmin;
    private boolean isBlocked;

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}
