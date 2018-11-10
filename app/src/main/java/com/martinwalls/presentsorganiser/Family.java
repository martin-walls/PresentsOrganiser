package com.martinwalls.presentsorganiser;

import java.util.ArrayList;
import java.util.List;

public class Family implements Comparable<Family> {
    private String familyName;
    private List<Person> familyMembers = new ArrayList<>();
    private boolean isSection;
    private boolean isPersonWithoutFamily = false;

    public Family() {}

    public Family(String familyName) {
        this.familyName = familyName;
    }

    public Family(String familyName, List<Person> familyMembers) {
        this.familyName = familyName;
        this.familyMembers = familyMembers;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public List<Person> getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(List<Person> familyMembers) {
        this.familyMembers = familyMembers;
    }

    public boolean isSection() {
        return isSection;
    }

    public void setSection(boolean section) {
        isSection = section;
    }

    public boolean isPersonWithoutFamily() {
        return isPersonWithoutFamily;
    }

    public void setPersonWithoutFamily(boolean personWithoutFamily) {
        isPersonWithoutFamily = personWithoutFamily;
    }

    @Override
    public int compareTo(Family f) {
        return this.getFamilyName().compareToIgnoreCase(f.getFamilyName());
    }
}
