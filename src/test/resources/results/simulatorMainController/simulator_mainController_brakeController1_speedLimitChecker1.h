#ifndef SIMULATOR_MAINCONTROLLER_BRAKECONTROLLER1_SPEEDLIMITCHECKER1
#define SIMULATOR_MAINCONTROLLER_BRAKECONTROLLER1_SPEEDLIMITCHECKER1
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
class simulator_mainController_brakeController1_speedLimitChecker1{
public:
double currentVelocity;
double currentSpeedLimit;
bool speedLimitSurpassed;
void init()
{
}
void execute()
{
if(((currentVelocity > currentSpeedLimit))){
speedLimitSurpassed = true;
}
else {
speedLimitSurpassed = false;
}
}

};
#endif
