from datetime import timedelta, date
from random import randrange
import random
import MySQLdb

##################### PARAMETERS #####################

weeks = 10
exercise_per_day = 10
email = "ola@ua.pt"


######################################################

conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass')
c = conn.cursor()
c.execute('USE muscle2')

c.execute('''SELECT Name FROM EXERCISE''')
exercises = c.fetchall()

def daterange(start_date, end_date):
    for n in range(int ((end_date - start_date).days)):
        yield start_date + timedelta(n)

def fill_exercise_week(weeks, exercise_per_day, email):
	end_date = date.today()
	start_date = end_date - timedelta(weeks = weeks)

	for dt in daterange(start_date, end_date):
		for exercise_moment in range(exercise_per_day):
			avg_int = "{0:.2f}".format(random.uniform(0.3, 1.9))
			set_amt = randrange(1,6)
			hours = "{0:02d}".format(randrange(25))
			minutes = "{0:02d}".format(randrange(25))
			seconds = "{0:02d}".format(randrange(25))
		
			c.execute('''INSERT INTO EXERCISE_HISTORY(
							Date_Time,
							Exercise_name,
							User_email,
							Average_intensity,
							Set_amount,
							Shared) 

						VALUES(
							%s " "%s":"%s":"%s,
							%s,
							%s,
							%s,
							%s,
							%s)''',[	str(dt),hours,minutes,seconds,
										exercises[randrange(len(exercises))][0],
										email,
										str(float(avg_int)),
										str(set_amt),
										str(randrange(2))									
									])
		
			ex_h_id = c.lastrowid 
			set_minutes = "{0:02d}".format(randrange(2))
			set_seconds = "{0:02d}".format(randrange(61))
			intensity = "{0:.2f}".format(random.uniform(0.1, 1.9))
			int_dev = "{:.8f}".format(random.uniform(-0.3, 0.4))

			for set_number in range(1,set_amt+1):
			
				c.execute('''INSERT INTO SETS(
								Exercise_history_id, 
								Set_number,
								Repetitions,
								Weight,
								Intensity,
								Resting_Time,
								Intensity_deviation) 

							VALUES(
								%s,
								%s,
								%s,
								%s,
								%s,
								"00:"%s":"%s,
								%s)''',[	ex_h_id,
											set_number,
											str(randrange(2,17)),
											str(randrange(15,46,5)),	
											str(intensity),
											set_minutes,set_seconds,
											str(int_dev)
										])
			#easier this way, time is short
			c.execute('''DELETE FROM EXERCISE_HISTORY WHERE Date_time="0000-00-00 00:00:00"''')

	conn.commit()
	c.close()
	conn.close()

fill_exercise_week(7, 10, "ai@ua.pt")