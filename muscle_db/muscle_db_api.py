from flask import Flask, jsonify, request, make_response
import ssl
from passlib.hash import pbkdf2_sha256 
import MySQLdb
from datetime import datetime
import sys
import base64 
import json
import os.path
from flask_jsondash.charts_builder import charts
import c3pyo as c3
from itertools import accumulate
import requests
import base64


app = Flask(__name__)
# Config examples.
weekday = {"Sunday":7,"Monday":1,"Tuesday":5,"Wednesday":8,"Thursday":2,"Friday":6,"Saturday":9}

app.register_blueprint(charts)
app.config['SECRET_KEY'] = 'SECURE'

def _get_username():
    return 'anonymous'

# Config examples.
charts_config = dict(
    metadata=dict(
        created_by=_get_username,
    ),
)
app.config['JSONDASH'] = charts_config


@app.route('/hello', methods = ['GET', 'POST'])
def hello():
    print(request.form)
    if request.method == 'POST':
        return "Hello World" + request.form['Name']
    else:
        return "Hello World"


@app.route('/pietest')
def pietest():


        return json.dumps({
            "data d": 16,
            "data e": 77,
            "data b": 87,
            "data c": 41,
            "data a": 15
            })

#################
####DASHBOARD####
"""SELECT DAYNAME(Date_Time) AS dt,SUM(S.repetitions) AS Reps FROM EXE
                      184.105.139.70 - - [08/Jun/2017 05:05:53] "GET / HTTP/1.1" 404 -           │RCISE_HISTORY AS EH JOIN SETS AS S ON EH.id = S.Exercise_history_id GROUP 
                      ^Croot@phpmyadmin-512mb-lon1-01:~/muscle_db#         """                     """ │BY dt;"""

#################

@app.route('/muscle_chart')
def muscle_chart():
 
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')                  
        c.execute("""
                SELECT count(*) AS count, Muscle_name
                FROM (SELECT Muscle_Name,Exercise_name FROM MUSCLES_WORKED) AS m_w
                JOIN EXERCISE_HISTORY AS e_h
                ON m_w.Exercise_name = e_h.Exercise_name
                GROUP BY Muscle_name
                """)
        fetched = c.fetchall()
        r = { row["Muscle_name"] : row["count"]  for row in fetched }
        c.close()
        conn.close()
        return jsonify(r)

@app.route('/male_female_chart')
def male_female_chart():

    conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
    c = conn.cursor(MySQLdb.cursors.DictCursor)
    c.execute('USE muscle2')                  
    c.execute("""
            SELECT (CASE WHEN U.Gender = 0 THEN "Male" ELSE "Female" END) AS Gender, count(*) AS count
            FROM USER AS U
            GROUP BY U.Gender
            """)
    fetched = c.fetchall()
    print(fetched)
    c.close()
    conn.close()

    r = {"data": {
    "values": [
      {
        "Gender": "Male",
        "b": [ row["count"] for row in fetched if row["Gender"] == "Male"][0]
      },
      {
        "Gender": "Female",
        "b": [ row["count"] for row in fetched if row["Gender"] == "Female"][0]
      }
    ]
  },
  "mark": "bar",
  "encoding": {
    "x": {
      "field": "Gender",
      "title":" ",
      "type": "ordinal",
      "axis": {          
        "labelAngle": 0
      }
    },
    "y": {
      "field": "b",
      "title": " ", 
      "type": "quantitative"
    },
    "color": {"field": "Gender", "type": "nominal", "scale": {"domain": ["Male", "Female"], "range": ["teal","#ff0000"]}}
  }
  }
        
    return jsonify(r)

@app.route('/user_signup_history')
def user_signup_history():
    conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
    c = conn.cursor(MySQLdb.cursors.DictCursor)
    c.execute('USE muscle2')                  
    c.execute("""
            SELECT reg_date,COUNT(User_Email) AS reg_users 
            FROM (  SELECT User_email, MIN(Date) AS reg_date 
                    FROM USER_WEIGHT_HISTORY 
                    GROUP BY User_Email
                 ) AS reg_table 
            GROUP BY reg_date;
            """)
    fetched = c.fetchall()
    c.close()
    conn.close()

    users = [ row['reg_users'] for row in fetched ]
    users = list(accumulate(users))

    for i in range(len(fetched)):
        fetched[i]['reg_users'] = users[i]

    values = [ {"date":str(row['reg_date']),"users":row['reg_users']} for row in fetched ]


    r = {   "data": {
                "values": values
            },
            "mark": "line",
            "encoding": {
                "x": {
                  "field":"date", 
                  "title":" ",
                  "timeUnit":"utcyearmonthdate",
                  "type": "temporal"
                },
                "y": {
                  "field": "users",
                  "title": " ", 
                  "type": "quantitative"
                }

            }   
        }
    return jsonify(r)

@app.route('/avg_weekly_exercises_chart')
def avg_weekly_exercises_chart():
    conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
    c = conn.cursor(MySQLdb.cursors.DictCursor)
    c.execute('USE muscle2')                  
    c.execute("""
            SELECT weekday, CAST(AVG(set_count) AS UNSIGNED) AS avg_weekday_sets
            FROM(                 
                SELECT DAYNAME(Date_Time) AS weekday, 
                       DAYOFWEEK(Date_Time) AS day_num, 
                       TO_DAYS(Date_Time) AS date,
                       COUNT(S.Set_number) AS  set_count 

                FROM EXERCISE_HISTORY AS EH 
                JOIN SETS AS S 
                  ON EH.id = S.Exercise_history_id                 
                GROUP BY date
                ORDER BY day_num
                ) AS helper_table             
            
            GROUP BY weekday
            ORDER BY day_num              
            """)
    fetched = c.fetchall()

    r = {
        "data": {
            "values": [{'avg_weekday_sets': row['avg_weekday_sets'], "weekday": weekday[row['weekday']]} for row in fetched ]
        },
        "mark": "bar",
        "encoding": {
            "x": {
                "field": "weekday",
                "title":"Weekday",
                "type": "temporal",
                "timeUnit":"day",
                "axis": {          
                    "labelAngle": 0
                }
            },
            "y": {
                "field": "avg_weekday_sets",
                "title": "Number of Sets", 
                "type": "quantitative"
            }
        }
    }
    return jsonify(r)
    c.close()
    conn.close()
    

#@app.route('/add_user/<email>/<password>/<name>/<height>/<weight>/<profile_image>/<gender>/<date_of_birth>')

# FIELDS REQUIRED: email, password, name, height, weight, profile_image_name, gender, date_of_birth, profile_image
@app.route('/add_user', methods = ['POST'])
def add_user():

    try:

        email = request.form['email']
        password = request.form['password']
        name = request.form['name']
        height = request.form['height']
        weight = request.form['weight']

        if 'profile_image_name' and 'profile_image'in request.args:
            profile_image_name = request.form['profile_image_name']
            profile_image = request.form['profile_image']
        else:
            profile_image_name = ""
            profile_image = ""

        gender = request.form['gender']
        date_of_birth = datetime.strptime(request.form['date_of_birth'], "%d-%m-%Y").date()

        print(email)
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()

        c.execute('USE muscle2')
        
        c.execute("SELECT * FROM USER WHERE Email = %s", [email])

        password = pbkdf2_sha256.encrypt(password, rounds=200000, salt_size=16)

        if c.fetchall():
            return "User already Registered"
        

        if profile_image != "":
            c.execute('''INSERT INTO USER (Email, Password, Name, Height, Weight, Date_of_birth, Profile_image, Gender) VALUES (%s, %s, %s , %s, %s, %s, %s, %s)''', (email, password, name, height, weight, date_of_birth, profile_image_name, gender))
            
            f = file("./img/profiles/" + profile_image_name, 'wb')
            f.write(profile_image)
            f.close()
        else:
            c.execute('''INSERT INTO USER (Email, Password, Name, Height, Weight, Date_of_birth, Gender) VALUES (%s, %s, %s , %s, %s, %s, %s)''', (email, password, name, height, weight, date_of_birth, gender))

        u_id = c.lastrowid
        conn.commit()

        c.close()
        conn.close()

        return "User added"
    except Exception as e:
        return  str(e)


