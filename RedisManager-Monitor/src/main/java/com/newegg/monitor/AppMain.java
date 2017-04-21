package com.newegg.monitor;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.naming.ConfigurationException;
import com.newegg.monitor.util.ArgsUtil;
import com.newegg.monitor.util.MonitorUtil;
import com.newegg.monitor.util.RestTemplate;

public class AppMain {
	
	public static void main(String[] args) throws Exception {
		ArgsUtil util = ArgsUtil.parser(args);
		Long period = util.getLong("period");
		if(period == null){
			period = 300L;
		}
		final String website = util.getString("website");
		if(website == null || "".equals(website)){
			throw new ConfigurationException("please enter args[website], to configure the reporting website! example:[--website=http://localhost:8080/metric]");
		}
		final String ip = util.getString("ip");
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					Map<String, String> info = MonitorUtil.getSystemInfo(ip);
					RestTemplate.send(website, info);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 0, period  * 1000);
	}
}