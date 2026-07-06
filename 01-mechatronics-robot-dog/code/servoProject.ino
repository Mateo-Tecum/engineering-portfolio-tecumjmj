#include <Servo.h>
#include <math.h>

const int POT_PIN = A0;
const int SERVO_PIN = 9;

// Servo calibration - ADJUST THESE!
const int PULSE_MIN = 1000;
const int PULSE_MAX = 2000;
const int PULSE_MID = 1500;
const int SWEEP_AMPLITUDE = 400;

// Frequency sweep range
const float FREQ_MIN = 0.1;
const float FREQ_MAX = 10.0;

// Timing
unsigned long lastTime = 0;
float phase = 0.0;
float currentFrequency = 1.0;

// For amplitude tracking
int maxPulse = PULSE_MID;
int minPulse = PULSE_MID;
unsigned long lastResetTime = 0;
const int RESET_INTERVAL = 2000;  // Reset min/max every 2 seconds

Servo myServo;

void setup() {
  Serial.begin(115200);
  myServo.attach(SERVO_PIN, PULSE_MIN, PULSE_MAX);
  
  Serial.println("\n\n========================================");
  Serial.println("    SERVO BANDWIDTH MEASUREMENT");
  Serial.println("========================================");
  Serial.println("Potentiometer controls oscillation frequency");
  Serial.println("Observe when amplitude visibly decreases");
  Serial.println("\nFreq(Hz)\tActual Sweep\t\tTracking?");
  Serial.println("--------\t------------\t\t---------");
}

void loop() {
  // Read frequency command
  int potValue = analogRead(POT_PIN);
  currentFrequency = map(potValue, 0, 1023, FREQ_MIN * 100, FREQ_MAX * 100) / 100.0;
  
  // Update phase
  unsigned long now = micros();
  float dt = (now - lastTime) / 1000000.0;
  lastTime = now;
  
  phase += 2.0 * PI * currentFrequency * dt;
  if (phase > 2.0 * PI) phase -= 2.0 * PI;
  
  // Compute and send command
  int commandPulse = PULSE_MID + SWEEP_AMPLITUDE * sin(phase);
  commandPulse = constrain(commandPulse, PULSE_MIN, PULSE_MAX);
  myServo.writeMicroseconds(commandPulse);
  
  // Track actual achieved positions (for bandwidth detection)
  if (commandPulse > maxPulse) maxPulse = commandPulse;
  if (commandPulse < minPulse) minPulse = commandPulse;
  
  // Report every 2 seconds
  if (millis() - lastResetTime > RESET_INTERVAL) {
    int actualSweep = maxPulse - minPulse;
    int commandedSweep = 2 * SWEEP_AMPLITUDE;
    float trackingRatio = (float)actualSweep / commandedSweep;
    
    Serial.print(currentFrequency, 2);
    Serial.print(" Hz\t\t");
    Serial.print(actualSweep);
    Serial.print(" µs\t\t");
    
    if (trackingRatio > 0.9) {
      Serial.println("GOOD");
    } else if (trackingRatio > 0.7) {
      Serial.println("REDUCED");
    } else if (trackingRatio > 0.3) {
      Serial.println("POOR");
    } else {
      Serial.println("FAILED");
    }
    
    // Reset tracking
    maxPulse = PULSE_MID;
    minPulse = PULSE_MID;
    lastResetTime = millis();
  }
  
  delay(10);
