import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Encoder {
    private final Path file;
    private final String password;

    private final static String DIGEST_ALGORITHM = "MD5";
    private final static String SKF_ALGORITHM = "PBEWithMD5AndDES";

    public Encoder(Path file, String password) {
        this.file = file;
        this.password = password;
    }

    public void encode() throws Exception {
        code(Cipher.ENCRYPT_MODE);
        PasswordManager.savePassword(password, file);
    }

    public void decode() throws Exception {
        checkPassword();
        code(Cipher.DECRYPT_MODE);
        PasswordManager.deletePassword(password, file);
    }

    private void checkPassword() throws InvalidPasswordException, IOException, NoSuchAlgorithmException {
        if (!PasswordManager.isPasswordCorrect(password, file))
            throw new InvalidPasswordException();
    }

    private void code(int mode) throws Exception {
        Cipher cipher = createCipher(mode);

        Path tmpFile = Files.createTempFile("encTmp", ".txt");

        try (FileInputStream fis = new FileInputStream(file.toFile());
             FileOutputStream fos = new FileOutputStream(tmpFile.toFile())) {
            code(fis, fos, cipher);
        }

        Files.move(tmpFile, file, StandardCopyOption.REPLACE_EXISTING);
    }

    private void code(FileInputStream fis, FileOutputStream fos, Cipher cipher) throws IOException, BadPaddingException, IllegalBlockSizeException {
        int bufferSize = 2048 * 8;
        byte[] buff = new byte[bufferSize];
        int readBytesAmount;
        while ((readBytesAmount = fis.read(buff,0, bufferSize)) > 0)
            fos.write(
                    readBytesAmount < bufferSize ?
                            cipher.doFinal(buff, 0, readBytesAmount) :
                            cipher.update(buff, 0, readBytesAmount));
    }

    private Cipher createCipher(int mode) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        SecretKey key = createSecretKey();
        byte[] digest = createMessageDigestBytes();
        byte[] salt = createSalt(digest);
        PBEParameterSpec spec = new PBEParameterSpec(salt, 20);

        Cipher cipher = Cipher.getInstance(SKF_ALGORITHM);
        cipher.init(mode, key, spec);

        return cipher;
    }

    private SecretKey createSecretKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(SKF_ALGORITHM);
        return keyFactory.generateSecret(keySpec);
    }

    private byte[] createMessageDigestBytes() throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(DIGEST_ALGORITHM);
        md.update(password.getBytes(StandardCharsets.UTF_8));
        return md.digest();
    }

    private byte[] createSalt(byte[] digest) {
        byte[] salt = new byte[8];
        System.arraycopy(digest, 0, salt, 0, 8);
        return salt;
    }

}
