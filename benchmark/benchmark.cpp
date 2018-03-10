#ifndef BENCHMARK
#define BENCHMARK

#include "./model/Helper.h"
#include "./model/de_rwth_armin_modeling_autopilot_motion_motionPlanning.h"
#include <iostream>
#include <chrono>

void execute(de_rwth_armin_modeling_autopilot_motion_motionPlanning&, double&, double&);

// https://stackoverflow.com/a/19471595/1432357
class Timer
{
public:
    Timer() : beg_(clock_::now()) {}
    void reset() { beg_ = clock_::now(); }
    double elapsed() const {
        return std::chrono::duration_cast<second_>
            (clock_::now() - beg_).count(); }

private:
    typedef std::chrono::high_resolution_clock clock_;
    typedef std::chrono::duration<double, std::ratio<1> > second_;
    std::chrono::time_point<clock_> beg_;
};

int main () {
  std::cout << "initialization...\n";
  Helper::init();
  de_rwth_armin_modeling_autopilot_motion_motionPlanning motionPlanning;
  motionPlanning.init();
  const int N = 1000000;
  double _currentDirectionX = 0.0;
  double _desiredDirectionX = 1.0;
  // warm up, load DLLs if needed
  execute(motionPlanning, _currentDirectionX, _desiredDirectionX);
  std::cout << "running benchmark\n";
  Timer tmr;
  for (int i=0; i<N; i++) {
    execute(motionPlanning, _currentDirectionX, _desiredDirectionX);
  }
  double t = tmr.elapsed();
  double avgDuration = t / N;
  std::cout << "avgDuration = " << avgDuration << " sec\n";
  return 0;
}

void execute(de_rwth_armin_modeling_autopilot_motion_motionPlanning& cmp,
        double& _currentDirectionX,
        double& _desiredDirectionX) {
    cmp.currentDirectionX = _currentDirectionX;
    cmp.currentDirectionY = 1.0;
    cmp.desiredDirectionX = _desiredDirectionX;
    cmp.desiredDirectionY = 1.0;
    cmp.signedDistanceToTrajectory = 0.15;
    cmp.currentVelocity = 10.0;
    cmp.desiredVelocity = 11.0;
    cmp.execute();
    _currentDirectionX = cmp.steering;
    _desiredDirectionX = cmp.brakes;
}

#endif
