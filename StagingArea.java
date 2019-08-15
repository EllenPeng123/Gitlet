package gitlet;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class StagingArea implements Serializable {
    public static final long serialVersionUID = 42L;
    Map<String, File> filesAdded; // filename to file

    public StagingArea() {
        if (new  File(".gitlet/stagingArea").exists()) {
            StagingArea stage = this.readObject();
            this.filesAdded = stage.filesAdded;
        } else {
            this.filesAdded = new HashMap<String, File>();
        }
    }
    public boolean checkFile(File file) {
        /**checks if a file is a part of the staging area
         *
         */

        return filesAdded.containsValue(file);
    }

    public void addFiles(String filename) {
        //We should be checking to see if the file has been modified first
        /**
         * This method adds file to the staging area
         */
        if (!filesAdded.containsKey(filename)) {
            filesAdded.put(filename, new File(filename));
            this.writeObject();
        }
    }

    public static StagingArea readObject() {
        /**
         * This method reads and deserializes the object of Staging Area class.
         * The file where the following object is stores is - .gitlet/stagingArea
         */
        try {
            FileInputStream file = new FileInputStream(".gitlet/stagingArea");
            ObjectInputStream in = new ObjectInputStream(file);
            StagingArea stage = (StagingArea)  in.readObject();
            in.close();
            file.close();
            return stage;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public void writeObject() {
        /**
         * This method serializes and writes the object of Staging Area class.
         */
        try {
            FileOutputStream fileOut = new FileOutputStream(".gitlet/stagingArea");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            System.out.println("IO Exception");

        }
    }
    public boolean checkFileName(String fileName) {
        try {
            return filesAdded.containsKey(fileName);
        } catch (NullPointerException e) {
            return false;
        }
    }
}

