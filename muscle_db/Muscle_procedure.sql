/*DELIMITER $$
CREATE PROCEDURE muscle2.GET_MY_PLAN_EXERCISE_COUNT(User VARCHAR(255))
BEGIN    
	SELECT Count(Plan_id) AS Plan_Exercise_Count 
    FROM EXERCISE_HISTORY 
    WHERE Plan_id = (SELECT ID
					 FROM PLAN_HISTORY
					 WHERE User_email = User AND End_Date = 0000-00-00);	  #Current exercise    
END; $$*/

DELIMITER $$
CREATE PROCEDURE muscle2.SET_USER_PLAN(User VARCHAR(255),new_plan_id INT)
BEGIN
	UPDATE USER SET Plan = new_plan_id WHERE Email = User;
    
    UPDATE PLAN_HISTORY SET End_Date = CURRENT_DATE() WHERE End_Date = 0000-00-00 AND User_email = User;
    
    INSERT INTO PLAN_HISTORY(Start_Date, End_Date, Met_objective, Plan_id, User_email)
				VALUES(CURRENT_DATE(),0000-00-00,0,new_plan_id,User);
END $$