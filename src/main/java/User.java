
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class User extends Person {

    private static String userFile = "User.txt";
    private String password;

    public User() {

    }

    public User(String name, String email, String password) {
        super(name, email);
        this.password = password;
    }

    public User(String name, String email) {
        super(name, email);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static void login() {
        Scanner scanner = new Scanner(System.in);
        boolean login = false;
        BufferedReader reader = null;
        String line = null;
        boolean invalid = false;
        String email;
        String password;
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."
                + "[a-zA-Z0-9_+&*-]+)*@"
                + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
                + "A-Z]{2,7}$";

        System.out.println("\n=====================");
        System.out.println("      Login Menu");
        System.out.println("  Enter \"E\" To Exit");
        System.out.println("=====================");
        do {
            invalid = false;
            System.out.print("Enter Email > ");
            email = scanner.nextLine();
            if (email.equalsIgnoreCase("e")) {
                return;
            } else if (email == null || !Pattern.compile(emailRegex).matcher(email).matches()) {
                invalid = true;
                System.out.println("Email invalid! Please try again!");
            }
        } while (invalid);

        System.out.print("Enter Password > ");
        password = scanner.nextLine();
        if (password.equalsIgnoreCase("e")) {
            return;
        }

        User user = new User();

        try {
            reader = new BufferedReader(new FileReader(userFile));
            while ((line = reader.readLine()) != null) {
                String decryptedText = user.decryption(line);
                String[] row = decryptedText.split("[|]");

                if (email.compareTo(row[1]) == 0 && password.compareTo(row[2]) == 0) {
                    System.out.println("\nlogin Success!");
                    IMS.systemPause();
                    login = true;
                }
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (login) {

            IMS.dashboard();
        } else {
            System.out.println("\nLogin Failed! Please try again later!");
            IMS.systemPause();

            return;
        }
    }

    public static void signup() {
        Scanner scanner = new Scanner(System.in);

        BufferedReader reader = null;
        String line = null;
        boolean invalid = false;
        String name = null;
        String email = null;
        String password = null;
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."
                + "[a-zA-Z0-9_+&*-]+)*@"
                + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
                + "A-Z]{2,7}$";

        System.out.println("\n=====================");
        System.out.println("    Sign Up Menu");
        System.out.println("  Enter \"E\" To Exit");
        System.out.println("=====================");
        do {
            invalid = false;
            System.out.print("Enter Name > ");
            name = scanner.nextLine();

            if (name.equalsIgnoreCase("e")) {
                return;
            } else if (name == null || name.isEmpty()) {
                invalid = true;
                System.out.println("Name invalid! Please try again!");
            }
        } while (invalid);

        do {
            invalid = false;
            System.out.print("Enter Email > ");
            email = scanner.nextLine();
            if (email.equalsIgnoreCase("e")) {
                return;
            } else if (email == null || !Pattern.compile(emailRegex).matcher(email).matches() || email.isEmpty()) {
                invalid = true;
                System.out.println("Email invalid! Please try again!");
            }
        } while (invalid);

        do {
            invalid = false;
            System.out.print("Enter Password > ");
            password = scanner.nextLine();
            if (password.equalsIgnoreCase("e")) {
                return;
            } else if (password == null || password.length() < 8 || password.isEmpty()) {
                invalid = true;
                System.out.println("Password is invalid! A minimum length of 8 characters is required!");
            }
        } while (invalid);

        User user = new User();

        try {
            reader = new BufferedReader(new FileReader(userFile));
            while ((line = reader.readLine()) != null) {
                String decodedText = user.decryption(line);
                String[] row = decodedText.split("[|]");
                if (email.compareTo(row[1]) == 0) {
                    System.out.println("Email repeated!");

                    System.out.println("\nRegister Failed! Please try again later!");
                    IMS.systemPause();
                    return;
                }
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile, true))) {
            String encryptedText = user.encryption(name + "|" + email + "|" + password);
            writer.append(encryptedText);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("\nRegister Success!");
        IMS.systemPause();

        return;
    }

    public String encryption(String line) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        SecretKey secretKey = null;
        byte[] encryptedBytes = null;

        // Load the secret key from file
        try (BufferedReader br = new BufferedReader(new FileReader("secretKey.txt"))) {
            String row = br.readLine();
            byte[] decodedKey = Base64.getDecoder().decode(row);
            secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to Open File !!!");
            return null;
        }

        try {

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            encryptedBytes = cipher.doFinal(line.getBytes());

        } catch (Exception e) {
            Logger.getLogger(Supplier.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decryption(String line) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        SecretKey secretKey = null;
        byte[] decryptedBytes = null;

        try (BufferedReader br = new BufferedReader(new FileReader("secretKey.txt"))) {
            String row = br.readLine();
            byte[] decodedKey = Base64.getDecoder().decode(row);

            secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to Open File !!!");
        }

        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        try {
            decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(line));
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(Supplier.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(Supplier.class.getName()).log(Level.SEVERE, null, ex);
        }

        String plainText = new String(decryptedBytes);

        return plainText;

    }

}
