import java.util.ArrayList;

public class CustomException extends Exception {
    ArrayList<String> errors;

    CustomException(ArrayList<String> errors) {
        super();
        this.errors = errors;
    }

    void printErrors() {
        if (this.errors == null) return;

        System.out.println("Errors:");
        for (String error : this.errors) {
            System.out.println("-> " + error);
        }
    }
}
