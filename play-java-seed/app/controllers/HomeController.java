package controllers;

import play.mvc.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import models.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    private int lastID = 0;
    private  ArrayList<PatientVisit> existingPatients;
    private ArrayList<Room> roomsPatientsStr;
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(views.html.index.render());
    }

    public Result newpatient() {
        return ok(views.html.patient.render());
    }
    public Result patientportal() {
        return ok(views.html.patientportal.render());
    }
    public Result checkpatient(String string) {
        ArrayList<ArrayList> patients = new ArrayList<>();
        existingPatients = getResults();
        Map<Integer, Integer> timemap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> counter = new HashMap<Integer, Integer>();
        Map<Integer, String> namemap = new HashMap<Integer, String>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url="jdbc:mysql://127.0.0.1:3306/emergency_waitlist";
            Connection conn = DriverManager.getConnection(url, "root", "24121972");

            String sqlSelect = "select * from priorites";
            Statement statement = conn.createStatement();

            ResultSet r1 = statement.executeQuery(sqlSelect);

            while (r1.next()) {
               int id = r1.getInt("priority_id");
               int time = r1.getInt("appr_time");
               String desc = r1.getString("priority_desc");
                timemap.put(id, time);
                namemap.put(id, desc);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }


            for (PatientVisit existingPatient : existingPatients) {
                if (existingPatient.getCardNumber() != string) {
                    int key = existingPatient.getPriority();
                    if (counter.containsKey(key)) {
                        int k = counter.get(key) + 1;
                        counter.replace(key, k);
                    } else {
                        counter.put(key, 1);
                    }
                } else {
                    break;
                }
        }
        int numberPatients = 0;
        int time = 0;
        for (int i=1; i<6; i++) {
            numberPatients += counter.getOrDefault(i, 0);
            time += counter.getOrDefault(i, 0) * timemap.getOrDefault(i, 0);

        }
        String finalresult = "Number of patients before you in line: " + numberPatients + "\nApproximate waiting time: "
                + 90 + " minutes";
        return ok(Json.toJson(finalresult));
    }


    public Result version() {
        ObjectNode versionResult = Json.newObject();
        versionResult.put("appname", "Emergency waitlist system");
        versionResult.put("version", "v0.1.0");
        return ok(versionResult);
    }
    public Result getlocal() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String timeNow = dtf.format(now);
        return ok(Json.toJson(timeNow));
    }

    public Result registerpatient(Http.Request request) throws IOException, ClassNotFoundException, SQLException {
        JsonNode newpatientjson = request.body().asJson();
        PatientVisit newpatient = new PatientVisit();
        newpatient.setName(newpatientjson.get("name").asText(""));
        newpatient.setCardNumber(newpatientjson.get("card_number").asText(""));
        newpatient.setIssue(newpatientjson.get("issue").asText(""));
        newpatient.setDateOfBirth(newpatientjson.get("dateOfBirth").asText(""));
        newpatient.setGender(newpatientjson.get("gender").asText(""));
        newpatient.setPriority(newpatientjson.get("priority").asInt(0));
        lastID ++;

        Class.forName("com.mysql.jdbc.Driver");
        String url="jdbc:mysql://127.0.0.1:3306/emergency_waitlist";
        Connection conn = DriverManager.getConnection(url, "root", "24121972");
        Statement statement = conn.createStatement();

        String sqlInsert = "INSERT INTO patients " +
                "(patient_id, card_number, patient_name, gender, date_of_birth, med_issue, priority_id) " +
                "VALUES ("
        + lastID + ", \"" + newpatient.getCardNumber() + "\", \"" + newpatient.getName()
                + "\", \"" + newpatient.getGender() + "\", \"" + newpatient.getDateOfBirth()
                + "\", \"" + newpatient.getIssue() + "\", " + newpatient.getPriority() + ");";

       statement.execute(sqlInsert);
        conn.close();
        return ok();
    }

    public Result database() {
        ArrayList<String> roomsStr = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url="jdbc:mysql://127.0.0.1:3306/emergency_waitlist";
            Connection conn = DriverManager.getConnection(url, "root", "24121972");

            String sqlSelect = "select * from rooms";
            Statement statement = conn.createStatement();

            ResultSet r1 = statement.executeQuery(sqlSelect);

           while (r1.next()) {
                System.out.println(r1.getString("room_id"));
               System.out.println(r1.getString("room_status"));
                roomsStr.add(r1.getString("room_id"));
            }
            conn.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        JsonNode rooms = Json.toJson(roomsStr);
        return ok(rooms);
    }

    public Result rooms() {
        roomsPatientsStr = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url="jdbc:mysql://127.0.0.1:3306/emergency_waitlist";
            Connection conn = DriverManager.getConnection(url, "root", "24121972");

            String sqlSelect = "select * from rooms";
            Statement statement = conn.createStatement();

            ResultSet resultSet = statement.executeQuery(sqlSelect);

            while (resultSet.next()) {
                Room newRoom = new Room(resultSet.getInt("room_id"),
                        resultSet.getString("doctor_assigned"), resultSet.getInt("room_status"));

                roomsPatientsStr.add(newRoom);
            }
            roomsPatientsStr.forEach(action ->
            {
                if (!action.isOccupied()) {
                    action.setPatientAssigned(existingPatients.get(0).getName());
                    action.setOccupied(true);
                    String sqlModifyRoom = "UPDATE rooms " +
                            "SET room_status = 1 " +
                            "WHERE room_id = " + action.getRoomID() + ";";
                    try {
                        statement.execute(sqlModifyRoom);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    String sqlModifyPatient = "update patients " +
                            "set room_id = " + action.getRoomID() +
                            " where patient_id = " + existingPatients.get(0).getId() + ";";
                    try {
                        statement.execute(sqlModifyPatient);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    existingPatients.remove(0);
                }
                else {
                    String whoPatient = "select patient_name from patients where room_id =  " +
                            action.getRoomID() + ";";
                    String patientName = "";
                    try {
                        ResultSet resultName = statement.executeQuery(whoPatient);
                        resultName.next();
                        patientName = resultName.getString("patient_name");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    action.setPatientAssigned(patientName);
                }
            });

            conn.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        JsonNode rooms = Json.toJson(roomsPatientsStr);

        return ok(rooms);
    }


    public Result releasepatient(int n) {
        int roomNumber = Integer.parseInt("10" + (n+1));
        System.out.println(""+roomNumber);
        final String[] patientName = {""};
        roomsPatientsStr.forEach(action -> {
            if (action.getRoomID() == roomNumber) {
                patientName[0] = action.getPatientAssigned();
            }});
        System.out.println(""+patientName[0]);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url="jdbc:mysql://127.0.0.1:3306/emergency_waitlist";
            Connection conn = DriverManager.getConnection(url, "root", "24121972");
            String sqlModifyRoom = "UPDATE rooms " +
                    "SET room_status = 0 " +
                    "WHERE room_id = " + roomNumber + ";";
            String deletePatient = "DELETE FROM patients WHERE patient_name = \"" + patientName[0] + "\";";
            Statement statement = conn.createStatement();
            statement.execute(sqlModifyRoom);
            statement.execute(deletePatient);
            conn.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        existingPatients = getResults();
        rooms();
        return ok();
    }
    public Result loadpatients() {
        existingPatients = getResults();
        return ok();
    }
    public Result loadtable() {
        ArrayList<ArrayList> patients = new ArrayList<>();
        existingPatients.forEach(x -> {
            if (x.getRoom() == 0) {patients.add(x.patientForTable());}});
        return ok(Json.toJson(patients));
    }
    private ArrayList<PatientVisit> getResults () {
        existingPatients = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url="jdbc:mysql://127.0.0.1:3306/emergency_waitlist";
            Connection conn = DriverManager.getConnection(url, "root", "24121972");

            String sqlSelect = "select * from patients";
            Statement statement = conn.createStatement();

            ResultSet patientTable = statement.executeQuery(sqlSelect);

            while (patientTable.next()) {
                lastID = patientTable.getInt("patient_id");
                PatientVisit thisPatient = new PatientVisit(
                        lastID,
                        patientTable.getString("card_number"),
                        patientTable.getString("patient_name"),
                        patientTable.getString("gender"),
                        patientTable.getString("date_of_birth"),
                        patientTable.getString("med_issue"),
                        patientTable.getString("arrival_time"),
                        patientTable.getInt("priority_id"),
                patientTable.getInt("room_id"));
                existingPatients.add(thisPatient);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        Collections.sort(existingPatients);
        Comparator<PatientVisit> priorityOrder =  new Comparator<PatientVisit>() {
            public int compare(PatientVisit p1, PatientVisit p2) {
                return p1.getPriority() - p2.getPriority();
            }
        };
        Collections.sort(existingPatients, priorityOrder);
        return existingPatients;
    }
}

class Room {
    private boolean occupied = false;
    private String doctor;
    private String patientAssigned;
    private int roomID;

    public Room() {
        doctor = "";
        patientAssigned = "";
        roomID = 0;
    }
    public Room(int id, String doctor, int status) {
        if (status != 0) {
            occupied = true;
        }
        roomID = id;
        this.doctor = doctor;
    }


    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getPatientAssigned() {
        return patientAssigned;
    }

    public void setPatientAssigned(String patientAssigned) {
        this.patientAssigned = patientAssigned;
    }

    public int getRoomID() {
        return roomID;
    }

}

     