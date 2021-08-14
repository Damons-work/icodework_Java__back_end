package cn.js.icode.tool.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import team.bangbang.common.config.Constants;
import team.bangbang.common.file.FileReader;
import team.bangbang.common.file.FileWriter;
import team.bangbang.common.utility.LogicUtility;
import cn.js.icode.tool.data.Dbtable;
import cn.js.icode.tool.data.DbtableField;

/**
 * 开发辅助工具 -> 代码生成器引擎
 * 
 * @author ICode Studio
 * @version 1.0 2009-2-11
 */
public class GeneratorEngine {
	/* 数据库表定义数据 */
	private Dbtable tbl = null;

	/* 生成的文件信息<file_name, file_content> */
	private Map<String, byte[]> mp = null;

	/* 保存生成文件的目录，该目录如果为null，则生成的文件不写入到硬盘中 */
	private String outputDir = null;
	
	/* 根据表名后缀，需要选择生成的文件<表名后缀，文件名关键字（可以有多个关键字）> */
	private static Map<String, String[]> option = new HashMap<String, String[]>();

	/** 分类目录：config */
	public static final String CATEGORY_CONFIG = "config";

	/** 分类目录：code */
	public static final String CATEGORY_CODE = "code";

	/** 分类目录：page */
	public static final String CATEGORY_PAGE = "page";

	/** 源代码使用的字符集 */
	public final static String SOURCE_CHARSET = "UTF-8";

	/**
	 * 构建生成器引擎
	 * 
	 * @param te
	 *            数据库表信息
	 * @param blWriteFile
	 *            是否将生成的文件写入到硬盘文件中
	 */
	public GeneratorEngine(Dbtable tbl) {
		this.tbl = tbl;

		mp = new HashMap<String, byte[]>();
		
		// 如果是base、master后缀的表，生成Select文件
		String[] ss1 = {"Select"};
		option.put("base", ss1);
		option.put("master", ss1);
	}

	/**
	 * 保存生成文件的目录，该目录如果为null，则生成的文件不写入到硬盘中
	 * 
	 * @param outputDir
	 *            保存生成文件的目录
	 */
	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	/**
	 * 生成所有模板中配置的编码文件、配置文件等文档。
	 * 
	 * @param templateDir
	 *            具体模板目录
	 */
	public void doGenerate(String templateDir) throws IOException {
		// 创建配置文件
		String msg1 = generateFromTemplate(templateDir + File.separator
				+ CATEGORY_CONFIG, CATEGORY_CONFIG);

		// 创建代码文件
		String msg2 = generateFromTemplate(templateDir + File.separator
				+ CATEGORY_CODE, CATEGORY_CODE);

		// 创建页面文件
		String msg3 = generateFromTemplate(templateDir + File.separator
				+ CATEGORY_PAGE, CATEGORY_PAGE);		

		// 写入log文件
		try {
			FileWriter fw = new FileWriter("icode_assistant.log");
			// 写入日期
			StringBuffer sb = new StringBuffer();
			sb.append("===============   " + new Date() + "   ===============")
					.append("\r\n");
			sb.append(msg1).append(msg2).append(msg3).append("\r\n\r\n\r\n");

			fw.writeString(sb.toString(), SOURCE_CHARSET, true);
		} catch (IOException ioex) {
			System.err.println("日志文件写入失败！");
			throw ioex;
		}
		// hint
		System.out.println("所有文件创建完毕！");
	}

	/**
	 * @return 生成的文件信息，Map<file_name, file_content>
	 */
	public Map<String, byte[]> getOutputFiles() {
		return mp;
	}

