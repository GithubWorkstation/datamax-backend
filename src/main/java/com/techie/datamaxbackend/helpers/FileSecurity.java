package com.techie.datamaxbackend.helpers;

import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.web.multipart.MultipartFile;

public class FileSecurity {

	private static final int numberOfBits = 256;
	private static final String algorithm = "AES";

	private static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
		keyGenerator.init(numberOfBits);

		SecretKey key = keyGenerator.generateKey();
		return key;
	}

	public static String getNewSecretKey() throws NoSuchAlgorithmException {
		SecretKey key = generateSecretKey();
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	public static SecretKey decodeSecretKey(String encodedKeyString) {
		byte[] bytes = Base64.getDecoder().decode(encodedKeyString);
		SecretKey originalKey = new SecretKeySpec(bytes, algorithm);
		return originalKey;
	}

	public static byte[] encryptFile(MultipartFile file, String secretKey) throws IOException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		// User Secret Key
		SecretKey key = decodeSecretKey(secretKey);

		// Cipher Instance for encrypting file
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key);

		// File Content in byte[]
		byte[] fileContent = file.getBytes();
		return cipher.doFinal(fileContent);
	}

	public static byte[] decryptFile(String filePath, String secretKey) throws IOException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		SecretKey key = decodeSecretKey(secretKey);

		// Cipher Instance for encrypting file
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.DECRYPT_MODE, key);
		
		// File
		File file = new File(filePath);
		byte[] fileContent = Files.readAllBytes(file.toPath());
		return cipher.doFinal(fileContent);
	}

}
