### Entities:

1. Patients. We need to store data about their card number, name, gender, date of birth, medical issue, arrival time, priority level of emergency and a room number (when assigned)

2. Priorities. We need to store data about priority level (1-5), verbal explanation of the level, approximate time of operation. 

3. Rooms. We need to store data about room number, assigned specialist, and boolean occupation status: is room occupied (true) or available (false).



### Database ERD (relationship diagram)
![Database Schema](/docs/schema.png)


### Attributes:

1. Patients:
- patient id (int) - PRIMARY KEY, 
- patient card number (varchar),
- patient name (varchar),
- patient gender (varchar),
- patient date of birth (date),
- patient arrival time (datestamp),
- assigned priority id (int) - FOREIGN KEY to PRIORITIES entity,
- assigned room id (int) - FOREIGN KEY to ROOMS entity.

2. Priorities:
- priority id (int) - PRIMARY KEY, 
- priority description (varchar),
- approximate time in minutes (int).

3. Rooms:
- room id (int) - PRIMARY KEY, 
- assigned doctor (varchar),
- status of occupation (boolean).





