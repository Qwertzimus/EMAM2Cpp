#ifndef OPTIMIZER_COMPLEXMATRIXLEFTMULTIPLICATIONADDITION2
#define OPTIMIZER_COMPLEXMATRIXLEFTMULTIPLICATIONADDITION2
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "octave/oct.h"
class optimizer_complexMatrixLeftMultiplicationAddition2{
public:
Matrix mat1;
Matrix mat2;
Matrix mat4;
double factor;
Matrix matOut;
void init()
{
mat1=Matrix(1000,200);
mat2=Matrix(200,200);
mat4=Matrix(200,200);
matOut=Matrix(1000,200);
}
void execute()
{
Matrix a = mat1;
Matrix b = mat1;
Matrix c = a;
Matrix matd = b;
matOut = mat1*(mat2+mat4);
}

};
#endif
