package com.thorough.library.mybatis.persistence.model.entity;


import javax.persistence.Column;

public class IdUserDateDelFlagRemarksNameEntity<P> extends IdUserDateDelFlagRemarksEntity<P>{
    private static final long serialVersionUID = 1L;

    @Column(name = "name")
    protected String name; 	    // 名称

    public IdUserDateDelFlagRemarksNameEntity(){
        super();
    }

    public IdUserDateDelFlagRemarksNameEntity(P id){
        super(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getFieldName() {
        return "name";
    }

}
