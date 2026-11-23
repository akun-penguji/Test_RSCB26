
import AESsecurity.EnkripsiAES;

public class DecryptDB {
    public static void main(String[] args) {
        String host = "DUMMY";
        String db = "DUMMY";
        String user = "DUMMY";
        String pas = "DUMMY";

        System.out.println("HOST: " + EnkripsiAES.decrypt(host));
        System.out.println("DATABASE: " + EnkripsiAES.decrypt(db));
        System.out.println("USER: " + EnkripsiAES.decrypt(user));
        System.out.println("PAS: " + EnkripsiAES.decrypt(pas));
    }
}
