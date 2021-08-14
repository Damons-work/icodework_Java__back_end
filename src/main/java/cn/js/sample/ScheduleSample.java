package cn.js.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import team.bangbang.common.service.IWorker;

/**
 * 定时任务示例
 *
 * @author ICode Studio
 * @version 1.0  2019-10-27
 */
@Component
public class ScheduleSample implements IWorker {
	/* 日志对象 */
	private static Logger logger = LoggerFactory.getLogger(ScheduleSample.class);
	private int x = 0;
	
	/**
	 * 定时任务的逻辑执行部分
	 */
	@Scheduled(cron = "0/10 * * * * ?")  // 每10秒执行一次
	public void run() {
		System.out.println(getDescription() + " : " + x);
		logger.info( getDescription() + " : " + x++);
	}

	/**
	 * @return 定时任务的描述，用于简化代码维护，加强代码可读性
	 */
	public String getDescription() {
		return "定时任务示例";
	}
}
