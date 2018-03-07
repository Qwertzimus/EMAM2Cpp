#ifndef DE_RWTH_ARMIN_MODELING_AUTOPILOT_MOTION_MOTIONPLANNING_BOUNDSBRAKES_EB
#define DE_RWTH_ARMIN_MODELING_AUTOPILOT_MOTION_MOTIONPLANNING_BOUNDSBRAKES_EB
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "octave/oct.h"
class de_rwth_armin_modeling_autopilot_motion_motionPlanning_boundsBrakes_eb{
public:
double lowerBound;
double upperBound;
double input;
double output;
void init()
{
}
void execute()
{
if((input < lowerBound)){
output = lowerBound;
}
else if((input > upperBound)){
output = upperBound;
}
else {
output = input;
}
}

};
#endif
