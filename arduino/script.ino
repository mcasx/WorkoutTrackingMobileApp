/**
 * 1. START EXERCISE, WEIGHT, REP = 0
 * 2. USER REACHED DOWN, REP = 1, INTENSITY, WEIGHT, GOING UP
 * 3. USER UP, WAIT 5 SEC IF USER GOING DOWN, CONTINUE, IF NOT STOP
 * 5. USER REACHED DOWN, REP = 2, INTENSITY, WEIGHT, GOING UP
 * 6. USER UP, WAIT 5 SEC IF USER GOING DOWN, CONTINUE, IF NOT STOP 
 * 7. USER STOPPED, REP = 2, WEIGHT, MEDIUM INTENSITY
 * 
 */

#include <AcceleroMMA7361.h>

AcceleroMMA7361 accelero;
int tx=1;
int rx=0;
char inSerial[15];
char* currentOP;

int currentWeight = 0;

void setup(){
  //BLUETOOTH
  Serial.begin(9600);
  pinMode(tx, OUTPUT);
  pinMode(rx, INPUT);
  //ACCEL
  accelero.begin(2,3,4,5,A0,A1,A2);
  accelero.setARefVoltage(5);                   //sets the AREF voltage to 3.3V
  accelero.setSensitivity(LOW);                   //sets the sensitivity to +/-6G
  accelero.calibrate();
}

void loop(){
    int i=0;
    int m=0;
    delay(500);                                         
    if (Serial.available() > 0) {             
       while (Serial.available() > 0) {
         inSerial[i]=Serial.read(); 
         i++;      
       }
       inSerial[i]='\0';
      Check_Protocol(inSerial);
     }
     if(!strcmp(currentOP,"start")){
      sendZ_Accel();
    }
}

void setWeight(int val){
  currentWeight = val;
}
  
void Check_Protocol(char inStr[]){   
  int i=0;
  int m=0;
  //Serial.println(inStr);

  
  
  if(!strcmp(inStr,"start")){
    currentOP = "start";

    for(m=0;m<11;m++){
      inStr[m]=0;
    }
    i=0;
  }

  else if(!strcmp(inStr,"stop")){
    currentOP = "stop";
    for(m=0;m<11;m++){
      inStr[m]=0;
    }
    i=0;
  }
       
  else{
    for(m=0;m<11;m++){
      inStr[m]=0;
    }
    i=0;

  }
}

void sendZ_Accel(){
  int count = 0;
  int maxZ = 0;
  int minZ = 0;
  int z;
  
  while(count <= 500){
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
