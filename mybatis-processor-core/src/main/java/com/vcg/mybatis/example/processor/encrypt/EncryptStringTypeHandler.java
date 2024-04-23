package com.vcg.mybatis.example.processor.encrypt;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.StringTypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public class EncryptStringTypeHandler extends BaseTypeHandler<String> {

    private static Function<String, String> ENCRYPT_FUNCTION = s -> s;

    private static Function<String, String> DECRYPT_FUNCTION = s -> s;


    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, encrypt(parameter));
    }

    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String result = rs.getString(columnName);
        return result == null ? null : decrypt(result);
    }

    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String result = rs.getString(columnIndex);
        return result == null ? null : decrypt(result);
    }

    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String result = cs.getString(columnIndex);
        return result == null ? null : decrypt(result);
    }

    /**
     * 设置加密解密转换方法.
     */
    public static void setEncrypt(Function<String, String> encryptFunction,
                                  Function<String, String> decryptFunction) {
        ENCRYPT_FUNCTION = encryptFunction;
        DECRYPT_FUNCTION = decryptFunction;
    }


    /**
     * 解密内容.
     */
    public static String decrypt(String result) {
        return DECRYPT_FUNCTION.apply(result);
    }


    /**
     * 加密内容.
     *
     * @param value 加密值.
     * @return 加密后的字符串.
     */
    public static String encrypt(String value) {
        return ENCRYPT_FUNCTION.apply(value);
    }
}