#FIELDS REQUIRED: email
@app.route('/rm_user', methods = ['POST'])
def rm_user():
    try:
        email = request.form['email']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()

        c.execute('USE muscle2')
    
        c.execute("SELECT * FROM USER WHERE Email = %s", [email])

        user = c.fetchall()

        if not user:
            return 'User not present'

        c.execute("DELETE FROM USER WHERE Email = %s", [email])
        conn.commit()
        c.close()
        conn.close()

        return "User Removed"
    except Exception as e:
        return str(e)

#FIELDS REQUIRED: rm comment
@app.route('/rm_comment', methods = ['POST'])
def rm_comment():
    try:
        comment = request.form['comment']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()

        c.execute('USE muscle2')
    
        c.execute("DELETE FROM COMMENTS WHERE ID = %s", [comment])

        conn.commit()
        c.close()
        conn.close()

        return "Comment Removed"
    except Exception as e:
        return str(e)


#FIELDS REQUIRED: objective
@app.route('/add_plan', methods = ['POST'])
def add_plan():
    try:
        objective = request.form['objective']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle2')
        c.execute("INSERT INTO PLAN (Objective) VALUES (%s)", [objective])
        conn.commit()
        id = c.lastrowid
        c.close()
        conn.close()
        return 'Plan ' + str(id) + ' added'
    except Exception as e:
        return str(e)

#FIELDS REQUIRED: id
@app.route('/rm_plan', methods = ['POST'])
def rm_plan():
    try:
        id = request.form['id']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle2')

        c.execute("SELECT * FROM PLAN WHERE ID = %s", [id])

        if not c.fetchall:
            return "Plan " + id + " not present in Database"
        c.execute("DELETE FROM PLAN WHERE ID = %s", [id])
        conn.commit()

        c.close()
        conn.close()
        return 'Plan ' + id + ' removed'
    except Exception as e:
        return str(e)


#FIELDS REQUIRED: name, plan_id
@app.route('/add_training', methods = ['POST'])
def add_training():
    try:
        name = request.form['name']
        plan_id = request.form['plan_id']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle2')
        c.execute("INSERT INTO TRAINING (Name, Plan_id) VALUES (%s, %s)", [name, plan_id])
        conn.commit()
        id = c.lastrowid
        c.close()
        conn.close()
        return 'Training ' + str(id) + ' added'
    except Exception as e:
        return str(e)

#FIELDS REQUIRED: id
@app.route('/rm_training', methods = ['POST'])
def rm_training():
    try:
        id = request.form['id']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle2')

        c.execute("SELECT * FROM PLAN WHERE ID = %s", [id])
        if not c.fetchall():
            return "Training " + id + " not present in the Database"

        c.execute("DELETE FROM TRAINING WHERE ID = %s", [id])
        conn.commit()
        c.close()
        conn.close()
        return 'Training ' + id + ' removed'
    except Exception as e:
        return str(e)

#FIELDS REQUIRED: name, kind, description, image 
@app.route('/add_exercise', methods = ['POST'])
def add_exercise():
    try:
        name = request.form['name']
        kind = request.form['kind']
        description = request.form['description']
        image = request.form['image']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle2')
        c.execute("INSERT INTO EXERCISE (Name, Kind, Description, Image) VALUES (%s, %s, %s, %s)", [name, kind, description, image])
        conn.commit()
        c.close()
        conn.close()
        return 'Exercise ' + name + ' added'
    except Exception as e:
        return str(e)


#FIELDS REQUIRED: name
@app.route('/rm_exercise', methods = ['POST'])
def rm_exercise():
    try:
        name = request.form['name']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle2')

        c.execute("SELECT * FROM EXERCISE WHERE Name = %s", [name])
        if not c.fetchall():
            return "Exercise " + id + " not present in the Database"

        c.execute("DELETE FROM EXERCISE WHERE Name = %s", [name])
        conn.commit()
        c.close()
        conn.close()
        return 'Exercise ' + name + ' removed'
    except Exception as e:
        return str(e)

#FIELDS REQUIRED: training_id, exercise_name, sets, repetitions, weight, resting_time
@app.route('/add_training_exercise', methods = ['POST'])
def add_training_exercise():
    try:
        training_id = request.form['training_id']
        exercise_name = request.form['exercise_name']
        sets = request.form['sets']
        repetitions = request.form['repetitions']
        weight = request.form['weight']
        resting_time = request.form['resting_time']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle2')
        c.execute("INSERT INTO TRAINING_EXERCISE (Training_id, Exercise_name, Sets, Repetitions, Weight, Resting_time) VALUES (%s, %s, %s, %s, %s, %s)", [training_id, exercise_name, sets, repetitions, weight])
        conn.commit()
        c.close()
        conn.close()
        return "Relation (" + name + ", " + training_id +") added"
    except Exception as e:
        return str(e)


#FIELDS REQUIRED: training_id, exercise_name
@app.route('/rm_training_exercise', methods = ['POST'])
def rm_training_exercise(training_id, exercise_name):
    try:
        training_id = request.form['training_id']
        exercise_name = request.form['exercise_name']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle2')
        c.execute("SELECT * FROM TRAINING_EXERCISE WHERE Training_id = %s and Exercise_name = %s", [training_id, exercise_name])

        if not c.fetchall():
            return "There is no relation in the Database with such parameters"

        c.execute("DELETE FROM TRAINING_EXERCISE WHERE Training_id = %s and Exercise_name = %s", [training_id, exercise_name])
        conn.commit()
        c.close()
        conn.close()
        return "Relation (" + name + ", " + training_id +") Removed"
    except Exception as e:
        return str(e)

#FIELDS REQUIRED: user_email
@app.route('/get_training_exercises',methods = ['POST'])
def get_training_exercises():
    try:
        training = request.form['training']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')

        c.execute("""SELECT Exercise_name,Sets,Repetitions,Weight,Resting_Time
                     FROM TRAINING_EXERCISE
                     WHERE Training_id = %s;""",[training])

        fetched = c.fetchall()
        #Date time in string
        for i in range(len(fetched)):
            fetched[i]['Resting_Time'] = str(fetched[i]['Resting_Time'])
        c.close()
        conn.close()
        return jsonify(fetched)
    except Exception as e:
        return str(e)

#FIELDS REQUIRED: user_email
@app.route('/get_my_training_exercises',methods = ['POST'])
def get_my_training_exercises():
    try:
        training = request.form['training']
        user = request.form['User_email']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')

        c.execute("""SELECT Exercise_name,Sets,Repetitions,Weight,Resting_Time
                     FROM TRAINING_EXERCISE
                     WHERE Training_id = %s;""",[training])
        
        fetched = c.fetchall()

        c.execute("""CALL GET_MY_PLAN_EXERCISE_COUNT(%s)""",[user]) 

        fetched += c.fetchall()
        #Date time in string
        for i in range(len(fetched)-1):
            fetched[i]['Resting_Time'] = str(fetched[i]['Resting_Time'])

        c.close()
        conn.close()
        return jsonify(fetched)
    except Exception as e:
        return str(e)

#FIELDS REQUIRED: name, image
@app.route('/add_muscle_group', methods = ['POST'])
def add_muscle_group():
    try:
        name = request.form['name']
        image = request.image['image']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle2')
        c.execute("INSERT INTO MUSCLE_GROUP (Name, Image) VALUES (%s, %s)", [name, image])
        conn.commit()
        c.close()
        conn.close()
        return "Muscle Group " + name + " added"
    except Exception as e:
        return str(e)

