package gitlet;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

public class Commit implements Serializable {

    String message;
    ArrayList<String> branchName;
    String hashID;
    String timestamp;  //https://stackoverflow.com/questions
                        // /8345023/need-to-get-current-timestamp-in-java
    String parentHashID;
    ArrayList<String> nextHashID;
    ArrayList<Commit> nextCommit;
    HashMap<String, byte[]> fileContents; // filename : 'Contents in file'

    public Commit(ArrayList<String> master) {
        /**
         * Constructor used for initial commit, during gitlet init
         */
        branchName = master;
        message = "initial commit";
        parentHashID = null;
        fileContents = new HashMap<String, byte[]>();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timestamp = sdf.format(date);
        nextHashID = new ArrayList<String>();
        try {
            hashID = Utils.sha1(writeObject(this));
        } catch (IOException e) {
            hashID = "temporarily unavailable";
        }

    }
    public Commit(ArrayList<String> bName, String msg, String pHashID,
                  HashMap<String, byte[]> fileCont) {
        /**
         * Constructor for initializing commits after initial commit
         */
        branchName = bName;
        message = msg;
        parentHashID = pHashID;
        fileContents = new HashMap<String, byte[]>();
        for (String f : fileCont.keySet()) {
            fileContents.put(f, fileCont.get(f));
        }
        Date date  = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timestamp = sdf.format(date);
        nextHashID = new ArrayList<String>();
        try {
            hashID = Utils.sha1(writeObject(this));
        } catch (IOException e) {
            hashID = "temporarily unavailable";
        }

    }

    public boolean checkFileInLatestCommit(File currentFile) {
        /**
         * checks to see if file is in latest commit
         */
        if (this.fileContents == null) {
            return false;
        }
        return this.fileContents.containsValue(currentFile);
    }

    public void recLog(Map<String, Commit> commits) {
        /**
         * recursive traverses through commits in gitlet instance variable listOfCommits
         */
        Gitlet.printCommit(this.hashID, this.message, timestamp);
        if (this.parentHashID != null) {
            commits.get(this.parentHashID).recLog(commits);
        }
    }

    public static boolean checkFile(String currCommit, Map<String, Commit> commits, String file) {
        /**
         * checks files in currCommit
         */
        try {
            if (!commits.get(currCommit).fileContents.containsKey(file)) {
                return checkFile((commits.get(currCommit).parentHashID), commits, file);
            }
            return commits.get(currCommit).fileContents.containsKey(file);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static byte[] writeObject(Commit c) throws IOException {
        /**
         * forms a byte stream of the object c and returns it
         */
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(c);
        return out.toByteArray();
    }

}
