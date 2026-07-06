volatile int encoderCount = 0;
const int ENC_A = 2;
const int ENC_B = 3;
const int LED_PINS[] = {4, 5, 6, 7};
const int FULL_TURN_COUNTS = 96;  // 24 PPR × 4 quadrature counts
const int COUNTS_PER_BINARY_STEP = 6;  // 96 counts ÷ 16 binary steps = 6
int previousCount = 0;

void setup() {
  Serial.begin(9600);
  pinMode(ENC_A, INPUT);
  pinMode(ENC_B, INPUT);

  for (int i = 0; i < 4; i++) {
    pinMode(LED_PINS[i], OUTPUT);
    digitalWrite(LED_PINS[i], LOW);
  }
  
  attachInterrupt(digitalPinToInterrupt(ENC_A), encoderISR, CHANGE);
  attachInterrupt(digitalPinToInterrupt(ENC_B), encoderISR, CHANGE);
}

void loop() {
  static int lastDisplayedCount = 0;
  int currentCount;
  
  noInterrupts();
  currentCount = encoderCount;
  interrupts();
  
  if (currentCount != lastDisplayedCount) {
    Serial.print("Encoder count: ");
    Serial.print(currentCount);
    Serial.print(" | Binary count: ");
    
    // Calculate binary count (0-15) with saturation
    int binaryCount;
    
    // Apply saturation limits
    if (currentCount >= FULL_TURN_COUNTS) {
      binaryCount = 15;  // Saturate at max (full turn)
    } 
    else if (currentCount <= 0) {
      binaryCount = 0;   // Saturate at min
    }
    else {
      // For counts between 0 and FULL_TURN_COUNTS
      // Change binary every 6 counts (96/16 = 6)
      binaryCount = currentCount / COUNTS_PER_BINARY_STEP;
      
      // Should never exceed 15 due to saturation above, but just in case
      if (binaryCount > 15) {
        binaryCount = 15;
      }
    }
    
    Serial.println(binaryCount);
    
    // Update LEDs based on binaryCount
    for (int i = 0; i < 4; i++) {
      int ledState = (binaryCount >> i) & 0x01;
      digitalWrite(LED_PINS[i], ledState ? HIGH : LOW);
    }
    
    lastDisplayedCount = currentCount;
  }
  
  delay(10);
}

void encoderISR() {
  static int8_t oldState = 0;
  int8_t state = (digitalRead(ENC_A) << 1) | digitalRead(ENC_B);
  
  if (oldState != state) {
    if ((oldState == 0 && state == 1) ||
        (oldState == 1 && state == 3) ||
        (oldState == 3 && state == 2) ||
        (oldState == 2 && state == 0)) {
      encoderCount++;
    }
    else if ((oldState == 0 && state == 2) ||
             (oldState == 2 && state == 3) ||
             (oldState == 3 && state == 1) ||
             (oldState == 1 && state == 0)) {
      encoderCount--;
    }
    oldState = state;
  }
}