#FIELDS REQUIRED: name
@app.route('/rm_muscle_group', methods = ['POST'])
def rm_muscle_group():
    try:
        name = request.form['name']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle2')
        conn.commit()
        c.close()
        conn.close()
        return "Muscle Group " + name + " deleted"
    except Exception as e:
        return str(e)


#FIELDS REQUIRED: exercise_name, muscle_name, primari
@app.route('/add_muscles_worked', methods = ['POST'])
def add_muscles_worked():
    try:
        exercise_name = request.form['exercise_name']
        muscle_name = request.form['muscle_name']
        primari = request.form['pramari']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle2')
        c.execute("INSERT INTO MUSCLES_WORKED (Exercise_name, Muscle_name, Primari) VALUES (%s, %s, %s)", [exercise_name, muscle_name, primari])
        conn.commit()
        c.close()
        conn.close()
        return "Relation (" + exercise_name + ", " + muscle_name +") added"
    except Exception as e:
        return str(e)

#FIELDS REQUIRED: exercise_name, muscle_name
@app.route('/rm_muscles_worked', methods = ['POST'])
def rm_muscles_worked():
    try:
        exercise_name = request.form['exercise_name']
        muscle_name = request.form['muscle_name']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle2')
        c.execute("DELETE FROM MUSCLES_WORKED WHERE Exercise_name = %s and Muscle_name = %s", [exercise_name, muscle_name])
        conn.commit()
        c.close()
        conn.close()
        return "Relation (" + exercise_name + " " + muscle_name +") deleted"
    except Exception as e:
        return str(e)

#FIELDS REQUIRED: date_time, user, exercise_name, set_amount, average_intensity
@app.route('/add_exercise_history', methods = ['POST'])
def add_exercise_history():
    try:
        date_time = request.form['date_time']
        user = request.form['user']
        exercise_name = request.form['exercise_name']
        set_amount = request.form['set_amount'] if request.form['set_amount'] != 'null' else None
        average_intensity = request.form['average_intensity'] if request.form['average_intensity'] != "null" else None
        plan_id = int(request.form['plan_id']) if request.form['plan_id'] != "null" else None

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle2')
        c.execute("INSERT INTO EXERCISE_HISTORY (Date_Time, User_email, Exercise_name, Set_amount, Average_intensity, Plan_id,Shared) VALUES (%s, %s, %s, %s, %s, %s,0)", [date_time, user, exercise_name, set_amount, average_intensity, plan_id])
        conn.commit()
        id = c.lastrowid
        c.close()
        conn.close()
        return str(id)
    except Exception as e:
        return str(e)

#FIELDS REQUIRED: date_time, user, exercise_name, set_amount, average_intensity
@app.route('/add_exercise_history_with_plan', methods = ['POST'])
def add_exercise_history_with_plan():
    try:
        date_time = request.form['date_time']
        user = request.form['user']
        exercise_name = request.form['exercise_name']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle2')
        c.execute("CALL SAVE_EX_WITH_PLAN(%s,%s,%s)",[user,date_time,exercise_name])

        c.execute("SELECT ID FROM EXERCISE_HISTORY WHERE User_email = %s order by Date_Time DESC LIMIT 1",[user])

        fetched = c.fetchall()
        
        conn.commit()
        c.close()
        conn.close()
        return str(fetched[0][0])
    except Exception as e:
        return str(e)

#FIELDS REQUIRED: id
@app.route('/rm_exercise_history', methods = ['POST'])
def rm_exercise_history():
    try:
        id = request.form['id']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle2')

        c.execute("DELETE FROM EXERCISE_HISTORY WHERE ID = %s", [id])
        conn.commit()
        c.close()
        conn.close()
        return 'Exercise_history ' + id + ' removed'
    except Exception as e:
        return str(e)


@app.route('/add_set', methods = ['POST'])
def add_set():
    try:
        exercise_history_id = int(request.form['exercise_history_id'])
        set_number = int(request.form['set_number'])
        repetition = int(request.form['repetitions'])
        weight = int(request.form['weight']) if request.form['weight'] != "null" else None
        intensity = float(request.form['intensity']) if request.form['intensity'] != "null" else None
        resting_time = request.form['resting_time'] if request.form['resting_time'] != "null" else None
        intensity_deviation = request.form['intensity_deviation'] if request.form['intensity_deviation'] != "null" else None

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle2')
        c.execute("INSERT INTO SETS (Exercise_history_id, Set_number, Repetitions, Weight, Intensity, Resting_Time, Intensity_deviation) VALUES (%s, %s, %s, %s, %s, %s, %s)", [exercise_history_id, set_number, repetition, weight, intensity, resting_time, intensity_deviation])
        conn.commit()
        id = c.lastrowid
        c.close()
        conn.close()
        return "Set: " + str(id)
    except Exception as e:
        return str(e)


#FIELDS REQUIRED: start_date, end_date, objective_met, plan, user
@app.route('/add_plan_history', methods = ['POST'])
def add_plan_history():
    try:
        start_date = request.form['start_date']
        end_date = request.form['end_date']
        objective_met = request.form['objective_met']
        plan = request.form['plan']
        user = request.form['user']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle2')
        c.execute("INSERT INTO PLAN_HISTORY (Start_date, End_Date, Objective_met, Plan, User) VALUES (%s, %s, %s, %s, %s)", [start_date, end_date, objective_met, plan, user])
        conn.commit()
        id = c.lastrowid
        c.close()
        conn.close()
        return "Plan History " + str(id) + " added"
    except Exception as e:
        return str(e)


#FIELDS REQUIRED: email, plan_id
@app.route('/set_user_plan', methods=['POST'])
def set_user_plan():
    try:
        email = request.form['email']
        plan_id = request.form['plan_id']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)

        c.execute('USE muscle2')
        c.execute("CALL SET_USER_PLAN(%s,%s)", [email,plan_id])

        conn.commit()
        c.close()
        conn.close()
 
        return "Plan " + str(plan_id) + " atribuited to " + str(email)
    except Exception as e:
        return str(e)
 
#FIELDS REQUIRED: email
@app.route('/user_exists', methods=['POST'])
def user_exists():
    try:
        email = request.form['email']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')
 
        c.execute("SELECT * FROM USER WHERE Email = %s", [email])
 
        fetched = c.fetchall()
 
        c.close()
        conn.close()
        if fetched:
            return "True"
 
        return "False"
    except Exception as e:
        return str(e)
 
#FIELDS REQUIRED: email, password
@app.route('/user_login', methods=['POST'])
def user_login():
    try:
        email = request.form['email']
        password = request.form['password']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')
 
 
        c.execute("SELECT Password FROM USER WHERE Email = %s", [email])
        fetched = c.fetchall();
        c.close()
        conn.close()
        if not fetched:
            print("udne")
            return "User does not exist"
        print(str(pbkdf2_sha256.verify(password, fetched[0]['Password'])))
 
        return str(pbkdf2_sha256.verify(password, fetched[0]['Password']))
 
    except Exception as e:
        return str(e)

@app.route('/get_is_shared', methods=['POST'])
def get_is_shared():
    try:
        exercise_id = request.form['exercise_id']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')
 
 
        c.execute("SELECT Shared FROM EXERCISE_HISTORY WHERE ID = %s", [exercise_id])
        fetched = c.fetchall();
        c.close()
        conn.close()
        return jsonify(fetched)
 
    except Exception as e:
        return str(e)

@app.route('/set_shared', methods=['POST'])
def set_shared():
    try:
        exercise_id = request.form['exercise_id']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')
        c.execute("UPDATE EXERCISE_HISTORY SET Shared = 1 WHERE ID = %s", [exercise_id])
        conn.commit()
        c.close()
        conn.close()
        return "Set shared: " + exercise_id
 
    except Exception as e:
        return str(e)

