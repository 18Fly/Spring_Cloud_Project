package org.project.feign.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 信息返回的格式
 *
 * @param <T> 通配符，以便返回各种类型对象
 */
@Data
public class Return<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    private Map map = new HashMap(); //动态数据

    private String token; //token认证

    public static <T> Return<T> success(T object) {
        Return<T> r = new Return<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    /**
     * 用户信息和生成的Token
     *
     * @param object
     * @param token
     * @param <T>
     * @return 返回查询的用户信息和生成的Token
     */
    public static <T> Return<T> success(T object, String token) {
        Return<T> r = new Return<T>();
        r.data = object;
        r.code = 1;
        r.token = token;
        return r;
    }

    public static <T> Return<T> error(String msg) {
        Return r = new Return();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public Return<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
