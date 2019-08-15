package gitlet;
/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */

    /**
     * creates a new object called HAndle Args which handles all the arguments
     * @param args
     */
    public static void main(String... args) {
        HandleArgs argsMethod = new HandleArgs(args);
        argsMethod.handle();
    }

}
