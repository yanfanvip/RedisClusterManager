package com.newegg.redis.shell.client;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;

public interface SftpInterface extends Closeable{
	public boolean isExist(String directory) throws Exception;
	public void mkdir(String directory) throws Exception;
	public void upload(String directory, File file) throws FileNotFoundException, Exception;
	public void upload(String directory, File file,String name) throws FileNotFoundException, Exception;
	public void delete(String directory, String deleteFile)throws Exception;
	public void close();
	public String getHosts();
}
