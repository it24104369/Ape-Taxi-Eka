package com.taxi.admin.util;

import com.taxi.admin.model.AdminUser;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class FileUtil {

    private static final String FILE_PATH = "admins.txt";

    public static void writeToFile(String filepath, String data){
        try (FileWriter writer=new FileWriter(filepath,true))   {
            writer.write(data + "\n");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public static List<String> readAllAdmins(){
        List<String> adminList=new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            adminList.addAll(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return adminList;
    }

    private static void writeAllLines(String filepath,List<String> lines){
        try {
            Files.write(Paths.get(filepath),lines);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void logAdminActivity(String activity){
        try {
            String logMessage = LocalDateTime.now()+" - " +activity+ "\n";
            Files.write(Paths.get("activity_log.txt"), logMessage.getBytes(), StandardOpenOption.APPEND);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static AdminUser getAdminByUsername(String username){
        List<String> lines =readAllAdmins();
        for(String line : lines) {
            AdminUser admin = AdminUser.fromFileFormat(line);
            if (admin.getUsername().equals(username)) {
                return admin;
            }
        }
        return null;
    }

    public static void updateAdmin(AdminUser updatedAdmin){
        List<String> lines =readAllAdmins();
        List<String> updatedLines =new ArrayList<>();

        for(String line : lines){
            AdminUser admin=AdminUser.fromFileFormat(line);
            if(admin.getUsername().equals(updatedAdmin.getUsername())){
                updatedLines.add(updatedAdmin.toFileFormat());
            }
            else{
                updatedLines.add(line);
            }
        }
        writeAllLines("admins.txt",updatedLines);
    }

    public static void deleteAdmin(String username){
        List<String> lines =readAllAdmins();
        List<String> updatedLines = lines.stream().filter(line -> !line.startsWith(username+",")).collect(Collectors.toList());
        writeAllLines("admins.txt",updatedLines);
    }

    public static boolean isUsernametaken(String username){
        List<String> lines=readAllAdmins();
        for(String line: lines){
            AdminUser admin=AdminUser.fromFileFormat(line);
            if(admin.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

}
