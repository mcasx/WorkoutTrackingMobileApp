from flask import Flask, jsonify
from passlib.hash import pbkdf2_sha256
import MySQLdb
app = Flask(__name__)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port = "80")

@app.route('/hello')
def hello():
    return "Hello World"

@app.route('/add_user/<email>/<password>/<name>/<height>/<weight>/<profile_image>/<gender>/<date_of_birth>')
def add_user(email, password, name, height, weight, profile_image, gender, date_of_birth):

    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()

        c.execute('USE muscle')
        c.execute("SELECT * FROM USER WHERE Email = '{}'".format(email))

        password = pbkdf2_sha256.encrypt(password, rounds=200000, salt_size=16)

        if c.fetchall():
            return 'User already Registered'

        if profile_image != "Null":
            c.execute('''INSERT INTO USER (Email, Password, Name, Height, Weight, Date_of_birth, Profile_image, Gender) VALUES (%s, %s, %s , %s, %s, %s, %s, %s)''', (email, password, name, height, weight, date_of_birth, profile_image, gender))

        else:
            c.execute('''INSERT INTO USER (Email, Password, Name, Height, Weight, Date_of_birth, Gender) VALUES (%s, %s, %s , %s, %s, %s, %s)''', (email, password, name, height, weight, date_of_birth, gender))

        conn.commit()

        c.close()
        conn.close()
        return 'User added'
    except Exception as e:
        return str(e)


@app.route('/rm_user/<email>')
def rm_user(email):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()

        c.execute('USE muscle')

        c.execute("SELECT * FROM USER WHERE Email = '{}'".format(email))

        user = c.fetchall()

        if not user:
            return 'User not present'

        c.execute("DELETE FROM USER WHERE Email = '{}'".format(email))
        conn.commit()
        c.close()
        conn.close()

        return "User Removed"
    except Exception as e:
        return str(e)


@app.route('/add_plan/<objective>')
def add_plan(objective):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle')
        c.execute("INSERT INTO PLAN (Objective) VALUES ('{}')".format(objective))
        conn.commit()
        id = c.lastrowid
        c.close()
        conn.close()
        return 'Plan ' + str(id) + ' added'
    except Exception as e:
        return str(e)


@app.route('/rm_plan/<id>')
def rm_plan(id):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle')

        c.execute("SELECT * FROM PLAN WHERE ID = {}".format(id))

        if not c.fetchall:
            return "Plan " + id + " not present in Database"
        c.execute("DELETE FROM PLAN WHERE ID = {}".format(id))
        conn.commit()

        c.close()
        conn.close()
        return 'Plan ' + id + ' removed'
    except Exception as e:
        return str(e)

@app.route('/add_training/<name>/<plan_id>')
def add_training(name, plan_id):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle')
        c.execute("INSERT INTO TRAINING (Name, Plan_id) VALUES ('{}', {})".format(name, plan_id))
        conn.commit()
        id = c.lastrowid
        c.close()
        conn.close()
        return 'Training ' + str(id) + ' added'
    except Exception as e:
        return str(e)

@app.route('/rm_training/<id>')
def rm_training(id):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle')

        c.execute("SELECT * FROM PLAN WHERE ID = {}".format(id))
        if not c.fetchall():
            return "Training " + id + " not present in the Database"

        c.execute("DELETE FROM TRAINING WHERE ID = {}".format(id))
        conn.commit()
        c.close()
        conn.close()
        return 'Training ' + id + ' removed'
    except Exception as e:
        return str(e)

@app.route('/add_exercise/<name>/<kind>/<description>/<image>')
def add_exercise(name, kind, description, image):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle')
        c.execute("INSERT INTO EXERCISE (Name, Kind, Description, Image) VALUES ('{}', '{}', '{}', '{}')".format(name, kind, description, image))
        conn.commit()
        c.close()
        conn.close()
        return 'Exercise ' + name + ' added'
    except Exception as e:
        return str(e)

@app.route('/rm_exercise/<name>')
def rm_exercise(name):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle')

        c.execute("SELECT * FROM EXERCISE WHERE Name = '{}'".format(name))
        if not c.fetchall():
            return "Exercise " + id + " not present in the Database"

        c.execute("DELETE FROM EXERCISE WHERE Name = '{}'".format(name))
        conn.commit()
        c.close()
        conn.close()
        return 'Exercise ' + name + ' removed'
    except Exception as e:
        return str(e)

