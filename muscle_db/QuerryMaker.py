from Data import *
import random
from random import randint
from passlib.hash import pbkdf2_sha256

domains = [ "hotmail.com", "gmail.com", "aol.com", "mail.com" , "mail.kz", "yahoo.com"]
letters = ["a", "b", "c", "d","e", "f", "g", "h", "i", "j", "k", "l"]

def get_one_random_domain(domains):
        return domains[random.randint( 0, len(domains)-1)]


def get_one_random_name(letters):
    email_name = ""
    for i in range(7):
        email_name = email_name + letters[random.randint(0,11)]
    return email_name

def generate_random_emails():

    emails = []
    names = []
    for i in range(0,100):
         one_name = str(get_one_random_name(letters))
         names += [(one_name)]
         one_domain = str(get_one_random_domain(domains))
         emails += [(one_name  + "@" + one_domain)]

    return (emails, names)






for row in muscle_group_rows:
    pass
    #print("INSERT INTO MUSCLE_GROUP (Name, Image) VALUES ('{}', '{}');".format(row[1], row[1] + ".jpg"))



for row in exercise_rows:
    #print("INSERT INTO EXERCISE (Name, Description, Image, Kind) Values ('{}', '{}', '{}', 'Strenght');".format(row[1], row[2], row[3]))
    pass





emails, names = generate_random_emails()

#input(generate_random_emails()[1])


password = pbkdf2_sha256.encrypt("123456", rounds=200000, salt_size=16)

for i in range(0, 100):
#    print("INSERT INTO USER (Email, Name, Date_of_birth, Password, Gender, Height, Weight, Profile_image, Plan) VALUES ('{}','{}','{}', '{}', {}, {}, {}, '{}.jpg', {});".format(emails[i], names[i], "" + str(randint(1950, 2000)) + "-" + str(randint(1, 12)) + "-" + str(randint(1,27)), password, randint(0,1), randint(140, 210), randint(50, 140), emails[i], randint(1,10)))
    pass

for i in range(0, 200):
    pass
    #print("INSERT INTO EXERCISE_HISTORY (Date_Time, Exercise_name, User_email, Set_amount, Average_intensity) Values ('{}', '{}', 'ola@ua.pt', {}, {});".format(\
    #str(randint(1950, 2000)) + "-" + str(randint(1, 12)) + "-" + str(randint(1,27)) + " " +  str(randint(0, 24)) + ":" + str(randint(0, 60)) + ":" + str(randint(0, 24))\
    #, str(exercise_rows[randint(0, len(exercise_rows) - 1)][0]), randint(1,5), random.gauss(1, 0.3)))


for row in exercise_rows:
    pass
    #print("INSERT INTO MUSCLES_WORKED (Exercise_name, Muscle_name, Primari) Values ('{}', '',);".format(row[0]))


for i  in range (0,5):

    #startDate = 1
    #endDate = 0
    #while(startDate >= endDate):
    #    startDate = str(randint(2014, 2017)) + "-" + str(randint(1, 12)) + "-" + str(randint(1,27)) + " "
    #    endDate = str(randint(2014, 2017)) + "-" + str(randint(1, 12)) + "-" + str(randint(1,27)) + " "

    #print("INSERT INTO PLAN_HISTORY (Start_date, End_Date, User_email, Plan_id, Met_objective) Values ('{}', '{}', 'ola@ua.pt', {}, {});".format(\
    #startDate, endDate, randint(1,9), randint(0,1)))
    pass

for row in exercise_history_rows:
    pass
#    for i in range(0, int(row[5])):
#        print("INSERT INTO SETS (exercise_history_id, Set_number, Repetitions, Weight, Intensity, Resting_time, Intensity_deviation) Values ({}, {}, {}, {}, {}, '{}', {});".format(\
#        row[0],
#        i+1,
#        abs(int(random.gauss(10, 2))),
#        abs(int(random.gauss(6, 1))*5),
#        abs(random.gauss(1, 0.25)),
#        str(randint(0, 0)) + ":" + str(randint(0, 1)) + ":" + str(randint(0, 50)),
#        random.gauss(0.0, 0.1)))


for row in plan_rows:
    rand = randint(2,4)

    if rand == 2:
        print("INSERT INTO TRAINING (Name, Plan_id) VALUES ('Back, Sholders and Biceps', {});".format(row[0]))
        print("INSERT INTO TRAINING (Name, Plan_id) VALUES ('Chest, Legs and Triceps', {});".format(row[0]))

    elif rand == 3:
        print("INSERT INTO TRAINING (Name, Plan_id) VALUES ('Legs and Shoulders', {});".format(row[0]))
        print("INSERT INTO TRAINING (Name, Plan_id) VALUES ('Chest and Triceps', {});".format(row[0]))
        print("INSERT INTO TRAINING (Name, Plan_id) VALUES ('Back and Biceps', {});".format(row[0]))

    elif rand == 4:
        print("INSERT INTO TRAINING (Name, Plan_id) VALUES ('Chest and Triceps', {});".format(row[0]))
        print("INSERT INTO TRAINING (Name, Plan_id) VALUES ('Back and Biceps', {});".format(row[0]))
        print("INSERT INTO TRAINING (Name, Plan_id) VALUES ('Legs', {});".format(row[0]))
        print("INSERT INTO TRAINING (Name, Plan_id) VALUES ('Shoulders', {});".format(row[0]))
