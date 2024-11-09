package student.inti.librarysystem.util;

public class DriveUrlConverter {
    public static String convertToDirect(String driveUrl) {
        // Extract file ID from various Google Drive URL formats
        String fileId = null;

        if (driveUrl.contains("drive.google.com/file/d/")) {
            // Format: https://drive.google.com/file/d/[fileId]/view...
            fileId = driveUrl.split("/file/d/")[1].split("/")[0];
        } else if (driveUrl.contains("drive.google.com/open?id=")) {
            // Format: https://drive.google.com/open?id=[fileId]
            fileId = driveUrl.split("open\\?id=")[1].split("&")[0];
        }

        if (fileId != null) {
            return "https://drive.google.com/uc?export=download&id=" + fileId;
        }

        return driveUrl; // Return original if not a Drive URL
    }
}