#ifndef TEST_MATH_SINHCOMMANDTEST
#define TEST_MATH_SINHCOMMANDTEST
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "octave/oct.h"
#include "Helper.h"
#include "octave/builtin-defun-decls.h"
class test_math_sinhCommandTest{
public:
void init()
{
}
void execute()
{
double a = (Helper::getDoubleFromOctaveListFirstResult(Fsinh(Helper::convertToOctaveValueList(1/2 ),1)));
}

};
#endif
