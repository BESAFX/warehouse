package com.besafx.app.entity.enums;
public enum StudentCondition {
    Fire("مفصول"),
    Delay("مؤجل"),
    Restore("استعادة دورة"),
    Pause("منقطع");
    private String name;
    StudentCondition(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public static StudentCondition findByName(String name){
        for(StudentCondition v : values()){
            if( v.getName().equals(name)){
                return v;
            }
        }
        return null;
    }
    public static StudentCondition findByValue(String value){
        for(StudentCondition v : values()){
            if( v.name().equals(value)){
                return v;
            }
        }
        return null;
    }
}
