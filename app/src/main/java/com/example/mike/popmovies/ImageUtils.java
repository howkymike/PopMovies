package com.example.mike.popmovies;


import android.content.Context;
import android.util.Log;
import java.io.File;

/**
 * Created by Mike on 2/23/2018.
 */


// to simplify the code in future
public class ImageUtils {

    private static final String TAG = ImageUtils.class.getSimpleName();


    public static void deleteImage(Context context, String fileName) {
        File dir = context.getFilesDir();
        File file = new File(dir, fileName);
        boolean deleted = file.delete();
        Log.v(TAG, "Deleting status: " + deleted+ ", "+ fileName);
    }
}
