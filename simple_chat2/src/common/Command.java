package common;

public class Command {
    public static char symbol = '#';

    public static String[] parse(String msg){
        String[] cmdArgs = msg.split(" ");
        cmdArgs[0] = cmdArgs[0].substring(1);
        return cmdArgs;
    }
}
