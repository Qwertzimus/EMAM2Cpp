//
#include <iostream>
#include "coin/IpIpoptApplication.hpp"
#include "coin/IpSolveStatistics.hpp"
#include "${viewModel.nlpClassName}.hpp"

using namespace Ipopt;

int solveOptimizationProblemIpOpt(double &x, double &y)
{
	// Create an instance of your nlp...
	SmartPtr<TNLP> ${viewModel.nlpClassName?lower_case} = new ${viewModel.nlpClassName}();

	// Create an instance of the IpoptApplication
	//
	// We are using the factory, since this allows us to compile this
	// example with an Ipopt Windows DLL
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
	}

	return (int)status;
}

