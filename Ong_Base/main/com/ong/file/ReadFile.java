package com.ong.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.ong.log.helpers.Log;

/**
 * 文件读取
 * 
 * @Description: 文件读取
 * @Author: Ong
 * @CreateDate: 2017-06-27 19:00:00
 * @E-mail: 865208597@qq.com
 */
public class ReadFile {

	private static Log logger = Log.getLog(ReadFile.class);

	/**
	 * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。
	 * 
	 * 以字节为单位读取文件内容，一次读一个字节
	 * 
	 * @param fileName
	 *            文件的名
	 */
	public static String readFileBySingleBytes(String fileName) {
		String resultStr = new String();
		StringBuilder sbuild = new StringBuilder();
		File file = new File(fileName);
		InputStream in = null;
		try {
//			logger.info("以字节为单位读取文件内容，一次读一个字节：");
			// 一次读一个字节
			in = new FileInputStream(file);
			int tempbyte;
			while ((tempbyte = in.read()) != -1) {
				sbuild.append(tempbyte);
			}
			in.close();
		} catch (IOException e) {
			logger.error("IOException is {0}", e);
			e.printStackTrace();
			return resultStr;
		}
		resultStr = sbuild.toString();
		return resultStr;
	}

	/**
	 * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。
	 * 
	 * 以字节为单位读取文件内容，一次读多个字节（100）
	 * 
	 * @param fileName
	 *            文件的名
	 */
	public static String readFileByMultipleBytes(String fileName) {
		String resultStr = new String();
		StringBuilder sbuild = new StringBuilder();
		InputStream in = null;
		try {
//			logger.info("以字节为单位读取文件内容，一次读多个字节：");
			// 一次读多个字节
			byte[] tempbytes = new byte[100];
			in = new FileInputStream(fileName);
			showAvailableBytes(in);
			// 读入多个字节到字节数组中，byteread为一次读入的字节数
			while ((in.read(tempbytes)) != -1) {
				sbuild.append(tempbytes);
			}
		} catch (Exception e1) {
			logger.error("Exception is {0}", e1);
			e1.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
					logger.error("IOException is {0}", e1);
				}
			}
		}
		resultStr = sbuild.toString();
		return resultStr;
	}

	/**
	 * 以行为单位读取文件，常用于读面向行的格式化文件
	 * 
	 * @param fileName
	 *            文件名
	 */
	public static String readFileByLines(String fileName) {
		String resultStr = new String();
		StringBuilder sbuild = new StringBuilder();
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
//			logger.info("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				sbuild.append(tempString);
			}
			reader.close();
			resultStr = sbuild.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}

		return resultStr;
	}

	/**
	 * 以字符为单位读取文件，常用于读文本，数字等类型的文件 
	 * 
	 * 以字符为单位读取文件内容，一次读一个字节
	 * 
	 * @param fileName
	 *            文件名
	 */
	public static String readFileBySingleChars(String fileName) {
		String resultStr = new String();
		StringBuilder sbuild = new StringBuilder();
		File file = new File(fileName);
		Reader reader = null;
		try {
//			logger.info("以字符为单位读取文件内容，一次读一个字节：");
			// 一次读一个字符
			reader = new InputStreamReader(new FileInputStream(file));
			int tempchar;
			while ((tempchar = reader.read()) != -1) {
				// 对于windows下，rn这两个字符在一起时，表示一个换行。
				// 但如果这两个字符分开显示时，会换两次行。
				// 因此，屏蔽掉r，或者屏蔽n。否则，将会多出很多空行。
				if (((char) tempchar) != 'r') {
					sbuild.append((char) tempchar);
				}
			}
			reader.close();
			resultStr = sbuild.toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception {0}", e);
		}
		return resultStr;
	}

	/**
	 * 以字符为单位读取文件，常用于读文本，数字等类型的文件
	 * 
	 *  以字符为单位读取文件内容，一次读多个字节（30）
	 * 
	 * @param fileName
	 *            文件名
	 */
	public static String readFileByMultipleChars(String fileName) {
		String resultStr = new String();
		StringBuilder sbuild = new StringBuilder();
		Reader reader = null;
		try {
//			logger.info("以字符为单位读取文件内容，一次读多个字节：");
			// 一次读多个字符
			char[] tempchars = new char[30];
			int charread = 0;
			reader = new InputStreamReader(new FileInputStream(fileName));
			// 读入多个字符到字符数组中，charread为一次读取字符数
			while ((charread = reader.read(tempchars)) != -1) {
				// 同样屏蔽掉r不显示
				if ((charread == tempchars.length) && (tempchars[tempchars.length - 1] != 'r')) {
					sbuild.append(tempchars);
				} else {
					for (int i = 0; i < charread; i++) {
						if (tempchars[i] == 'r') {
							continue;
						} else {
							sbuild.append(tempchars[i]);
						}
					}
				}
			}
			resultStr = sbuild.toString();
		} catch (Exception e1) {
			logger.error("Exception {0}", e1);
			e1.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}

		return resultStr;
	}

	/**
	 * 显示输入流中还剩的字节数
	 * 
	 * @param in
	 */
	private static void showAvailableBytes(InputStream in) {
		try {
			logger.info("当前字节输入流中的字节数为:" + in.available());
		} catch (IOException e) {
			logger.error("IOException is {0}", e);
			e.printStackTrace();
		}
	}
}
