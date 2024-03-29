import MySQLdb


confirmation = ""
while( confirmation not in ('y','Y','n','N') ):
    confirmation = input("WARNING: If the table already exists it will be overwritten, do you wish to proceed? (y/n)\n")

if confirmation in ('n','N'):
    print("Return without change to database")
    exit()

conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
c = conn.cursor()
c.execute('DROP DATABASE IF EXISTS muscle2')
c.execute('CREATE DATABASE muscle2')
c.execute('USE muscle2')

c.execute('''CREATE TABLE PLAN(
    ID INTEGER AUTO_INCREMENT,
    Objective VARCHAR(127),
    PRIMARY KEY (ID)
    )'''
)

c.execute('''CREATE TABLE USER(
    Email VARCHAR(255),
    Name VARCHAR(128) NOT NULL,
    Password TEXT NOT NULL,
    Date_of_birth DATE NOT NULL,
    Gender BOOL NOT NULL,
    Height SMALLINT NOT NULL,
    Weight FLOAT(4,1) NOT NULL,
    Access_token TEXT,
    Profile_image TEXT,
    Plan INTEGER,
    PRIMARY KEY(email),
    FOREIGN KEY(Plan) REFERENCES PLAN(ID)
    )'''
)


c.execute('''CREATE TABLE TRAINING(
    ID INTEGER AUTO_INCREMENT,
    Name VARCHAR(128) NOT NULL,
    Plan_id INTEGER NOT NULL,
    PRIMARY KEY(ID),
    FOREIGN KEY(Plan_id) REFERENCES PLAN(ID)
    )'''
)
c.execute('''CREATE TABLE EXERCISE(
    Name VARCHAR(128),
    Image TEXT,
    Description TEXT,
    Kind VARCHAR(32) NOT NULL,
    PRIMARY KEY(Name)
    )'''
)

c.execute('''CREATE TABLE TRAINING_EXERCISE(
    ID Integer AUTO_INCREMENT,
    Training_id INTEGER,
    Exercise_name VARCHAR(128),
    Sets TINYINT UNSIGNED NOT NULL,
    Repetitions TINYINT UNSIGNED NOT NULL,
    Resting_time TIME,
    Weight TINYINT UNSIGNED,
    FOREIGN KEY(Training_id) REFERENCES TRAINING(ID),
    FOREIGN KEY(Exercise_name) REFERENCES EXERCISE(Name),
    PRIMARY KEY(ID)
    )'''
)

c.execute('''CREATE TABLE MUSCLE_GROUP(
    Name VARCHAR(32),
    Image TEXT,
    PRIMARY KEY(Name)
    )'''
)

c.execute('''CREATE TABLE MUSCLES_WORKED(
    Muscle_name VARCHAR(32),
    Exercise_name VARCHAR(128),
    Primari BIT,
    PRIMARY KEY(Muscle_name, Exercise_name),
    FOREIGN KEY(Muscle_name) REFERENCES MUSCLE_GROUP(Name),
    FOREIGN KEY(Exercise_name) REFERENCES EXERCISE(Name)
    )'''
)

c.execute('''CREATE TABLE EXERCISE_HISTORY(
    ID INTEGER AUTO_INCREMENT,
    Date_Time DATETIME NOT NULL,
    Exercise_name VARCHAR(128) NOT NULL,
    User_email VARCHAR(255) NOT NULL,
    Average_intensity FLOAT(3,1),
    Set_amount INTEGER,
	Shared INTEGER NOT NULL,
    PRIMARY KEY(ID),
    FOREIGN KEY(Exercise_name) REFERENCES EXERCISE(Name),
    FOREIGN KEY(User_email) REFERENCES USER(Email)
    )'''
)

c.execute('''CREATE TABLE SETS(
	Exercise_history_id INTEGER,
	Set_number INTEGER,
	Repetitions INTEGER,
	Weight INTEGER,
	Intensity FLOAT(3,1),
	Resting_Time TIME,
    Intensity_deviation FLOAT,
	PRIMARY KEY (Exercise_history_id, Set_number),
	FOREIGN KEY (Exercise_history_id) REFERENCES EXERCISE_HISTORY(ID)
	)'''
)

