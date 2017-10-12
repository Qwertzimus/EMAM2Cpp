#ifndef SIMULATOR_MAINCONTROLLER_BRAKECONTROLLER1
#define SIMULATOR_MAINCONTROLLER_BRAKECONTROLLER1
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "simulator_mainController_brakeController1_speedLimitChecker1.h"
class simulator_mainController_brakeController1{
public:
double currentSpeedLimit;
double currentVelocity;
double brakeForce;
simulator_mainController_brakeController1_speedLimitChecker1 speedLimitChecker1;
void init()
{
speedLimitChecker1.init();
}
void execute()
{
speedLimitChecker1.currentSpeedLimit = currentSpeedLimit;
speedLimitChecker1.currentVelocity = currentVelocity;
speedLimitChecker1.execute();
if((speedLimitChecker1.speedLimitSurpassed)){
brakeForce = 1/2 ;
}
else {
brakeForce = 0/1 ;
}
}

};
#endif