#FIELDS REQUIRED: email, gender, other user chars are opt
#email will later be replaced by ID
@app.route('/update_user_char', methods=['POST'])
def update_user_char():
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')

        email = request.form['email']

        # used to aid variable size sql update  
        height_substring = ""
        weight_substring = ""
        dob_substring = ""
        profile_pic_substring = ""
        name_substring = ""
        var_insert_tuple = ()

        if 'name' in request.form:
            name_substring = "Name = %s, "
            var_insert_tuple += (request.form['name'],)

        if 'height' in request.form:
            height_substring = "Height = %s, "
            var_insert_tuple += (request.form['height'],)

        if 'weight' in request.form:
            weight_substring = "Weight = %s, "
            weight = request.form['weight']
            var_insert_tuple += (weight,)

            c.execute('''INSERT INTO USER_WEIGHT_HISTORY(
                            User_email,
                            Weight,
                            Date) 
                        VALUES(
                            %s,
                            %s,
                            NOW())''',[ email,
                                        weight,
                            ])
            


        if 'date_of_birth' in request.form:
            dob_substring = "Date_of_birth = %s, "
            var_insert_tuple += (request.form['date_of_birth'],)

        if 'profile_pic' in request.form:
            profile_pic_substring = "Profile_image = %s, "

            img_dir = "user_profile_pics/"
            img_path = "{0}.jpg".format(email)
            #img_path = "user_{:0>{width}}_profile_pic".format(ID,width=11)
            var_insert_tuple += (img_path,)
            print(request.form['profile_pic'])
            #save image in user_profile_pics 
            with open(img_dir + img_path,"wb") as f:
                f.write(base64.b64decode(request.form['profile_pic']))
        
        gender = request.form['gender']
        
        c.execute("UPDATE USER SET " + 
            name_substring + 
            height_substring + 
            weight_substring + 
            dob_substring + 
            profile_pic_substring + "Gender = %s WHERE Email = %s", var_insert_tuple + (gender, email))

        conn.commit()
        c.close()
        conn.close()

        return "User " + str(email) + " characteristics updated"

    
    except Exception as e:
        raise

@app.route('/update_user_height', methods=['POST'])
def update_user_height():
    try:

        email = request.form['email']
        height = request.form['height']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')
        
        c.execute("UPDATE USER SET Height= %s WHERE Email = %s", (height, email))

        conn.commit()
        c.close()
        conn.close()

        return "User " + str(email) + " height updated"

    
    except Exception as e:
        return str(e)

@app.route('/update_user_weight', methods=['POST'])
def update_user_weight():
    try:

        email = request.form['email']
        weight = request.form['weight']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')
        
        c.execute("UPDATE USER SET Weight= %s WHERE Email = %s", (weight, email))

        c.execute('''INSERT INTO USER_WEIGHT_HISTORY(
                        User_email,
                        Weight,
                        Date) 
                    VALUES(
                        %s,
                        %s,
                        NOW())''',[ email,
                                    weight,
                        ])


        conn.commit()
        c.close()
        conn.close()

        return "User " + str(email) + " weight updated"

    
    except Exception as e:
        return str(e)

@app.route('/update_user_profile_pic', methods=['POST'])
def update_user_profile_pic():
    try:

        email = request.form['email']
        profile_pic = request.form['profile_pic']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')

        img_dir = "user_profile_pics/"
        img_path = "{0}.jpg".format(email)

        with open(img_dir + img_path,"wb") as f:
            f.write(base64.b64decode(request.form['profile_pic']))
        
        c.execute("UPDATE USER SET Profile_image= %s WHERE Email = %s", (img_path, email))

        conn.commit()
        c.close()
        conn.close()

        return "User " + str(email) + " profile_pic updated"

    
    except Exception as e:
        return str(e)


@app.route('/get_all_exercises', methods=['POST'])
def get_all_exercises():
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')


        c.execute("SELECT * FROM EXERCISE")
        fetched = c.fetchall()
        c.close()
        conn.close()
        
        return jsonify(fetched);
    except Exception as e:
        return jsonify(e)


@app.route('/get_user_plan', methods=['POST'])
def get_user_plan():
    try:
        email = request.form['email']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')

        c.execute("SELECT Plan FROM USER WHERE Email = %s", [email])
        fetched = c.fetchall()
        c.close()
        conn.close()
        return jsonify(fetched[0]);

    except Exception as e:
        return str(e)

#FIELDS REQUIRED: email
@app.route('/get_user_plan_trainings',methods = ['POST'])
def get_user_plan_trainings():
    try:
        email = request.form['email']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')

        c.execute("""SELECT ID,Name
                     FROM (SELECT * FROM TRAINING) AS T
                           JOIN (SELECT Plan,Email FROM USER) AS U
                           ON T.Plan_id = U.Plan AND U.Email = %s;""",[email])
        
        fetched = c.fetchall()
        tmp = c.fetchall()
        c.close()
        conn.close()
        return jsonify(fetched)
    except Exception as e:
        return str(e)

#FIELDS REQUIRED: plan_id
@app.route('/get_plan_trainings',methods = ['POST'])
def get_plan_trainings():
    try:
        plan = request.form['plan_id']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')

        c.execute("""SELECT ID,Name
                     FROM TRAINING
             WHERE Plan_id  = %s;""",[plan])
        
        fetched = c.fetchall()
        tmp = c.fetchall()
        c.close()
        conn.close()
        return jsonify(fetched)
    except Exception as e:
        return str(e)


@app.route('/get_plans',methods = ['POST'])
def get_plans():
    try:
        
        plan_name = "%" + request.form['plan_name'] + "%"
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')

        c.execute("""SELECT * FROM PLAN where Name LIKE %s""", [plan_name])
        fetched = c.fetchall()
        tmp = c.fetchall()
        c.close()
        conn.close()
        return jsonify(fetched)
    except Exception as e:
        return str(e)


@app.route('/get_exercise', methods=['POST'])
def get_exercise():
    try:
        exercise_name = request.form['exercise_name']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')
        c.execute("SELECT * from EXERCISE WHERE Name = %s", [exercise_name])

        fetched = c.fetchall()
        c.close()
        conn.close()

        return jsonify(fetched[0])
    except Exception as e:
        return str(e)

@app.route('/get_exercises_by_muscle_group', methods=['POST'])
def get_exercises_by_muscle_group():
    try:
        muscle_group = request.form['muscle_group']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')


        c.execute("SELECT Exercise_name FROM MUSCLES_WORKED WHERE Muscle_name = %s", [muscle_group])
        exercise_names = c.fetchall()
        fetched = c.fetchall()
        c.close()
        conn.close()

        all_exercises = []

        return jsonify(exercise_names);

        #return str(exercise_names);

    except Exception as e:
        return str(e)


@app.route('/get_exercise_history_of_user', methods=['POST'])
def get_exercise_history_of_user():
    try:

        email = request.form['email']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')


        c.execute("SELECT * FROM EXERCISE_HISTORY WHERE User_email = %s ORDER BY Date_time DESC LIMIT 100", [email])
        fetched = c.fetchall()
        c.close()
        conn.close()
        return jsonify(fetched)
    except Exception as e:
        return str(e)

@app.route('/get_distinct_exercise_history_of_user', methods=['POST'])
def get_distinct_exercise_history_of_user():
    try:

        email = request.form['email']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')


        c.execute("SELECT DISTINCT * FROM EXERCISE_HISTORY WHERE User_email = %s  ORDER BY Date_time DESC LIMIT 100 GROUP BY Exercise_name ", [email])
        fetched = c.fetchall()
        c.close()
        conn.close()
        return jsonify(fetched)
    except Exception as e:
        return str(e)


