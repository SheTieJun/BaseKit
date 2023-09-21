package asm;

public class VariableEntry {
    //名字
    String name;
    //描述
    String desc;

    public VariableEntry(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "VariableEntry{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }
}