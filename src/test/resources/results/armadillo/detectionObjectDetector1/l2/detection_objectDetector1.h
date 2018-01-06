#ifndef DETECTION_OBJECTDETECTOR1
#define DETECTION_OBJECTDETECTOR1
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "armadillo.h"
#include "detection_objectDetector1_spectralClusterer.h"
#include <thread>
using namespace arma;
class detection_objectDetector1{
public:
mat imgFront;
mat clusters;
detection_objectDetector1_spectralClusterer spectralClusterer;
void init()
{
imgFront=mat(2500,3);
clusters=mat(2500,1);
spectralClusterer.init();
}
void execute()
{
spectralClusterer.imgMatrix = imgFront;
std::thread thread1( [ this ] {this->spectralClusterer.execute();});
thread1.join();
clusters = spectralClusterer.clusters;
}

};
#endif
