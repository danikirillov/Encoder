import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.APPEND;

public class PasswordManager {
    private static final Path PWDS_STORAGE = Path.of("files/pwds.txt");
    private static final String DELIMITER = " spl ";
    private static final String PWDS_ENCODE_ALG = "MD5";

    public static boolean isPasswordCorrect(String password, Path file) throws IOException, NoSuchAlgorithmException {
        String encryptedPassword = getEncryptedPassword(password);
        String fileName = file.getFileName().toString();

        return Files
                .lines(PWDS_STORAGE)
                .anyMatch(line -> {
                    String[] words = line.split(DELIMITER);
                    return words.length == 2
                            && words[0].equals(fileName)
                            && words[1].equals(encryptedPassword);
                });
    }

    public static void savePassword(String password, Path file) throws IOException, NoSuchAlgorithmException {
        try (PrintStream writer = new PrintStream(Files.newOutputStream(PWDS_STORAGE, APPEND))) {
            writer.println(file.getFileName() + DELIMITER + getEncryptedPassword(password));
        }
    }

    public static String getEncryptedPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(PWDS_ENCODE_ALG);
        return new String(messageDigest.digest(password.getBytes())).replaceAll("\r", "");
    }


    public static void deletePassword(String password, Path file) throws NoSuchAlgorithmException, IOException {
        String encryptedPassword = getEncryptedPassword(password);
        String fileName = file.getFileName().toString();

        Path tmp = Files.createTempFile(null, null);

        Files.write(tmp, removePassword(encryptedPassword, fileName));

        Files.move(tmp, PWDS_STORAGE, StandardCopyOption.REPLACE_EXISTING);
    }

    private static List<String> removePassword(String encryptedPassword, String fileName) throws IOException {
        return Files
                .lines(PWDS_STORAGE)
                .filter(line -> {
                    String[] words = line.split(DELIMITER);
                    return words.length == 2
                            && !(words[0].equals(fileName)
                            && words[1].equals(encryptedPassword));
                })
                .collect(Collectors.toList());
    }

}
