/**
 * 1. START EXERCISE, WEIGHT, REP = 0
 * 2. USER REACHED DOWN, REP = 1, INTENSITY, WEIGHT, GOING UP
 * 3. USER UP, WAIT 5 SEC IF USER GOING DOWN, CONTINUE, IF NOT STOP
 * 5. USER REACHED DOWN, REP = 2, INTENSITY, WEIGHT, GOING UP
 * 6. USER UP, WAIT 5 SEC IF USER GOING DOWN, CONTINUE, IF NOT STOP 
 * 7. USER STOPPED, REP = 2, WEIGHT, MEDIUM INTENSITY
 * 
 */

#include <TimedAction.h>
#include <AcceleroMMA7361.h>

AcceleroMMA7361 accelero;
int tx=1;
int rx=0;
char inSerial[15];
char* currentOP = "null";
int gravityValue;
int currentWeight;
bool sensorsOn = false; //change to false!!!!!!!!
int meanAcc = 0;
int meanCount = 0;

void checkBluetoothStatus(){
  int i=0;
  int m=0;
  if (Serial.available() > 0) {             
     while (Serial.available() > 0) {
       inSerial[i]=Serial.read(); 
       i++;      
     }
     inSerial[i]='\0';
    Check_Protocol(inSerial);
   }
}

TimedAction bluetooth = TimedAction(500,checkBluetoothStatus); //every 500ms bluetooth checks for income messages
TimedAction weightCheck = TimedAction(1000,check_weight); //every 1s print input val

void setup(){
  //BLUETOOTH
  Serial.begin(9600);
  pinMode(tx, OUTPUT);
  pinMode(rx, INPUT);
  pinMode(12, INPUT);

  //LASER
  pinMode(7, OUTPUT);
  
  //ACCEL
  calibration(); // accelerometer calibration + define gravityValue
}

void loop(){                      
  bluetooth.check();  //every 500ms bluetooth checks for income messages
  //weightCheck.check(); 

  /*if(!strcmp(currentOP,"start_sensors")){
    sensorsOn = true; //TURN ON SENSORS
  }
  else if(!strcmp(currentOP,"stop_sensors")){
    sensorsOn = false; //TURN OFF SENSORS
  }*/

  //Serial.println(digitalRead(12));

  if(sensorsOn){
    if(digitalRead(12) == 0){
      //FOR TESTS PURPOUSE
      /*Serial.println("3");
      delay(1000);
      Serial.println("2");
      delay(1000);
      Serial.println("1");
      delay(1000);*/

      /*int zMean = 0;
      int zCount = 0;
      
      while(digitalRead(12) == 0){
        zCount++;
        zMean = zMean + acc_during_100_ms();
      }*/
      /*Serial.print("Mean acc: ");
      //Serial.println(zMean/zCount);
      Serial.println(acc_during_100_ms());*/
      //delay(5000);
      meanAcc = meanAcc + acc_during_100_ms();
      meanCount++;
    }
    else if(meanCount > 1){
      Serial.print("{\"meanAcc\":\"");Serial.print(meanAcc/meanCount);Serial.println("\"}");
      //Serial.print("Mean acc: ");
      //Serial.println(zMean/zCount);
      meanAcc = 0;
      meanCount = 0;
    }
    
  }

  //Serial.println(digitalRead(13));
  /*int x = 0;
  int val = 0;
  while(x < 100){
    val = val + acc_during_10_ms();
    x++;
  }
  Serial.println(val/100);*/
  /*delay(1000);
  Serial.println("3");
  delay(1000);
  Serial.println("2");
  delay(1000);
  Serial.println("1");
  delay(1000);
  Serial.println(acc_during_100_ms());*/

  //delay(100000);
  /*while(true){
    Serial.println(digitalRead(13));
    delay(100);
  }*/
     /*if(!strcmp(currentOP,"start")){
      sendZ_Accel();
     }
     else if(!strcmp(currentOP,"getG")){
      getCurrentG();
     }*/
     /*int count = 0;
     int val = 0;
     while(digitalRead(13) == 0);
     Serial.println("out");
     delay(1000);
     while(digitalRead(13) == 0){
      val = val + getCurrentG();
      count++;
     }
     val = val / count;
     Serial.println();
     Serial.println(val);
     Serial.println(count);
     delay(10000);*/
     //getCurrentG();
    //Serial.println(accelero.getZAccel());
}

