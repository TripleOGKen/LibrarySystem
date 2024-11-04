package student.inti.librarysystem.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileManager {
    private static final String TAG = "ProfileManager";
    private static final String PROFILE_DIR = "profile_pictures";

    public static String saveProfilePicture(@NonNull Context context, @NonNull Uri imageUri) {
        try {
            // Create directory if it doesn't exist
            File directory = new File(context.getFilesDir(), PROFILE_DIR);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) {
                    Log.e(TAG, "Failed to create directory");
                    return null;
                }
            }

            // Create file for current user
            File outputFile = new File(directory, "P23014788.jpg");

            // Get bitmap from Uri and save it
            Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(), imageUri);

            FileOutputStream fos = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            return outputFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Error saving profile picture", e);
            return null;
        }
    }

    public static File getProfilePicture(@NonNull Context context, @NonNull String userId) {
        File file = new File(context.getFilesDir(), PROFILE_DIR + "/" + userId + ".jpg");
        return file.exists() ? file : null;
    }

    public static void deleteProfilePicture(@NonNull Context context, @NonNull String userId) {
        File file = new File(context.getFilesDir(), PROFILE_DIR + "/" + userId + ".jpg");
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                Log.e(TAG, "Failed to delete profile picture");
            }
        }
    }
}