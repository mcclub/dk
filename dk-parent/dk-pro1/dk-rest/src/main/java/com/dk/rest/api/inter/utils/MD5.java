package com.dk.rest.api.inter.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public final class MD5 {

	private MD5() {

	}

	/**
	 * Used building output as Hex
	 */
	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String hash(String text, String key) {
		if (text == null) {
			throw new IllegalArgumentException("text can't be null");
		}
		if (key == null) {
			throw new IllegalArgumentException("key can't be null");
		}
		try {
			String S = md5(key);
			byte[] textData = text.getBytes("UTF-8");
			int len = textData.length;
			int n = (len + 15) / 16;
			byte[] tempData = new byte[n * 16];
			for (int i = len; i < n * 16; i++) {
				tempData[i] = 0;
			}
			System.arraycopy(textData, 0, tempData, 0, len);
			textData = tempData;
			String[] c = new String[n];
			for (int i = 0; i < n; i++) {
				c[i] = new String(textData, 16 * i, 16,"UTF-8");
			}
			// end c
			String[] b = new String[n];
			String hash;

			// 2.����b(i)
			// b(1)=MD5(S+c(1))
			// b(2)=MD5(b(1)+c(2))
			// ...
			// b(n)=MD5(b(n-1)+c(n))
			String temp = S;
			String target = "";
			for (int i = 0; i < n; i++) {
				b[i] = md5(temp + c[i]);
				temp = b[i];
				target += b[i];
			}

			// 3.hash=MD5(b(1)+b(2)+...+b(n))
			hash = md5(target);
			return hash;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * Converts an array of bytes into an array of characters representing the
	 * hexidecimal values of each byte in order. The returned array will be
	 * double the length of the passed array, as it takes two characters to
	 * represent any given byte.
	 * 
	 * @param data
	 *            a byte[] to convert to Hex characters
	 * @return A char[] containing hexidecimal characters
	 */
	private static char[] encodeHex(byte[] data) {

		int l = data.length;

		char[] out = new char[l << 1];

		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS[0x0F & data[i]];
		}

		return out;
	}

	private static MessageDigest getMD5Digest() {
		try {
			MessageDigest md5MessageDigest = MessageDigest.getInstance("MD5");
			md5MessageDigest.reset();
			return md5MessageDigest;
		} catch (NoSuchAlgorithmException nsaex) {
			return null;
		}
	}

	
	public static String md5(String content) {
		try {
			byte[] data = getMD5Digest().digest(
					content.getBytes());
			char[] chars = encodeHex(data);
			return new String(chars);
		} catch (Exception ex) {
			return null;
		}
	}

	public static String md5UTF8(String content) {
		try {
			byte[] data = getMD5Digest().digest(
					content.getBytes("UTF-8"));
			char[] chars = encodeHex(data);
			return new String(chars);
		} catch (Exception ex) {
			return null;
		}
	}

	public static String md5GBK(String content) {
		try {
			byte[] data = getMD5Digest().digest(
					content.getBytes("GBK"));
			char[] chars = encodeHex(data);
			return new String(chars);
		} catch (Exception ex) {
			return null;
		}
	}


	public static String getMd5ByFile(File file) {
		String value = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			MappedByteBuffer byteBuffer = in.getChannel().map(
					FileChannel.MapMode.READ_ONLY, 0, file.length());
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(byteBuffer);
			BigInteger bi = new BigInteger(1, md5.digest());
			value = bi.toString(16);
			while (value.length() < 32) {
				value = "0" + value;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	}
}