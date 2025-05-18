package com.taxi.admin.service;

import com.taxi.admin.model.AdminUser;
import com.taxi.admin.util.FileUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    public boolean isUsernameTaken(String username) {
        return FileUtil.isUsernametaken(username);
    }

    public void saveAdmin(AdminUser admin) {
        FileUtil.writeToFile("admins.txt", admin.toFileFormat());
    }

    public void updateAdmin(AdminUser admin) {
        FileUtil.updateAdmin(admin);
    }

    public void deleteAdmin(String username) {
        FileUtil.deleteAdmin(username);
    }

    public List<String> getAllAdmins() {
        return FileUtil.readAllAdmins();
    }

    public AdminUser getAdminByUsername(String username) {
        return FileUtil.getAdminByUsername(username);
    }

    public void logActivity(String message) {
        FileUtil.logAdminActivity(message);
    }
}
