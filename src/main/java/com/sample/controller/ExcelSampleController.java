package com.sample.controller;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import team.bangbang.common.config.Config;
import team.bangbang.common.file.ExcelReader;
import team.bangbang.common.file.ExcelWriter;
import team.bangbang.common.utility.LogicUtility;
/**
 * 选项配置 - Controller
 *
 * @author ICode Studio
 * @version 1.0  2018-10-13
 */
@Controller
@RequestMapping("/sample")
public class ExcelSampleController {
	/**
	 * 显示Excel上传页面
	 * 
	 * Excel的上传在此页面由上传组件完成。
	 *
	 * @return 数据展示页面
	 */
	@GetMapping("/excelRead.do")
	public ModelAndView readExcel() {
		ModelAndView view = new ModelAndView("sample/excelRead");
		
		return view;
	}
	
	/**
	 * 读取上传的Excel，将数据返回到页面展示
	 * 
	 * Excel的上传由上传组件在调用此操作之前完成。
	 * 
	 * 仅仅演示第1个工作簿10行、10列以内的数据，用于说明Excel的读取方法
	 * 
	 * @param file 本地文件名，全路径为{file.attachment.directory}/file，
	 * 其中{file.attachment.directory}为application.properties文件中配置的附件目录
	 *
	 * @return 数据展示页面
	 */
	@PostMapping(value = "/excelRead.do")
	public ModelAndView readExcelAction(String fileName) {
		ModelAndView view = new ModelAndView("sample/excelRead");
		
		if(!fileName.startsWith("http://") && !fileName.startsWith("https://")) {
			// Excel文件全路径
			String path = Config.getProperty("file.attachment.directory");
			if (path == null) {
				// 没有配置附件目录
				return view;
			}
	
			if (!path.endsWith("/") || !path.endsWith("\\")) {
				path += "/";
			}
					
			fileName = path + fileName;
			if(File.separatorChar == '\\') {
				fileName = fileName.replace('/', File.separatorChar);
			} else {
				fileName = fileName.replace('\\', File.separatorChar);
			}
		}
		
		// 结果数据
		List<Object[]> rowDataList = new ArrayList<Object[]>();
		
		try {
			ExcelReader er = new ExcelReader(fileName);
			// 读取第1个工作簿
			er.setSheetIndex(0);
			
			while(rowDataList.size() < 10) {
				// 一行数据
				Object[] rowData = new String[10];
				// 最多读10列，真正写业务程序不要局限在10列
				for(int i = 0; i < 10; i++) {
					// 每readCell一次，会自动准备读取后面一个单元格内容
					// readCell()返回值可能会为null
					rowData[i] = er.readCell(); 
				}
				rowDataList.add(rowData);
				
				// 转移到下一行，自动从第1列开始
				er.nextRow();
			}
			
			// 关闭Excel文件
			er.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 将数据带至页面显示
		view.addObject("rowDataList", rowDataList);
        
		return view;
	}
	
	/**
	 * 生成Excel文件，填写随机数据，提供下载链接
	 * 
	 * 仅仅填写第1个工作簿10行、10列以内的数据，用于说明Excel的写入方法
	 *
	 * @return 修改页面
	 */
	@GetMapping("/excelWrite.do")
	public ModelAndView writeExcel() {
		ModelAndView view = new ModelAndView("sample/excelWrite");
		
		// 生成一个临时文件，使用temp.tmp作为文件名
		// 在业务中，可以使用当前操作账户的id号作为文件名，以避免临时文件泛滥，同时保证不同操作账户临时文件的独立性。
		String path = Config.getProperty("file.attachment.directory");
		if (path == null) {
			// 没有配置附件目录
			return view;
		}

		if (!path.endsWith("/") || !path.endsWith("\\")) {
			path += "/";
		}

		if(File.separatorChar == '\\') {
			path = path.replace('/', File.separatorChar);
		} else {
			path = path.replace('\\', File.separatorChar);
		}
		
		String fileName = "temp.tmp";
		String file = path + fileName;
		
		try {
			ExcelWriter er = new ExcelWriter(file);
			// 创建工作簿
			// 加上时间，别以为我骗你的，每次真的都是实际生成文件数据
			er.createSheet("示例工作簿" + LogicUtility.getTimeAsString(), 0);
			// 写表头
			// 表头表格开始位置
			Dimension start = er.getOffset();
			// 写 10 列
			for(int i = 0; i < 10; i++) {
				er.writeCell("第" + (i + 1) + "列");
			}
			// 表头表格结束位置
			Dimension end = er.getOffset();
			
			// 设置表头表格样式
			er.setHeader(start, end);
			
			// 转换到下一行，开始写数据
			er.nextRow();
			// 数据表格开始位置
			start = er.getOffset();
			// 写10行
			for(int row = 1; row <= 10; row++) {
				String s = row + "行";
				for(int col = 1; col <= 10; col++) {
					String temp = s + col + "列";
					er.writeCell(temp);
				}
				
				// 如果写完第10行，记录数据表格结束位置
				if(row == 10) {
					end = er.getOffset();
				}
				
				// 转换到下一行
				er.nextRow();
			}
			// 设置数据表格样式
			er.setBorder(start, end);
			
			// 关闭excel文件
			er.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 文件名带到页面，提供下载
		view.addObject("fileName", fileName);
		
		return view;
	}
}