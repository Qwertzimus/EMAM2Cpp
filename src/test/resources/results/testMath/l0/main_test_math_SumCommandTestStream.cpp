#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#include "Helper.h"
#include "test_math_sumCommandTest.h"
int main(int argc, char** argv)
{
Helper::init();
test_math_sumCommandTest testInstance;
testInstance.init();
testInstance.execute();
if(testInstance.out1!=6/1){printf("Mismatch at executionStep 0");
}
octave_quit();
printf("Execution ended successfully!\n");
}
