#include <jni.h>
#include "ceres/ceres.h"
using ceres::AutoDiffCostFunction;
using ceres::CostFunction;
using ceres::Problem;
using ceres::Solver;
using ceres::Solve;


void test(){
	Problem problem;
}
/*	如果需要在安卓中使用可以使用下面形式调取的静态库函数
#ifdef __cplusplus
extern "C" {

#endif
JNIEXPORT jint JNICALL Java_com_qiwei_samples_JniDemo_add(JNIEnv *env, jclass cls)
{
    jdouble initial_x = 5.0;
    jdouble x = initial_x;
    Problem problem;
    CostFunction* cost_function =
        new AutoDiffCostFunction<CostFunctor, 1, 1>(new CostFunctor);
    problem.AddResidualBlock(cost_function, NULL, &x);

    Solver::Options options;
    options.linear_solver_type = ceres::DENSE_QR;
    options.minimizer_progress_to_stdout = true;
    Solver::Summary summary;
    Solve(options, &problem, &summary);
    std::cout << summary.BriefReport() << "\n";
    std::cout << "x : " << initial_x
        << " -> " << x << "\n";
    return 0;
}
#ifdef __cplusplus
}

#endif*/
