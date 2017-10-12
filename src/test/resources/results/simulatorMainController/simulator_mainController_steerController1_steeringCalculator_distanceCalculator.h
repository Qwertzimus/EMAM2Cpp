#ifndef SIMULATOR_MAINCONTROLLER_STEERCONTROLLER1_STEERINGCALCULATOR_DISTANCECALCULATOR
#define SIMULATOR_MAINCONTROLLER_STEERCONTROLLER1_STEERINGCALCULATOR_DISTANCECALCULATOR
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "octave/oct.h"
#include "Helper.h"
#include "octave/builtin-defun-decls.h"
class simulator_mainController_steerController1_steeringCalculator_distanceCalculator{
public:
double x[2];
double y[2];
double gpsX;
double gpsY;
double distance;
void init()
{
}
void execute()
{
double res = (y[2/1 -1]-y[1/1 -1])*gpsX;
res -= (x[2/1 -1]-x[1/1 -1])*gpsY;
res += x[2/1 -1]*y[1/1 -1];
res -= y[2/1 -1]*x[1/1 -1];
double xDiff = x[1/1 -1]-x[2/1 -1];
double yDiff = y[1/1 -1]-y[2/1 -1];
res /= (Helper::getDoubleFromOctaveListFirstResult(Fsqrt(Helper::convertToOctaveValueList(xDiff*xDiff+yDiff*yDiff),1)));
distance = res;
}

};
#endif
