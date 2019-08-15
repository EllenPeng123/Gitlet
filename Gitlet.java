package gitlet;

import java.util.Map;
import java.util.HashMap;
import java.util.*;
import java.io.*;

public class Gitlet implements Serializable {

    public static final long serialVersionUID = 42L; // required for serializing


    private Map<String, Commit> listOfCommits; // maps commitHashID to Commit object
    private String currentBranch; // contains the branchName of the current branch
    private String currentCommit; // contains the HashID of the current HEAD commit
    ArrayList<File> filesAdded = new ArrayList<>();
    ArrayList<String> filesRemoved = new ArrayList<>();
    private HashMap<String, Commit> branch = new HashMap<>();
    private HashMap<String, Commit> split = new HashMap<>();

    public Gitlet(String stringy) {
        /**
         * This constructor is called in readObjectGitlet()
         * If no file is found, it initializes the object to null
         */
        listOfCommits = new HashMap<String, Commit>();
        currentBranch = null;
        currentCommit = null;
        filesAdded = null;
        filesRemoved = new ArrayList<String>();
        branch = new HashMap<>();
    }

    public Gitlet() {
        /**
         * This is the main constructor called in HandleArgs Class
         */
        Gitlet g = readObjectGitlet();
        try {
            this.listOfCommits = g.listOfCommits;
        } catch (NullPointerException e) {
            this.listOfCommits = new HashMap<String, Commit>();
        }
        this.currentBranch = g.currentBranch;
        this.currentCommit = g.currentCommit;
        this.filesRemoved = g.filesRemoved;
        this.setFiles();
        this.branch = g.branch;
        this.split = g.split;
    }

    public Gitlet readObjectGitlet() {
        /**
         * Reads a file named Commits.txt in directory .gitlet, serializes gitlet object
         * Reads gitlet objects and instances from this file
         */
        if (new File(".gitlet/").exists()) {
            try {
                FileInputStream file = new FileInputStream(".gitlet/Commits.txt");
                ObjectInputStream in = new ObjectInputStream(file);
                Gitlet g = (Gitlet) in.readObject();
                in.close();
                file.close();
                return g;
            } catch (FileNotFoundException e) {
                return new Gitlet("new Constructor");
            } catch (IOException e) {
                return null;
            } catch (ClassNotFoundException e) {
                return null;
            }
        } else {
            return new Gitlet("new Constructor");
        }

    }


    /**
     * Writes a file named Commits.txt (Saves gitlet object and its instances in this file)
     */
    public void writeObjectGitlet() {
        try {
            FileOutputStream fileOut = new FileOutputStream(".gitlet/Commits.txt");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            System.out.println("IO Exception");

        }
    }

    /**
     * Makes a branch named master
     */
    public void makeMasterBranch() {
        setCurrentBranch("master");
        // make commit and get hashID
        Commit newCommit = new Commit(setToArray("master"));
        listOfCommits.put(newCommit.hashID, newCommit);
        currentCommit = newCommit.hashID;
        branch.put("master", listOfCommits.get(currentCommit));
    }

    /**
     * Sets the list of filenames in the working directory edited by user
     * as filesAdded of the gitlet object
     */

    public void setFiles() {
        TrackingFiles files = new TrackingFiles();
        filesAdded = files.getFiles();
    }

    public void setCurrentBranch(String s) {
        currentBranch = s;
    }

    public void init() {
        /**
         * Initializes a .gitlet directory
         * Initializes a master branch with a initial commit
         */
        Init initial = new Init();
        if (initial.makeDir()) {
            makeMasterBranch();
        }
    }

    public void add(String filename) {
        /**
         * This method adds files to the StagingArea.
         * It checks if the file is already staged.
         * Then, it adds it to the instance variable - filesAdded.
         */


        File ourFile = new File(filename);
        if (new File(filename).exists()) {
            StagingArea stage = new StagingArea();
            Commit c = listOfCommits.get(currentCommit);
            boolean flag = false;
            try {
                flag = TrackingFiles.checkFile(ourFile, c.fileContents.get(filename));
            } catch (NullPointerException e) {
                flag = false;
            }
            if (filesRemoved.contains(filename)) {
                filesRemoved.remove(filename);
            } else if (!flag) {
                if (stage.filesAdded.containsKey(filename)) {
                    System.out.print("Is already staged");
                } else {
                    stage.addFiles(filename);
                }
            }
            stage.writeObject();
        } else {
            System.out.println("File does not exist.");
        }

    }

