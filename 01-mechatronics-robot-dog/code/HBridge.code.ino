// ----- Pin definitions -----
const int potPin = A0;

// High-side PMOS control pins (through 10k/2k voltage divider)
const int leftHighCtrl  = 2;   // LOW = left PMOS ON, HIGH = OFF
const int rightHighCtrl = 3;   // LOW = right PMOS ON, HIGH = OFF

// Low-side NMOS pins (direct from Arduino)
const int leftLow  = 5;        // bottom-left NMOS (PWM capable)
const int rightLow = 6;        // bottom-right NMOS (PWM capable)

// ----- Tuning values -----
const int deadbandLow  = 470;
const int deadbandHigh = 550;

void setup() {
  pinMode(leftHighCtrl, OUTPUT);
  pinMode(rightHighCtrl, OUTPUT);
  pinMode(leftLow, OUTPUT);
  pinMode(rightLow, OUTPUT);

  // Initialize all outputs to OFF state
  digitalWrite(leftHighCtrl, HIGH);   // PMOS OFF (HIGH = off due to divider)
  digitalWrite(rightHighCtrl, HIGH);  // PMOS OFF
  analogWrite(leftLow, 0);
  analogWrite(rightLow, 0);

  Serial.begin(9600);
  Serial.println("H-Bridge Motor Control Started");
}

void loop() {
  int potValue = analogRead(potPin);

  Serial.print("Potentiometer: ");
  Serial.print(potValue);
  Serial.print("  |  ");

  if (potValue >= deadbandLow && potValue <= deadbandHigh) {
    // Dead zone - motor stopped (coast mode)
    Serial.println("STOP");
    stopMotor();
  }
  else if (potValue > deadbandHigh) {
    // Clockwise direction
    int pwmValue = map(potValue, deadbandHigh, 1023, 0, 255);
    pwmValue = constrain(pwmValue, 0, 255);
    Serial.print("CW  |  PWM: ");
    Serial.println(pwmValue);
    driveClockwise(pwmValue);
  }
  else {
    // Counter-clockwise direction
    int pwmValue = map(potValue, 0, deadbandLow, 255, 0);
    pwmValue = constrain(pwmValue, 0, 255);
    Serial.print("CCW |  PWM: ");
    Serial.println(pwmValue);
    driveCounterClockwise(pwmValue);
  }

  delay(50);  // Small delay for stability
}

// Drive motor clockwise (forward)
void driveClockwise(int pwmValue) {
  // Diagonal 1: Left-High (PMOS) ON + Right-Low (NMOS) PWM
  
  digitalWrite(leftHighCtrl, LOW);   // Left PMOS ON (LOW through divider)
  digitalWrite(rightHighCtrl, HIGH); // Right PMOS OFF
  analogWrite(leftLow, 0);           // Left low-side OFF
  analogWrite(rightLow, pwmValue);   // Right low-side PWM
}

// Drive motor counter-clockwise (reverse)
void driveCounterClockwise(int pwmValue) {
  // Diagonal 2: Right-High (PMOS) ON + Left-Low (NMOS) PWM
  
  digitalWrite(rightHighCtrl, LOW);  // Right PMOS ON (LOW through divider)
  digitalWrite(leftHighCtrl, HIGH);  // Left PMOS OFF
  analogWrite(rightLow, 0);          // Right low-side OFF
  analogWrite(leftLow, pwmValue);    // Left low-side PWM
}

// Stop motor - all MOSFETs OFF (coast mode)
void stopMotor() {
  digitalWrite(leftHighCtrl, HIGH);  // Left PMOS OFF
  digitalWrite(rightHighCtrl, HIGH); // Right PMOS OFF
  analogWrite(leftLow, 0);           // Left low-side OFF
  analogWrite(rightLow, 0);          // Right low-side OFF
}