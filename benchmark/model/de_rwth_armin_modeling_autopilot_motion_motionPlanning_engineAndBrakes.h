#ifndef DE_RWTH_ARMIN_MODELING_AUTOPILOT_MOTION_MOTIONPLANNING_ENGINEANDBRAKES
#define DE_RWTH_ARMIN_MODELING_AUTOPILOT_MOTION_MOTIONPLANNING_ENGINEANDBRAKES
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "octave/oct.h"
#include "de_rwth_armin_modeling_autopilot_motion_motionPlanning_engineAndBrakes_pidParams.h"
#include "de_rwth_armin_modeling_autopilot_motion_motionPlanning_engineAndBrakes_pidError.h"
#include "de_rwth_armin_modeling_autopilot_motion_motionPlanning_engineAndBrakes_abs1.h"
#include "de_rwth_armin_modeling_autopilot_motion_motionPlanning_engineAndBrakes_pid.h"
#include "de_rwth_armin_modeling_autopilot_motion_motionPlanning_engineAndBrakes_engineOrBrakes.h"
class de_rwth_armin_modeling_autopilot_motion_motionPlanning_engineAndBrakes{
public:
double currentVelocity;
double desiredVelocity;
double engine;
double brakes;
de_rwth_armin_modeling_autopilot_motion_motionPlanning_engineAndBrakes_pidParams pidParams;
de_rwth_armin_modeling_autopilot_motion_motionPlanning_engineAndBrakes_pidError pidError;
de_rwth_armin_modeling_autopilot_motion_motionPlanning_engineAndBrakes_abs1 abs1;
de_rwth_armin_modeling_autopilot_motion_motionPlanning_engineAndBrakes_pid pid;
de_rwth_armin_modeling_autopilot_motion_motionPlanning_engineAndBrakes_engineOrBrakes engineOrBrakes;
void init()
{
pidParams.init();
pidError.init();
abs1.init();
pid.init();
engineOrBrakes.init();
}
void execute()
{
pidParams.currentVelocity = currentVelocity;
pidParams.desiredVelocity = desiredVelocity;
pidParams.execute();
pidError.currentVelocity = currentVelocity;
pidError.desiredVelocity = desiredVelocity;
pidError.execute();
abs1.input = pidError.error;
abs1.execute();
pid.paramP = pidParams.paramP;
pid.paramI = pidParams.paramI;
pid.paramD = pidParams.paramD;
pid.paramDecayCoefficient = pidParams.paramDecayCoefficient;
pid.error = abs1.output;
pid.execute();
engineOrBrakes.error = pidError.error;
engineOrBrakes.controlSignal = pid.control;
engineOrBrakes.execute();
engine = engineOrBrakes.engine;
brakes = engineOrBrakes.brakes;
}

};
#endif
