package {{metadata.packageName}};


import javax.sql.DataSource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.function.Consumer;

public class {{metadata.typeHandlerClazzSimpleName}} {

    {{#metadata.columnMetadataList}}
    public void decode{{firstUpFieldName}}(ResultSet resultSet, {{metadata.domainClazzName}} t, String name, Class<{{javaType}}> targetType) throws SQLException {
        {{#isEnums}}
        String value = resultSet.getString(name);
        if(value == null){
            return;
        }
        t.set{{firstUpFieldName}}(Enum.valueOf(targetType, value));
        {{/isEnums}}
        {{^isEnums}}
        {{javaType}} value = resultSet.getObject(name, targetType);
        if(value == null){
            return;
        }
        t.set{{firstUpFieldName}}(value);
        {{/isEnums}}
    }

    public Object encode{{firstUpFieldName}}({{javaType}} value) {
        {{^isEnums}}return value;{{/isEnums}}{{#isEnums}}return value == null ? null: String.valueOf(value);{{/isEnums}}
    }

    public List encode{{firstUpFieldName}}List(List<{{javaType}}> values) {
        {{^isEnums}}return values;{{/isEnums}}
        {{#isEnums}}return values.stream().map(String::valueOf).collect(java.util.stream.Collectors.toList());{{/isEnums}}
    }

    {{/metadata.columnMetadataList}}
}

