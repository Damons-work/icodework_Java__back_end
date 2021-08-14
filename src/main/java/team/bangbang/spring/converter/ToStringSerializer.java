package team.bangbang.spring.converter;

import java.io.IOException;
import java.lang.reflect.Type;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

/**
 * 修正fastjson，将long转换为string显示
 *
 * @author 帮帮组
 * @version 1.0 2018年10月16日
 */
public class ToStringSerializer implements ObjectSerializer {
	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType,

			int features) throws IOException {

		SerializeWriter out = serializer.out;

		if (object == null) {
			out.writeNull();
			return;
		}

		String strVal = object.toString();

		out.writeString(strVal);
	}
}
