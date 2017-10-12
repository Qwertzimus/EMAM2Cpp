#ifndef PAPER_MATHUNIT
#define PAPER_MATHUNIT
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "octave/oct.h"
#include "paper_mathUnit_matrixModifier_1_.h"
class paper_mathUnit{
public:
Matrix mat1[10];
Matrix mat2[10];
Matrix mat3[10];
Matrix mat4[10];
double factor[10];
Matrix matOut1[10];
paper_mathUnit_matrixModifier_1_ matrixModifier[10];
void init()
{
mat1[0]=Matrix(1000,200);
mat1[1]=Matrix(1000,200);
mat1[2]=Matrix(1000,200);
mat1[3]=Matrix(1000,200);
mat1[4]=Matrix(1000,200);
mat1[5]=Matrix(1000,200);
mat1[6]=Matrix(1000,200);
mat1[7]=Matrix(1000,200);
mat1[8]=Matrix(1000,200);
mat1[9]=Matrix(1000,200);
mat2[0]=Matrix(1000,200);
mat2[1]=Matrix(1000,200);
mat2[2]=Matrix(1000,200);
mat2[3]=Matrix(1000,200);
mat2[4]=Matrix(1000,200);
mat2[5]=Matrix(1000,200);
mat2[6]=Matrix(1000,200);
mat2[7]=Matrix(1000,200);
mat2[8]=Matrix(1000,200);
mat2[9]=Matrix(1000,200);
mat3[0]=Matrix(200,10);
mat3[1]=Matrix(200,10);
mat3[2]=Matrix(200,10);
mat3[3]=Matrix(200,10);
mat3[4]=Matrix(200,10);
mat3[5]=Matrix(200,10);
mat3[6]=Matrix(200,10);
mat3[7]=Matrix(200,10);
mat3[8]=Matrix(200,10);
mat3[9]=Matrix(200,10);
mat4[0]=Matrix(10,100);
mat4[1]=Matrix(10,100);
mat4[2]=Matrix(10,100);
mat4[3]=Matrix(10,100);
mat4[4]=Matrix(10,100);
mat4[5]=Matrix(10,100);
mat4[6]=Matrix(10,100);
mat4[7]=Matrix(10,100);
mat4[8]=Matrix(10,100);
mat4[9]=Matrix(10,100);
matOut1[0]=Matrix(1000,100);
matOut1[1]=Matrix(1000,100);
matOut1[2]=Matrix(1000,100);
matOut1[3]=Matrix(1000,100);
matOut1[4]=Matrix(1000,100);
matOut1[5]=Matrix(1000,100);
matOut1[6]=Matrix(1000,100);
matOut1[7]=Matrix(1000,100);
matOut1[8]=Matrix(1000,100);
matOut1[9]=Matrix(1000,100);
matrixModifier[0].init();
matrixModifier[1].init();
matrixModifier[2].init();
matrixModifier[3].init();
matrixModifier[4].init();
matrixModifier[5].init();
matrixModifier[6].init();
matrixModifier[7].init();
matrixModifier[8].init();
matrixModifier[9].init();
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
matrixModifier[2].mat1 = mat1[2];
matrixModifier[2].mat2 = mat2[2];
matrixModifier[2].mat3 = mat3[2];
matrixModifier[2].mat4 = mat4[2];
matrixModifier[2].factor = factor[2];
std::thread thread3( [ this ] {this->matrixModifier[2].execute();});
matrixModifier[3].mat1 = mat1[3];
matrixModifier[3].mat2 = mat2[3];
matrixModifier[3].mat3 = mat3[3];
matrixModifier[3].mat4 = mat4[3];
matrixModifier[3].factor = factor[3];
std::thread thread4( [ this ] {this->matrixModifier[3].execute();});
matrixModifier[4].mat1 = mat1[4];
matrixModifier[4].mat2 = mat2[4];
matrixModifier[4].mat3 = mat3[4];
matrixModifier[4].mat4 = mat4[4];
matrixModifier[4].factor = factor[4];
std::thread thread5( [ this ] {this->matrixModifier[4].execute();});
matrixModifier[5].mat1 = mat1[5];
matrixModifier[5].mat2 = mat2[5];
matrixModifier[5].mat3 = mat3[5];
matrixModifier[5].mat4 = mat4[5];
matrixModifier[5].factor = factor[5];
std::thread thread6( [ this ] {this->matrixModifier[5].execute();});
matrixModifier[6].mat1 = mat1[6];
matrixModifier[6].mat2 = mat2[6];
matrixModifier[6].mat3 = mat3[6];
matrixModifier[6].mat4 = mat4[6];
matrixModifier[6].factor = factor[6];
std::thread thread7( [ this ] {this->matrixModifier[6].execute();});
matrixModifier[7].mat1 = mat1[7];
matrixModifier[7].mat2 = mat2[7];
matrixModifier[7].mat3 = mat3[7];
matrixModifier[7].mat4 = mat4[7];
matrixModifier[7].factor = factor[7];
std::thread thread8( [ this ] {this->matrixModifier[7].execute();});
matrixModifier[8].mat1 = mat1[8];
matrixModifier[8].mat2 = mat2[8];
matrixModifier[8].mat3 = mat3[8];
matrixModifier[8].mat4 = mat4[8];
matrixModifier[8].factor = factor[8];
std::thread thread9( [ this ] {this->matrixModifier[8].execute();});
matrixModifier[9].mat1 = mat1[9];
matrixModifier[9].mat2 = mat2[9];
matrixModifier[9].mat3 = mat3[9];
matrixModifier[9].mat4 = mat4[9];
matrixModifier[9].factor = factor[9];
std::thread thread10( [ this ] {this->matrixModifier[9].execute();});
thread1.join();
thread2.join();
thread3.join();
thread4.join();
thread5.join();
thread6.join();
thread7.join();
thread8.join();
thread9.join();
thread10.join();
matOut1[0] = matrixModifier[0].matOut;
matOut1[1] = matrixModifier[1].matOut;
matOut1[2] = matrixModifier[2].matOut;
matOut1[3] = matrixModifier[3].matOut;
matOut1[4] = matrixModifier[4].matOut;
matOut1[5] = matrixModifier[5].matOut;
matOut1[6] = matrixModifier[6].matOut;
matOut1[7] = matrixModifier[7].matOut;
matOut1[8] = matrixModifier[8].matOut;
matOut1[9] = matrixModifier[9].matOut;
}

};
#endif
