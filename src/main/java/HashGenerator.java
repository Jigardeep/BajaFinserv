import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class HashGenerator {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar test.jar <PRN Number> <path to json file>");
            System.exit(1);
        }

        String prnNumber = args[0].toLowerCase();
        String jsonFilePath = args[1];

        try {
            // Read and parse JSON file
            JSONObject jsonObject = new JSONObject(new JSONTokener(new FileReader(jsonFilePath)));
            String destinationValue = findDestinationValue(jsonObject);

            if (destinationValue == null) {
                System.err.println("Key 'destination' not found in the JSON file.");
                System.exit(1);
            }

            // Generate random alphanumeric string
            String randomString = generateRandomString(8);

            // Generate MD5 hash
            String toHash = prnNumber + destinationValue + randomString;
            String md5Hash = generateMD5Hash(toHash);

            // Output the result
            System.out.println(md5Hash + ";" + randomString);

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static String findDestinationValue(JSONObject jsonObject) {
        // Traverse JSON and find the value for the key "destination"
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (key.equals("destination")) {
                return value.toString();
            }
            if (value instanceof JSONObject) {
                String result = findDestinationValue((JSONObject) value);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
