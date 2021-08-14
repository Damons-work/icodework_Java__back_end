package team.bangbang.spring.converter;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

/**
 * @author 帮帮组
 * @version 1.0 2018-9-15
 */
@Configuration
public class HttpMessageConverterConfig {
	/* 将long转换为string显示 */
	private static final ToStringSerializer longToString = new ToStringSerializer();

	/**
	 * 引入Fastjson解析json，不使用默认的jackson
	 * 必须在pom.xml引入fastjson的jar包，并且版必须大于1.2.10
	 * @return HttpMessageConverters对象
	 */
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        //1、定义一个convert转换消息的对象
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
        supportedMediaTypes.add(MediaType.APPLICATION_PDF);
        supportedMediaTypes.add(MediaType.APPLICATION_RSS_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_XHTML_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_XML);
        supportedMediaTypes.add(MediaType.IMAGE_GIF);
        supportedMediaTypes.add(MediaType.IMAGE_JPEG);
        supportedMediaTypes.add(MediaType.IMAGE_PNG);
        supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
        supportedMediaTypes.add(MediaType.TEXT_HTML);
        supportedMediaTypes.add(MediaType.TEXT_MARKDOWN);
        supportedMediaTypes.add(MediaType.TEXT_PLAIN);
        supportedMediaTypes.add(MediaType.TEXT_XML);
        fastConverter.setSupportedMediaTypes(supportedMediaTypes);

        //2、添加fastjson的配置信息
        FastJsonConfig fastJsonConfig = fastConverter.getFastJsonConfig();
        if (fastJsonConfig == null) {
        	fastJsonConfig = new FastJsonConfig();
        }

        SerializerFeature[] serializerFeatures = new SerializerFeature[]{
                // 输出key是包含双引号
                SerializerFeature.QuoteFieldNames,
                // 是否输出为null的字段,若为null 则显示该字段
                SerializerFeature.WriteMapNullValue,
                // 数值字段如果为null，则输出为0
        		// SerializerFeature.WriteNullNumberAsZero,
        		// 输出非字符串类型的值为字符串
                // SerializerFeature.WriteNonStringValueAsString,
                // 循环引用
                SerializerFeature.DisableCircularReferenceDetect
        };

        fastJsonConfig.setSerializerFeatures(serializerFeatures);

        fastJsonConfig.setCharset(Charset.forName("UTF-8"));

        SerializeConfig serializeConfig = fastJsonConfig.getSerializeConfig();
        if(serializeConfig == null) {
        	serializeConfig = new SerializeConfig();
        }

        // 替换长整型的数据处理
        serializeConfig.put(BigInteger.class, longToString);
        serializeConfig.put(Long.class, longToString);
        serializeConfig.put(Long.TYPE, longToString);
        fastJsonConfig.setSerializeConfig(serializeConfig);
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");

        //3、在convert中添加配置信息
        fastConverter.setFastJsonConfig(fastJsonConfig);
        //4、将convert添加到converters中
        HttpMessageConverter<?> converter = fastConverter;

        return new HttpMessageConverters(converter);
    }
}
