#include <Adafruit_GFX.h>    // Core graphics library
#include <SPI.h>
#include <Adafruit_ILI9341.h>
#include "TouchScreen.h"

// These are the four touchscreen analog pins
#define YP A2  // must be an analog pin, use "An" notation!
#define XM A3  // must be an analog pin, use "An" notation!
#define YM 8   // can be a digital pin
#define XP 9   // can be a digital pin

// This is calibration data for the raw touch data to the screen coordinates
#define TS_MINX 150
#define TS_MINY 120
#define TS_MAXX 920
#define TS_MAXY 940

#define MINPRESSURE 10
#define MAXPRESSURE 1000

// The display uses hardware SPI, plus #9 & #10
#define TFT_CS 13
#define TFT_DC 12
Adafruit_ILI9341 tft = Adafruit_ILI9341(TFT_CS, TFT_DC);

TouchScreen ts = TouchScreen(XP, YP, XM, YM, 300);
unsigned long last_gesture_time = millis();
int last_gesture_x;
int last_gesture_y;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  
  Serial.println(F("All set up!"));

  tft.begin();
  tft.fillScreen(ILI9341_BLACK);
  
}

void loop() {
  // put your main code here, to run repeatedly:
  TSPoint p = ts.getPoint();
  int delta_x;
  int delta_y;

  // we have some minimum pressure we consider 'valid'
  // pressure of 0 means no pressing!
  if (p.z < MINPRESSURE || p.z > MAXPRESSURE) {
     return;
  }

  unsigned long current_gesture_time = millis();
  p.x = map(p.x, TS_MINX, TS_MAXX, 0, tft.width());
  p.y = map(p.y, TS_MINY, TS_MAXY, 0, tft.height());

  /*
  Serial.print("("); Serial.print(p.x);
  Serial.print(", "); Serial.print(p.y);
  Serial.println(")");
  */

  //Serial.println(last_gesture);
  //Serial.println(current_gesture);

  // is it a continuing or new gesture?
  if (current_gesture_time - last_gesture_time > 100) {
    // new gesture 
    Serial.println("new gesture");
  } else {
      // continuing gesture
      delta_x = p.x - last_gesture_x;
      delta_y = p.y - last_gesture_y;

      Serial.print("delta x:");
      Serial.println(delta_x);
      Serial.print("delta y:");
      Serial.println(delta_y);
  }

  last_gesture_time = current_gesture_time;
  last_gesture_x = p.x;
  last_gesture_y = p.y;

  delay(10);

}
