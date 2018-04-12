#include<cppad/ipopt/solve.hpp>
#include<iostream>
#include <armadillo>
#include "${viewModel.callIpoptName}.h"

namespace
{
    using CppAD::AD;
    using namespace arma;

    <#list viewModel.knownVariablesWithType as var>
    static ${var};
    </#list>

    class FG_eval {
    public:
        typedef CPPAD_TESTVECTOR(AD<double>) ADvector;

        void operator()(ADvector &fg,const ADvector &x) {
            assert (fg.size() == (${viewModel.numberConstraints?c} + 1));
            assert (x.size() == ${viewModel.numberVariables?c});

            // create active optimization var
            <#if viewModel.optimizationVariableDimensions?size == 0>
            ${viewModel.optimizationVariableTypeActive} ${viewModel.optimizationVariableName} = x[0];
            <#elseif viewModel.optimizationVariableDimensions?size == 1>
            ${viewModel.optimizationVariableTypeActive} ${viewModel.optimizationVariableName} = ${viewModel.optimizationVariableTypeActive}(${viewModel.optimizationVariableDimensions[0]});
            for (int i = 0; i < ${viewModel.optimizationVariableDimensions[0]?c}; i++)
            {
                xTmp(i) = x[i];
            }
            <#elseif viewModel.optimizationVariableDimensions?size == 2>
            ${viewModel.optimizationVariableTypeActive} ${viewModel.optimizationVariableName} = ${viewModel.optimizationVariableTypeActive}(${viewModel.optimizationVariableDimensions[0]}, ${viewModel.optimizationVariableDimensions[1]});
            for (int i = 0; i < ${viewModel.optimizationVariableDimensions[0]?c}; i++)
            {
                for (int j = 0; j < ${viewModel.optimizationVariableDimensions[1]?c}; j++)
                {
                    xTmp(i, j) = x[i + j];
                }
            }
            <#elseif viewModel.optimizationVariableDimensions?size == 3>
            ${viewModel.optimizationVariableTypeActive} ${viewModel.optimizationVariableName} = ${viewModel.optimizationVariableTypeActive}(${viewModel.optimizationVariableDimensions[0]}, ${viewModel.optimizationVariableDimensions[1]}, ${viewModel.optimizationVariableDimensions[2]});
            for (int i = 0; i < ${viewModel.optimizationVariableDimensions[0]?c}; i++)
            {
                for (int j = 0; j < ${viewModel.optimizationVariableDimensions[1]?c}; j++)
                {
                    for (int k = 0; k < ${viewModel.optimizationVariableDimensions[2]?c}; k++)
                    {
                        xTmp(i, j, k) = x[i + j + k];
                    }
                }
            }
            </#if>

            // f(x)
            int i = 0;
            <#if viewModel.optimizationProblemType.name() == "MINIMIZATION">
            fg[i] = ${viewModel.objectiveFunction};
            <#else>
            fg[i] = -1 * ( ${viewModel.objectiveFunction} );
            </#if>

            // g_i(x)
            <#list viewModel.constraintFunctions as g>
            i++;
            fg[i] = ${g};
            </#list>
            //
            return;
        }
    };
}

using namespace arma;

bool ${viewModel.callIpoptName}::solveOptimizationProblemIpOpt(
    ${viewModel.optimizationVariableType} &x,
    double &y <#if 0 < viewModel.knownVariablesWithType?size>,</#if>
    <#list viewModel.knownVariablesWithType as arg>
    const ${arg}<#sep>,</#sep>
    </#list>
)
{
    bool ok = true;

    // assign parameter variables
    <#list viewModel.knownVariables as var>
    ::${var} = ${var};
    </#list>

    typedef CPPAD_TESTVECTOR(double)Dvector;

    // number of independent variables (domain dimension for f and g)
    size_t nx = ${viewModel.numberVariables?c};
    // number of constraints (range dimension for g)
    size_t ng = ${viewModel.numberConstraints?c};
    // initial value of the independent variables
    Dvector xi(nx);
    int i = 0;
    <#list viewModel.initX as x>
    xi[i] = ${x};
    i++;
    </#list>
    // lower and upper limits for x
    Dvector xl(nx),xu(nx);
    i = 0;
    <#list viewModel.xL as x>
    xl[i] = ${x};
    i++;
    </#list>
    i = 0;
    <#list viewModel.xU as x>
    xu[i] = ${x};
    i++;
    </#list>
    // lower and upper limits for g
    Dvector gl(ng),gu(ng);
    i = 0;
    <#list viewModel.gL as g>
    gl[i] = ${g};
    i++;
    </#list>
    i = 0;
    <#list viewModel.gU as g>
    gu[i] = ${g};
    i++;
    </#list>

    // object that computes objective and constraints
    FG_eval fg_eval;

    // options
    std::string options;
    // turn off any printing
    options+="Integer print_level  3\n";
    options+="String  sb   yes\n";
    // maximum number of iterations
    options+="Integer max_iter     500\n";
    // approximate accuracy in first order necessary conditions;
    // see Mathematical Programming, Volume 106, Number 1,
    // Pages 25-57, Equation (6)
    options+="Numeric tol  1e-6\n";
    // derivative testing
    options+="String  derivative_test    second-order\n";
    // maximum amount of random pertubation; e.g.,
    // when evaluation finite diff
    options+="Numeric point_perturbation_radius  0.\n";

    // place to return solution
    CppAD::ipopt::solve_result<Dvector> solution;

    // solve the problem
    CppAD::ipopt::solve<Dvector, FG_eval>(
        options,xi,xl,xu,gl,gu,fg_eval,solution
    );

    // Check some of the solution values
    ok&=solution.status==CppAD::ipopt::solve_result<Dvector>::success;

    // assign solution values
    <#if viewModel.optimizationVariableDimensions?size == 0>
    x = solution.x[0];
    <#elseif viewModel.optimizationVariableDimensions?size == 1>
    for (int i = 0; i < ${viewModel.optimizationVariableDimensions[0]?c}; i++)
    {
        x(i) = solution.x[i];
    }
    <#elseif viewModel.optimizationVariableDimensions?size == 2>
    for (int i = 0; i < ${viewModel.optimizationVariableDimensions[0]?c}; i++)
            {
            for (int j = 0; j < ${viewModel.optimizationVariableDimensions[1]?c}; j++)
        {
            x(i, j) = solution.x[i + j];
        }
    }
    <#elseif viewModel.optimizationVariableDimensions?size == 3>
    for (int i = 0; i < ${viewModel.optimizationVariableDimensions[0]?c}; i++)
    {
        for (int j = 0; j < ${viewModel.optimizationVariableDimensions[1]?c}; j++)
        {
            for (int k = 0; k < ${viewModel.optimizationVariableDimensions[2]?c}; k++)
            {
                x(i, j, k) = solution.x[i + j + k];
            }
        }
    }
    </#if>
    // objective value
    <#if viewModel.optimizationProblemType.name() == "MINIMIZATION">
    y = solution.obj_value;
    <#else>
    y = -1 * solution.obj_value;
    </#if>

    // print short message
    std::cout<<std::endl<<std::endl<<"Solving status: "<<solution.status<<"!"<<std::endl;
    std::cout<<"${viewModel.optimizationProblemType.name()?capitalize} variable value: "<<std::endl<<"x = "<<std::endl<<x<<std::endl;
    std::cout<<"${viewModel.optimizationProblemType.name()?capitalize} objective value: "<<std::endl<<"y = "<<y<<std::endl;
    return ok;
}