@app.route('/get_last_exercise_of_user', methods=['POST'])
def get_last_exercise_of_user():

    try:
        user_email = request.form['user_email']
        exercise_name = request.form['exercise_name']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')

                  
        c.execute("SELECT * FROM EXERCISE_HISTORY WHERE User_email = '%s' and Exercise_name = '%s' ORDER BY Date_time DESC", [user_email, exercise_name])
        fetched = c.fetchall()
        c.close()
        conn.close()
        return jsonify(fetched[0])
    except Exception as e:
        return str(e)




@app.route('/get_exercise_types_stats_of_user', methods=['POST'])
def get_exercise_types_stats_of_user():
    try:
        user_email = request.form['User_email']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')                  
        c.execute("SELECT count(ID) AS count, Kind FROM EXERCISE_HISTORY as eh join EXERCISE as e on e.Name=eh.Exercise_name WHERE eh.User_email = %s GROUP BY Kind;", [user_email])
        fetched = c.fetchall()
        c.close()
        conn.close()
        return jsonify(fetched)
    except Exception as e:
        return str(e)

@app.route('/get_exercise_muscle_stats_of_user', methods=['POST'])
def get_exercise_muscle_stats_of_user():
    try:
        user_email = request.form['User_email']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')                  
        c.execute("""
                SELECT count(*) AS count, Muscle_name
                FROM (SELECT Muscle_Name, Exercise_name FROM MUSCLES_WORKED) AS m_w
                JOIN (SELECT Exercise_name, User_email FROM EXERCISE_HISTORY) AS e_h
                ON m_w.Exercise_name = e_h.Exercise_name AND e_h.User_email = %s
                GROUP BY Muscle_name
                """, [user_email])
        fetched = c.fetchall()
        c.close()
        conn.close()
        return jsonify(fetched)

    except Exception as e:
        return str(e)

@app.route('/get_muscle_groups', methods=['POST'])
def get_muscle_groups():

    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')
        c.execute("SELECT * FROM MUSCLE_GROUP")
        fetched = c.fetchall()
        c.close()
        conn.close()
        return jsonify(fetched)
    except Exception as e:
        return str(e)

@app.route('/get_weight_history', methods=['POST'])
def get_weight_history():
    try:
        user_email = request.form['user_email']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')
        c.execute("SELECT Weight, Date FROM USER_WEIGHT_HISTORY WHERE User_email = %s", [user_email])
        fetched = c.fetchall()
        c.close()
        conn.close()
        return jsonify(fetched)
    except Exception as e:
        return str(e)

@app.route('/get_exercise_weight_history', methods=['POST'])
def get_exercise_weight_history():
    try:
        email = request.form['email']
        exercise_name = request.form['exercise_name']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')
        c.execute("""SELECT Weight, Date_Time 
             	     FROM (SELECT ID, Date_Time
               		   FROM EXERCISE_HISTORY
               		   WHERE User_email = %s
                 	     AND Exercise_name = %s
               		   ORDER BY Date_Time) AS EH
             	     JOIN (SELECT Exercise_history_id, Weight FROM SETS) AS S
                     ON EH.ID = S.Exercise_history_id
                     GROUP BY(Date_Time)""", [email,exercise_name])
        fetched = c.fetchall()

        #Date time in string
        for i in range(len(fetched)):
            fetched[i]['Date_Time'] = str(fetched[i]['Date_Time'])
            fetched[i]['Weight'] = str(fetched[i]['Weight'])        
        c.close()
        conn.close()
        return jsonify(fetched)
    except Exception as e:
        return str(e)

@app.route('/get_local_db', methods=['POST'])
def get_local_db():
    try:
        email = request.form['email']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')


        # get user data
        c.execute("SELECT * FROM USER WHERE Email = %s", [email])
        fetched = c.fetchall()
        user_data = {"USER" : [{k:fetched[0][k] for k in fetched[0] if k !='Password'}]}


        # get plan data
        c.execute('''SELECT * FROM PLAN ''')
        fetched = c.fetchall()
        plan_data = {"PLAN" : fetched}

                
        # get training data
        c.execute('''SELECT * FROM  TRAINING''')
        fetched = c.fetchall()
        training_data = {"TRAINING" : fetched}


        # get exercise data
        c.execute('''SELECT * FROM EXERCISE ''')
        fetched = c.fetchall()
        exercise_data = {"EXERCISE" : fetched}

        #print(exercise_data)


        # get training_exercise data
        c.execute('''SELECT * FROM TRAINING_EXERCISE ''')
        fetched = c.fetchall()
        training_exercise_data = {"TRAINING_EXERCISE" : fetched}


        # get muscle_group data
        c.execute('''SELECT * FROM MUSCLE_GROUP ''')
        fetched = c.fetchall()
        muscle_group_data = {"MUSCLE_GROUP" : fetched}


        # get muscle_group data
        c.execute('''SELECT * FROM MUSCLES_WORKED ''')
        fetched = c.fetchall()
        muscles_worked_data = {"MUSCLES_WORKED" : fetched}

        # temporary table created to assure sets are sent according to chosen tuples of exercise_history
        c.execute('''CREATE TEMPORARY TABLE IF NOT EXISTS EXERCISE_HISTORY_CACHE 
                     AS (   SELECT * 
                            FROM EXERCISE_HISTORY AS EH
                            WHERE User_email = %s
                            ORDER BY EH.Date_Time DESC
                            LIMIT 100    )''',[email])

        
        # get exercise_history data (of user)
        c.execute('''SELECT * FROM EXERCISE_HISTORY_CACHE''')
        fetched = c.fetchall()
        #print(fetched)
        
        exercise_history_data = {"EXERCISE_HISTORY" : fetched}

        # get sets data (of selected exercise history)
        c.execute('''SELECT  S.Exercise_history_id, S.Set_number, S.repetitions, S.Weight,S.Intensity, S.Resting_Time, S.Intensity_deviation
                     FROM SETS AS S
                     JOIN EXERCISE_HISTORY_CACHE AS EH
                       ON S.Exercise_history_id = EH.ID''')
        fetched = c.fetchall()
        sets_data = {"SETS" : fetched}

        c.execute('''DROP TEMPORARY TABLE IF EXISTS EXERCISE_HISTORY_CACHE''')


        # get plan_history data (of user)
        c.execute('''SELECT * FROM PLAN_HISTORY WHERE User_email = %s''',[email])
        fetched = c.fetchall()
        plan_history_data = {"PLAN_HISTORY" : fetched}


        # get user_weight_history data (of user)
        c.execute('''SELECT * FROM USER_WEIGHT_HISTORY WHERE User_email = %s''',[email])
        fetched = c.fetchall()
        user_weight_history_data = {"USER_WEIGHT_HISTORY" : fetched}

        c.close()
        conn.close()
        #return jsonify(,default = str)
        dump = json.dumps([exercise_data, plan_data, user_data, 
                           training_data, training_exercise_data,muscle_group_data,
                           muscles_worked_data, exercise_history_data, plan_history_data,
                           sets_data,user_weight_history_data], indent = 4, default = str)

        r = make_response(dump)
        r.mimetype = 'application/json'
        return r

    except Exception as e:
        raise

@app.route('/get_user_profile_pic', methods=['POST'])
def get_user_profile_pic():
    try:
 
        email = request.form['email']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')
 
 
        c.execute("SELECT Profile_image FROM USER WHERE Email = %s", [email])
 
        fetched = c.fetchone()['Profile_image']
        print(fetched)
 
        if(not fetched):
            return "User profile image not found"
       
        img_dir = "user_profile_pics/"
        img_path = fetched
        #img_path = "user_{:0>{width}}_profile_pic".format(ID,width=11)
 
 
        #get image in user_profile_pics
        with open(img_dir + img_path, "rb") as image_file:
            string_img = base64.b64encode(image_file.read()).decode()

        res = make_response(json.dumps({"Profile" : string_img}))       
        res.mimetype = 'application/json'
        
        c.close()
        conn.close()
 
        return res       
 
    except Exception as e:
        return str(e)

