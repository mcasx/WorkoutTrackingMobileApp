import MySQLdb

host = 'localhost'
uname = 'muscle'
passwd = 'some_pass'

def exerciseAverages(user_id, exercise_name, weight, repetitions):
	try:
		conn = mysqldb.connect(host=host, user=uname, password=passwd, database='muscle')
		c = conn.cursor(MySQLdb.cursors.DictCursor)

		c.execute('SELECT Gender, Weight 
				FROM (User WHERE Email = '%s') AS u
				JOIN User_History
				ON u.Email = User_history.User
				ORDER BY Date DESC', (user_id))
		r = c.fetchall()[0]
		gender, u_weight = r["Gender"], r["Weight"]
		weight_min = u_weight - 5
		weight_max = u_weight + 5
		
		c.execute('SELECT Objective
				FROM (SELECT * FROM USER WHERE Email = '%s') AS u
				JOIN PLAN
				ON u.Plan = PLAN.ID', (user_id) )

		objective = c.fetchall()[0]['Objective']
		
		c.execute('	SELECT AVG(Intensity) AS avg_int, AVG(Intensity_deviation) AS int_dev
					FROM USER 
					WHERE Gender = '%s' AND Exercise_name = '%s' AND User_weight BETWEEN '%s' AND '%s' AND Weight = '%s' AND Repetitions = '%s' ', (gender, exercise_name, weight_min, weight_max, weight, repetitions) )
		
		r = c.fetchall()
		c.close()
		conn.close()
		
		return jsonify(r)

	except Exception as e:
		return str(e)

def recommendNextExercise(user_id):
	try:
		conn = mysqldb.connect(host=host, user=uname, password=passwd, database='muscle')
		c = conn.cursor(MySQLdb.cursors.DictCursor)
		
		c.execute('SELECT Exercise_name, COUNT(Email) as count
				FROM ( SELECT DISTINCT u.Email, e.Exercise_name
					FROM ( SELECT Email
						FROM USER
						JOIN (SELECT * 
							FROM EXERCISE_HISTORY
							WHERE Exercise_name IN (Select Exercise_name FROM EXERCISE_HISTORY WHERE User = '%s') ) AS e_h
						ON USER.Email = e_h.User) AS u
					JOIN ( SELECT Exercise_name
						FROM EXERCISE_HISTORY ) AS e
					ON u.Email = e.User )
				GROUP BY Exercise_name
				ORDER BY count DESC', (user_id))
		r = c.fetchall()
		c.close()
		conn.close()
		return jsonify(r[:3])

	except Exception as e:
		return str(e)

def recommendNextPlan(user_id):
	try:
		conn = mysqldb.connect(host=host, user=uname, password=passwd, database='muscle')
		c = conn.cursor(MySQLdb.cursors.DictCursor)
		
		c.execute('SELECT ID, COUNT(Email) as count
				FROM ( SELECT DISTINCT u.Email, e.ID
					FROM ( SELECT Email
						FROM USER
						JOIN (SELECT * 
							FROM EXERCISE_HISTORY
							WHERE Exercise_name IN (Select Exercise_name FROM EXERCISE_HISTORY WHERE User = '%s') ) AS e_h
						ON USER.Email = e_h.User) AS u
					JOIN ( SELECT ID
						FROM PLAN_HISTORY ) AS e
					ON u.Email = e.User )
				GROUP BY Exercise_name
				ORDER BY count DESC', (user_id))
		r = c.fetchall()
		c.close()
		conn.close()
		return jsonify(r[:3])

	except Exception as e:
		return str(e)
