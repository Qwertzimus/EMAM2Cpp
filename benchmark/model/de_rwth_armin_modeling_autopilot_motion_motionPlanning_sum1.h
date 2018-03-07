#ifndef DE_RWTH_ARMIN_MODELING_AUTOPILOT_MOTION_MOTIONPLANNING_SUM1
#define DE_RWTH_ARMIN_MODELING_AUTOPILOT_MOTION_MOTIONPLANNING_SUM1
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "octave/oct.h"
class de_rwth_armin_modeling_autopilot_motion_motionPlanning_sum1{
public:
double t1;
double t2;
double result;
void init()
{
}
void execute()
{
result = t1+t2;
}

};
#endif
