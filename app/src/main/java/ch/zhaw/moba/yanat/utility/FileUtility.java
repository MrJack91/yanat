package ch.zhaw.moba.yanat.utility;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by michael on 19.03.16.
 */
public class FileUtility {

    protected Context context = null;

    public FileUtility(Context context) {
        this.context = context;
    }

    public File saveFile(Uri returnUri, String filename) {
        // src: http://www.mysamplecode.com/2012/06/android-internal-external-storage.html

        // handle paths
        String src = returnUri.getPath();
        File source = new File(src);

        Log.d("src is ", source.toString());
        Log.d("FileName is ", filename);

        // create folder structure & copy file
        File newFile = this.getChildrenFolder(filename);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(returnUri);
            FileOutputStream outputStream = new FileOutputStream(newFile); // true will be same as Context.MODE_APPEND

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newFile;
    }

    public static boolean copyFile(String from, String to) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(from);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(from);
                FileOutputStream fs = new FileOutputStream(to);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private File getChildrenFolder(String path) {
        File dir = context.getFilesDir();
        List<String> dirs = new ArrayList<String>(Arrays.<String>asList(path.split("/")));
        for (int i = 0; i < dirs.size(); ++i) { // use size-1 to skip file name
            // is folder name
            dir = new File(dir, dirs.get(i)); //Getting a file within the dir.
            if (i != dirs.size() - 1) {
                // until its last part, create folder
                if (!dir.exists()) {
                    dir.mkdir();
                }
            }
        }
        return dir;
    }

    /*
    private static void directoryExist(File destination) {
        if (!destination.isDirectory()) {
            if (destination.mkdirs()) {
                Log.d("Carpeta creada", "....");
            } else {
                Log.d("Carpeta no creada", "....");
            }
        }
    }
    */


}
