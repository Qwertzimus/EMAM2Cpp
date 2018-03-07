#ifndef TEST_MATH_MATRIXASSIGNMENTTEST
#define TEST_MATH_MATRIXASSIGNMENTTEST
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "octave/oct.h"
#include "Helper.h"
#include "octave/builtin-defun-decls.h"
class test_math_matrixAssignmentTest{
public:
void init()
{
}
void execute()
{
Matrix matA = (Helper::getMatrixFromOctaveListFirstResult(Fones(Helper::convertToOctaveValueList(2, 2),1)));
Matrix matB = (Helper::getMatrixFromOctaveListFirstResult(Fones(Helper::convertToOctaveValueList(2, 2),1)));
matA(1-1, 1-1) = matB(1-1, 1-1);
}

};
#endif