	/**
	 * 循环读取模板配置目录下指定子目录下的所有模板文件，按照每个模板文件转化生成目标文件
	 * 
	 * @param templateSubDir
	 *            模板子目录
	 * @param category
	 *            分类目录（page/config/code）
	 * @return 文件生成的摘要信息
	 */
	private String generateFromTemplate(String templateSubDir, String category) {
		// 获得子目录下的所有文件
		File dir = new File(templateSubDir);
		if (!dir.exists()) {
			System.err.println("目录 " + templateSubDir + " 不存在！");
			return "";
		}

		File[] fs = dir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				// 选择文件
				return f.isFile();
			}
		});
		
		if (fs == null) {
			return "";
		}

		StringBuffer msg = new StringBuffer();
		// 循环处理每个文件
		for (int i = 0; i < fs.length; i++) {
			// 模板文件名
			String templateFile = fs[i].getAbsolutePath();
			// 读取模板文件内容
			String content = readFile(templateFile);

			// 目标文件名
			String targetFileName = getTargetFileFullName(category, fs[i].getName());

			msg.append("Generatinging " + targetFileName);
			// 替换模板中的全局标签
			content = replaceOverallTags(content);
			// 替换字段标记
			content = replaceFieldTags(content);
			// 去除多余的空行
			content = compressBlankLine(content);
			// 创建文件
			boolean bl = writeFile(targetFileName, content);
			msg.append(bl ? " ok!" : " error!")
					.append(Constants.LINE_SEPARATOR);
		}

		// 子目录
		File[] sons = dir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				// 选择匹配数据库表名称的目录
				// 忽略文件
				if(f.isFile()) return false;
				
				// 获得目录名称
				String name = f.getName();
				// 如果目录名称是表名称的一部分，则使用此子目录
				return (tbl.getTableName().indexOf(name) >= 0);
			}
		});

		for(int i = 0; sons != null && i < sons.length; i++) {
			msg.append(generateFromTemplate(sons[i].getAbsolutePath(), category));
		}

		return msg.toString();
	}

	/**
	 * 获得目标文件名
	 * 
	 * @param category
	 *            分类目录（page/config/code）
	 * @param templateFileName
	 *            模板文件的文件名称，不包含上级目录路径
	 * @return 压缩包中的目标文件名，包含上级目录路径
	 */
	private String getTargetFileFullName(String category,
			String templateFileName) {
		templateFileName = templateFileName.replaceAll("\\[TableName\\]",
				tbl.getTableName());
		templateFileName = templateFileName.replaceAll("\\[TableDesc\\]", tbl.getTableDescription());
		templateFileName = templateFileName.replaceAll("\\[ModuleName\\]",
				getModuleName());
		templateFileName = templateFileName.replaceAll("\\[PojoName\\]",
				getPojoName());
		templateFileName = templateFileName.replaceAll(
				"\\[LowerFirstPojoName\\]", lowerCaseFirst(getPojoName()));
		// 如果是代码文件，添加包action,data,mpi,gate,dao的目录
		String temp = templateFileName;

		// 获得代码类的类型
		String classType = null;
		if (temp.indexOf("Action.") > 0) {
			classType = "action";
		} else if (temp.indexOf("Bean.") > 0) {
			classType = "bean";
		} else if (temp.indexOf("MPI.") > 0) {
			classType = "mpi";
		} else if (temp.indexOf("Gate.") > 0) {
			classType = "gate";
		} else if (temp.indexOf("DAO.") > 0) {
			classType = "dao";
		} else if (temp.indexOf("Service.") > 0) {
			classType = "service";
		} else if (temp.indexOf("Controller.") > 0) {
			classType = "controller";
		} else if (temp.indexOf("Mapper.") > 0) {
			classType = "mapper";
		} else if (temp.indexOf("Feign.") > 0) {
			classType = "feign";
		} else if (temp.indexOf("Micro.") > 0) {
			classType = "micro";
		} else if (temp.indexOf("VO.") > 0) {
			classType = "vo";
		} else if (temp.indexOf("DTO.") > 0) {
			classType = "dto";
		} else if (temp.indexOf("Entity.") > 0) {
			classType = "entity";
		} else {
			classType = "entity";
		}

		// 生成代码类，此时需要添加包目录
		if (category.equals(CATEGORY_CODE)) {

			// 包名转换为目录
			String packageMenu = tbl.getPackagePrefix() + "." + getModuleName()
					+ "." + classType;
			char cSep = File.separator.charAt(0);
			while (packageMenu.indexOf(".") >= 0) {
				packageMenu = packageMenu.replace('.', cSep);
			}

			return category + File.separator + packageMenu + File.separator
					+ templateFileName;

		}

		return category + File.separator + templateFileName;
	}

	/**
	 * 替换字符串中的全局标签
	 * 
	 * @param content
	 *            内容字符串
	 * @return 经全局标签替换后的字符串
	 */
	private String replaceOverallTags(String content) {
		// 替换全局标签变量:
		String str = content.replaceAll("<icode:currentTimeMillis/>",
				getCurrentTimeMillis());
		str = str.replaceAll("<icode:today/>", LogicUtility.getDateAsString());

		// 表信息
		str = str.replaceAll("<icode:tableName/>", tbl.getTableName());
		str = str.replaceAll("<icode:tableDesc/>", tbl.getTableDescription());

		// 关键字段信息
		// 关键字变量
		int nPk = getPkIndex();
		DbtableField pk = getField(nPk);
		str = str.replaceAll("<icode:pkName/>", pk.getFieldName());
		str = str.replaceAll("<icode:lowerFirstPkName/>",
				lowerCaseFirst(pk.getFieldName()));
		str = str.replaceAll("<icode:upperFirstPkName/>",
				upperCaseFirst(pk.getFieldName()));
		str = str.replaceAll("<icode:pkDesc/>", pk.getFieldDescription());
		str = str.replaceAll("<icode:pkSqlType/>", getSqlType(pk));
		str = str.replaceAll("<icode:pkJavaType/>", getJavaType(pk));
		str = str.replaceAll("<icode:pkLength/>", pk.getFieldLength());

		// 类信息
		str = str.replaceAll("<icode:packageName/>", getPackageName());
		str = str.replaceAll("<icode:moduleName/>", getModuleName());
		str = str.replaceAll("<icode:pojoName/>", getPojoName());
		str = str.replaceAll("<icode:lowerFirstPojoName/>",
				lowerCaseFirst(getPojoName()));
		
		// 处理表名称判断
		// <icode:ifTableName has="">……</icode:ifTableName>		
		String[] arr = null;
		while((arr = getTagContents("ifTableName", str)) != null) {
			// 获取 has 属性
			Map<String, String> attrs = toAttributes(arr[0]);
			String has = attrs.get("has");
			boolean match = ifTableName(has);
			/*
			 *         String[0]: 标签属性<br>
			 *         String[1]: 括中的文本内容<br>
			 *         String[2]: 整个标记的文本
			 */			
			str = (match ? str.replace(arr[2], arr[1]) : str.replace(arr[2], ""));
		}

		// 是否有日期
		// 是否存在日期型的字段（用于在页面中添加日期型的样式和脚本）
		boolean hasDateField = false;
		for (int i = 0; i < getFieldCount(); i++) {
			DbtableField fe = getField(i);
			if ("date".equals(fe.getFieldType())) {
				hasDateField = true;
				break;
			}
		}

		String[] date = null;
		while ((date = getTagContents("hasDateField", str)) != null) {
			if (hasDateField) {
				str = str.replace(date[2], date[1]);
			} else {
				str = str.replace(date[2], "");
			}
		}

		// 有关键字段类型的判断
		// 关键字段是否已经被处理
		// 上一轮的pkType属性
		String[] ifType = null;
		while ((ifType = getTagContents("pkType", str)) != null) {
			if (ifType[0].trim().length() == 0
					|| ifType[0].indexOf(" " + pk.getFieldType()) >= 0) {
				// 处理当前fieldType标签，继续下一个fieldType标签处理
				str = str.replace(ifType[2], ifType[1]);
			} else {
				// 当前字段类型不匹配模板中这一块的内容
				// 跳到下一段
				str = str.replace(ifType[2], "");
			}
		} // end while

		return str;
	}

	/**
	 * @param tplContent
	 *            模板文件的文本内容
	 * @return 目标文件的文本内容
	 */
	private String replaceFieldTags(String tplContent) {
		// 处理其它字段
		// 处理字段循环语句
		String[] strLoop = null;
		while ((strLoop = getTagContents("loopField", tplContent)) != null) {
			// 解释执行，循环字段
			int nCount = getFieldCount();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < nCount; i++) {
				// 当前字段需要替换的字符串原型
				String orgLoop = strLoop[1];
				DbtableField fe = getField(i);
				// 跳过关键字
				if (fe.getIsPk() != null && fe.getIsPk().booleanValue())
					continue;

				// 得到strLoop内的icode:fieldType标记
				String[] ifType = getTagContents("fieldType", orgLoop);
				// 没有特别指定的字段类型，所有字段应用相同的代码段
				if (ifType == null) {
					// 将本段内容
					sb.append(replaceFieldInfo(orgLoop, fe));
					continue;
				}

				// 有字段类型的判断
				while ((ifType = getTagContents("fieldType", orgLoop)) != null) {
					if (ifType[0].trim().length() == 0
							|| ifType[0].indexOf(" " + fe.getFieldType()) >= 0) {
						String strNew = replaceFieldInfo(ifType[1], fe);
						// 处理了一个fieldType标签，继续下一个fieldType标签处理
						orgLoop = orgLoop.replace(ifType[2], strNew);
					} else {
						// 当前字段类型不匹配模板中这一块的内容
						// 跳到下一段
						orgLoop = orgLoop.replace(ifType[2], "");
					}
				} // end while

				sb.append(compressBlankLine(orgLoop));
			} // end for

			tplContent = tplContent.replace(strLoop[2], sb.toString());
		}

		return tplContent;
	}

	/**
	 * @param str
	 *            需要替换字段标签的字符串
	 * @param fe
	 *            字段信息
	 * @return 替换了字段标签后的字符串
	 */
	private String replaceFieldInfo(String str, DbtableField fe) {
		// 处理逻辑判断，以下逻辑判断标记，一个字段只允许各有一个，注意顺序
		// isFixedDict标记与isDynamicDict标记互斥
		// 若当前字段符合其中之一条件，
		// 则直接选用标记内包含的内容。相当于if - else if - else 功能

		// 1. icode:isFixedDict
		boolean blIsFixedDict = false;
		String[] isFixedDict = getTagContents("isFixedDict", str);
		if (isFixedDict != null) {
			// 规范约定： 固定数据字典的字段名以Flag结尾
			if (fe.isFixDict()) {
				str = str.replace(isFixedDict[2], isFixedDict[1]);
				blIsFixedDict = true;
			} else {
				str = str.replace(isFixedDict[2], "");
			}
		}

		// 2. icode:isDynamicDict
		boolean blIsDynamicDict = false;
		// isFixedDict标记与isDynamicDict标记互斥
		String[] isDynamicDict = getTagContents("isDynamicDict", str);
		if (isDynamicDict != null) {
			// 规范约定： 固定数据字典的字段名以Code结尾，且字段类型为字符类型
			if(fe.isDynamicDict()) {
				// 或者是字段名以Code结尾，且字段类型为字符型
				str = str.replace(isDynamicDict[2], isDynamicDict[1]);
				blIsDynamicDict = true;
			} else {
				str = str.replace(isDynamicDict[2], "");
			}
		}

		// 3. icode:else
		String[] arrElse = getTagContents("else", str);
		if (arrElse != null) {
			if (blIsFixedDict || blIsDynamicDict) {
				// 使用数据字典块的字符串，则滤除<icode:else>标记
				str = str.replace(arrElse[2], "");
			} else {
				// 未使用数据字典块的字符串，使用<icode:else>标记
				str = str.replace(arrElse[2], arrElse[1]);
			}
		}

		// 4. icode:isNotNull
		String[] notNull = null;
		// 可能有多个icode:isNotNull
		while((notNull = getTagContents("isNotNull", str)) != null) {
			if (fe.getIsNotNull() != null && fe.getIsNotNull().booleanValue()) {
				str = str.replace(notNull[2], notNull[1]);
			} else {
				str = str.replace(notNull[2], "");
			}
		}

		// 5. icode:isLast
		String[] isLast = null;
		// 可能有多个icode:isLast
		while((isLast = getTagContents("isLast", str)) != null) {
			if (fe.getOrderBy() + 1 == getFieldCount()) {
				str = str.replace(isLast[2], isLast[1]);
			} else {
				str = str.replace(isLast[2], "");
			}
		}

		// 6. icode:isNotLast
		String[] isNotLast = null;
		// 可能有多个icode:isNotLast
		while((isNotLast = getTagContents("isNotLast", str)) != null) {
			if (fe.getOrderBy() + 1 < getFieldCount()) {
				str = str.replace(isNotLast[2], isNotLast[1]);
			} else {
				str = str.replace(isNotLast[2], "");
			}
		}

		// 7. icode:hasLength
		String[] hasLength = null;
		// 可能有多个icode:hasLength
		while((hasLength = getTagContents("hasLength", str)) != null) {
			if (fe.getFieldLength() != null
					&& fe.getFieldLength().trim().length() > 0) {
				str = str.replace(hasLength[2], hasLength[1]);
			} else {
				str = str.replace(hasLength[2], "");
			}
		}

		str = str.replaceAll("<icode:fieldName/>", fe.getFieldName());
		str = str.replaceAll("<icode:upperFirstFieldName/>",
				upperCaseFirst(fe.getFieldName()));
		str = str.replaceAll("<icode:lowerFirstFieldName/>",
				upperCaseFirst(fe.getFieldName()));
		str = str.replaceAll("<icode:upperFirstPropertyName/>",
				upperCaseFirst(fe.getPropertyName()));
		str = str.replaceAll("<icode:lowerFirstPropertyName/>",
				lowerCaseFirst(fe.getPropertyName()));
		str = str.replaceAll("<icode:fieldDesc/>", fe.getFieldDescription());
		str = str.replaceAll("<icode:fieldSqlType/>", getSqlType(fe));
		str = str.replaceAll("<icode:fieldJavaType/>", getJavaType(fe));
		str = str.replaceAll("<icode:fieldLength/>", fe.getFieldLength());
		int nLen = 0;
		try {
			nLen = Integer.parseInt(fe.getFieldLength());
		} catch (Exception ex) {
		}
		str = str
				.replaceAll("<icode:unicodeLength/>", String.valueOf(nLen / 2));
		str = str.replaceAll("<icode:fieldIndex/>",
				String.valueOf(fe.getOrderBy() + 1));
		if (blIsFixedDict) {
			// 不需要关联数据字典
			str = str.replaceAll("<icode:fieldDictPojo/>", fe.getPropertyName()
					.replaceAll("Flag$", ""));
			str = str.replaceAll("<icode:lowerFirstFieldDictPojo/>",
					lowerCaseFirst(fe.getPropertyName().replaceAll("Flag$", "")));
			str = str.replaceAll("<icode:upperFirstFieldDictPojo/>",
					upperCaseFirst(fe.getPropertyName().replaceAll("Flag$", "")));
			str = str.replaceAll("<icode:fieldDictDesc/>", fe
					.getFieldDescription().replaceAll("标识", ""));
		} else if (blIsDynamicDict) {
			// 需要关联数据字典
			if (fe.getPropertyName().endsWith("Id")) {
				str = str
				.replaceAll(
						"<icode:fieldDictPojo/>",
						fe.getFieldName().replaceAll(
								"Id$", ""));
				str = str
						.replaceAll(
								"<icode:lowerFirstFieldDictPojo/>",
								lowerCaseFirst(fe.getPropertyName().replaceAll(
										"Id$", "")));
				str = str.replaceAll("<icode:upperFirstFieldDictPojo/>",
						upperCaseFirst(fe.getPropertyName().replaceAll("Id$", "")));
			}

			if (fe.getPropertyName().endsWith("Code")) {
				str = str.replaceAll("<icode:fieldDictPojo/>",
						fe.getPropertyName()
								.replaceAll("Code$", ""));
				str = str.replaceAll("<icode:lowerFirstFieldDictPojo/>",
						lowerCaseFirst(fe.getPropertyName()
								.replaceAll("Code$", "")));
				str = str.replaceAll("<icode:upperFirstFieldDictPojo/>",
						upperCaseFirst(fe.getPropertyName().replaceAll("Code$", "")));
			}

			str = str.replaceAll("<icode:fieldDictDesc/>", fe
					.getFieldDescription().replaceAll("编号", ""));
		}

		return str;
	}

	/**
	 * 去除多余的空行，多个空行变为1个空行
	 * 
	 * @param str 需要压缩空行的字符串
	 * 
	 * @return 空行压缩后的字符串
	 */
	private static String compressBlankLine(String str) {
		if(str == null) return str;
		
		BufferedReader br = new BufferedReader(new StringReader(str));
		StringBuffer sb = new StringBuffer();
		
		String line = null;
		// 上一行是否是空行
		boolean lastIsBlank = false;
		try {
			while((line = br.readLine()) != null) {
				// 当前行是否是空行？
				boolean thisIsBlank = (line.trim().length() == 0);
				
				if(lastIsBlank && thisIsBlank) {
					// 忽略当前行
				} else {
					sb.append(line).append("\r\n");
					lastIsBlank = thisIsBlank;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}

	/**
	 * @param srcText
	 *            原字符串，例如：<br>
	 * 
	 *            <pre>
	 *              &lt;icode:fieldType boolean string double&gt;
	 *              abc
	 *              &lt;/icode:fieldType&gt;
	 * </pre>
	 * 
	 * @return 得到icode:[tag]标签中的判断语句和括中的文本内容<br>
	 *         如果该标签不存在，则返回null<br>
	 *         String[0]: 标签属性<br>
	 *         String[1]: 括中的文本内容<br>
	 *         String[2]: 整个标记的文本
	 */
	private String[] getTagContents(String tag, String srcText) {
		// 开始标签
		String strStart = "<icode:" + tag;
		// 结束标签
		String strEnd = "</icode:" + tag + ">";
		int nStart = srcText.indexOf(strStart);
		int nEnd = srcText.indexOf(strEnd);
		if (nStart >= 0 && nEnd > nStart) {
			// temp: boolean string double> abc
			String temp = srcText.substring(nStart + strStart.length(), nEnd);
			String[] arr = new String[3];
			// 整个标记的文本
			arr[2] = strStart + temp + strEnd;
			// 得到判断语句<icode:fieldType boolean string double>
			nEnd = temp.indexOf(">");
			// boolean string double
			arr[0] = temp.substring(0, nEnd);
			// abc
			arr[1] = temp.substring(nEnd + 1);
			// 返回
			return arr;
		} else {
			return null;
		}
	}

	/**
	 * 向指定文件中写入内容
	 * 
	 * @param fileMenu
	 *            文件路径，包括分类目录，如果是代码类，还含有包目录
	 * @param content
	 *            待写入内容
	 * @return true: 写入成功 false: 写入失败
	 */
	private boolean writeFile(String fileMenu, String content) {
		FileWriter fw = null;
		try {
			// 记录生成信息
			mp.put(fileMenu, content.getBytes(SOURCE_CHARSET));

			// 设定了硬盘输出目录
			if (outputDir != null) {
				String fullMenu = outputDir + File.separator + fileMenu;
				// 检查目录是否存在
				int nIndex = fullMenu.lastIndexOf(File.separator);
				checkDirectory(fullMenu.substring(0, nIndex));

				// 写入文件
				fw = new FileWriter(fullMenu);
				fw.writeString(content, SOURCE_CHARSET);
			}

			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * @param fileName
	 *            文件名称
	 * @return 文件的文本内容
	 */
	private String readFile(String fileName) {
		// 文件内容
		String content = null;
		FileReader fr = null;
		try {
			fr = new FileReader(fileName);
			content = fr.readString(SOURCE_CHARSET);
		} catch (Exception ex) {
		}

		return content;
	}

	/**
	 * 检查是否存在指定的目录，如果不存在，则生成该目录
	 * 
	 * @param dir
	 *            指定的目录
	 */
	private void checkDirectory(String dir) {
		File f = new File(dir);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	/**
	 * @return 毫秒形式的当前时间
	 */
	private String getCurrentTimeMillis() {
		return String.valueOf(System.currentTimeMillis());
	}

	/**
	 * @return 所在模块名称
	 */
	private String getModuleName() {
		String tablename = tbl.getTableName().toLowerCase();
		int nIndex = tablename.indexOf("_");
		if (nIndex < 0)
			return tablename;
		else
			return tablename.substring(0, nIndex);
	}

	/**
	 * @return 得到代码文件包名
	 */
	private String getPackageName() {
		return tbl.getPackagePrefix() + "." + lowerCaseFirst(getModuleName());
	}

	/**
	 * @return POJO名称
	 */
	private String getPojoName() {
		String tablename = tbl.getTableName().toLowerCase();
		int nIndex = tablename.indexOf("_");
		// 无下划线分割
		if (nIndex < 0)
			return upperCaseFirst(tablename);

		// 有下划线分割
		tablename = tablename.substring(nIndex);
		// 把第一个下滑线之后的所有下划线后第一个字母大写
		String pojoName = "";
		char loopChar = ' ';
		boolean divided = false;
		for (int i = 0; i < tablename.length(); i++) {
			loopChar = tablename.charAt(i);
			if (loopChar == '_') {
				divided = true;
			} else {
				pojoName += (divided ? Character.toUpperCase(loopChar)
						: loopChar);
				divided = false;
			}
		}

		// 去掉尾部的Master或者Base字符
		pojoName = pojoName.replaceFirst("Master$", "");
		pojoName = pojoName.replaceFirst("Base$", "");

		return pojoName;
	}

	/**
	 * @return 字段数量
	 */
	private int getFieldCount() {
		return tbl.getFieldList().size();
	}

	/**
	 * @param nIndex
	 *            字段序号
	 * @return 字段信息
	 */
	private DbtableField getField(int nIndex) {
		if (0 <= nIndex && nIndex < getFieldCount()) {
			return tbl.getFieldList().get(nIndex);
		} else {
			return null;
		}
	}

	/**
	 * @return 关键字段在所有字段中的序号
	 */
	private int getPkIndex() {
		int nCount = getFieldCount();
		for (int i = 0; i < nCount; i++) {
			DbtableField fe = getField(i);
			if (fe.getIsPk() != null && fe.getIsPk().booleanValue())
				return i;
		}

		return -1;
	}

	/**
	 * 把传入的字符串首字母小写
	 * 
	 * @param str
	 *            传入的字符串
	 * @return 首字母小写的字符串
	 */
	private String lowerCaseFirst(String str) {
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}

	/**
	 * 把传入的字符串首字母大写
	 * 
	 * @param str
	 *            传入的字符串
	 * @return 首字母大写的字符串
	 */
	private String upperCaseFirst(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * 检查表名称是否含有关键字，只要含有其中1个关键字，即返回true
	 * 
	 * @param has 关键字，多个关键字以半角逗号间隔
	 * 
	 * @return 表名称是否含有关键字
	 */
	private boolean ifTableName(String has) {
		if(has == null || has.trim().length() == 0) {
			// 未设定has属性，默认匹配
			return true;
		}
		
		String[] keys = has.split(",");
		for(String key : keys) {
			if(key == null || key.trim().length() == 0) {
				continue;
			}
			
			key = key.trim();
			
			if(tbl.getTableName().indexOf(key) > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return 字段的Java类型
	 */
	private String getJavaType(DbtableField fe) {
		// 得到该字段信息
		String strType = fe.getFieldType();
		if (strType.equals("string") || strType.equals("text")) {
			strType = "String";
		} else if (strType.equals("integer")) {
			// Java中最大的整数长度是10位，因此长度超过10，就定义为长整型			
			int n = LogicUtility.parseInt(fe.getFieldLength(), 0);
			strType = (n < 10 ? "Integer" : "Long");
		} else if (strType.equals("boolean")) {
			strType = "Boolean";
		} else if (strType.equals("double")) {
			strType = "Double";
		} else if (strType.equals("date")) {
			strType = "Date";
		}

		return strType;
	}

	/**
	 * @return 字段的SQL类型（MySQL数据库）
	 */
	private String getSqlType(DbtableField fe) {
		// 得到该字段信息
		String sqlType = fe.getFieldType();
		if (sqlType.equals("string")) {
			// 如果字段名以id结尾，表示主键或者外键字段
			String name = fe.getFieldName().toLowerCase();
			if (name.endsWith("id"))
				sqlType = "char";
			else
				sqlType = "varchar";
		} else if (sqlType.equals("integer")) {
			// Java中最大的整数长度是10位，因此长度超过10，就定义为长整型			
			int n = LogicUtility.parseInt(fe.getFieldLength(), 0);
			if(n <= 4) {
				sqlType = "tinyint";
			} else if(n < 10) {
				sqlType = "int";
			} else {
				sqlType = "bigint";
			}
		} else if (sqlType.equals("boolean")) {
			// boolean值映射于MySQL中为int类型
			sqlType = "tinyint";
		} else if (sqlType.equals("double")) {
			//
		} else if (sqlType.equals("date")) {
			// date值映射于MySQL中为DateTime类型
			return "DateTime";
		} else if (sqlType.equals("text")) {
		}

		return sqlType;
	}

	/**
	 * 将属性转化为Map对象
	 * 
	 * @param attributeString 标签的所有属性
	 * @return 属性Map对象
	 */
	private Map<String, String> toAttributes(String attributeString) {
		if(attributeString == null || attributeString.trim().length() == 0) {
			return null;
		}
		
		int nIndex = 0;
		
		String[] ss = attributeString.split("\\s+");
		Map<String, String> mp = new HashMap<String, String>();
		for(String s : ss) {
			// key=value
			if(s == null || s.trim().length() == 0) {
				continue;
			}
			
			nIndex = s.indexOf("=");
			if(nIndex < 0) {
				mp.put(s.trim(), null);
				continue;
			} else if(nIndex == 0) {
				// 忽略
				continue;
			}

			String key = s.substring(0, nIndex);
			String value = s.substring(nIndex + 1).trim();
			
			// 去除引号
			if(value.startsWith("\"") || value.startsWith("\'")) {
				value = value.substring(1);
			}
			if(value.endsWith("\"") || value.endsWith("\'")) {
				value = value.substring(0, value.length() - 1);
			}
			
			mp.put(key, value);
		}
		
		return mp;
	}
}