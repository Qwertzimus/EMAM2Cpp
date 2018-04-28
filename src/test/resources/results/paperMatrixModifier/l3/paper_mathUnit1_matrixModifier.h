#ifndef PAPER_MATHUNIT1_MATRIXMODIFIER
#define PAPER_MATHUNIT1_MATRIXMODIFIER
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "octave/oct.h"
#include <thread>
class paper_mathUnit1_matrixModifier{
public:
Matrix mat1;
Matrix mat2;
Matrix mat3;
Matrix mat4;
Matrix mat5;
Matrix matOut;
void init()
{
mat1=Matrix(1000,2);
mat2=Matrix(2,1000);
mat3=Matrix(1000,2);
mat4=Matrix(2,10000);
mat5=Matrix(10000,10000);
matOut=Matrix(1000,10000);
}
void execute()
{
matOut = (mat1*(mat2*mat3))*(mat4*mat5);
}

};
#endif
