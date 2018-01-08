#ifndef DETECTION_OBJECTDETECTOR9_SPECTRALCLUSTERER_1__NORMALIZEDLAPLACIAN
#define DETECTION_OBJECTDETECTOR9_SPECTRALCLUSTERER_1__NORMALIZEDLAPLACIAN
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "armadillo.h"
#include "HelperA.h"
#include <thread>
using namespace arma;
class detection_objectDetector9_spectralClusterer_1__normalizedLaplacian{
const int n = 2500;
public:
mat degree;
mat W;
mat nLaplacian;
void init()
{
degree=mat(n,n);
W=mat(n,n);
nLaplacian=mat(n,n);
}
void execute()
{
nLaplacian = (HelperA::getSqrtMat((inv(degree))))*W*(HelperA::getSqrtMat((inv(degree))));
}

};
#endif
