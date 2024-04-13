import java.security.MessageDigest;

public class ShaMiningApp {

    public static void main(String[] args) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] blockData = System.getProperty("user.name").getBytes();
        int len = blockData.length;
        int nonceLen = 8;
        byte[] nonceData = new byte[len+nonceLen];
        System.arraycopy(blockData, 0, nonceData, nonceLen, len);

        int difficultyLevel = 10;

        int nonce = 0;

        md.reset();
        md.update(nonceData);
        byte[] digest = md.digest();

        int digestFirstByte = digest[0];
        if (digestFirstByte < difficultyLevel) {
            // found!

        }
    }
}
