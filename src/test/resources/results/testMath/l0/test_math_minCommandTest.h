#ifndef TEST_MATH_MINCOMMANDTEST
#define TEST_MATH_MINCOMMANDTEST
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "octave/oct.h"
#include "Helper.h"
#include "octave/builtin-defun-decls.h"
class test_math_minCommandTest{
public:
void init()
{
}
void execute()
{
double a = (Helper::getDoubleFromOctaveListFirstResult(Fmin(Helper::convertToOctaveValueList(15, 4),1)));
}

};
#endif