@app.route('/get_user_profile', methods=['POST'])
def get_user_profile():
    try:
 
        email = request.form['email']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')

        c.execute("""SELECT Email, Name, Height,Weight, Profile_image, Gender,Date_of_birth, TIMESTAMPDIFF(YEAR,Date_of_birth,CURDATE()) AS Age FROM USER WHERE Email = %s""", [email])

        fetched = c.fetchone()
        
        img_dir = "user_profile_pics/"
        img_path = fetched['Profile_image']
        
        string_img = None

        if img_path != None and os.path.isfile(img_dir + img_path):
            with open(img_dir + img_path, "rb") as image_file:
                string_img = base64.b64encode(image_file.read()).decode()
        

        fetched['Profile_image'] = string_img

        c.close()
        conn.close()
        
        return jsonify(fetched)

 
    except Exception as e:
        return str(e)



@app.route('/get_users_like', methods=['POST'])
def get_users_like():
    try:
        user_email = request.form['email']
        
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""
            SELECT Email
            FROM USER
            WHERE Email LIKE %s
            LIMIT 20
            """, ['%'+user_email+'%'])

        r = c.fetchall()
        c.close()
        conn.close()
        return jsonify(r)

    except Exception as e:
        return str(e)

#####################
##  HISTORY STATS  ##
#####################

@app.route('/get_this_ex_avg_stats', methods=['POST'])
def get_this_ex_avg_stats():
    try:
        exercise_id = request.form['id']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""
            SELECT AVG(Set_amount) AS avg_sets,AVG(Repetitions) AS avg_reps, AVG(Weight) AS avg_weight, AVG(Intensity) AS avg_int, SEC_TO_TIME(AVG(TIME_TO_SEC(Resting_Time))) AS avg_rest
            FROM (SELECT ID,Exercise_name, Set_amount
                  FROM EXERCISE_HISTORY
                  WHERE ID = %s) AS EH
            JOIN (SELECT * FROM SETS) AS S
            ON S.Exercise_history_id = EH.ID
            GROUP BY Exercise_name;
            """, [exercise_id])

        fetched = c.fetchall()
        #Parse value to  string
        print(fetched)
        for i in range(len(fetched)):
            fetched[i]['avg_sets'] = str(fetched[i]['avg_sets'])
            fetched[i]['avg_rest'] = str(fetched[i]['avg_rest'])
            fetched[i]['avg_weight'] = str(fetched[i]['avg_weight'])
            fetched[i]['avg_reps'] = str(fetched[i]['avg_reps'])
        c.close()
        conn.close()
        return jsonify(fetched)

    except Exception as e:
        return str(e)

@app.route('/get_user_avg_stats_ex', methods=['POST'])
def get_user_avg_stats_ex():
    try:
        user_email = request.form['user_email']
        exercise = request.form['exercise']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""
            SELECT AVG(Set_amount) AS avg_sets,AVG(Repetitions) AS avg_reps, AVG(Weight) AS avg_weight, AVG(Intensity) AS avg_int, SEC_TO_TIME(AVG(TIME_TO_SEC(Resting_Time))) AS avg_rest
            FROM (SELECT ID,Exercise_name, Set_amount
                  FROM EXERCISE_HISTORY
                  WHERE User_email = %s AND Exercise_name = %s) AS EH
            JOIN (SELECT * FROM SETS) AS S
            ON S.Exercise_history_id = EH.ID
            GROUP BY Exercise_name;
            """, [user_email, exercise])

        fetched = c.fetchall()
        #Parse value to  string
        print(fetched)
        for i in range(len(fetched)):
            fetched[i]['avg_sets'] = str(fetched[i]['avg_sets'])
            fetched[i]['avg_rest'] = str(fetched[i]['avg_rest'])
            fetched[i]['avg_weight'] = str(fetched[i]['avg_weight'])
            fetched[i]['avg_reps'] = str(fetched[i]['avg_reps'])
        c.close()
        conn.close()
        return jsonify(fetched)

    except Exception as e:
        return str(e)

@app.route('/get_avg_stats_ex', methods=['POST'])
def get_avg_stats_ex():
    try:
        exercise = request.form['exercise']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""
            SELECT AVG(Set_amount) AS avg_sets,AVG(Repetitions) AS avg_reps, AVG(Weight) AS avg_weight, AVG(Intensity) AS avg_int, SEC_TO_TIME(AVG(TIME_TO_SEC(Resting_Time))) AS avg_rest
            FROM (SELECT ID,Exercise_name, Set_amount
                  FROM EXERCISE_HISTORY
                  WHERE Exercise_name = %s) AS EH
            JOIN (SELECT * FROM SETS) AS S
            ON S.Exercise_history_id = EH.ID
            GROUP BY Exercise_name;
            """, [exercise])

        fetched = c.fetchall()
        #Parse value to  string
        print(fetched)
        for i in range(len(fetched)):
            fetched[i]['avg_sets'] = str(fetched[i]['avg_sets'])
            fetched[i]['avg_rest'] = str(fetched[i]['avg_rest'])
            fetched[i]['avg_weight'] = str(fetched[i]['avg_weight'])
            fetched[i]['avg_reps'] = str(fetched[i]['avg_reps'])
        c.close()
        conn.close()
        return jsonify(fetched)

    except Exception as e:
        return str(e)


@app.route('/does_user_bump', methods=['POST'])
def does_user_bump():
    try:
        user_email = request.form['email']
        exercise_id = int(request.form['exercise_id'])
        
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""
            SELECT *
            FROM BUMPS
            WHERE User_email = %s
            and Exercise = %s
            """, [user_email, exercise_id])

        r = c.fetchall()        
        c.close()       
        conn.close()            

        if(len(r) == 0):
            return "false"
        else:
            return "true"
            
        

    except Exception as e:
        return str(e)


@app.route('/delete_user_bump', methods=['POST'])
def delete_user_bump():
    try:
        user_email = request.form['email']
        exercise_id = request.form['exercise_id']
        
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""
            DELETE
            FROM BUMPS
            WHERE (User_email = %s
            and Exercise = %s)
            """, [user_email, exercise_id])

        conn.commit()
        c.close()       
        conn.close()

        return "Sou obrigado a returnar uma resposta senao app crasha"
        
    except Exception as e:
        return str(e)


@app.route('/add_user_bump', methods=['POST'])
def add_user_bump():
    try:
        user_email = request.form['email']
        exercise_id = int(request.form['exercise_id'])
        
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""
            INSERT INTO BUMPS VALUES (%s, %s)
            """, [user_email, exercise_id])

        conn.commit()
        c.close()       
        conn.close()
        
        return "Sou obrigado a returnar uma resposta sena a app crasha"         

    except Exception as e:
        return str(e)



#####################
##   SOCIAL FEED   ##
#####################

@app.route('/get_user_feed', methods=['POST'])
def get_user_feed():
    try:
        user_email = request.form['user_email']
        amount = int(request.form['amount'])

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""
            SELECT *
            FROM EXERCISE_HISTORY
            JOIN (SELECT Following FROM FOLLOWERS WHERE User_email = %s) AS f
            ON EXERCISE_HISTORY.User_email = f.Following AND EXERCISE_HISTORY.Shared = 1
            ORDER BY Date_Time
            LIMIT %s
            """, [user_email, amount])

        r = c.fetchall()
        c.close()
        conn.close()
        return jsonify(r)

    except Exception as e:
        return str(e)

