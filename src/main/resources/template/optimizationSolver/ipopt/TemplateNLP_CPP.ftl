#include "${viewModel.nlpClassName}.hpp"

#include <cassert>

using namespace Ipopt;
using namespace arma;

/* Constructor. */
${viewModel.nlpClassName}::${viewModel.nlpClassName}()
{}

${viewModel.nlpClassName}::~${viewModel.nlpClassName}()
{}

bool ${viewModel.nlpClassName}::get_nlp_info(Index& n, Index& m, Index& nnz_jac_g,
	Index& nnz_h_lag, IndexStyleEnum& index_style)
{
	n = ${viewModel.numberVariables};

	m = ${viewModel.numberConstraints};

	// assume the jacobian is dense. Hence, it contains n*m nonzeros
	nnz_jac_g = n * n;

	// assume the hessian is also dense and has n*n total nonzeros
	nnz_h_lag = n * n;

	generate_tapes(n, m);

	// use the C style indexing (0-based)
	index_style = C_STYLE;

	return true;
}

bool ${viewModel.nlpClassName}::get_bounds_info(Index n, Number* x_l, Number* x_u,
	Index m, Number* g_l, Number* g_u)
{
    int i = 0;
    <#list viewModel.xL as x>
        x_l[i] = ${x};
        i++;
    </#list>

    i = 0;
    <#list viewModel.xU as x>
        x_u[i] = ${x};
        i++;
    </#list>

    i = 0;
    <#list viewModel.gL as g>
        g_l[i] = ${g};
        i++;
    </#list>

    i = 0;
    <#list viewModel.gU as g>
        g_u[i] = ${g};
        i++;
    </#list>

	return true;
}

bool ${viewModel.nlpClassName}::get_starting_point(Index n, bool init_x, Number* x,
	bool init_z, Number* z_L, Number* z_U,
	Index m, bool init_lambda,
	Number* lambda)
{
	// assume we only have starting values for x
	assert(init_x == true);
	assert(init_z == false);
	assert(init_lambda == false);

	// set the starting point
    int i = 0;
    <#list viewModel.initX as x>
        x[i] = ${x};
        i++;
    </#list>

    return true;
}

template<class T> void ${viewModel.nlpClassName}::convertXToOptVar(Index n, const T *x, ${viewModel.optimizationVariableType} &optVar)
{
    <#if viewModel.numberVariables <= 1>
    optVar = ((adouble) x[0]).getValue();
    <#else>
    std::vector optAsVec = std::vector(n);
    for (int i = 0; i < n; i++)
    {
        optAsVec[i] = ((adouble) x[i]).getValue();
    }
    optVar = conv_to<${viewModel.optimizationVariableType}>::from(optAsVec);
    </#if>
}

template<class T> bool ${viewModel.nlpClassName}::eval_obj(Index n, const T *x, T& obj_value)
{
    // init optimization var
    ${viewModel.optimizationVariableType} ${viewModel.optimizationVariableName};
    convertXToOptVar(n, x, ${viewModel.optimizationVariableName});

    // evaluate obj_value
    obj_value = ${viewModel.objectiveFunction};

	return true;
}

template<class T> bool ${viewModel.nlpClassName}::eval_constraints(Index n, const T *x, Index m, T* g)
{
    // init optimization var
    ${viewModel.optimizationVariableType} ${viewModel.optimizationVariableName};
    convertXToOptVar(n, x, ${viewModel.optimizationVariableName});

    // evaluate constraint function
    int i = 0;
    <#list viewModel.constraintFunctions as g>
        g[i] = ${g};
        i++;
    </#list>

	return true;
}

bool ${viewModel.nlpClassName}::eval_f(Index n, const Number* x, bool new_x, Number& obj_value)
{
	eval_obj(n, x, obj_value);

	return true;
}

bool ${viewModel.nlpClassName}::eval_grad_f(Index n, const Number* x, bool new_x, Number* grad_f)
{

	gradient(tag_f, n, x, grad_f);

	return true;
}

bool ${viewModel.nlpClassName}::eval_g(Index n, const Number* x, bool new_x, Index m, Number* g)
{

	eval_constraints(n, x, m, g);

	return true;
}

