package com.martinwalls.presentsorganiser;

import java.util.List;

public class GivenPresent implements Comparable<GivenPresent> {
    private int presentId;
    private List<Person> recipientList;
    private int year;
    private String present;
    private String notes;
    private boolean isBought;
    private boolean isSent;

    private boolean isSection;

    public GivenPresent() {}

    public GivenPresent(int year) {
        this.year = year;
    }
    public GivenPresent(int year, boolean isSection) {
        this.year = year;
        this.isSection = isSection;
    }

    public GivenPresent(int year, List<Person> recipientList, String present, String notes,
                        boolean isBought, boolean isSent) {
        this.year = year;
        this.recipientList = recipientList;
        this.present = present;
        this.notes = notes;
        this.isBought = isBought;
        this.isSent = isSent;
        this.isSection = false;
    }

    public GivenPresent(int year, int presentId, List<Person> recipientList, String present, String notes,
                        boolean isBought, boolean isSent) {
        this.year = year;
        this.presentId = presentId;
        this.recipientList = recipientList;
        this.present = present;
        this.notes = notes;
        this.isBought = isBought;
        this.isSent = isSent;
        this.isSection = false;
    }

    public int getPresentId() {
        return presentId;
    }

    public void setPresentId(int presentId) {
        this.presentId = presentId;
    }

    public List<Person> getRecipientList() {
        return recipientList;
    }

    public void setRecipientList(List<Person> recipientList) {
        this.recipientList = recipientList;
    }

    public void addRecipient(Person recipient) {
        this.recipientList.add(recipient);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getPresent() {
        return present;
    }

    public void setPresent(String present) {
        this.present = present;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        this.isBought = bought;
    }

    public int isBoughtInt() {
        if (isBought) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public int isSentInt() {
        if (isSent) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean isSection() {
        return isSection;
    }

    public void setSection(boolean section) {
        isSection = section;
    }

    @Override
    public int compareTo(GivenPresent present) {
        // to sort ascending by year
        return Integer.valueOf(present.getYear()).compareTo(Integer.valueOf(this.getYear()));
    }

    public boolean isEqual(String newPresent, String newNotes,
                           boolean newBought, boolean newSent) {
        boolean isEqual = false;
        if (present.equals(newPresent)
            && notes.equals(newNotes)
            && isBought == newBought
            && isSent == newSent) {
            isEqual = true;
        }
        return isEqual;
    }
}
