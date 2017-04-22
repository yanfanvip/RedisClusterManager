package org.redis.manager.shell;

import org.apache.log4j.Logger;
import org.redis.manager.notify.Notify;
import org.redis.manager.shell.client.ScpClient;
import org.redis.manager.shell.client.SftpClient;
import org.redis.manager.shell.client.SftpInterface;

public class SftpFactory {
	private static final Logger LOGGER = Logger.getLogger(SftpFactory.class);

	public static SftpInterface create(String host,String user,String pass) throws Exception{
		SftpInterface sftp = null;
		try {
			sftp = new SftpClient(host, user, pass);
			LOGGER.info("sftp mode");
		} catch (Exception e) {
			sftp = new ScpClient(host, user, pass);
			LOGGER.info("scp mode");
		}
		return sftp;
	}
	
	public static SftpInterface create(String host,String user,String pass,Notify notify) throws Exception{
		SftpInterface sftp = null;
		try {
			sftp = new SftpClient(host, user, pass,notify);
			LOGGER.info("sftp mode");
		} catch (Exception e) {
			sftp = new ScpClient(host, user, pass, notify);
			LOGGER.info("scp mode");
		}
		return sftp;
	}
}