@app.route('/get_user_feed_and_pictures', methods=['POST'])
def get_user_feed_and_pictures():
    try:
        user_email = request.form['user_email']
        amount = int(request.form['amount'])

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""
            SELECT *
            FROM EXERCISE_HISTORY
            JOIN (SELECT Following FROM FOLLOWERS WHERE User_email = %s) AS f
              ON EXERCISE_HISTORY.User_email = f.Following AND EXERCISE_HISTORY.Shared = 1
            JOIN (SELECT Profile_image, Email FROM USER) AS p
              ON f.Following = p.Email
            ORDER BY Date_Time DESC
            LIMIT %s
            """, [user_email, amount])

        r = c.fetchall()

        #get image in user_profile_pics
        profile_dict = {row['Email']:row['Profile_image'] for row in r}
        
        img_dir = "user_profile_pics/"
        
        for email, img_path in profile_dict.items():
            if img_path != None and os.path.isfile(img_dir + img_path):
                with open(img_dir + img_path, "rb") as image_file:
                    profile_dict[email] = base64.b64encode(image_file.read()).decode()
            else:
                profile_dict[email] = None

        c.close()
        conn.close()
        return jsonify(feed = r, pictures = profile_dict)

    except Exception as e:
        return str(e)

    
@app.route('/get_comments_exercise', methods=['POST'])
def get_comments_exercise():
    try:
        exercise_id = request.form['exercise_id']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""
            SELECT COMMENTS.ID, User_email, Comment, Name, Profile_image
            FROM COMMENTS JOIN USER on COMMENTS.User_email = USER.Email
            WHERE Exercise = %s ORDER BY ID DESC
            """, [exercise_id])
        r = c.fetchall()


        img_dir = "user_profile_pics/"

        profile_dict = {row['User_email']:row['Profile_image'] for row in r}

        for email, img_path in profile_dict.items():
            if img_path != None and os.path.isfile(img_dir + img_path):
                with open(img_dir + img_path, "rb") as image_file:
                    profile_dict[email] = base64.b64encode(image_file.read()).decode()
            else:
                profile_dict[email] = None

        c.close()
        conn.close()
        return jsonify(comments = r, pictures = profile_dict)

    except Exception as e:
        return str(e)


@app.route('/add_comment_to_exercise', methods=['POST'])
def add_comment_to_exercise():
    try:
        email = request.form['email']
        exercise_id = int(request.form['exercise_id'])
        comment = request.form['comment']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""
            INSERT INTO COMMENTS (User_email, Exercise, Comment) VALUES (%s, %s, %s)
            """, [email, exercise_id, comment])

        r = c.fetchall()
        c.close()
        conn.commit()
        conn.close()
        return jsonify(r)

    except Exception as e:
        return str(e)


@app.route('/get_bumps_exercise', methods=['POST'])
def get_bumps_exercise():
    try:
        exercise_id = request.form['exercise_id']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""
            SELECT COUNT(*) as bumps
            FROM BUMPS
            WHERE Exercise = %s
            """, [exercise_id])

        r = c.fetchall()
        c.close()
        conn.close()
        return jsonify(r)

    except Exception as e:
        return str(e)

@app.route('/get_users_bumped_exercise', methods=['POST'])
def get_users_bumped_exercise():
    try:
        exercise_id = request.form['exercise_id']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""
            SELECT User_email
            FROM BUMPS
            WHERE Exercise = %s
            """, [exercise_id])

        r = c.fetchall()
        c.close()
        conn.close()
        return jsonify(r)

    except Exception as e:
        return str(e)

@app.route('/get_user_following_amount', methods=['POST'])
def get_user_following_amount():
    try:
        email = request.form['user_email']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""
            SELECT COUNT(*) as Amount
            FROM FOLLOWERS
            WHERE User_email = %s
            """, [email])

        r = c.fetchall()
        c.close()
        conn.close()
        return jsonify(r)

    except Exception as e:
        return str(e)

@app.route('/get_user_following', methods=['POST'])
def get_user_following():
    try:
        email = request.form['user_email']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""
            SELECT Following
            FROM FOLLOWERS
            WHERE User_email = %s
            """, [email])

        r = c.fetchall()
        c.close()
        conn.close()
        return jsonify(r)

    except Exception as e:
        return str(e)

@app.route('/get_follow_info', methods=['POST'])
def get_follow_info():
    try:
        user_email = request.form['user_email']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""SELECT Following FROM FOLLOWERS WHERE User_email = %s""", [user_email])

        fetched_following = [ email['Following'] for email in c.fetchall() ]
        
        c.execute("""SELECT User_email FROM FOLLOWERS WHERE Following = %s""", [user_email])

        fetched_followers = [ email['User_email'] for email in c.fetchall() ]

        c.close()
        conn.close()
        return jsonify(following = fetched_following,followers = fetched_followers)

    except Exception as e:
        raise


@app.route('/add_to_following', methods=['POST'])
def add_to_following():
    try:
        follower = request.form['follower_email']
        following = request.form['following_email']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute('''INSERT INTO FOLLOWERS (User_email, Following) VALUES (%s, %s)''', [follower, following])
        conn.commit()

        c.close()
        conn.close()


        return "Following " + following 

    except Exception as e:
        return str(e)

@app.route('/rm_from_following', methods=['POST'])
def rm_from_following():
    try:
        unfollower = request.form['unfollower']
        unfollowed = request.form['unfollowed']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute('''DELETE FROM FOLLOWERS WHERE User_email = %s AND Following = %s''', [unfollower, unfollowed])
        conn.commit()

        c.close()
        conn.close()


        return "Unfollowed " + unfollowed
    except Exception as e:
        return str(e)

#####################
## DATA PROCESSING ##
#####################

def get_expected_exercise_result():
    try:
        user_email = request.form['user_email']
        exercise_name = request.form['exercise_name']
        weight = request.form['weight']
        set_number = request.form['set_number']
        
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)

        c.execute('USE muscle2')
        c.execute("""
            SELECT Gender, Weight 
            FROM (SELECT * FROM USER WHERE Email = %s) AS u
            JOIN EXERCISE_HISTORY
            ON u.Email = EXERCISE_HISTORY.User_email
            ORDER BY Date_time DESC""", [user_email])

        r = c.fetchall()[0]
        gender, u_weight = r["Gender"], r["Weight"]
        weight_min = u_weight - 5
        weight_max = u_weight + 5
            
        c.execute("""
            SELECT Objective 
            FROM (SELECT * FROM USER WHERE Email = %s) AS u 
            JOIN PLAN 
            ON u.Plan = PLAN.ID""", [user_email])

        objective = c.fetchall()[0]['Objective']
        
        c.execute("""
            SELECT AVG(Intensity) AS avg_int, AVG(Intensity_deviation) AS avg_deviation, AVG(Resting_Time) as avg_rest
            FROM USER 
            JOIN (SELECT User_email, Intensity, Intensity_deviation, Resting_Time
                FROM (SELECT * FROM SETS WHERE Weight = %s AND Set_number = %s) AS s
                JOIN (SELECT * FROM EXERCISE_HISTORY WHERE Exercise_name = %s) AS e_h
                ON s.Exercise_history_id = e_h.ID) AS temp
            ON USER.Email = temp.User_email
            WHERE Gender = %s AND Weight BETWEEN %s AND %s""", [weight, set_number, exercise_name, gender, weight_min, weight_max])
        
        r = c.fetchall()
        c.close()
        conn.close()

        return jsonify(r)

    except Exception as e:
        return str(e)


