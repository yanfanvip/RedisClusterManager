package com.newegg.redis.util;

import java.io.BufferedInputStream;  
import java.io.BufferedOutputStream;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;  
import java.util.zip.ZipEntry;  
import java.util.zip.ZipInputStream;  
import org.apache.commons.compress.archivers.ArchiveInputStream;  
import org.apache.commons.compress.archivers.ArchiveStreamFactory;  
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class GzipUtil {
	private static final Logger LOGGER = Logger.getLogger(GzipUtil.class);
    
    /**
     * <解压缩文件>
     * <解压标准gzip格式的压缩包>
     * @param zipfile 需要解压的文件路径(具体到文件)
     * @param outputDirectory 解压目标路径
     */
    public static List<String> untar(String zipfile, String outputDirectory) {
    	if (StringUtils.isEmpty(zipfile) || StringUtils.isEmpty(outputDirectory)){
            return null;
        }
    	List<String> files = new ArrayList<String>();
        ArchiveInputStream in = null;
        BufferedInputStream bis = null;
        BufferedOutputStream dest = null;
        GZIPInputStream gis = null;
        try {
            gis = new GZIPInputStream(new BufferedInputStream(new FileInputStream(zipfile)));
            in = new ArchiveStreamFactory().createArchiveInputStream("tar", gis);
            bis = new BufferedInputStream(in);
            TarArchiveEntry entry = null;
            while ((entry = (TarArchiveEntry)in.getNextEntry()) != null) {
                String name = outputDirectory + File.separator +entry.getName();
                files.add(name);
                File file = new File(name);
                if(file.exists()){ file.delete(); }
                if (name.endsWith("/")) {
                	file.mkdirs();
                } else {
                	file.createNewFile();
                	int count;
                    byte data[] = new byte[2048];
                    dest = new BufferedOutputStream(new FileOutputStream(file));
                    while ((count = bis.read(data, 0, 2048)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                }
            }
            return files;
        } catch (Exception e) {
        	LOGGER.error("untar error", e);
        	return null;
        } finally {
            try {
            	if (null != gis) { gis.close(); }
                if (null != bis) { bis.close(); }
                if(in != null){ in.close(); }
            } catch (IOException e) {
            	LOGGER.error("", e);
            }
        }
    }
    
    /**
     * <p>获取压缩包里面的所有文件列表<p>
     */
    public static List<String> getTarFiles(String zipfile){
    	if (StringUtils.isEmpty(zipfile)){
            return null;
        }
    	List<String> files = new ArrayList<String>();
        ArchiveInputStream in = null;
        GZIPInputStream gis = null;
        try {
            gis = new GZIPInputStream(new BufferedInputStream(new FileInputStream(zipfile)));
            in = new ArchiveStreamFactory().createArchiveInputStream("tar", gis);
            TarArchiveEntry entry = null;
            while ((entry = (TarArchiveEntry)in.getNextEntry()) != null) {
                files.add(entry.getName());
            }
            return files;
        } catch (Exception e) {
        	LOGGER.error("untar error", e);
        	return null;
        } finally {
            try {
            	if (null != gis) { gis.close(); }
                if(in != null){ in.close(); }
            } catch (IOException e) {
            	LOGGER.error("", e);
            }
        }
    }
    
    /**
     * <解压缩zip包>
     * @param zipfile 压缩包
     * @param outputDirectory 解压路径
     * @return 根目录下的所有文件
     */
    public static List<String> unzip(String zipfile, String outputDirectory) {
    	if (StringUtils.isEmpty(zipfile) || StringUtils.isEmpty(outputDirectory)){
            return null;
        }
    	List<String> files = new ArrayList<String>();
    	ZipInputStream zis = null;
    	BufferedOutputStream dest = null;
        try {
            zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipfile)));
            ZipEntry entry = null;
            while ((entry = zis.getNextEntry()) != null) {
                String name = outputDirectory + File.separator + entry.getName();
                files.add(name);
                File file = new File(name);
                if(file.exists()){ file.delete(); }
                if (name.endsWith("/")) {
                	file.mkdirs();
                } else {
                	file.createNewFile();
                    int count;
                    byte data[] = new byte[2048];
                    dest = new BufferedOutputStream(new FileOutputStream(file));
                    while ((count = zis.read(data, 0, 2048)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                }
            }
            return files;
        }catch (Exception e) {
        	LOGGER.error("unzip error", e);
        	return null;
        }finally {
            try {
                if (null != zis) { zis.close(); }
            } catch (IOException e) {LOGGER.error("", e);}
        }
    }
    
    /**
     * <p>获取压缩包里面的所有文件列表<p>
     */
    public static List<String> getZipFiles(String zipfile){
    	if (StringUtils.isEmpty(zipfile)){
            return null;
        }
    	List<String> files = new ArrayList<String>();
    	ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipfile)));
            ZipEntry entry = null;
            while ((entry = zis.getNextEntry()) != null) {
                files.add(entry.getName());
            }
            return files;
        }catch (Exception e) {
        	LOGGER.error("unzip error", e);
        	return null;
        }finally {
            try { if (null != zis) { zis.close(); } } catch (IOException e) {LOGGER.error("", e);}
        }
    }
    
    public static List<String> getTarRootFile(String tarfile){
    	List<String> files = GzipUtil.getTarFiles(tarfile);
    	List<String> roots = new ArrayList<String>(); 
    	for (String f : files) {
    		if(f.endsWith("/") && f.split("/").length == 1){
    			roots.add(f.split("/")[0]);
    		}
		}
    	return roots;
    }
    
    public static void main(String[] args) {
    	List<String> files = GzipUtil.getTarRootFile("E:\\Tools\\JDK\\jre-8u65-linux-i586.gz");
    	for (String string : files) {
			System.out.println(string);
		}
    }
}