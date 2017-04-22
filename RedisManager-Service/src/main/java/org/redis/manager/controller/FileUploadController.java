package org.redis.manager.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.MultipartConfigElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.redis.manager.context.AppConfig;
import org.redis.manager.model.enums.ServerTypeEnum;
import org.redis.manager.service.RedisInstallService;
import org.redis.manager.util.GzipUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/upload")
@Configuration
public class FileUploadController extends BaseController{
	static Log log = LogFactory.getLog(FileUploadController.class);
	
	@Autowired
	AppConfig appConfig;
	
	@Autowired
	RedisInstallService redisInstallService;
	
    /**  
     * 文件上传具体实现方法;  
     */  
    @RequestMapping("/redis")  
    @ResponseBody  
    public Object handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
    	try {
    		SaveFileFromInputStream(file.getInputStream(), appConfig.getResource() + "/upload", file.getOriginalFilename());
    		checkFile(appConfig.getResource() + "/upload" , file.getOriginalFilename());
        	return SUCCESS();
		} catch (Exception e) {
			new File(appConfig.getResource() + "/upload" , file.getOriginalFilename()).delete();
			log.error("upload fail", e);
			return FAIL(e.getMessage());
		}
    }
    
    private void checkFile(String path, String filename) throws Exception {
    	ServerTypeEnum type = null;
		if(filename.toLowerCase().indexOf(".x86") != -1){
			type = ServerTypeEnum.x86;
		}
		if(filename.toLowerCase().indexOf(".x64") != -1){
			type = ServerTypeEnum.x64;
		}
    	String uploadFile = filename;
    	if(filename.endsWith(".gz")){
    		try {
    			uploadFile = GzipUtil.getTarRootFile(path + "/" + filename).get(0);
			} catch (Exception e) {
				throw new Exception("this file is not gzip file; please use 'tar zcvf' to compressed files", e);
			}
    	}
    	if(uploadFile.startsWith("redis") && filename.endsWith(".gz")){
    		if(type == null){
    			throw new Exception("please change the upload file name like {redis}.{version}.{x86/x64}.gz");
    		}
    		new File(path + "/" + filename).renameTo(new File(appConfig.getResource() + "/"+ filename));
    	}else if(uploadFile.startsWith("jre") || uploadFile.startsWith("jdk")){
    		if(type == null){
    			throw new Exception("please change the upload file name like {jre}.{version}.{x86/x64}.gz");
    		}
    		new File(path + "/" + filename).renameTo(new File(appConfig.getResource() + "/"+ filename));
    	}else if(uploadFile.equals("systemMonitor")){
    		new File(path + "/" + filename).renameTo(new File(appConfig.getResource() + "/"+ "systemMonitor.gz"));
    	}else if(uploadFile.equals("redis.conf.template")){
    		new File(path + "/" + filename).renameTo(new File(appConfig.getResource() + "/"+ "redis.conf.template"));
    	}else{
    		throw new Exception("this file is not resource");
    	}
	}
    
    /**保存文件
     * @param stream
     * @param path
     * @param filename
     * @throws IOException
     */
    public void SaveFileFromInputStream(InputStream stream,String path, String filename) throws IOException {
    	File file = new File(path + "/"+ filename);
    	file.getParentFile().mkdirs();
    	file.delete();
    	file.createNewFile();
        FileOutputStream fs=new FileOutputStream(file);
        byte[] buffer =new byte[1024*1024];
        int byteread = 0; 
        while ((byteread=stream.read(buffer))!=-1) {
           fs.write(buffer,0,byteread);
           fs.flush();
        }
        fs.close();
        stream.close();      
    }
    
	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize("128MB");
		factory.setMaxRequestSize("500MB");
		return factory.createMultipartConfig();
	}
}
