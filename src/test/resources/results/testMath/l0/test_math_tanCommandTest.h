#ifndef TEST_MATH_TANCOMMANDTEST
#define TEST_MATH_TANCOMMANDTEST
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "octave/oct.h"
#include "Helper.h"
#include "octave/builtin-defun-decls.h"
class test_math_tanCommandTest{
public:
void init()
{
}
void execute()
{
double a = (Helper::getDoubleFromOctaveListFirstResult(Ftan(Helper::convertToOctaveValueList(0.5),1)));
}

};
#endif
