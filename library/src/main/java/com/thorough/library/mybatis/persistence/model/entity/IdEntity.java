package com.thorough.library.mybatis.persistence.model.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thorough.library.mybatis.persistence.annotation.Primary;
import com.thorough.library.mybatis.persistence.idgen.IdGenerate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.xml.bind.annotation.XmlTransient;
import java.util.HashMap;
import java.util.Map;


public abstract class IdEntity<P> extends CommonEntity<P>{

	private static final long serialVersionUID = 1L;

	/**
	 * 实体编号（唯一标识）
	 */
	@Primary(name = "id")
	protected P id;


	/**
	 * 自定义SQL（SQL标识，SQL内容）
	 */
	@JsonIgnore
	@XmlTransient
	@JSONField(serialize = false)
	protected Map<String, String> sqlMap;

	/**
	 * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
	 * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
	 */

	public IdEntity() {
	}

	public IdEntity(P id) {
		this();
		this.id = id;
	}

	public P getId() {
		return id;
	}

	public void setId(P id) {
		this.id = id;
	}

	public static String getFieldId() {
		return "id";
	}


	@JsonIgnore
	@XmlTransient
	public Map<String, String> getSqlMap() {
		if (sqlMap == null){
			sqlMap = new HashMap<>();
		}
		return sqlMap;
	}

	@JsonBackReference
	public void setSqlMap(Map<String, String> sqlMap) {
		this.sqlMap = sqlMap;
	}

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        IdEntity<?> that = (IdEntity<?>) obj;
        return null == this.getId() ? false : this.getId().equals(that.getId());
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

	/**
	 * 插入之前执行方法，需要手动调用
	 */
	@Override
	public void preInsert(){
		// 不限制ID为UUID，调用setIsNewRecord()使用自定义ID
		if (!this.isNewRecord){
            P clazz = this.getPrimaryKeyType();
			if (String.class.isAssignableFrom((Class<?>) clazz)){
                setId((P) IdGenerate.uuid());
			}else if (Long.class.isAssignableFrom((Class<?>) clazz)){
                setId((P) IdGenerate.nextId());
			}
		}
	}

	/**
	 * 更新之前执行方法，需要手动调用
	 */
	@Override
	public void preUpdate(){
	}

	@Override
	public boolean isNewRecord() {
		Class clazz = (Class) this.getPrimaryKeyType();
		if (String.class.isAssignableFrom(clazz)){
			return isNewRecord || StringUtils.isBlank((String) getId());
		}else if (Integer.class.isAssignableFrom(clazz)){
			return isNewRecord || getId() != null || (Integer)getId() != 0;
		} else throw new RuntimeException("不支持的主键类型");
	}
}
