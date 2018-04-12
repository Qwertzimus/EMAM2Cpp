#ifndef __${viewModel.callIpoptName?upper_case}_H__
#define __${viewModel.callIpoptName?upper_case}_H__
#include<armadillo>

using namespace arma;

class ${viewModel.callIpoptName}
{
    public:
        static bool solveOptimizationProblemIpOpt(
            ${viewModel.optimizationVariableType} &x,
            double &y<#if 0 < viewModel.knownVariablesWithType?size>,</#if>
            <#list viewModel.knownVariablesWithType as arg>
            const ${arg}<#sep>,</#sep>
            </#list>
        );
};

#endif