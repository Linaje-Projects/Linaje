/*
 * Copyright 2022 Pablo Linaje
 * 
 * This file is part of Linaje Framework.
 *
 * Linaje Framework is free software: you can redistribute it and/or modify it under the terms 
 * of the GNU Lesser General Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 *
 * Linaje Framework is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Linaje Framework.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package linaje.utils;

import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Base64;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import linaje.statics.Constants;



public class Encryptor {

	/*
	PBEWith<digest>And<encryption>	Parameters for use with the PBEWith<digest>And<encryption> algorithm. Examples: PBEWithMD5AndDES, and PBEWithHmacSHA1AndDESede.
	
	<digest>
	MD2
	MD5
	SHA1
	SHA256
	SHA384
	SHA512
	
	<encryption>
	AES
	Blowfish
	DES
	DESede
	DiffieHellman
	DSA
	OAEP
	RC2
	
	PBEWithSHA1AndRC2_40
		This mode encrypts with 40-bit RC2. As an encryption scheme, this has pretty much nothing going for it by today's standards. It was once a useful option when the US had a ludicrous export policy that made it difficult or illegal to export cryptography software with stronger than 40-bit encryption. By today's standards (in fact, by pretty much any standards), 40-bit encryption is a joke.
	PBEWithMD5AndDES
		This option combines all the benefits of slow, insecure 56-bit encryption with an insecure hash function (MD5). Don't use it, except to interface with legacy code.
	PBEWithMD5AndTripleDES
		Overall, this option is probably more-or-less secure. It uses the triple DES algorithm, which gives up to 112-bit security. However, this is a very slow algorithm for that level of security and the key is generated using the MD5 hash algorithm, now considered insecure1. If you must use one of the built-in PBE schemes, use this. But if possible, don't.
	 */
	
	public static final String ALGORITHM_DEFAULT = "PBEWithSHA1AndRC2_40";
	public static final String SALT_DEFAULT = "salt1234";
	public static final String PASS_DEFAULT = "pass1234";
	
	private int iterations = 1500;
	
	private String algorithm = null;
	private byte[] salt = null;
	private String charSet = null;
	private char[] pass = null;
	
	private static Encryptor instance = null;
	
	public Encryptor() {
		this(null, null);
	}
	
	public Encryptor(String password) throws UnsupportedEncodingException {
		this(Base64.getEncoder().encodeToString(password.getBytes()).toCharArray());
	}
	
	public Encryptor(char[] password) {
		this(password, null);
	}
	
	public Encryptor(char[] password, byte[] salt) {
		super();
		setPass(password);
		setSalt(salt);
	}
	
	public static Encryptor getInstance() {
		if (instance == null)
			instance = new Encryptor();
		return instance;
	}
	
	private byte[] transform(int mode, byte[] data) throws Throwable {
		
		Cipher cipher = getCipher(mode);
		//cipher.update(data);
		byte[] decriptedData = cipher.doFinal(data);
				
		return decriptedData;
	}

	public byte[] encryptData(byte[] data) throws Throwable {
		
		byte[] encriptedData = transform(Cipher.ENCRYPT_MODE, data);
		byte[] encriptedDataPadded = addPadding(encriptedData);
		
		return encriptedDataPadded;
	}
	
	public byte[] decryptData(byte[] data) throws Throwable {
		
		//byte[] dataPadded = addPadding(data); No añadimos el padding porque ya se ha codificado con él
		byte[] decryptedData = transform(Cipher.DECRYPT_MODE, data);
		
		return decryptedData;
	}

	public String encryptText(String nonEncryptedText) throws Throwable {

		byte[] nonEncryptedData = nonEncryptedText.getBytes(getCharSet());
		byte[] encryptedData = encryptData(nonEncryptedData);
		String encryptedText = Base64.getEncoder().encodeToString(encryptedData);//Usamos base64 para obtener unos caracteres mas estandar y que podamos enviar mas facilmente.
		//String encryptedText = new String(encryptedData, getCharSet());
			
		return encryptedText;
	}
	
	public String decryptText(String encryptedText) throws Throwable {

		
		byte[] encryptedData = Base64.getDecoder().decode(encryptedText);
		//byte[] encryptedData = encryptedText.getBytes(getCharSet());
		byte[] decryptedData = decryptData(encryptedData);
		String decryptedText = new String(decryptedData, getCharSet());
		
		return decryptedText;
	}
	
	public void encryptFile(File nonEncryptedFile, File encryptTargetFile) throws Throwable {

		byte[] nonEncryptedData = Files.read(nonEncryptedFile);
		byte[] encryptedData = encryptData(nonEncryptedData);
				
		Files.save(encryptedData, encryptTargetFile);
	}
	
	public void decryptFile(File encryptedFile, File decryptTargetFile) throws Throwable {

		byte[] encryptedData = Files.read(encryptedFile);
		byte[] decryptedData = decryptData(encryptedData);
		
		Files.save(decryptedData, decryptTargetFile);
	}
	
	protected Cipher getCipher(int mode) throws Throwable {
		
		return getCipherPBE(mode);
	}
	
	private Cipher getCipherPBE(int mode) throws Throwable {
		
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(getSalt(), iterations);
		PBEKeySpec pbeKeySpec = new PBEKeySpec(getPass());
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(getAlgorithm());
		SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

		Cipher cipher = Cipher.getInstance(getAlgorithm());
		cipher.init(mode, secretKey, pbeParamSpec);
		
		return cipher;
	}
	
	public void savePropertiesEncrypted(Properties properties, File file) throws Throwable {
		
		StringWriter sw = new StringWriter();
		properties.store(sw, null);
		String encryptedProperties = encryptText(sw.toString());
		Files.saveText(encryptedProperties, file);
	}

	public Properties readEncryptedProperties(File fichero) throws Throwable {
		
		String encryptedPropertiesText = Files.readText(fichero);
		return readEncryptedProperties(encryptedPropertiesText);
	}
	
	public Properties readEncryptedProperties(InputStreamReader in) throws Throwable {
		
		String encryptedPropertiesText = Reader.read(in);
		return readEncryptedProperties(encryptedPropertiesText);
	}

	public Properties readEncryptedProperties(URL url) throws Throwable {
		
		String encryptedPropertiesText = Utils.readURL(url);
		return readEncryptedProperties(encryptedPropertiesText);
	}
	
	public Properties readEncryptedProperties(String encryptedPropertiesText) throws Throwable {
		
		String propertiesText = decryptText(encryptedPropertiesText);
		Properties properties = new Properties();
		StringReader sr = new StringReader(propertiesText);
		properties.load(sr);

		return properties;
	}

	private static byte[] addPadding(byte[] byteArray) {
		
		final int MULTIPLE = 8;
		int length = byteArray.length;
		int rest = length%MULTIPLE;
		
		if (rest == 0) {
			return byteArray;
		}
		else {
			byte[] byteArrayPadded = new byte[length + MULTIPLE - rest];
			System.arraycopy(byteArray, 0, byteArrayPadded, 0, byteArray.length);
			return byteArrayPadded;
		}
	}
	public String getAlgorithm() {
		if (algorithm == null)
			algorithm = ALGORITHM_DEFAULT;
		return algorithm;
	}
	public byte[] getSalt() {
		if (salt == null) {
			try {
				salt = SALT_DEFAULT.getBytes(getCharSet());
			}
			catch (UnsupportedEncodingException e) {
				salt = SALT_DEFAULT.getBytes();
			}
			
		}
		return salt;
	}
	public String getCharSet() {
		if (charSet == null)
			charSet = Constants.CHARSET_DEFAULT;
		return charSet;
	}
	public char[] getPass() {
		if (pass == null)
			pass = PASS_DEFAULT.toCharArray();
		return pass;
	}
	
	public void setAlgorithm(String algoritm) {
		this.algorithm = algoritm;
	}
	public void setSalt(byte[] salt) {
		this.salt = salt;
	}
	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}
	public void setPass(char[] pass) {
		this.pass = pass;
	}
}
