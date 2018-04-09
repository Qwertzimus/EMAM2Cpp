#include<cppad/ipopt/solve.hpp>
#include<armadillo>
#include<iostream>

namespace
{
    using CppAD::AD;
    using namespace arma;

    class FG_eval {
    public:
        typedef CPPAD_TESTVECTOR(AD<double>) ADvector;

        void operator()(ADvector &fg,const ADvector &x) {
            assert (fg.size() == (${viewModel.numberConstraints} + 1));
            assert (x.size() == ${viewModel.numberVariables});

            // create active optimization var
            <#if viewModel.numberVariables <= 1>
            ${viewModel.optimizationVariableTypeActive} ${viewModel.optimizationVariableName} = x[0];
            <#else>
            std::vector<AD<double>> optAsVec = std::vector<AD<double>>(${viewModel.numberVariables?c});
            for (int i = 0; i < ${viewModel.numberVariables}; i++)
            {
                optAsVec[i] = x[i];
            }
            ${viewModel.optimizationVariableTypeActive} ${viewModel.optimizationVariableName} = conv_to<${viewModel.optimizationVariableTypeActive}>::from(optAsVec);
            </#if>

            // f(x)
            int i = 0;
            fg[i] = ${viewModel.objectiveFunction};

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

bool solveOptimizationProblemIpOpt(${viewModel.optimizationVariableType} &x,double &y)
{
    bool ok = true;
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
    <#if viewModel.numberVariables <= 1>
    x = solution.x[0];
    <#else>
    std::vector<double> optAsVec = std::vector<double>(nx);
    for (int i = 0; i < nx; i++)
    {
        optAsVec[i] = solution.x[i];
    }
    x = conv_to<${viewModel.optimizationVariableType}>::from(optAsVec);
    </#if>

    y = solution.obj_value;

    // print short message
    std::cout<<std::endl<<std::endl<<"Solving status: "<<solution.status<<"!"<<std::endl;
    std::cout<<"Primal value: "<<std::endl<<"x = "<<std::endl<<x<<std::endl;
    std::cout<<"Objective value: "<<std::endl<<"y = "<<y<<std::endl;
    return ok;
}