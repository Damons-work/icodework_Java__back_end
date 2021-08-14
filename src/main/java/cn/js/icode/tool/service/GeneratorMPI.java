package cn.js.icode.tool.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import team.bangbang.common.config.Config;
import team.bangbang.common.exception.BizException;
import cn.js.icode.tool.data.Dbtable;

/**
 * 开发辅助工具对外操作类 <br>
 * 
 * config.properties中必须配置参数有：<br>
 * directory.generator.template 模板配置主目录<br>
 * 
 * @author ICode Studio
 * @version 1.0 2009-2-11
 */
public final class GeneratorMPI {
	/**
	 * 生成所有模板中配置的编码文件、配置文件等文档。
	 * 
	 * @param codeTemplate
	 *            Code模板
	 * @param table
	 *            数据库表信息
	 * 
	 * @return 生成的文件信息，Map<file_name, file_content>
	 * @throws BizException
	 */
	public static Map<String, byte[]> doGenerate(String codeTemplate,
			Dbtable table) throws IOException {
		// 获得 code.generator.template.directory 模板配置主目录
		String templateDir = Config.getProperty("code.generator.template.directory");
		if (templateDir == null) {
			throw new IOException("application.properties文件中没有配置"
					+ "code.generator.template.directory目录参数");
		}

		// 具体模板配置目录
		templateDir += File.separator + codeTemplate;

		// 构造生成器引擎，生成的代码不需要写入硬盘文件
		GeneratorEngine engine = new GeneratorEngine(table);

		// 生成文件
		engine.doGenerate(templateDir);

		return engine.getOutputFiles();
	}

	/**
	 * 将内存中的文件名（含目录层次）和文件内容直接打包到Zip文件中
	 * 
	 * @param zipFileName
	 *            压缩产生的zip文件名（含路径）
	 * @param content
	 *            文件名（含目录层次）和文件内容
	 * @throws IOException
	 */
	public static void zip(String zipFileName, Map<String, byte[]> content)
			throws IOException {
		ZipOutputStream out = null;
		try {
			// 默认字符集
			// String encoding = System.getProperty("file.encoding");
			// out = new ZipOutputStream(new FileOutputStream(zipFileName),
			// encoding);
			out = new ZipOutputStream(new FileOutputStream(zipFileName));

			// 压缩包内根目录
			out.putNextEntry(new ZipEntry("/"));

			// 文件名
			Set<String> fNames = content.keySet();
			for (String name : fNames) {
				String name2 = name.replaceAll("\\\\", "/");

				out.putNextEntry(new ZipEntry(name2));
				byte[] bts = content.get(name);
				out.write(bts);
			}
		} catch (IOException ioex) {
			throw ioex;
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException ioex2) {
			}
		}
	}
}
