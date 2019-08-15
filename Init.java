package gitlet;
import java.util.*;
import java.io.*;
public class Init {
    /**
     * 1. Make the .gitlet directory
     * 2. initialize default master branch
     * 3. Initially has a commit with no files.
     * 4. Checks if there is already a git VCS, abort!
     * 5. " A gitlet version-control system already exists in the current directory."
     */
    public boolean makeDir() {
        if (!checkDir()) {
            //create a .gitlet directory here
            File gitlet = new File(".gitlet");
            gitlet.mkdir();
            return true;
        } else {
            giveError();
            return false;
        }
    }

    public boolean checkDir() {
        // checks if there exists a .gitlet directory;
        return new File(".gitlet").exists();
    }

    public void giveError() {
        System.out.println("A gitlet version-control system already "
                + "exists in the current directory.");
    }
}