@app.route('/get_recommended_exercises', methods=['POST'])
def get_recommended_exercises():
    try:
        user_email = request.form['user_email']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""
            SELECT Exercise_name, COUNT(Email) as count
            FROM ( SELECT DISTINCT u.Email, e.Exercise_name
                FROM ( SELECT Email
                    FROM USER
                    JOIN (SELECT * 
                        FROM EXERCISE_HISTORY
                        WHERE Exercise_name IN (Select Exercise_name FROM EXERCISE_HISTORY WHERE User_email = %s) ) AS e_h
                        ON USER.Email = e_h.User_email) AS u
                JOIN ( SELECT Exercise_name, User_email
                    FROM EXERCISE_HISTORY ) AS e
                ON u.Email = e.User_email ) AS temp
            GROUP BY Exercise_name
            ORDER BY count DESC
            LIMIT 3
            """, [user_email])
        r = c.fetchall()
        c.close()
        conn.close()
        return jsonify(r)

    except Exception as e:
        return str(e)

@app.route('/get_recommended_plans', methods=['POST'])
def get_recommended_plans():
    try:
        user_email = request.form['user_email']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""
            SELECT Plan_id AS ID, Name, COUNT(Email) as count
            FROM ( SELECT DISTINCT u.Email, e.Plan_id
                FROM ( SELECT Email
                    FROM USER
                    JOIN (SELECT * 
                        FROM EXERCISE_HISTORY
                        WHERE Exercise_name IN (Select Exercise_name FROM EXERCISE_HISTORY WHERE User_email = %s) ) AS e_h
                    ON USER.Email = e_h.User_email) AS u
                JOIN ( SELECT Plan_id, User_email
                    FROM PLAN_HISTORY ) AS e
                ON u.Email = e.User_email ) AS temp
        JOIN PLAN
        ON PLAN.ID = temp.Plan_id
            GROUP BY ID
            ORDER BY count DESC
            LIMIT 3""", [user_email])

        r = c.fetchall()
        c.close()
        conn.close()
        return jsonify(r)

    except Exception as e:
        return str(e)
    
    
@app.route('/get_sets_of_exercise_history', methods=['POST'])
def get_sets_of_exercise_history():
    #try:
    id = int(request.form['id'])
    conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
    c = conn.cursor(MySQLdb.cursors.DictCursor)
    
    c.execute('USE muscle2')
    c.execute("SELECT * from SETS WHERE Exercise_history_id=%s", [id])

    r = c.fetchall()
    
    for x in r:
        x['Resting_Time'] = str(x['Resting_Time'])
    c.close()
    conn.close()
    return jsonify(r) 

    #except Exception as e:
    #   return str(e)
    
    
@app.route('/get_recommended_follows', methods=['POST'])
def get_recommended_follows():
    try:
        email = request.form['email_user']
        limit = int(request.form['limit'])
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""
                SELECT FOLLOWERS.Following, count(FOLLOWERS.Following) AS count
                FROM FOLLOWERS
                JOIN (SELECT Following
                    FROM FOLLOWERS
                    WHERE User_email = %s) AS t
                ON FOLLOWERS.User_email = t.Following
                GROUP BY FOLLOWERS.Following
                ORDER BY count DESC
                LIMIT %s
                """, [email, limit])

        r = c.fetchall()
        c.close()
        conn.close()
        return jsonify(r) 

    except Exception as e:
        return str(e)



@app.route('/is_exercise_history_shared', methods=['POST'])
def is_exercise_history_shared():
    try:
        id = request.form['exercise_id']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""SELECT Shared from EXERCISE_HISTORY WHERE ID = %s""", [id])

        r = c.fetchall()
        c.close()
        conn.close()
        return jsonify(r) 

    except Exception as e:
        return str(e)

@app.route('/set_exercise_history_shared', methods=['POST'])
def set_exercise_history_shared():
    try:
        id = int(request.form['exercise_id'])
        shared = int(request.form['shared']);
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        print("UPDATE EXERCISE_HISTORY SET Shared = {} where ID = {}""".format(shared, id))
        c.execute('USE muscle2')
        c.execute("""UPDATE EXERCISE_HISTORY SET Shared = %s where ID = %s""", [shared, id])

        r = c.fetchall()
        c.close()
        conn.commit()
        conn.close()
        return jsonify(r) 

    except Exception as e:
        return str(e)

@app.route('/get_muscle_progress', methods=['POST'])
def get_muscle_progress():
    try:
        email = request.form['user_email']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)

        c.execute('USE muscle2')
        progress = { "Back":1, "Biceps":1, "Chest":1, "Legs":1, "Shoulders":1, "Triceps":1 }
        
        for muscle in progress:
            c.execute('SELECT EXERCISE_PER_MUSCLE_ON_DATE_BLOCK(%s,%s,%s,%s)', [email, muscle, 7,0])
            temp = [ x for x in list(c.fetchall()[0].values()) if x != None ]
            if temp != []:  
                exercises = { e:0 for e in temp[0].split('|') }
                inComparison = { e:0 for e in temp[0].split('|') }
                
                for exercise in exercises:
                    avg = 0
                    i = 0
                    while i < 10:
                        c.execute('SELECT AVG_WEIGHT_DATE_BLOCK(%s,%s,%s,%s)', [email, exercise, 7*(i+1), 7*i])
                        a = list(c.fetchall()[0].values())[0]
                        if a: 
                            avg = a * 0.7 + avg * 0.3 if avg != 0 else a
                        i+=1

                    exercises[exercise] = avg

                    c.execute('SELECT AVG_WEIGHT_DATE_BLOCK(%s,%s,%s,%s)', [email, exercise, 7, 0])
                    a = list(c.fetchall()[0].values())[0]
                    if a:
                        inComparison[exercise] = a
                
                l = [ inComparison[x]/exercises[x] for x in exercises if exercises[x] != 0 ]
                if l != []:
                    progress[muscle] = sum(l) / len(l)

        return jsonify(progress)

    except Exception as e:
        return str(e)

@app.route('/get_user_height', methods=['POST'])
def get_user_height():
    try:
        id = request.form['email']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""SELECT Height from USER WHERE Email = %s""", [id])

        r = c.fetchall()
        c.close()
        conn.close()
        return jsonify(r[0]) 

    except Exception as e:
        return str(e)

@app.route('/get_exercise_image_name', methods=['POST'])
def get_exercise_image_name():
    try:
        id = request.form['exercise_name']
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""SELECT Image from EXERCISE WHERE Name = %s""", [id])

        r = c.fetchall()
        c.close()
        conn.close()
        return r[0]["Image"] 

    except Exception as e:
        return str(e)

@app.route('/add_fitbit_user', methods=['GET'])
def add_fitbit_user():
    try:
        code = request.args.get('code')
        user = request.args.get('state')
        
        payload = {}
        payload['client_id'] = "228KV8"
        payload['grant_type'] = "authorization_code"
        payload['code'] = code
        payload['redirect_uri'] = 'https://138.68.158.127/add_fitbit_user'

        base_url = "https://api.fitbit.com/oauth2/token"

        #toConvert = "228KV8:16ddd260cc613d6da167794a104941f4"
        encoded_data = 'MjI4S1Y4OjE2ZGRkMjYwY2M2MTNkNmRhMTY3Nzk0YTEwNDk0MWY0'
        autho = "Basic" + " " + encoded_data +"="
        header = {"Authorization": autho}

        r = requests.post(base_url, headers = header, data = payload).json()
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle2')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        
        c.execute('USE muscle2')
        c.execute("""UPDATE USER SET Access_token = %s WHERE Email = %s""", [r['access_token'], user])
        c.close()
        conn.commit()
        conn.close()
        return "Fitbit account successfully synced"

    except Exception as e:
        print(str(e))

if __name__ == '__main__':
    context = ssl.SSLContext(ssl.PROTOCOL_TLSv1_2)
    context.load_cert_chain('muscle-selfsigned.crt', 'muscle-selfsigned.key')
    app.run(host="0.0.0.0", port = "443", ssl_context = context)
