#ifndef TEST_MATH_ASINCOMMANDTEST
#define TEST_MATH_ASINCOMMANDTEST
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "armadillo.h"
using namespace arma;
class test_math_asinCommandTest{
public:
void init()
{
}
void execute()
{
double a = (asin(0.5));
}

};
#endif
