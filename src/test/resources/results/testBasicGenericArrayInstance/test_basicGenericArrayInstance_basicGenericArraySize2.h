#ifndef TEST_BASICGENERICARRAYINSTANCE_BASICGENERICARRAYSIZE2
#define TEST_BASICGENERICARRAYINSTANCE_BASICGENERICARRAYSIZE2
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "octave/oct.h"
class test_basicGenericArrayInstance_basicGenericArraySize2{
const int n = 3;
public:
double val1[3];
double valOut[3];
void init()
{
}
void execute()
{
for( auto i=1;i<=n;++i){
valOut(i) = val1[i-1]*2;
}
for( auto i=1;i<=n;++i){
valOut(i) = val1[i-1]*3;
}
}

};
#endif
