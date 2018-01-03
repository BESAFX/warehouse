package com.besafx.app.entity.enums;

public enum ContractType {
    Contract1("عقد دورات تأهيلية / دورات تطويرية / برامج تدريبية - رجال"),
    Contract4("عقد دورات تأهيلية / دورات تطويرية / برامج تدريبية - سيدات"),
    Contract3("عقد برامج اللغة الإنجليزية - رجال");
    private String name;

    ContractType(String name) {
        this.name = name;
    }

    public static ContractType findByName(String name) {
        for (ContractType v : values()) {
            if (v.getName().equals(name)) {
                return v;
            }
        }
        return null;
    }

    public static ContractType findByValue(String value) {
        for (ContractType v : values()) {
            if (v.name().equals(value)) {
                return v;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
}