@app.route('/add_training_exercise/<training_id>/<exercise_name>/<sets>/<repetitions>/<weight>')
def add_training_exercise(training_id, exercise_name, sets, repetitions, weight):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle')
        c.execute("INSERT INTO TRAINING_EXERCISE (Training_id, Exercise_name, Sets, Repetitions, Weight) VALUES ({}, '{}', {}, {}, {})".format(training_id, exercise_name, sets, repetitions, weight))
        conn.commit()
        c.close()
        conn.close()
        return "Relation (" + name + ", " + training_id +") added"
    except Exception as e:
        return str(e)


@app.route('/rm_training_exercise/<training_id>/<exercise_name>')
def rm_training_exercise(training_id, exercise_name):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle')
        c.execute("SELECT * FROM TRAINING_EXERCISE WHERE Training_id = {} and Exercise_name = '{}'".format(training_id, exercise_name))

        if not c.fetchall():
            return "There is no relation in the Database with such parameters"

        c.execute("DELETE FROM TRAINING_EXERCISE WHERE Training_id = {} and Exercise_name = '{}'".format(training_id, exercise_name))
        conn.commit()
        c.close()
        conn.close()
        return "Relation (" + name + ", " + training_id +") Removed"
    except Exception as e:
        return str(e)

@app.route('/add_muscle_group/<name>/<image>')
def add_muscle_group(name, image):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle')
        c.execute("INSERT INTO MUSCLE_GROUP (Name, Image) VALUES ('{}', '{}')".format(name, image))
        conn.commit()
        c.close()
        conn.close()
        return "Muscle Group " + name + " added"
    except Exception as e:
        return str(e)

@app.route('/rm_muscle_group/<name>')
def rm_muscle_group(name):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle')
        conn.commit()
        c.close()
        conn.close()
        return "Muscle Group " + name + " deleted"
    except Exception as e:
        return str(e)


@app.route('/add_muscles_worked/<exercise_name>/<muscle_name>/<primari>')
def add_muscles_worked(exercise_name, muscle_name, primari):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle')
        c.execute("INSERT INTO MUSCLES_WORKED (Exercise_name, Muscle_name, Primari) VALUES ('{}', '{}', {})".format(exercise_name, muscle_name, primari))
        conn.commit()
        c.close()
        conn.close()
        return "Relation (" + exercise_name + ", " + muscle_name +") added"
    except Exception as e:
        return str(e)

@app.route('/rm_muscles_worked/<exercise_name>/<muscle_name>')
def rm_muscles_worked(exercise_name, muscle_name):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle')
        c.execute("DELETE FROM MUSCLES_WORKED WHERE Exercise_name = '{}' and Muscle_name = '{}'".format(exercise_name, muscle_name))
        conn.commit()
        c.close()
        conn.close()
        return "Relation (" + exercise_name + " " + muscle_name +") deleted"
    except Exception as e:
        return str(e)

@app.route('/add_exercise_history/<date_time>/<user>/<exercise_name>/<sets>/<repetitions>/<weight>/<intensity>')
def add_exercise_history(date_time, user, exercise_name, sets, repetitions, weight, intensity):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle')
        c.execute("INSERT INTO EXERCISE_HISTORY (Date_Time, User, Exercise_name, Sets, Repetitions, Weight, Intensity) VALUES ('{}', '{}', '{}', {}, {}, {}, {})".format(date_time, user, exercise_name, sets, repetitions, weight, intensity))
        conn.commit()
        id = c.lastrowid
        c.close()
        conn.close()
        return "Exercise History " + str(id) + " added"
    except Exception as e:
        return str(e)

@app.route('/rm_exercise_history/<id>')
def rm_exercise_history(id):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle')

        c.execute("DELETE FROM EXERCISE_HISTORY WHERE ID = {}".format(id))
        conn.commit()
        c.close()
        conn.close()
        return 'Exercise_history ' + id + ' removed'
    except Exception as e:
        return str(e)


@app.route('/add_plan_history/<start_date>/<end_date>/<objective_met>/<plan>/<user>')
def add_plan_history(start_date, end_date, objective_met, plan, user):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle')
        c.execute("INSERT INTO PLAN_HISTORY (Start_date, End_Date, Objective_met, Plan, User) VALUES ('{}', '{}', {}, {}, '{}')".format(start_date, end_date, objective_met, plan, user))
        conn.commit()
        id = c.lastrowid
        c.close()
        conn.close()
        return "Plan History " + str(id) + " added"
    except Exception as e:
        return str(e)


