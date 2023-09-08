mimport java.util.Base64;

public class IntToBase64 {

    public static void main(String[] args) {
        for(int i = 0; i < 10000; i++) {
            String base64 = intToBase64(i);
            String bytesBase64 = intBytesToBase64(i);
            System.out.println(i + " : " + base64 + "  :  " + bytesBase64);
        }

    }

    private static String intBytesToBase64(int intValue) {
        // Convertir l'entier en tableau de bytes
        byte[] intBytes = new byte[4];
        intBytes[0] = (byte) (intValue >> 24);
        intBytes[1] = (byte) (intValue >> 16);
        intBytes[2] = (byte) (intValue >> 8);
        intBytes[3] = (byte) intValue;

        // Encoder le tableau de bytes en Base64
        String base64String = Base64.getEncoder().encodeToString(intBytes);
        return base64String;
    }

    private static final String base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    public static String intToBase64(int number) {
        StringBuilder customBase64 = new StringBuilder();

        do {
            int remainder = number % 64;
            char ch = base64Chars.charAt(remainder);
            customBase64.insert(0, ch);
            number /= 64;
        } while (number > 0);

        return customBase64.toString();
    }

}