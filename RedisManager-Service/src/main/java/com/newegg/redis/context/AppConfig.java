package com.newegg.redis.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

	/**
	 * 历史数据保存期限
	 */
	@Value("${monitor.history.duration.hours:48}")
	private int monitor_history_duration=48;
	/**
	 * Redis监控频率
	 */
	@Value("${monitor.redis.period.second:60}")
	private int monitor_redis_period_second = 60;
	

	public int getMonitor_history_duration() {
		return monitor_history_duration;
	}

	public void setMonitor_history_duration(int monitor_history_duration) {
		this.monitor_history_duration = monitor_history_duration;
	}

	public int getMonitor_redis_period_second() {
		return monitor_redis_period_second;
	}

	public void setMonitor_redis_period_second(int monitor_redis_period_second) {
		this.monitor_redis_period_second = monitor_redis_period_second;
	}
	
	
}