    public ArrayList<String> setToArray(String strName) {
        /**
         * Sets a given string to an arrayList of type String.
         */
        ArrayList<String> arrName = new ArrayList<String>();
        arrName.add(strName);
        return arrName;
    }

    public void branch(String branchName) {
        /**
         * This method executes the command "branch".
         * It creates a new branch and before doing that,
         * it checks whether a branch of that name is already made.
         */
        if (getAllbranches().contains(branchName)) {
            System.out.println("A branch with that name already exists.");
        }
        boolean setFlag = false;
        for (String bName : listOfCommits.get(currentCommit).branchName) {
            if (branchName == bName) {
                setFlag = true;
            }
        }
        if (!setFlag) {
            listOfCommits.get(currentCommit).branchName.add(branchName);

        }
        branch.put(branchName, listOfCommits.get(currentCommit));
        split.put(branchName, listOfCommits.get(currentCommit));
    }

    public void commit(String msg) {
        /**
         * This method initializes a commit everytime it is called.
         */
        StagingArea stage = new StagingArea();
        if (!stage.filesAdded.isEmpty() || !filesRemoved.isEmpty()) {
            HashMap<String, byte[]> files = new HashMap<String, byte[]>();
            for (String key : stage.filesAdded.keySet()) {
                files.put(key, Utils.readContents(stage.filesAdded.get(key)));
            }
            Commit makeCommit = new Commit(setToArray(currentBranch), msg,
                    currentCommit, files);
            listOfCommits.put(makeCommit.hashID, makeCommit);
            try {
                listOfCommits.get(currentCommit).nextHashID.add(makeCommit.hashID);
            } catch (NullPointerException e) {
                listOfCommits.get(currentCommit).nextHashID = new ArrayList<String>();
                listOfCommits.get(currentCommit).nextHashID.add(makeCommit.hashID);
            }
            currentCommit = makeCommit.hashID;
            filesRemoved.clear();
            stage.filesAdded.clear();
            stage.writeObject();
            branch.replace(currentBranch, listOfCommits.get(currentCommit));
        } else {
            System.out.println("No changes added to the commit.");
        }
    }

    public void globLog() {
        /**
         * This method executes global-log command,
         * it works very closely with log method mentioned below.
         */
        for (String key : listOfCommits.keySet()) {
            printCommit(key, listOfCommits.get(key).message, listOfCommits.get(key).timestamp);
        }
    }

    public void log() {
        /**
         * This method is called by handleArgs handle() method.
         * This calls recLog which is a recursive method in Commit class.
         * recLog recursively traverses through commits in the listOfCommits.
         * For each commit, it calls static method printCommit() in gitlet class.
         */
        listOfCommits.get(currentCommit).recLog(listOfCommits);
    }


    public static void printCommit(String key, String messg, String timestamp) {
        /**
         * Prints commits in the following format
         */
        System.out.println("===");
        System.out.println("Commit " + key);
        System.out.println(timestamp);
        System.out.println(messg);
        System.out.println();
    }

    public void remove(String fileName) {
        /**
         * This executes the remove command. Temporarily ignore it.
         */
        File file = new File(fileName);
        StagingArea stage = new StagingArea();
        if (Commit.checkFile(currentCommit, listOfCommits, fileName)) {
            // then remove
            filesRemoved.add(fileName);
            if (stage.checkFileName(fileName)) {
                stage.filesAdded.remove(fileName);
            }
            Utils.restrictedDelete(fileName);
        } else if (stage.checkFileName(fileName)) {
            stage.filesAdded.remove(fileName);
        } else {
            System.out.println("No reason to remove the file");
        }
        stage.writeObject();
    }

