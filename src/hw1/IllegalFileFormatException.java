package hw1;

public class IllegalFileFormatException extends Exception {
    public IllegalFileFormatException() {
        super("This File Format isn't Supported!");
    }
}
