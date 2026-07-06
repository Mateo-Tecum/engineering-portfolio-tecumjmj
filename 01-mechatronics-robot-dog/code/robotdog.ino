#include <Stepper.h>
#include <Servo.h>
#include <Adafruit_NeoPixel.h>

// ============= PIN DEFINITIONS =============
// Ultrasonic
#define TRIG_PIN 7
#define ECHO_PIN 6

// Joystick
#define JOY_X_PIN A1
#define JOY_Y_PIN A2
#define JOY_BUTTON_PIN 4

// Actuators
#define TAIL_PIN 3
#define PUMP_PIN A5
#define NEO_PIN A4

// Stepper pins (28BYJ-48 via ULN2003)
#define IN1 8
#define IN2 9
#define IN3 10
#define IN4 11

// ============= CONSTANTS =============
const int stepsPerRev = 2048;
Stepper legStepper(stepsPerRev, IN1, IN3, IN2, IN4);
Servo tailServo;
Adafruit_NeoPixel pixels(8, NEO_PIN, NEO_GRB + NEO_KHZ800);

// ============= STATE MACHINE =============
enum DogState { SLEEPING, CURIOUS, HAPPY, OVERLOAD };
DogState currentState = SLEEPING;

// ============= EXCITEMENT SYSTEM =============
int excitementLevel = 0;        // 0-100
const int EXCITEMENT_DECAY = 1; // Lose 1 point per second
unsigned long lastDecayTime = 0;

// ============= LEG CONTROL =============
int legPosition = 0;  // 0 = down, 400 = up (~70 degrees)
const int legUpPos = 400;
const int legDownPos = 0;

// ============= PUMP CONTROL =============
unsigned long lastPeeTime = 0;
const unsigned long PEE_COOLDOWN = 20000; // 20 seconds

// ============= JOYSTICK TRACKING =============
int lastJoyX = 512;
int lastJoyY = 512;
unsigned long lastJoyReadTime = 0;

void setup() {
  Serial.begin(9600);
  Serial.println("Piddles Plus Booting Up...");
  
  // Initialize pins
  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);
  pinMode(JOY_BUTTON_PIN, INPUT_PULLUP);
  pinMode(PUMP_PIN, OUTPUT);
  digitalWrite(PUMP_PIN, LOW);
  
  // Initialize stepper
  legStepper.setSpeed(10); // 10 RPM
  moveLegTo(legDownPos);   // Start with leg down
  
  // Initialize servo
  tailServo.attach(TAIL_PIN);
  tailServo.write(90);     // Neutral position
  
  // Initialize NeoPixels
  pixels.begin();
  setEyeColor(0, 0, 20);   // Dim blue (sleeping)
  
  Serial.println("Ready! Pet the joystick to wake me up!");
}

void loop() {
  unsigned long currentMillis = millis();
  
  // Read all inputs
  long distance = readUltrasonic();
  int joyActivity = readJoystick();
  bool buttonPressed = readJoystickButton();
  
  // Calculate excitement from both sources
  calculateExcitement(distance, joyActivity, buttonPressed);
  
  // Decay excitement over time (1 per second)
  if (currentMillis - lastDecayTime > 1000) {
    excitementLevel = max(0, excitementLevel - EXCITEMENT_DECAY);
    lastDecayTime = currentMillis;
  }
  
  // Update state based on excitement
  updateState();
  
  // Run current state behavior
  runStateBehavior();
  
  // Small delay for stability
  delay(30);
}

// ============= SENSOR FUNCTIONS =============
long readUltrasonic() {
  digitalWrite(TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, LOW);
  
  long duration = pulseIn(ECHO_PIN, HIGH, 30000);
  if (duration == 0) return 100; // Timeout = nothing detected
  
  long distance = duration * 0.034 / 2;
  if (distance > 100) distance = 100; // Cap at 1m
  return distance;
}

int readJoystick() {
  // Read current joystick position
  int joyX = analogRead(JOY_X_PIN);
  int joyY = analogRead(JOY_Y_PIN);
  
  // Calculate how much it moved since last read
  int movement = abs(joyX - lastJoyX) + abs(joyY - lastJoyY);
  
  // Update last position
  lastJoyX = joyX;
  lastJoyY = joyY;
  
  // Return movement amount (0-1024 range)
  return movement;
}

bool readJoystickButton() {
  // Button is active LOW (pressed = LOW)
  return !digitalRead(JOY_BUTTON_PIN);
}

