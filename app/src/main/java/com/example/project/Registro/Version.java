package com.example.project.Registro;

public class Version {

    private String code_name;
    private boolean expandable;

    public Version(String code_name) {
        this.code_name = code_name;
        this.expandable = false;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public String getCode_name() {
        return code_name;
    }

    public void setCode_name(String code_name) {
        this.code_name = code_name;
    }

    @Override
    public String toString() {
        return "Version{" +
                "code_name='" + code_name + '\'' +
                '}';
    }
}
