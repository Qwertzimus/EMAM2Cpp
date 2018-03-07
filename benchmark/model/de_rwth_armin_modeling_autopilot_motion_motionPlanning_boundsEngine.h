#ifndef DE_RWTH_ARMIN_MODELING_AUTOPILOT_MOTION_MOTIONPLANNING_BOUNDSENGINE
#define DE_RWTH_ARMIN_MODELING_AUTOPILOT_MOTION_MOTIONPLANNING_BOUNDSENGINE
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "octave/oct.h"
#include "de_rwth_armin_modeling_autopilot_motion_motionPlanning_boundsEngine_eb.h"
class de_rwth_armin_modeling_autopilot_motion_motionPlanning_boundsEngine{
public:
double input;
double output;
double CONSTANTPORT5;
double CONSTANTPORT6;
de_rwth_armin_modeling_autopilot_motion_motionPlanning_boundsEngine_eb eb;
void init()
{
this->CONSTANTPORT5 = 0;
this->CONSTANTPORT6 = 2.5;
eb.init();
}
void execute()
{
eb.lowerBound = CONSTANTPORT5;
eb.upperBound = CONSTANTPORT6;
eb.input = input;
eb.execute();
output = eb.output;
}

};
#endif
