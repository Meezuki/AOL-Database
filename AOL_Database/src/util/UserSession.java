package util;

public class UserSession {
    private static String staffId;
    private static String staffName;

    // Set data saat login sukses
    public static void setSession(String id, String name) {
        staffId = id;
        staffName = name;
    }

    public static String getStaffId() {
        return staffId;
    }

    public static String getStaffName() {
        return staffName;
    }
    
    public static void cleanSession() {
        staffId = null;
        staffName = null;
    }
}