void check_weight(){
  Serial.print("Input VAL: ");
  Serial.println(digitalRead(12));
}
  
void Check_Protocol(char inStr[]){   
  int i=0;
  int m=0;
  
  if(!strcmp(inStr,"start_sensors")){
    currentOP = "start_sensors";
    digitalWrite(7, HIGH);
    sensorsOn = true; //TURN ON SENSORS
    Serial.println("{\"status\":\"start_sensors\"}");
    delay(100);
  }

  else if(!strcmp(inStr,"stop_sensors")){
    currentOP = "stop_sensors";
    digitalWrite(7, LOW);
    sensorsOn = false; //TURN ON SENSORS
    Serial.println("{\"status\":\"stop_sensors\"}");
  }

  else if(!strcmp(inStr,"calibrate")){
    calibration();
    //currentOP = "null";
  }

  else if(!strcmp(inStr,"current_op")){
    Serial.print("{\"current_op\":\"");Serial.print(currentOP);Serial.println("\"}");
  }

  else if(!strcmp(inStr,"current_g")){
    Serial.print("{\"current_g\":\"");Serial.print(accelero.getZAccel());Serial.println("\"}");
  }

  else if(!strcmp(inStr,"laser_1")){
    Serial.print("{\"laser_1\":\"");Serial.print(digitalRead(12));Serial.println("\"}");
  }
       
  for(m=0;m<11;m++){
    inStr[m]=0;
  }
  i=0;
}

int acc_during_100_ms(){
  int maxZ = 0;
  int z;
  //int time_since_last_reset = millis();
  int count = 0;
  
  //while((millis() - time_since_last_reset) < 1000){ // 100 ms
  while(count <= 100){
    z = accelero.getZAccel();
    count++;
    
    if(z > maxZ) maxZ = z;    
  }

  /*Serial.print("Count: ");
  Serial.println(count);*/
  return maxZ - gravityValue;
}

int getCurrentG(){
  int count = 0;
  int maxZ = 0;
  int minZ = 0;
  int z;
  
  while(count <= 100){
    z = accelero.getZAccel()-gravityValue;
    
    count++;
    
    if(z > maxZ) maxZ = z;
    if(z < minZ) minZ = z;
    
  }

  if(maxZ > (minZ*(-1))){
    Serial.println(maxZ);
  }
  else{
    Serial.println(minZ);
  }

  //return maxZ;
  
  /*Serial.print("{maxZ:");
  Serial.print(maxZ);
  Serial.print(", minZ:");
  Serial.print(minZ);
  Serial.print(", CurrentZ:");
  Serial.print(z);
  Serial.println("}");*/
}

void calibration(){
  accelero.begin(2,3,4,5,A0,A1,A2);
  accelero.setARefVoltage(5);                   //sets the AREF voltage to 3.3V
  accelero.setSensitivity(LOW);                   //sets the sensitivity to +/-6G
  accelero.calibrate();
  delay(100);
  int Gval = 0;
  for(int i = 0; i < 100; i++){
    Gval = Gval + accelero.getZAccel();
    delay(5);
  }
  gravityValue = Gval / 100;

  Serial.println("{defult_gravity:");
  Serial.println(gravityValue);
  Serial.println("}");
}

void sendZ_Accel(){
  int count = 0;
  int maxZ = 0;
  int minZ = 0;
  int z;
  
  while(count <= 1){
    z = accelero.getZAccel();
    
    count++;
    
    if(z > maxZ) maxZ = z;
    if(z < minZ) minZ = z;
    
  }
  
  Serial.print("{maxZ:");
  Serial.print(maxZ);
  Serial.print(", minZ:");
  Serial.print(minZ);
  Serial.print(", CurrentZ:");
  Serial.print(z);
  Serial.println("}");
}

void startMonitoring(){
  //receive start monitoring command
  Serial.println("{\"status\":\"waiting\"}");
  delay(5000);
  //user pulls down
  Serial.println("{\"status\":\"started\", \"weight\":50, \"rep\":0}");
  delay(2000);
  //user reach down
  Serial.println("{\"weight\":50, \"rep\":1, \"intensity\":1.2}"); 
  delay(2000);
  //user reach up and stops
  Serial.println("{\"status\":\"stopped\"}");
  
}
