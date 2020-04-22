import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class UI {

    public static Path getFilePath(BufferedReader reader) throws IOException {
        System.out.println("Введите адрес файла для зашифровки/расшифровки: ");
        Path file = Path.of(reader.readLine());
        return checkFileAndReturn(reader, file);
    }

    private static Path checkFileAndReturn(BufferedReader reader, Path file) throws IOException {
        if (!Files.isRegularFile(file)) {
            System.out.println("Вероятно вы ошиблись. Попоробуйте снова.");
            return getFilePath(reader);
        } else
            return file;
    }

    public static String getPassword(BufferedReader reader) throws IOException {
        System.out.println("Введите пароль: ");
        return reader.readLine();
    }

    /**
     * @return если зашифровать, то true. расшифровать - false
     */
    public static boolean chooseAction(BufferedReader reader) throws IOException {
        System.out.println("Выберите действие: 1 - зашифровать файл; 2 - расшифровать файл.");
        return reader.readLine().equals("1");
    }

    public static void printGG() {
        System.out.println("Опервция проведена успешно. Спсибо за использование сервиса.");
    }

    public static void printInvalidPassword() {
        System.out.println("Пароль неверный.");
    }
}
