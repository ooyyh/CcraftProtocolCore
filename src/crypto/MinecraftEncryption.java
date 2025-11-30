package crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public class MinecraftEncryption {

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        return keyGen.generateKeyPair();
    }

    public static SecretKey generateSharedSecret() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        return keyGen.generateKey();
    }

    public static byte[] encryptRSA(PublicKey publicKey, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static byte[] decryptRSA(PrivateKey privateKey, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public static PublicKey decodePublicKey(byte[] encodedKey) throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    public static Cipher createAESCipher(int mode, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        cipher.init(mode, key, new IvParameterSpec(key.getEncoded()));
        return cipher;
    }

    public static String getServerIdHash(String serverId, SecretKey sharedSecret, PublicKey publicKey) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.update(serverId.getBytes("ISO_8859_1"));
        digest.update(sharedSecret.getEncoded());
        digest.update(publicKey.getEncoded());
        return new String(digest.digest());
    }
}
