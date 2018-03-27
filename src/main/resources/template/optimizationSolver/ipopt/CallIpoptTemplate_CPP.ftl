//
#include <iostream>
#include "coin/IpIpoptApplication.hpp"
#include "coin/IpSolveStatistics.hpp"
#include "${viewModel.nlpClassName}.hpp"

using namespace Ipopt;

int solveOptimizationProblemIpOpt(double &x, double &y)
{
	// Create nlp instance
	SmartPtr<TNLP> ${viewModel.nlpClassName?lower_case} = new ${viewModel.nlpClassName}();

	// Create instance of the IpoptApplication
	SmartPtr<IpoptApplication> app = IpoptApplicationFactory();

	// Initialize the IpoptApplication and process the options
	ApplicationReturnStatus status;
	status = app->Initialize();
	if (status != Solve_Succeeded) {
		std::cout << std::endl << std::endl << "*** Error during initialization!" << std::endl;
		return (int)status;
	}

	status = app->OptimizeTNLP(${viewModel.nlpClassName?lower_case});

	if (status == Solve_Succeeded) {
		// Retrieve some statistics about the solve
		Index iter_count = app->Statistics()->IterationCount();
		std::cout << std::endl << std::endl << "*** The problem solved in " << iter_count << " iterations!" << std::endl;

		Number final_obj = app->Statistics()->FinalObjective();
		std::cout << std::endl << std::endl << "*** The final value of the objective function is " << final_obj << '.' << std::endl;
	
		y = final_obj;
		// TODO also assign x
	}

	return (int)status;
}