package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class PatientVisit implements Comparable {
    private int id;
    private String cardNumber;
    private String name;
    private String gender;
    private String dateOfBirth;
    private String arrivalTime;
    private String issue;
    private int priority;
    private int room = 0;

    public PatientVisit() {
        this.cardNumber = "";
        this.name = "";
        this.gender = "";
        this.dateOfBirth = "";
        this.issue = "";
        this.priority = 0;
    }

    public PatientVisit(int id, String card, String name, String gender, String dateOfBirth,
                        String issue, String arrivalTime, int priority, int room) {
        this.id = id;
        this.cardNumber = card;
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.issue = issue;
        this.arrivalTime = arrivalTime;
        this.priority = priority;
        this.room = room;
    }

    public PatientVisit(int id, String card, String name, String gender, String dateOfBirth,
                        String issue, String arrivalTime, int priority) {
        this.id = id;
        this.cardNumber = card;
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.issue = issue;
        this.arrivalTime = arrivalTime;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public ArrayList<String> patientForTable() {
        ArrayList<String> thisPatient = new ArrayList<>();
        thisPatient.add(name);
        thisPatient.add(cardNumber);
        thisPatient.add(gender);
        thisPatient.add(dateOfBirth);
        thisPatient.add(issue);
        thisPatient.add(String.valueOf(priority));

        return thisPatient;
    }

    @Override
    public int compareTo(Object o) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        Date date = null;
        try {
            date = sdf.parse(arrivalTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        String comparingDateTime = ((PatientVisit)o).getArrivalTime();
        Date comparing = null;
        try {
            comparing =  sdf.parse(comparingDateTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        int result = date.compareTo(comparing);

        return result;
    }
}
