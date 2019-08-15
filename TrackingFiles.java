package gitlet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.io.File;
/**
 * Track the editing files using this class.
 * For now let's edit - wubby.txt
 */


public class TrackingFiles {

    public static boolean checkFile(byte[] currFile, byte[] checkFile) {
        /**
         * This method compares two files as byte streams - currFile, checkFile
         */
        return Arrays.equals(currFile, checkFile);
    }
    public static boolean checkFile(File curr, byte[] checkFile) {
        /**
         * This method compares two files - one as byte stream and,
         * the other as a File object.
         */
        return checkFile(Utils.readContents(curr), checkFile);
    }
    public static List<String> getFileNames() {
        /**
         * This method returns a list of files names in the working directory,
         * edited by user.
         */
        File folder = new File(System.getProperty("user.dir"));
        return Utils.plainFilenamesIn(folder);
    }
    public static ArrayList<File> getFiles() {
        /**
         * This method return a list of File objects,
         * this method is still being perfected.
         * It is not used so far.
         */
        List<String> fileName = getFileNames();
        ArrayList<File> files = new ArrayList<>();
        try {
            for (String name : fileName) {
                File file = new File(name);
                files.add(file);
            }
            return files;
        } catch (NullPointerException e) {
            return null;
        }
    }
    public static void writeToFile(String file, byte[] fileCont) {
        File name = new File(file);
        try {
            FileOutputStream out = new FileOutputStream(name, false);
            out.write(fileCont);
            out.close();
        } catch (IOException e) {
            System.out.println("Damn !!");
        }
    }
    public static void writeToFileConflict(String file, byte[] head, byte[] othfile) {
        File name = new File(file);
        try {
            FileOutputStream out = new FileOutputStream(name, false);
            out.write(("<<<<<<< HEAD\n").getBytes());
            if (head != null) {
                out.write(head);
            }
            out.write(("=======\n").getBytes());
            if (othfile != null) {
                out.write(othfile);
            }
            out.write((">>>>>>>\n").getBytes());
            out.close();
        } catch (IOException e) {
            System.out.println("Damn !!");
        }
    }
}
