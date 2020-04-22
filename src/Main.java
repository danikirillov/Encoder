import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            process(reader);

        } catch (InvalidPasswordException e) {
            UI.printInvalidPassword();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void process(BufferedReader reader) throws Exception {
        Path file = UI.getFilePath(reader);
        String password = UI.getPassword(reader);
        Encoder encoder = new Encoder(file, password);

        if (UI.chooseAction(reader))
            encoder.encode();
        else
            encoder.decode();

        UI.printGG();
    }
}