@app.route('/rm_plan_history/<id>')
def rm_plan_history(id):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle')

        c.execute("DELETE FROM PLAN_HISTORY WHERE ID = {}".format(id))
        conn.commit()
        c.close()
        conn.close()
        return 'Plan_history ' + id + ' removed'
    except Exception as e:
        return str(e)


@app.route('/set_user_plan/<email>/<plan_id>')
def set_user_plan(email, plan_id):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute("UPDATE USER SET Plan_id = {} WHERE Email = '{}'").format(plan_id, email)
        conn.commit()
        c.close()
        conn.close()

        return "Plan " + str(plan_id) + " atribuited to " + str(email)
    except Exception as e:
        return str(e)

@app.route('/user_exists/<email>')
def user_exists(email):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle')

        c.execute("SELECT * FROM USER WHERE Email = '{}'".format(email))

        fetched = c.fetchall()

        c.close()
        conn.close()
        if fetched:
            return "True"

        return "False"
    except Exception as e:
        return str(e)

@app.route('/user_login/<email>/<password>')
def user_login(email, password):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle')


        c.execute("SELECT Password FROM USER WHERE Email = '{}'".format(email))
        fetched = c.fetchall();
        c.close()
        conn.close()
        print(fetched)
        if not fetched:
            return "User does not exist"

        return str(pbkdf2_sha256.verify(password, fetched[0]['Password']))

    except Exception as e:
        return str(e)

@app.route('/get_all_exercises')
def get_all_exercises():
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle')


        c.execute("SELECT * FROM EXERCISE")
        fetched = c.fetchall()
        c.close()
        conn.close()
        
        return jsonify(fetched);
    except Exception as e:
        return jsonify(e)


@app.route('/get_user_plan/<email>')
def get_user_plan(email):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle')

        c.execute("SELECT Plan FROM USER WHERE Email = '{}'".format(email))
        fetched = c.fetchall()
        c.close()
        conn.close()
        return jsonify(fetched[0]);

    except Exception as e:
        return str(e)


@app.route('/get_exercise/<exercise_name>')
def get_exercise(exercise_name):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle')

        print(exercise_name)
        c.execute("SELECT * from EXERCISE WHERE Name = '{}'".format(exercise_name))

        fetched = c.fetchall()
        c.close()
        conn.close()

        return jsonify(fetched[0])
    except Exception as e:
        return str(e)

@app.route('/get_exercises_by_muscle_group/<muscle_group>')
def get_exercises_by_muscle_group(muscle_group):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle')


        c.execute("SELECT Exercise_name FROM MUSCLES_WORKED WHERE Muscle_name = '{}'".format(muscle_group))
        exercise_names = c.fetchall()
        fetched = c.fetchall()
        c.close()
        conn.close()

        all_exercises = []

        """
        Para: David
        mudei cenas, capaz ter feito mrd, n sei, estava assim antes:


        for f in fetched:
            all_exercises += get_exercise(str(f))

        return str(fetched[0])

        """

        return jsonify(exercise_names);

        #return str(exercise_names);

    except Exception as e:
        return str(e)

@app.route('/get_exercise_history_of_user/<user_email>')
def get_exercise_history_of_user(user_email):
    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle')


        c.execute("SELECT * FROM EXERCISE_HISTORY WHERE User_email = '{}'".format(user_email))
        fetched = c.fetchall()
        c.close()
        conn.close()

        for f in fetched:
            f[Exercise] = get_exercise(str(f[Exercise_name]))

        return jsonify(fetched)
    except Exception as e:
        return str(e)


@app.route('/get_last_exercise_of_user/<user_email>/<exercise_name>')
def get_last_exercise_of_user(user_email, exercise_name):

    try:
        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle')


        c.execute("SELECT * FROM EXERCISE_HISTORY WHERE User_email = '{}' and Exercise_name = '{}'".format(user_email, exercise_name))
        fetched = c.fetchall()
        c.close()
        conn.close()
        d = fetched[0] 
        for f in fetched:
            if f["Date_time"] > d["Date_time"]:
                d = f

        return jsonify(f)
    except Exception as e:
        return str(e)
