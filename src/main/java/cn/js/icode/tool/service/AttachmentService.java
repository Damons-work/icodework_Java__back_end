package cn.js.icode.tool.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import team.bangbang.common.CommonMPI;
import team.bangbang.common.data.Pagination;
import cn.js.icode.tool.data.Attachment;
import cn.js.icode.tool.mapper.AttachmentMapper;

/**
 * 附件信息 - Service
 *
 * @author ICode Studio
 * @version 1.0  2020-05-27
 */
@Service
public final class AttachmentService {
	/* 附件信息（Attachment）Mapper */
	@Resource
	private AttachmentMapper _attachmentMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static AttachmentMapper attachmentMapper = null;

	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		attachmentMapper = _attachmentMapper;
	}

	/**
	 * 得到指定的附件信息
	 * 
	 * @param attachmentId
	 *			指定的附件编号
	 * @return 附件信息
	 */
	public static Attachment getObject(Long attachmentId) {
		if(attachmentMapper == null) {
			return null;
		}

		// 参数校验

		if(attachmentId == null || attachmentId == 0L) {
			return null;
		}

		// 查询条件
		Attachment form = new Attachment();
		form.setId(attachmentId);
		
		return getObject(form, null);
	}

	/**
	 * 插入一条附件信息
	 *
	 * @param data
	 *			插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(Attachment data) {
		if(attachmentMapper == null) {
			return 0;
		}

		if (data.getId() == null) {

			// 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
			long id = CommonMPI.generateSequenceId();
			data.setId(id);

		}

		return attachmentMapper.insert(data);
	}

	/**
	 * 删除附件信息
	 *
	 * @param where
	 *			删除条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(Attachment where, String appendix) {
		if(attachmentMapper == null) {
			return 0;
		}

		return attachmentMapper.delete(where, appendix);
	}

	/**
	 * 查询一条附件信息，并转化为相应的POJO对象
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static Attachment getObject(Attachment where, String appendix) {
		if(attachmentMapper == null) {
			return null;
		}

		return attachmentMapper.getObject(where, appendix);
	}

	/**
	 * 修改附件信息
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param data
	 *			更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(Attachment where, String appendix, Attachment data) {
		if(attachmentMapper == null) {
			return 0;
		}

		return attachmentMapper.update(where, appendix, data);
	}

	/**
	 * 查询多条附件信息，并转化为相应的POJO对象列表
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param pagination
	 *			分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<Attachment> list(Attachment where, String appendix, Pagination pagination) {
		if(attachmentMapper == null) {
			return Collections.emptyList();
		}

		return attachmentMapper.list(where, appendix, pagination);
	}

	/**
	 * 获得符合条件的附件信息数量
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(Attachment where, String appendix) {
		if(attachmentMapper == null) {
			return 0;
		}

		return attachmentMapper.count(where, appendix);
	}

	/**
	 * 保存指定记录的附件信息
	 * 
	 * @param bizId 指定记录的编号
	 * @param title 附件标题
	 * @param urls 附件信息的地址，可以有多个附件
	 * 
	 * @return 保存的数量
	 */
	public static int saveAttachments(Object bizId, String title, String[] urls) {
		String strId = String.valueOf(bizId);
		
		// 删除指定记录之前的指定标题的附件
		Attachment form = new Attachment();
		form.setBizId(strId);
		form.setTitle(title);
		delete(form, null);
		
		int count = 0;
		for(int i = 0; urls != null && i < urls.length; i++) {
			String url = urls[i];
			if(url == null || url.trim().length() == 0) {
				continue;
			}

			Attachment iu = new Attachment();
			iu.setTitle(title);
			iu.setBizId(strId);
			iu.setFileUrl(url);
			// 附件序号，从1开始，用于排序
			iu.setFileNo(i + 1);
			
			int n = insert(iu);
			if(n > 0) count++;
		}
		
		return count;
	}

	/**
	 * 获得指定记录的附件信息
	 * 
	 * @param bizId 指定记录的编号
	 * @param title 附件标题
	 * 
	 * @return 指定记录的附件信息，以附件序号（从1开始，用于排序）为KEY值。
	 *         之所以将KEY值转换为String类型，是为了便于在页面使用map数据
	 */
	public static Map<String, Attachment> getAttachments(Object bizId, String title) {
		String strId = String.valueOf(bizId);
		
		// 查询指定记录的附件信息
		Attachment form = new Attachment();
		form.setBizId(strId);
		form.setTitle(title);
		List<Attachment> iuList = list(form, null, null);
		
		Map<String, Attachment> mp = new HashMap<String, Attachment>();
		if(iuList == null || iuList.isEmpty()) {
			return mp;
		}
		
		for (int i = 0; iuList != null && i < iuList.size(); i++) {
			Attachment iu = iuList.get(i);
			// 附件序号，从1开始，用于排序
			mp.put(String.valueOf(iu.getFileNo()), iu);
		}
		
		return mp;
	}
}