c.execute('''CREATE TABLE PLAN_HISTORY(
    ID INTEGER AUTO_INCREMENT,
    Start_date DATE NOT NULL,
    End_Date DATE NOT NULL,
    Met_objective BIT NOT NULL,
    Plan_id INTEGER NOT NULL,
    User_email VARCHAR(255) NOT NULL,
    PRIMARY KEY(ID),
    FOREIGN KEY(Plan_id) REFERENCES PLAN(ID),
    FOREIGN KEY(User_email) REFERENCES USER(Email)
    )'''
)

c.execute('''CREATE TABLE USER_WEIGHT_HISTORY(
    User_email Varchar(255),
    Weight FLOAT(4,1),
    `Date` date,
    PRIMARY KEY(ID),
    FOREIGN KEY(Plan_id) REFERENCES PLAN(ID),
    FOREIGN KEY(User_email) REFERENCES USER(Email)
    )'''
)

c.execute('''CREATE TABLE FOLLOWERS(
	User_email VARCHAR(255) NOT NULL,
	Following VARCHAR(255) NOT NULL,
	PRIMARY KEY(User_email, Following),
	FOREIGN KEY(User_email) REFERENCES USER(Email),
	FOREIGN KEY(Following) REFERENCES USER(Email)
	)'''
)

c.execute('''CREATE TABLE BUMPS(
	User_email VARCHAR(255) NOT NULL,
	Exercise Integer NOT NULL,
	PRIMARY KEY(User_email, Exercise),
	FOREIGN KEY(User_email) REFERENCES USER(Email),
	FOREIGN KEY(Exercise) REFERENCES EXERCISE_HISTORY(ID)
	)'''
)

c.execute('''CREATE TABLE COMMENTS(
	ID Integer AUTO_INCREMENT,
	User_email VARCHAR(255) NOT NULL,
	Exercise Integer NOT NULL,
	Comment VARCHAR(255) NOT NULL,
	PRIMARY KEY(ID),
	FOREIGN KEY(User_email) REFERENCES USER(Email),
	FOREIGN KEY(Exercise) REFERENCES EXERCISE_HISTORY(ID)
	)'''
)

c.execute('''CREATE FUNCTION EXERCISE_PER_MUSCLE_ON_DATE_BLOCK(User VARCHAR(255),Muscle VARCHAR(32),T0 INT,T1 INT) RETURNS TEXT
	BEGIN
		DECLARE return_val TEXT;
		SET return_val = '';
		SELECT GROUP_CONCAT(Exercise_Name SEPARATOR '|') INTO return_val FROM (
		SELECT DISTINCT EXERCISE_HISTORY.Exercise_Name
		FROM EXERCISE_HISTORY
		JOIN MUSCLES_WORKED
		ON EXERCISE_HISTORY.Exercise_name = MUSCLES_WORKED.Exercise_name
		WHERE MUSCLES_WORKED.Muscle_Name = Muscle AND EXERCISE_HISTORY.User_email = User AND Date_Time BETWEEN DATE_SUB(NOW(), INTERVAL T0 DAY) AND DATE_SUB(NOW(), INTERVAL T1 DAY))AS f;
		RETURN return_val;
	END''')

c.execute('''CREATE FUNCTION AVG_WEIGHT_DATE_BLOCK(User VARCHAR(255), Exercise VARCHAR(255), T0 INT, T1 INT) RETURNS FLOAT
	BEGIN
		DECLARE return_val FLOAT;
		SET return_val = 0.0;
		SELECT AVG(Weight) into return_val
		FROM SETS
		JOIN (SELECT ID FROM EXERCISE_HISTORY WHERE User_email = User AND Exercise_name = Exercise AND Date_Time BETWEEN DATE_SUB(NOW(), INTERVAL T0 DAY) AND DATE_SUB(NOW(), INTERVAL T1 DAY)) as eh
		ON SETS.Exercise_history_id = eh.ID;
		RETURN return_val;
	END;''')
		
print("Database successfully initialized")
