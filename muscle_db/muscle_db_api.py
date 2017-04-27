from flask import Flask, jsonify, request
import ssl
from passlib.hash import pbkdf2_sha256
import MySQLdb
from datetime import datetime
import sys
import base64 


app = Flask(__name__)


@app.route('/hello', methods = ['GET', 'POST'])
def hello():
    print(request.form)
    if request.method == 'POST':
        return "Hello World" + request.form.get('Name')
    else:
        return "Hello World"



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
	        
	        """
	        Para: David
	        Mudei cenas
	        """
	        
	            #else:
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
        set_amount = request.form['set_amount']
        average_intensity = request.form['average_intensity']


        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor()
        c.execute('USE muscle2')
        c.execute("INSERT INTO EXERCISE_HISTORY (Date_Time, User_email, Exercise_name, Set_amount, Average_intensity) VALUES (%s, %s, %s, %s, %s)", [date_time, user, exercise_name, set_amount, average_intensity])
        conn.commit()
        id = c.lastrowid
        c.close()
        conn.close()
        return "Exercise History " + str(id) + " added"
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
        c = conn.cursor()
        c.execute("UPDATE USER SET Plan_id = %s WHERE Email = %s", [plan_id, email])
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

#FIELDS REQUIRED: email, gender, other user chars are opt
#email will later be replaced by ID
@app.route('/update_user_char', methods=['POST'])
def update_user_char():
    try:

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
            var_insert_tuple += (request.form['weight'],)

        if 'date_of_birth' in request.form:
            dob_substring = "Date_of_birth = %s, "
            var_insert_tuple += (request.form['date_of_birth'],)

        if 'profile_pic' in request.form:
            profile_pic_substring = "Profile_image = %s, "

            img_dir = "user_profile_pics/"
            img_path = "user_{0}_profile_pic.jpeg".format(email)
            #img_path = "user_{:0>{width}}_profile_pic".format(ID,width=11)
            var_insert_tuple += (img_path,)
            print(request.form['profile_pic'])
            #save image in user_profile_pics 
            with open(img_dir + img_path,"wb") as f:
                f.write(base64.b64decode(request.form['profile_pic']))
        
        gender = request.form['gender']

        conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
        c = conn.cursor(MySQLdb.cursors.DictCursor)
        c.execute('USE muscle2')

        print("UPDATE USER SET " + 
            name_substring +
            height_substring + 
            weight_substring + 
            dob_substring + 
            profile_pic_substring +
            "Gender = %s WHERE Email = %s", var_insert_tuple + (gender, email))
        
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


@app.route('/get_user_plan/', methods=['POST'])
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


        c.execute("SELECT * FROM EXERCISE_HISTORY WHERE User_email = %s ORDER BY Date_time DESC", [email])
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
        c.execute("SELECT count(ID), Kind FROM EXERCISE_HISTORY as eh join EXERCISE as e on e.Name=eh.Exercise_name WHERE eh.User_email = %s GROUP BY Kind;", [user_email])
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

#####################
##   SOCIAL FEED   ##
#####################

@app.route('/get_user_feed', methods=['POST'])
def get_user_feed():
	try:
		user_email = request.form['user_email']
		amount = request.form['amount']

		conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle')
		c = conn.cursor(MySQLdb.cursors.DictCursor)
		
		c.execute('USE muscle2')
		c.execute("""
			SELECT *
			FROM EXERCISE_HISTORY
			LEFT JOIN (SELECT Following FROM FOLLOWERS WHERE User_email = %s) AS f
			ON EXERCISE_HISTORY.User_email = f.Following
			ORDER BY Date_Time
			LIMIT %s
			""", [user_email, amount])

		r = c.fetchall()
		c.close()
		conn.close()
		return jsonify(r)

	except Exception as e:
		return str(e)
	
@app.route('get_comments_exercise', methods=['POST'])
def get_comments_exercise:
	try:
		exercise_id = request.form['exercise_id']

		conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle')
		c = conn.cursor(MySQLdb.cursors.DictCursor)
		
		c.execute('USE muscle2')
		c.execute("""
			SELECT User_email, Comment
			FROM COMMENTS
			WHERE Exercise = %s
			""", [exercise_id])

		r = c.fetchall()
		c.close()
		conn.close()
		return jsonify(r)

	except Exception as e:
		return str(e)

@app.route('get_bumps_exercise', methods=['POST'])
def get_bumps_exercise:
	try:
		exercise_id = request.form['exercise_id']

		conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle')
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

@app.route('get_users_bumped_exercise', methods=['POST'])
def get_users_bumped_exercise:
	try:
		exercise_id = request.form['exercise_id']

		conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle')
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

#####################
## DATA PROCESSING ##
#####################

@app.route('/get_expected_exercise_result', methods=['POST'])
def get_expected_exercise_result():
	try:
		user_email = request.form['user_email']
		exercise_name = request.form['exercise_name']
		weight = request.form['weight']
		repetitions = request.form['repetitions']
		set_number = request.form['set_number']
		
		conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle')
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
				FROM (SELECT * FROM SETS WHERE Repetitions = %s AND Weight = %s AND Set_number = %s) AS s
				JOIN (SELECT * FROM EXERCISE_HISTORY WHERE Exercise_name = %s) AS e_h
				ON s.Exercise_history_id = e_h.ID) AS temp
			ON USER.Email = temp.User_email
			WHERE Gender = %s AND Weight BETWEEN %s AND %s""", [repetitions, weight, set_number, exercise_name, gender, weight_min, weight_max])
		
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
		conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle')
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
		conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle')
		c = conn.cursor(MySQLdb.cursors.DictCursor)
		
		c.execute('USE muscle2')
		c.execute("""
			SELECT ID, COUNT(Email) as count
			FROM ( SELECT DISTINCT u.Email, e.ID
				FROM ( SELECT Email
					FROM USER
					JOIN (SELECT * 
						FROM EXERCISE_HISTORY
						WHERE Exercise_name IN (Select Exercise_name FROM EXERCISE_HISTORY WHERE User_email = %s) ) AS e_h
					ON USER.Email = e_h.User_email) AS u
				JOIN ( SELECT ID, User_email
					FROM PLAN_HISTORY ) AS e
				ON u.Email = e.User_email ) AS temp
			GROUP BY ID
			ORDER BY count DESC
			LIMIT 3""", [user_email])

		r = c.fetchall()
		c.close()
		conn.close()
		return jsonify(r)

	except Exception as e:
		return str(e)

if __name__ == '__main__':
    context = ssl.SSLContext(ssl.PROTOCOL_TLSv1_2)
    context.load_cert_chain('muscle-selfsigned.crt', 'muscle-selfsigned.key')
    app.run(host="0.0.0.0", port = "443", ssl_context = context)