void calculateExcitement(long distance, int joyMovement, bool buttonPressed) {
  // Distance factor: closer = more excitement
  if (distance < 20) {
    excitementLevel += 3;
  } else if (distance < 40) {
    excitementLevel += 2;
  } else if (distance < 70) {
    excitementLevel += 1;
  }
  
  // Joystick petting: movement = excitement
  if (joyMovement > 50) { // Significant movement
    excitementLevel += joyMovement / 100; // 50 movement = +0.5, 500 = +5
    Serial.print("Pet detected! +");
    Serial.println(joyMovement / 100);
  }
  
  // Button press = "good boy!" pat
  if (buttonPressed) {
    excitementLevel += 10;
    Serial.println("GOOD BOY! +10 excitement!");
    delay(200); // Debounce
  }
  
  // Cap excitement
  excitementLevel = min(100, excitementLevel);
}

// ============= STATE MANAGEMENT =============
void updateState() {
  DogState newState = currentState;
  
  // OVERLOAD requires excitement high AND cooldown passed
  if (excitementLevel >= 80 && millis() - lastPeeTime > PEE_COOLDOWN) {
    newState = OVERLOAD;
  } else if (excitementLevel >= 50) {
    newState = HAPPY;
  } else if (excitementLevel >= 20) {
    newState = CURIOUS;
  } else {
    newState = SLEEPING;
  }
  
  // Handle state transition
  if (newState != currentState) {
    onStateEnter(newState);
    currentState = newState;
  }
}

void onStateEnter(DogState state) {
  switch(state) {
    case SLEEPING:
      setEyeColor(0, 0, 20);    // Dim blue
      moveLegTo(legDownPos);
      Serial.println("State: SLEEPING");
      break;
      
    case CURIOUS:
      setEyeColor(0, 50, 0);    // Green
      Serial.println("State: CURIOUS");
      break;
      
    case HAPPY:
      setEyeColor(50, 50, 0);   // Yellow
      Serial.println("State: HAPPY");
      break;
      
    case OVERLOAD:
      setEyeColor(50, 0, 0);    // Red
      moveLegTo(legUpPos);
      activatePump();
      Serial.println("State: OVERLOAD - HERE IT COMES!");
      break;
  }
}

// ============= BEHAVIORS =============
void runStateBehavior() {
  switch(currentState) {
    case SLEEPING:
      sleepingBehavior();
      break;
    case CURIOUS:
      curiousBehavior();
      break;
    case HAPPY:
      happyBehavior();
      break;
    case OVERLOAD:
      overloadBehavior();
      break;
  }
}

void sleepingBehavior() {
  // Occasional tiny tail twitch
  static unsigned long lastTwitch = 0;
  if (millis() - lastTwitch > 3000) {
    tailServo.write(85);
    delay(100);
    tailServo.write(95);
    lastTwitch = millis();
  }
}

void curiousBehavior() {
  // Slow wag - speed based on excitement level
  static int angle = 70;
  static int direction = 1;
  int speed = map(excitementLevel, 20, 50, 2, 4);
  
  angle += direction * speed;
  if (angle > 110) direction = -1;
  if (angle < 70) direction = 1;
  
  tailServo.write(angle);
  delay(30);
}

void happyBehavior() {
  // Fast wag - speed based on excitement level
  static int angle = 50;
  static int direction = 1;
  int speed = map(excitementLevel, 50, 80, 5, 8);
  
  angle += direction * speed;
  if (angle > 130) direction = -1;
  if (angle < 50) direction = 1;
  
  tailServo.write(angle);
  delay(15);
}

void overloadBehavior() {
  // MAXIMUM WAG!
  static int angle = 30;
  static int direction = 1;
  
  angle += direction * 10;
  if (angle > 150) direction = -1;
  if (angle < 30) direction = 1;
  
  tailServo.write(angle);
  delay(8);
  
  // After peeing, return to HAPPY
  if (millis() - lastPeeTime > 5000) { // 5 seconds after pee
    currentState = HAPPY;
    moveLegTo(legDownPos);
  }
}

// ============= LEG CONTROL =============
void moveLegTo(int target) {
  static unsigned long lastStep = 0;
  int stepsToMove = target - legPosition;
  
  if (stepsToMove != 0 && millis() - lastStep > 100) {
    int direction = (stepsToMove > 0) ? 1 : -1;
    legStepper.step(direction);
    legPosition += direction;
    lastStep = millis();
    
    Serial.print("Leg moving: ");
    Serial.println(legPosition);
  }
}

// ============= PUMP CONTROL =============
void activatePump() {
  if (millis() - lastPeeTime > PEE_COOLDOWN) {
    digitalWrite(PUMP_PIN, HIGH);
    delay(600); // Adjust for desired volume
    digitalWrite(PUMP_PIN, LOW);
    lastPeeTime = millis();
    
    // Celebration blink!
    for(int i = 0; i < 3; i++) {
      setEyeColor(50, 0, 50); // Purple
      delay(150);
      setEyeColor(50, 0, 0);  // Red
      delay(150);
    }
  }
}

// ============= EYE CONTROL =============
void setEyeColor(int r, int g, int b) {
  for(int i = 0; i < 8; i++) {
    pixels.setPixelColor(i, pixels.Color(r, g, b));
  }
  pixels.show();
}