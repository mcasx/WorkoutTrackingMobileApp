import MySQLdb

host = 'localhost'
uname = 'muscle'
passwd = 'pass123'

### Add user to db
def adduser(name,dob,gender,height,weight,img):
	conn = MySQLdb.connect(host=host, user=uname, password=passwd, database='muscle')
	c = conn.cursor()
	c.execute('INSERT INTO user () VALUES (NULL,%s,%s,%s,%s,%s,%s)', (name,dob,gender,height,weight,img))
	conn.commit()
	c.close()
	conn.close()

def removeuser(user_id):
	conn = MySQLdb.connect(host=host, user=uname, password=passwd, database='muscle')
	c = conn.cursor()
	c.execute('DELETE FROM user() WHERE id = %s', (user_id))
	conn.commit()
	c.close()
	conn.close()

### Add plan to db
def addplan(name,goal):
	conn = MySQLdb.connect(host=host, user=uname, password=passwd, database='muscle')
	c = conn.cursor()
	c.execute('INSERT INTO plan () VALUES (NULL,%s,%s)', (name,goal))
	conn.commit()
	c.close()
	conn.close()

def removeplan(plan_id):
	conn = MySQLdb.connect(host=host, user=uname, password=passwd, database='muscle')
	c = conn.cursor()
	c.execute('DELETE FROM plan() WHERE id = %s', (plan_id))
	conn.commit()
	c.close()
	conn.close()

### Add exercise to db
def addexercise(name,img,description,e_type,musclezone):
	conn = MySQLdb.connect(host=host, user=uname, password=passwd, database='muscle')
	c = conn.cursor()
	c.execute('INSERT INTO exercise () VALUES (NULL,%s,%s,%s,%s,%s)', (name,img,description,e_type,musclezone))
	conn.commit()
	c.close()
	conn.close()

def removeexercise(exercise_id):
	conn = MySQLdb.connect(host=host, user=uname, password=passwd, database='muscle')
	c = conn.cursor()
	c.execute('DELETE FROM exercise() WHERE id = %s', (exercise_id))
	conn.commit()
	c.close()
	conn.close()

### Add an exercise to a plan
def addplan_exercise(plan_id, exercise_id):
	conn = MySQLdb.connect(host=host, user=uname, password=passwd, database='muscle')
	c = conn.cursor()
	c.execute('INSERT INTO plan_exercise() VALUES (%s,%s)', (plan_id, exercise_id))
	conn.commit()
	c.close()
	conn.close()

def removeplan_exercise(plan_id, exercise_id):
	conn = MySQLdb.connect(host=host, user=uname, password=passwd, database='muscle')
	c = conn.cursor()
	c.execute('DELETE FROM plan_exercise() WHERE plan_id = %s AND exercise_id = %s', (plan_id, exercise_id))
	conn.commit()
	c.close()
	conn.close()

### Add an exercise to a user
def adduser_exercise(user_id,exercise_id,distance,duration,reps,sets,weight):
	conn = MySQLdb.connect(host=host, user=uname, password=passwd, database='muscle')
	c = conn.cursor()
	c.execute('INSERT INTO user_exercise() VALUES (%s,%s,%s,%s,%s,%s,%s)', (user_id, exercise_id, distance, duration, reps, sets, weight))
	conn.commit()
	c.close()
	conn.close()

def removeuser_exercise(user_id, exercise_id):
	conn = MySQLdb.connect(host=host, user=uname, password=passwd, database='muscle')
	c = conn.cursor()
	c.execute('DELETE FROM user_exercise() WHERE user_id = %s AND exercise_id = %s', (user_id, exercise_id))
	conn.commit()
	c.close()
	conn.close()

### Add a plan to a user
def adduser_plan(user_id,plan_id):
	conn = MySQLdb.connect(host=host, user=uname, password=passwd, database='muscle')
	c = conn.cursor()
	c.execute('INSERT INTO user_plan() VALUES (%s,%s)', (user_id, plan_id))
	conn.commit()
	c.close()
	conn.close()

def removeuser_plan(user_id, plan_id):
	conn = MySQLdb.connect(host=host, user=uname, password=passwd, database='muscle')
	c = conn.cursor()
	c.execute('DELETE FROM user_plan() WHERE user_id = %s AND plan_id = %s', (user_id, plan_id))
	conn.commit()
	c.close()
	conn.close()
