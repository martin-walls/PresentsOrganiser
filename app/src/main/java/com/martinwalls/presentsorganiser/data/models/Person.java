package com.martinwalls.presentsorganiser.data.models;

@Deprecated
public class Person implements Comparable<Person> {
    private int id;
    private String name = "";
    private String family = "";
    private boolean isSection;
    private String sectionName;

    public Person() {}

    public Person(int id) {
        this.id = id;
    }

    public Person(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Person(int id, String name, String family) {
        this.id = id;
        this.name = name;
        this.family = family;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public boolean isSection() {
        return isSection;
    }

    public void setSection(boolean section) {
        isSection = section;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    @Override
    public int compareTo(Person p) {
        return this.getName().compareToIgnoreCase(p.getName());
    }
}
