#ifndef __${viewModel.callIpoptName?upper_case}_H__
#define __${viewModel.callIpoptName?upper_case}_H__

int solveOptimizationProblemIpOpt(${viewModel.optimizationVariableType} &x, double &y);

#endif