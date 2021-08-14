package cn.weixin.mp.aes;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import team.bangbang.common.exception.BizException;

/** 
 * AES加密方式 <br> 
 *  
 * @author [zlm]<br>
 * @version weixin<br>
 * @CreateDate 2018年2月25日 <br>
 * @see weixin.mp.aes <br>
 */
public class AesUtil {
    /**
     * 对明文进行加密 <br> 
     * @param value 铭文
     * @param keyStr 密钥
     * @return 密文<br>
     */ 
    public static String encode (String value,String keyStr){
        try {
            //密钥默认为ABCDEFGHIJKLMNOP
            if (keyStr == null || keyStr.trim().length() == 0) {
                keyStr = "ABCDEFGHIJKLMNOP";
            }
            if (keyStr.length() != 16) {
                throw new BizException("The key's length is not 16");
            }
            byte[] keyBytes = keyStr.getBytes("utf8");
            
            //Key转换
            Key key = new SecretKeySpec(keyBytes, "AES");
            
            //加密
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encodeResult = cipher.doFinal(value.getBytes());
          //返回加密的值&此处使用BASE64做转码功能，同时能起到2次加密的作用
            return Base64.getEncoder().encodeToString(encodeResult);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block <br>
            e.printStackTrace();
        }
        catch (BizException e) {
            // TODO Auto-generated catch block <br>
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 解密 <br> 
     * @param value 密文
     * @param keyStr 密钥key
     * @return 铭文<br>
     */ 
    public static String decode (String value,String keyStr){
        try {
            //密钥默认为ABCDEFGHIJKLMNOP
            if (keyStr == null || keyStr.trim().length() == 0) {
                keyStr = "ABCDEFGHIJKLMNOP";
            }
            if (keyStr.length() != 16) {
                throw new BizException("The key's length is not 16");
            }
            byte[] keyBytes = keyStr.getBytes("utf8");
            
            //Key转换
            Key key = new SecretKeySpec(keyBytes, "AES");
            
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            
            //解密
            cipher.init(Cipher.DECRYPT_MODE, key);
            
            byte[] encodedBytes = Base64.getDecoder().decode(value.getBytes()); 
            byte[] result = cipher.doFinal(encodedBytes);//对加密后的字节数组进行解密 
            return new String(result);

            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block <br>
            e.printStackTrace();
        }
        catch (BizException e) {
            // TODO Auto-generated catch block <br>
            e.printStackTrace();
        }
        return null;
    }
    
    public static void main(String[] args) {
        String value = encode("6456544", null);
        System.out.println(value);;
        
        System.out.println(decode(value, null));
        

    }
}