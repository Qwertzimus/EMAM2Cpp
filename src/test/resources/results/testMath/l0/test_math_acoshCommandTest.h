#ifndef TEST_MATH_ACOSHCOMMANDTEST
#define TEST_MATH_ACOSHCOMMANDTEST
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "octave/oct.h"
#include "Helper.h"
#include "octave/builtin-defun-decls.h"
class test_math_acoshCommandTest{
public:
void init()
{
}
void execute()
{
double a = (Helper::getDoubleFromOctaveListFirstResult(Facosh(Helper::convertToOctaveValueList(0.5),1)));
}

};
#endif
