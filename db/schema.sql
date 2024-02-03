CREATE database emergency_waitlist;

CREATE TABLE priorities
(priority_id int NOT NULL, priority_desc VARCHAR(50) NOT NULL, 
appr_time int NOT NULL, PRIMARY KEY (priority_id));

CREATE TABLE rooms
(room_id int NOT NULL, doctor_assigned VARCHAR(50), 
room_status boolean NOT NULL DEFAULT false, PRIMARY KEY (room_id));

CREATE TABLE patients
(patient_id int NOT NULL, card_number VARCHAR(50) NOT NULL, 
patient_name VARCHAR(50) NOT NULL, 
gender VARCHAR(50), date_of_birth DATE, 
med_issue VARCHAR(50), arrival_time TIMESTAMP DEFAULT NOW(), 
priority_id int, room_id int, 
PRIMARY KEY (patient_id),
FOREIGN KEY (priority_id) REFERENCES priorities(priority_id),
FOREIGN KEY (room_id) REFERENCES rooms(room_id));

insert into priorities (priority_id, priority_desc, appr_time)
values 
(1, "resuscitative", 30),
(2, "emergent", 20),
(3, "urgent", 18),
(4, "less urgent" , 15),
(5, "not urgent", 10);

insert into rooms (room_id, doctor_assigned)
values 
(101, "Emily Lai"),
(102, "Jessica Pham"),
(103, "Linda Levy"),
(104, "Mark Lakey"),
(105, "Robert Exley"),
(106, "William Maguire"),
(107, "David Brooke");

insert into patients (patient_id, card_number, patient_name, gender, date_of_birth, med_issue, priority_id)
values 
(1230001, "ABC", "Francoise Rautenstrauch", "female", "2002-06-15", "broken leg", 3),
(1230002, "BCD", "Kendra Loud", "female", "1950-08-29", "heart attack", 1),
(1230003, "CDE", "Lourdes Bauswell", "male", "1983-12-15", "cold", 5),
(1230004, "EFG", "Hannah Edmison", "female", "1990-10-21", "shoulder dislocation", 3),
(1230005, "FGH", "Tom Loeza", "male", "1967-10-22", "breath shortage", 2),
(1230006, "GHI", "Queenie Kramarczyk", "female", "2006-09-14", "vomit", 4),
(1230007, "HIJ", "Hui Portaro", "male", "1978-06-25", "overall trauma in car incident", 1),
(1230008, "IJK", "Danilo Pride", "male", "1944-08-04", "chest pain", 2),
(1230009, "JKL", "Valentin Billa", "male", "1963-10-23", "chest pain", 4),
(1230010, "KLM", "Willard Lablanc", "male", "2020-12-21", "high temprature", 4);