bool ${viewModel.nlpClassName}::eval_jac_g(Index n, const Number* x, bool new_x,
	Index m, Index nele_jac, Index* iRow, Index *jCol,
	Number* values)
{
	if (values == NULL) {
		// return the structure of the jacobian,
		// assuming that the Jacobian is dense

		Index idx = 0;
		for (Index i = 0; i < m; i++)
			for (Index j = 0; j < n; j++)
			{
				iRow[idx] = i;
				jCol[idx++] = j;
			}
	}
	else {
		// return the values of the jacobian of the constraints

		jacobian(tag_g, m, n, x, Jac);

		Index idx = 0;
		for (Index i = 0; i < m; i++)
			for (Index j = 0; j < n; j++)
				values[idx++] = Jac[i][j];

	}

	return true;
}

bool ${viewModel.nlpClassName}::eval_h(Index n, const Number* x, bool new_x,
	Number obj_factor, Index m, const Number* lambda,
	bool new_lambda, Index nele_hess, Index* iRow,
	Index* jCol, Number* values)
{
	if (values == NULL) {

		// the hessian for this problem is actually dense
		Index idx = 0;
		for (Index row = 0; row < n; row++) {
			for (Index col = 0; col <= row; col++) {
				iRow[idx] = row;
				jCol[idx] = col;
				idx++;
			}
		}

		assert(idx == nele_hess);
	}
	else {
		// return the values. This is a symmetric matrix, fill the lower left
		// triangle only

		for (Index i = 0; i < n; i++)
			x_lam[i] = x[i];
		for (Index i = 0; i < m; i++)
			x_lam[n + i] = lambda[i];
		x_lam[n + m] = obj_factor;

		hessian(tag_L, n + m + 1, x_lam, Hess);

		Index idx = 0;

		for (Index i = 0; i < n; i++)
		{
			for (Index j = 0; j <= i; j++)
			{
				values[idx++] = Hess[i][j];
			}
		}
	}

	return true;
}

void ${viewModel.nlpClassName}::finalize_solution(SolverReturn status,
	Index n, const Number* x, const Number* z_L, const Number* z_U,
	Index m, const Number* g, const Number* lambda,
	Number obj_value,
	const IpoptData* ip_data,
	IpoptCalculatedQuantities* ip_cq)
{

	printf("\n\nObjective value\n");
	printf("${viewModel.objectiveVariableName} = %e\n", obj_value);

    printf("\n\nPrimal value\n");
    printf("${viewModel.objectiveVariableName} = [");
    for(int i = 0; i < n; i++)
    {
        printf("%e; ", x[i]);
    }
    printf("]");
	// Memory deallocation for ADOL-C variables

	delete[] x_lam;

	for (Index i = 0; i < m; i++)
		delete[] Jac[i];
	delete[] Jac;

	for (Index i = 0; i < n + m + 1; i++)
		delete[] Hess[i];
	delete[] Hess;
}


//***************    ADOL-C part ***********************************

void ${viewModel.nlpClassName}::generate_tapes(Index n, Index m)
{
	Number *xp = new double[n];
	Number *lamp = new double[m];
	Number *zl = new double[m];
	Number *zu = new double[m];

	adouble *xa = new adouble[n];
	adouble *g = new adouble[m];
	adouble *lam = new adouble[m];
	adouble sig;
	adouble obj_value;

	double dummy;

	Jac = new double*[m];
	for (Index i = 0; i < m; i++)
		Jac[i] = new double[n];

	x_lam = new double[n + m + 1];

	Hess = new double*[n + m + 1];
	for (Index i = 0; i < n + m + 1; i++)
		Hess[i] = new double[i + 1];

	get_starting_point(n, 1, xp, 0, zl, zu, m, 0, lamp);

	trace_on(tag_f);

	for (Index i = 0; i < n; i++)
		xa[i] <<= xp[i];

	eval_obj(n, xa, obj_value);

	obj_value >>= dummy;

	trace_off();

	trace_on(tag_g);

	for (Index i = 0; i < n; i++)
		xa[i] <<= xp[i];

	eval_constraints(n, xa, m, g);


	for (Index i = 0; i < m; i++)
		g[i] >>= dummy;

	trace_off();

	trace_on(tag_L);

	for (Index i = 0; i < n; i++)
		xa[i] <<= xp[i];
	for (Index i = 0; i < m; i++)
		lam[i] <<= 1.0;
	sig <<= 1.0;

	eval_obj(n, xa, obj_value);

	obj_value *= sig;
	eval_constraints(n, xa, m, g);

	for (Index i = 0; i < m; i++)
		obj_value += g[i] * lam[i];

	obj_value >>= dummy;

	trace_off();

	delete[] xa;
	delete[] xp;
	delete[] g;
	delete[] lam;
	delete[] lamp;
	delete[] zu;
	delete[] zl;

}
