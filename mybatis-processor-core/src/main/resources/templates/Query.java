package {{query.packageName}};

import java.util.*;


public class {{query.queryClazzSimpleName}} {

{{#query.page}}
	/**
	 * 页码 默认 {{query.page}}.
	 */
	private Integer {{query.page}} {{#query.pageDefault}}= {{query.pageDefault}}{{/query.pageDefault}};

{{/query.page}}
{{#query.size}}
	/**
	 * 每页大小 默认 {{query.sizeDefault}}.
	 */
	private Integer {{query.size}} {{#query.size}}= {{query.sizeDefault}}{{/query.size}};

{{/query.size}}
{{#query.orderBy}}
	/**
	 * 排序字段 默认 {{query.orderBy}}.
	 */
	private String {{query.orderBy}}{{#query.orderByDefault}} = "{{query.orderByDefault}}"{{/query.orderByDefault}};

{{/query.orderBy}}
{{#query.sort}}
	/**
	 * asc/desc 默认 {{query.sort}}.
	 */
	private String {{query.sort}} {{#query.sortDefault}}= "{{query.sortDefault}}"{{/query.sortDefault}};
{{/query.sort}}

{{#query.criteria}}
{{#criteria}}
	{{#javaDoc}}
	/**
	 * {{{javaDoc}}}
	 */
	{{/javaDoc}}
	{{#single}}private {{javaType}} {{fieldName}};{{/single}}{{#collection}}private java.util.List<{{javaType}}> {{fieldName}};{{/collection}}
	{{#between}}{{#dateFormat}}@org.springframework.format.annotation.DateTimeFormat(pattern = "{{pattern}}"){{/dateFormat}}
	private {{javaType}} start{{firstUpFieldName}};

	{{#dateFormat}}@org.springframework.format.annotation.DateTimeFormat(pattern = "{{pattern}}"){{/dateFormat}}
	private {{javaType}} end{{firstUpFieldName}};
	{{/between}}

{{/criteria}}
{{/query.criteria}}
{{#query.page}}
	public Integer get{{query.firstUpPage}}(){
		return this.{{query.page}};
	}

	public {{query.queryClazzSimpleName}} set{{query.firstUpPage}}(Integer {{query.page}}){
		this.{{query.page}} = {{query.page}};
		return this;
	}
{{/query.page}}
{{#query.size}}
	public Integer get{{query.firstUpSize}}(){
		return this.{{query.size}};
	}

	public {{query.queryClazzSimpleName}} set{{query.firstUpSize}}(Integer {{query.size}}){
		this.{{query.size}} = {{query.size}};
		return this;
	}
{{/query.size}}
{{#query.orderBy}}
	public String get{{query.firstUpOrderBy}}(){
		return this.{{query.orderBy}};
	}

	public {{query.queryClazzSimpleName}} set{{query.firstUpOrderBy}}(String {{query.orderBy}}){
		this.{{query.orderBy}} = {{query.orderBy}};
		return this;
	}
{{/query.orderBy}}
{{#query.sort}}
	public String get{{query.firstUpSort}}(){
		return this.{{query.sort}};
	}

	public {{query.queryClazzSimpleName}} set{{query.firstUpSort}}(String {{query.sort}}){
		this.{{query.sort}} = {{query.sort}};
		return this;
	}
{{/query.sort}}

{{#query.criteria}}
{{#criteria}}
{{#single}}
	public {{javaType}} get{{firstUpFieldName}}() {
		return this.{{fieldName}};
	}

	public {{query.queryClazzSimpleName}} set{{firstUpFieldName}}({{javaType}} {{fieldName}}) {
		this.{{fieldName}} = {{fieldName}};
		return this;
	}

{{/single}}
{{#between}}
	public {{javaType}} getStart{{firstUpFieldName}}() {
		return this.start{{firstUpFieldName}};
	}

	public {{query.queryClazzSimpleName}} setStart{{firstUpFieldName}}({{javaType}} start{{firstUpFieldName}}) {
		this.start{{firstUpFieldName}} = start{{firstUpFieldName}};
		return this;
	}

	public {{javaType}} getEnd{{firstUpFieldName}}() {
		return this.end{{firstUpFieldName}};
	}

	public {{query.queryClazzSimpleName}} setEnd{{firstUpFieldName}}({{javaType}} end{{firstUpFieldName}}) {
		this.end{{firstUpFieldName}} = end{{firstUpFieldName}};
		return this;
	}

{{/between}}
{{#collection}}
	public java.util.List<{{javaType}}> get{{firstUpFieldName}}() {
		return this.{{fieldName}};
	}

	public {{query.queryClazzSimpleName}} set{{firstUpFieldName}}(java.util.List<{{javaType}}> {{fieldName}}) {
		this.{{fieldName}} = {{fieldName}};
		return this;
	}

{{/collection}}
{{/criteria}}
{{/query.criteria}}


    public {{query.exampleClazzName}} toExample(){
		{{query.exampleClazzName}} query = {{query.exampleClazzName}}.create();
		{{#query.page}}query.page({{query.page}},{{query.size}});{{/query.page}}
		{{#query.orderBy}}
		if({{query.orderBy}} != null){
			query.orderBy({{query.orderBy}},("desc".equalsIgnoreCase({{query.sort}})?"desc":"asc"));
		}
		{{/query.orderBy}}
		{{#query.criteria}}
		{{#or}}
		query.or();
		{{/or}}
		{{#criteria}}
		{{#single}}
        if ({{fieldName}} != null && !"".equals({{fieldName}})) {
			{{#equalTo}}query.and{{firstUpFieldName}}EqualTo({{fieldName}});{{/equalTo}}{{#notEqualTo}}query.and{{firstUpFieldName}}NotEqualTo({{fieldName}});{{/notEqualTo}}{{#greaterThan}}query.and{{firstUpFieldName}}GreaterThan({{fieldName}});{{/greaterThan}}{{#greaterThanOrEqualTo}}query.and{{firstUpFieldName}}GreaterThanOrEqualTo({{fieldName}});{{/greaterThanOrEqualTo}}{{#lessThan}}query.and{{firstUpFieldName}}LessThan({{fieldName}});{{/lessThan}}{{#lessThanOrEqualTo}}query.and{{firstUpFieldName}}LessThanOrEqualTo({{fieldName}});{{/lessThanOrEqualTo}}{{#like}}query.and{{firstUpFieldName}}Like({{fieldName}});{{/like}}{{#notLike}}query.and{{firstUpFieldName}}NotLike({{fieldName}});{{/notLike}}
        }
		{{/single}}
		{{#collection}}
        if ({{fieldName}} != null && !{{fieldName}}.isEmpty()) {
			{{#in}}query.and{{firstUpFieldName}}In({{fieldName}});{{/in}}{{#notIn}}query.and{{firstUpFieldName}}NotIn({{fieldName}});{{/notIn}}
        }
		{{/collection}}
		{{#between}}
        if (start{{firstUpFieldName}} != null || end{{firstUpFieldName}} !=null) {
        	if(start{{firstUpFieldName}} != null){
				query.and{{firstUpFieldName}}GreaterThanOrEqualTo({{^numberFormat}}start{{firstUpFieldName}}{{/numberFormat}}{{#numberFormat}}new java.util.Date(start{{firstUpFieldName}}){{/numberFormat}});
			}
        	if(end{{firstUpFieldName}} != null){
				query.and{{firstUpFieldName}}LessThanOrEqualTo({{^numberFormat}}end{{firstUpFieldName}}{{/numberFormat}}{{#numberFormat}}new java.util.Date(end{{firstUpFieldName}}){{/numberFormat}});
			}
        }
		{{/between}}
		{{/criteria}}
		{{/query.criteria}}
		return query;
    }


}
