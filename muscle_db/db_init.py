import MySQLdb


confirmation = ""
while( confirmation not in ('y','Y','n','N') ):
    confirmation = input("WARNING: If the table already exists it will be overwritten, do you wish to proceed? (y/n)\n")

if confirmation in ('n','N'):
    print("Return without change to database")
    exit()

conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
c = conn.cursor()
c.execute('DROP DATABASE IF EXISTS muscle')
c.execute('CREATE DATABASE muscle')
c.execute('USE muscle')

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
    Weight FLOAT(3,1) NOT NULL,
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
    Plan_id INTEGER,
    Exercise_name VARCHAR(128),
    Sets TINYINT UNSIGNED NOT NULL,
    Repetitions TINYINT UNSIGNED NOT NULL,
    Weight TINYINT UNSIGNED,
    FOREIGN KEY(Plan_id) REFERENCES PLAN(ID),
    FOREIGN KEY(Exercise_name) REFERENCES EXERCISE(Name),
    PRIMARY KEY(Plan_id, Exercise_name)
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
    Repetitions TINYINT UNSIGNED NOT NULL,
    Sets TINYINT UNSIGNED NOT NULL,
    Weight FLOAT(3,1) NOT NULL,
    Intensity INTEGER,
    PRIMARY KEY(ID),
    FOREIGN KEY(Exercise_name) REFERENCES EXERCISE(Name),
    FOREIGN KEY(User_email) REFERENCES USER(Email)
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

print("Database successfully initialized")