    public void removeBranch(String bName) {
        if (currentBranch.equals(bName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        if (!getAllbranches().contains(bName)) {
            System.out.println("A branch with that name does not exist.");
        }
    }

    public void find(String message) {
        /**
         * This method is called in handleArgs class by handle() method
         * It performs the find command. It finds the commit based on the commit message passed.
         */
        boolean checker = false;
        for (Commit c : listOfCommits.values()) {
            if (c.message.equals(message)) {
                checker = true;
                System.out.println(c.hashID);
            }
        }
        if (!checker) {
            System.out.println("Found no commit with that message.");
        }

    }

    public void checkOutBranch(String bName) {
        /**
         *  This is a partially implemented checkOutBranch() - bullet 3 in checkout spec sheet
         *  Build up is required for proper functioning
         */
        Commit currCommit = listOfCommits.get(currentCommit);
        if (isContainsBranch(bName) || currentBranch.equals(bName)) {
            if (currentBranch.equals(bName)) {
                System.out.println("No need to checkout the current branch.");
                return;
            }
        } else {
            System.out.println("No such branch exists.");
            return;
        }
        for (String filename : TrackingFiles.getFileNames()) {
            if (!checkIfFileIsInBranch(filename) 
                && getLatestCommit(bName).fileContents.containsKey(filename)) {
                String messg = "There is an untracked file in the way; delete it or add it first.";
                System.out.println(messg);
                System.exit(0);
            }
        }
        Commit latest = getLatestCommit(bName);
        for (String filename : currCommit.fileContents.keySet()) {
            if (!latest.fileContents.keySet().contains(filename)) {
                Utils.restrictedDelete(filename);
            }
        }
        for (String filename : latest.fileContents.keySet()) {
            byte[] fileTodisk = latest.fileContents.get(filename);
            File overwrite = new File(filename);
            try {
                FileOutputStream out = new FileOutputStream(overwrite, false);
                out.write(fileTodisk);
                out.close();
            } catch (IOException e) {
                System.out.println("IOException");
            } catch (NullPointerException e) {
                System.out.println("File does not exist in that commit.");

            }

        }

        currentBranch = bName;
        currentCommit = getLatestCommit(bName).hashID;
        return;
    }

    public void checkOutFile(String filename) {
        /**
         * Takes the version of the file in the most recent commit
         * and adds it to the working directory
         * overrides if necessary
         */
        Commit currCommit = listOfCommits.get(currentCommit);
        byte[] fileTodisk = currCommit.fileContents.get(filename);
        File overwrite = new File(filename);
        try {
            FileOutputStream out = new FileOutputStream(overwrite, false);
            out.write(fileTodisk);
            out.close();
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }


    public void checkout(String filename, String commitID) {
        /**
         * Takes the version of the file in the commit specified and
         * adds it to the working directory
         * Overrides if necessary
         */
        if (!listOfCommits.containsKey(commitID)) {
            if (!commitExist(commitID)) {
                System.out.println("No commit with that id exists.");
                return;
            }
        }
        if (commitID.length() != 8) {
            Commit currCommit = listOfCommits.get(commitID);
            byte[] fileTodisk = currCommit.fileContents.get(filename);
            File overwrite = new File(filename);
            try {
                FileOutputStream out = new FileOutputStream(overwrite, false);
                out.write(fileTodisk);
                out.close();
            } catch (IOException e) {
                System.out.println("IOException");
            } catch (NullPointerException e) {
                System.out.println("File does not exist in that commit.");
            }

        } else {
            Commit currCommit = returnCommit(commitID);
            byte[] fileTodisk = currCommit.fileContents.get(filename);
            File overwrite = new File(filename);
            try {
                FileOutputStream out = new FileOutputStream(overwrite, false);
                out.write(fileTodisk);
                out.close();
            } catch (IOException e) {
                System.out.println("IOException");
            } catch (NullPointerException e) {
                System.out.println("File does not exist in that commit.");
            }
        }
    }


    public void status() {
        /**
         * This is a functioning method that executes the status command.
         * Please contact Shruti for further assistance in comprehending the functionalities.
         */
        System.out.println("=== Branches ===");
        /**
         * Prints out all the branch names
         */
        System.out.println("*" + currentBranch);
        for (String b : getAllbranches()) {
            System.out.println(b);
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        /**
         * prints out all the files in the staging area
         */
        StagingArea stage = new StagingArea();
        for (String file : stage.filesAdded.keySet()) {
            System.out.println(file);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        try {
            for (String deletedFile : filesRemoved) {
                System.out.println(deletedFile);
            }
        } catch (NullPointerException e) {
            System.out.println();

        }

        // get arraylist of removed files , print files
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ==="); //just keeping headers
        System.out.println();
        System.out.println("=== Untracked Files ==="); //just keeping headers
    }

    public ArrayList<String> getAllbranches() {
        /** returns an array of all the branches
         *
         */
        ArrayList<String> branches = new ArrayList<String>();
        try {
            for (Commit c : listOfCommits.values()) {
                for (String b : c.branchName) {
                    if (!b.equals(currentBranch) && !branches.contains(b)) {
                        branches.add(b);
                    }
                }
            }
        } catch (NullPointerException e) {
            return null;
        }
        return branches;
    }

    public boolean isContainsBranch(String brch) {
        /** checks if a branch name is a part of the branches
         *
         */
        ArrayList<String> branches = getAllbranches();

        if (branches.contains(brch)) {
            return true;
        }
        return false;
    }

    public boolean commitExist(String commitid) {
        for (String makeCommit : listOfCommits.keySet()) {
            String firstEight = commitid.substring(0, 7);
            String toCompare = makeCommit.substring(0, 7);
            if (firstEight.equals(toCompare)) {
                return true;
            }
        }
        return false;
    }
    public Commit returnCommit(String commitid) {
        if (commitExist(commitid)) {
            for (String makeCommit : listOfCommits.keySet()) {
                String firstEight = commitid.substring(0, 7);
                String toCompare = makeCommit.substring(0, 7);
                if (firstEight.equals(toCompare)) {
                    return listOfCommits.get(makeCommit);
                }
            }
        }
        return null;
    }

    public void reset(String commitID) {
        String messg = "There is an untracked file in the way; delete it or add it first.";
        List<String> currFileNames = TrackingFiles.getFileNames();
        if (!commitExist(commitID) && !listOfCommits.containsKey(commitID)) {
            System.out.println("No commit with that id exists.");
            return;
        } else {
            Commit getFile = listOfCommits.get(commitID);
            for (String filename : TrackingFiles.getFileNames()) {
                if (!checkIfFileIsInBranch(filename) 
                    && getFile.fileContents.containsKey(filename)) {
                    byte[] fileInCommit = getFile.fileContents.get(filename);
                    File fileInDir = new File(filename);
                    byte [] fileinDir = Utils.readContents(fileInDir);
                    if (!TrackingFiles.checkFile(fileinDir, fileInCommit)) {
                        System.out.println(messg);
                        System.exit(0);
                    }
                }
            }
            for (String fileName : currFileNames) {
                File existingFile = new File(fileName);
                if (getFile.fileContents.get(fileName) == null) {
                    if (checkIfFileIsInBranch(fileName)) {
                        Utils.restrictedDelete(fileName);
                    }
                } else {
                    byte[] commitFile = getFile.fileContents.get(fileName);
                    try {
                        FileOutputStream out = new FileOutputStream(existingFile, false);
                        out.write(commitFile);
                        out.close();
                    } catch (IOException e) {
                        System.out.println("Damn !!");
                    }
                }

            } 
            currentCommit = commitID;
            StagingArea stage = new StagingArea();
            stage.filesAdded.clear();
            stage.writeObject();
            branch.replace(currentBranch, listOfCommits.get(currentCommit));
        }
    }
    public boolean checkIfFileIsInBranch(String fileName) {
        boolean msflaggy = false;
        File file = new File(fileName);
        for (Commit c : listOfCommits.values()) {
            try {
                if (c.fileContents.containsKey(fileName)) {
                    byte[] toCompare = c.fileContents.get(fileName);
                    if (TrackingFiles.checkFile(file, toCompare)) {
                        msflaggy = true;
                    }
                }
            } catch (NullPointerException e) {
                continue;

            }
        }
        return msflaggy;
    }

    public Commit getLatestCommit(String bName) {
        return branch.get(bName);
    }
    public void merge(String bName) {
        if (!isContainsBranch(bName) && !currentBranch.equals(bName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (bName.equals(currentBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        StagingArea stage = new StagingArea();
        if (!stage.filesAdded.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            stage.writeObject();
            return;
        }
        for (String filename : TrackingFiles.getFileNames()) {
            if (!checkIfFileIsInBranch(filename)) {
                String messg = "There is an untracked file in the way; delete it or add it first.";
                System.out.println(messg);
                System.exit(0);
            }
        }
        if (isContainsBranch(bName)) {
            Commit c = getSplitPoint(bName, currentBranch);
            HashMap<String, byte[]> filesSplitPnt = c.fileContents;
            HashMap<String, byte[]> filesCurrBrch = listOfCommits.get(currentCommit).fileContents;
            HashMap<String, byte[]> filesGivBrch =
                    listOfCommits.get(getLatestCommit(bName).hashID).fileContents;
            boolean conflict = false;
            if (filesSplitPnt.size() == filesGivBrch.size()) {
                if (filesSplitPnt.equals(filesGivBrch)) {
                    String msg = "Given branch is an ancestor of the current branch.";
                    stage.writeObject();
                    System.out.println(msg);
                }
            }
            if (filesSplitPnt.size() == filesCurrBrch.size()) {
                if (filesSplitPnt.equals(filesCurrBrch)) {
                    stage.writeObject();
                    System.out.println("Current branch fast-forwarded.");
                }
            }
            for (String fileName : filesSplitPnt.keySet()) {
                if (filesGivBrch.get(fileName) == null && filesSplitPnt.get(fileName) == null) {
                    continue;
                } else if (filesGivBrch.get(fileName) == null
                        && filesCurrBrch.get(fileName) == null) {
                    Utils.restrictedDelete(fileName);
                } else if (filesGivBrch.get(fileName) == null
                        && TrackingFiles.checkFile(filesCurrBrch.get(fileName),
                        filesSplitPnt.get(fileName))) {
                    Utils.restrictedDelete(fileName);
                    filesRemoved.add(fileName);
                } else if (filesGivBrch.get(fileName) == null
                        && !TrackingFiles.checkFile(filesCurrBrch.get(fileName),
                        filesSplitPnt.get(fileName))) {
                    TrackingFiles.writeToFileConflict(fileName, filesCurrBrch.get(fileName), null);
                    conflict = true;
                } else if (filesCurrBrch.get(fileName) == null
                        && !TrackingFiles.checkFile(filesSplitPnt.get(fileName),
                        filesGivBrch.get(fileName))) {
                    TrackingFiles.writeToFile(fileName, filesGivBrch.get(fileName));
                    stage.addFiles(fileName);
                } else if (TrackingFiles.checkFile(filesSplitPnt.get(fileName),
                        filesGivBrch.get(fileName))) {
                    continue;
                } else if (TrackingFiles.checkFile(filesSplitPnt.get(fileName),
                        filesCurrBrch.get(fileName))) {
                    TrackingFiles.writeToFile((fileName), filesGivBrch.get(fileName));
                    stage.addFiles(fileName);
                } else if (!TrackingFiles.checkFile(filesSplitPnt.get(fileName),
                        filesCurrBrch.get(fileName))
                        && !TrackingFiles.checkFile(filesSplitPnt.get(fileName),
                        filesGivBrch.get(fileName))) {
                    TrackingFiles.writeToFileConflict(fileName,
                            filesCurrBrch.get(fileName), filesGivBrch.get(fileName));
                    conflict = true;
                }
            }
            for (String fileName : filesGivBrch.keySet()) {
                if (!filesSplitPnt.containsKey(fileName)) {
                    TrackingFiles.writeToFile(fileName, filesGivBrch.get(fileName));
                    stage.addFiles(fileName);
                }
            }
            if (conflict) {
                System.out.println("Encountered a merge conflict.");
            } else {
                Commit merged = new Commit(setToArray(currentBranch), "Merged "
                        + currentBranch + " with "
                        + bName + ".", currentCommit, getLatestCommit(bName).fileContents);
                listOfCommits.get(currentCommit).nextHashID = setToArray(merged.hashID);
                currentCommit = merged.hashID;
                listOfCommits.put(merged.hashID, merged);
                stage.filesAdded.clear();
                stage.writeObject();
                branch.replace(currentBranch, listOfCommits.get(currentCommit));
                filesRemoved.clear();
                return;
            }
        }

    }

    public Commit getSplitPoint(String givenBranch, String currBranch) {
        if (givenBranch.equals("master")) {
            return split.get(currBranch);
        }
        return split.get(givenBranch);
    }

}


