#ifndef DE_RWTH_ARMIN_MODELING_AUTOPILOT_MOTION_MOTIONPLANNING_KEEPDIRECTION
#define DE_RWTH_ARMIN_MODELING_AUTOPILOT_MOTION_MOTIONPLANNING_KEEPDIRECTION
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "octave/oct.h"
#include "de_rwth_armin_modeling_autopilot_motion_motionPlanning_keepDirection_sab.h"
class de_rwth_armin_modeling_autopilot_motion_motionPlanning_keepDirection{
public:
double currentDirectionX;
double currentDirectionY;
double desiredDirectionX;
double desiredDirectionY;
double steeringAngle;
de_rwth_armin_modeling_autopilot_motion_motionPlanning_keepDirection_sab sab;
void init()
{
sab.init();
}
void execute()
{
sab.v1x = currentDirectionX;
sab.v1y = currentDirectionY;
sab.v2x = desiredDirectionX;
sab.v2y = desiredDirectionY;
sab.execute();
steeringAngle = sab.angle;
}

};
#endif
