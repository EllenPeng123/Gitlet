package gitlet;


public class HandleArgs {
    String[] args;

    public HandleArgs(String... arguments) {
        args = arguments;
    }

    public void handle() {
        if (args.length == 0) {
            System.out.println("Please enter a command");
            System.exit(0);
        }
        Gitlet g = new Gitlet();
        if (args[0].equals("init")) {
            g.init();
        } else if (args[0].equals("add")) {
            String filename = args[1];
            g.add(filename);
        } else if (args[0].equals("status")) {
            g.status();
        } else if (args[0].equals("commit")) {
            if (args[1].equals("")) {
                System.out.println("Please enter a commit message.");
            } else {
                g.commit(args[1]);
            }
        } else if (args[0].equals("global-log")) {
            g.globLog();
        } else if (args[0].equals("log")) {
            g.log();
        } else if (args[0].equals("rm")) {
            g.remove(args[1]);
        } else if (args[0].equals("rm-branch")) {
            g.removeBranch(args[1]);
        } else if (args[0].equals("find")) {
            g.find(args[1]);
        } else if (args[0].equals("branch")) {
            g.branch(args[1]);
        } else if (args[0].equals("checkout")) {
            if (args[1].equals("--")) {
                g.checkOutFile(args[2]);
            } else {
                try {
                    if (args[2].equals("--")) {
                        g.checkout(args[3], args[1]);
                    } else {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    g.checkOutBranch(args[1]);
                }
            }
        } else if (args[0].equals("reset")) {
            g.reset(args[1]);
        } else if (args[0].equals("merge")) {
            g.merge(args[1]);
        } else {
            System.out.println("No command with that name exists.");
            System.exit(0);
        }
        g.writeObjectGitlet(); // writes the gitlet object to save version
        // of files everytime main is run
    }
}

