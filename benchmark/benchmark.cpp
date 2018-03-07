#ifndef BENCHMARK
#define BENCHMARK

#include "./model/Helper.h"
#include "./model/de_rwth_armin_modeling_autopilot_motion_motionPlanning.h"
#include <iostream>
#include <chrono>

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
  std::cout << "running benchmark\n";
  Timer tmr;
  for (int i=0; i<N; i++) {
    motionPlanning.currentDirectionX = _currentDirectionX;
    motionPlanning.currentDirectionY = 1.0;
    motionPlanning.desiredDirectionX = _desiredDirectionX;
    motionPlanning.desiredDirectionY = 1.0;
    motionPlanning.signedDistanceToTrajectory = 0.15;
    motionPlanning.currentVelocity = 10.0;
    motionPlanning.desiredVelocity = 11.0;
    motionPlanning.execute();
    _currentDirectionX = motionPlanning.steering;
    _desiredDirectionX = motionPlanning.brakes;
  }
  double t = tmr.elapsed();
  double avgDuration = t / N;
  std::cout << "avgDuration = " << avgDuration << " sec\n";
  return 0;
}

#endif
