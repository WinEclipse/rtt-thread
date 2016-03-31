package org.rtthread.wizard.util;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

/**
 * LayoutUtil.Util
 * 
 * @author RDT Team
 * @date 2011-9-13
 */
public class Util {
	public static String getEclipseHome() throws IOException {
		return FileLocator.toFileURL(Platform.getInstallLocation().getURL()).getFile();
	}

	/**
	 * object to XML
	 */
	public static String object2XML(Object obj, String outFileName) throws FileNotFoundException {

		// 构造输出XML文件的字节输出流
		File outFile = new File(outFileName);
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile));

		// 构造一个XML编码器
		XMLEncoder xmlEncoder = new XMLEncoder(bos);
		// 使用XML编码器写对象
		xmlEncoder.writeObject(obj);
		// 关闭编码器
		xmlEncoder.close();
		return outFile.getAbsolutePath();
	}

	/**
	 * get object from XML 
	 */
	public static Object xml2Object(String inFileName) throws FileNotFoundException {
		// 构造输入的XML文件的字节输入流
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inFileName));
		// 构造一个XML解码器
		XMLDecoder xmlDecoder = new XMLDecoder(bis);
		// 使用XML解码器读对象
		Object obj = xmlDecoder.readObject();
		// 关闭解码器
		xmlDecoder.close();
		return obj;
	}

	/**
	 * get object from XML 
	 */
	public static Object xml2Object(InputStream bis) throws FileNotFoundException {

		// 构造一个XML解码器
		XMLDecoder xmlDecoder = new XMLDecoder(bis);
		// 使用XML解码器读对象
		Object obj = xmlDecoder.readObject();
		System.out.println(obj);
		// 关闭解码器
		xmlDecoder.close();
		return obj;
	}
}
