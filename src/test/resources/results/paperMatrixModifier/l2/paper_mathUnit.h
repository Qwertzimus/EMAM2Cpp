#ifndef PAPER_MATHUNIT
#define PAPER_MATHUNIT
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "octave/oct.h"
#include "paper_mathUnit_matrixModifier_1_.h"
#include <thread>
class paper_mathUnit{
public:
Matrix mat1[2];
Matrix mat2[2];
Matrix mat3[2];
Matrix mat4[2];
double factor[2];
Matrix matOut1[2];
paper_mathUnit_matrixModifier_1_ matrixModifier[2];
void init()
{
mat1[0]=Matrix(1000,200);
mat1[1]=Matrix(1000,200);
mat2[0]=Matrix(1000,200);
mat2[1]=Matrix(1000,200);
mat3[0]=Matrix(200,10);
mat3[1]=Matrix(200,10);
mat4[0]=Matrix(10,100);
mat4[1]=Matrix(10,100);
matOut1[0]=Matrix(1000,100);
matOut1[1]=Matrix(1000,100);
matrixModifier[0].init();
matrixModifier[1].init();
}
void execute()
{
matrixModifier[0].mat1 = mat1[0];
matrixModifier[0].mat2 = mat2[0];
matrixModifier[0].mat3 = mat3[0];
matrixModifier[0].mat4 = mat4[0];
matrixModifier[0].factor = factor[0];
std::thread thread1( [ this ] {this->matrixModifier[0].execute();});
matrixModifier[1].mat1 = mat1[1];
matrixModifier[1].mat2 = mat2[1];
matrixModifier[1].mat3 = mat3[1];
matrixModifier[1].mat4 = mat4[1];
matrixModifier[1].factor = factor[1];
std::thread thread2( [ this ] {this->matrixModifier[1].execute();});
thread1.join();
thread2.join();
matOut1[0] = matrixModifier[0].matOut;
matOut1[1] = matrixModifier[1].matOut;
}

};
#endif
