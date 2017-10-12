#ifndef SIMULATOR_MAINCONTROLLER_STEERCONTROLLER1_STEERINGCALCULATOR
#define SIMULATOR_MAINCONTROLLER_STEERCONTROLLER1_STEERINGCALCULATOR
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "octave/oct.h"
#include "simulator_mainController_steerController1_steeringCalculator_distanceCalculator.h"
#include "Helper.h"
#include "octave/builtin-defun-decls.h"
class simulator_mainController_steerController1_steeringCalculator{
public:
double x[2];
double y[2];
double gpsX;
double gpsY;
double orientation;
double currentSteeringAngle;
double newSteeringAngle;
simulator_mainController_steerController1_steeringCalculator_distanceCalculator distanceCalculator;
void init()
{
distanceCalculator.init();
}
void execute()
{
distanceCalculator.x[0] = x[0];
distanceCalculator.x[1] = x[1];
distanceCalculator.y[0] = y[0];
distanceCalculator.y[1] = y[1];
distanceCalculator.gpsX = gpsX;
distanceCalculator.gpsY = gpsY;
distanceCalculator.execute();
double globalOrientation = orientation*(M_PI/180/1 );
if(((globalOrientation > M_PI))){
globalOrientation -= 2/1 *M_PI;
}
double orientedDistance = distanceCalculator.distance;
double angleTowardsTrajectory = (Helper::getDoubleFromOctaveListFirstResult(Fatan(Helper::convertToOctaveValueList(orientedDistance/2/1 ),1)));
double orientationOfTrajectory;
double v1 = x[2/1 -1]-x[1/1 -1];
double v2 = y[2/1 -1]-y[1/1 -1];
double cosineAngle = v2/(Helper::getDoubleFromOctaveListFirstResult(Fsqrt(Helper::convertToOctaveValueList(v1*v1+v2*v2),1)));
double angle = (Helper::getDoubleFromOctaveListFirstResult(Facos(Helper::convertToOctaveValueList(cosineAngle),1)));
if(((v1 > 0/1 ))){
orientationOfTrajectory = -angle;
}
else {
orientationOfTrajectory = angle;
}
double angleTrajectoryAndCarDirection = orientationOfTrajectory-globalOrientation;
double finalAngle = angleTrajectoryAndCarDirection+angleTowardsTrajectory;
if(((finalAngle > M_PI))){
finalAngle -= 2/1 *M_PI;
}
else if(((finalAngle < -M_PI))){
finalAngle += 2/1 *M_PI;
}
newSteeringAngle = finalAngle*(180/1 /M_PI);
}

};
#endif
