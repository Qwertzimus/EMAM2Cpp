#ifndef TEST_MATH_NORMCOMMANDTEST
#define TEST_MATH_NORMCOMMANDTEST
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "armadillo.h"
using namespace arma;
class test_math_normCommandTest{
public:
mat CONSTANTCONSTANTVECTOR0;
void init()
{
CONSTANTCONSTANTVECTOR0 = mat(3,3);
CONSTANTCONSTANTVECTOR0(0,0) = 1;
CONSTANTCONSTANTVECTOR0(0,1) = 0;
CONSTANTCONSTANTVECTOR0(0,2) = 0;
CONSTANTCONSTANTVECTOR0(1,0) = 0;
CONSTANTCONSTANTVECTOR0(1,1) = 1;
CONSTANTCONSTANTVECTOR0(1,2) = 0;
CONSTANTCONSTANTVECTOR0(2,0) = 0;
CONSTANTCONSTANTVECTOR0(2,1) = 0;
CONSTANTCONSTANTVECTOR0(2,2) = 1;
}
void execute()
{
mat mat = CONSTANTCONSTANTVECTOR0;
mat a = (norm(mat));
}

};
#endif
