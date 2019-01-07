1、编译环境 在unbuntu 下(在windows下有个需要找ndk版本的错误，linux下则没有),ndk版本使用最新就行，我自己的是在android studio下载的。

2、接下来按照官方的提示步骤：
	一：使用命令行，进入ceres-solver-XXXX 目录下 输入 ndk-build，
	会提示错误：jni/Android.mk:84: *** Ceres requires Eigen; please invoke via EIGEN_PATH=... ndk-build.  Stop.
	这个错误，其实看了 Android.mk 文件的注释都能很简单的解决问题，在里面添加一句（意思就是把Eigen的路径添加进来并编译）：
	EIGEN_PATH=/home/dmj/ceres-solver-1.14.0/eigen/eigen-eigen-3/Eigen ndk-build -j
	解决这个问题在继续编译，如果提示有些文件找不到 就需要注意看下：
	CERES_INCLUDE_PATHS := $(CERES_EXTRA_INCLUDES)
	CERES_INCLUDE_PATHS += $(LOCAL_PATH)/../internal
			.	
			.
			.
			.
	语句下是否把头文件包含进来了，没有意外的话，这个时候应该会生存一个静态库libceres.a(在jni同目录下的object文件夹中)。
	特别要注意的是：
		在.mk文件有个配置选项，是一些编译的依赖选项，在solver.cc中会检查这几个标志是否定义，最后3个稀疏线性代数库，需要选中一个：
			LOCAL_CFLAGS := $(CERES_EXTRA_DEFINES) \	#
		        -DCERES_NO_LAPACK \				#
			-DCERES_STD_UNORDERED_MAP \			#
		        -DCERES_NO_SUITESPARSE \			# 不编译使用	SuiteSparse 
			-DCERES_NO_CXSPARSE \				# 不编译使用	CXSparse 
			-DCERES_USE_EIGEN_SPARSE \			# 编译使用	EIGEN_SPARSE
	
	二：接下来我们使用这个静态库（要在安卓使用，还需要把静态库打包成动态库）
	1、新建一个文件夹随便取个名字（接下来都将在这个文件下进行动态库的打包），
	2、进入该文件，新建一个jni文件（方便使用ndk-build,该文件也是放置你自己编写的cpp代码），将静态库拷贝进来，同时
	   将静态库所需要的头文件（就是打包静态库时Android.mk所包含的头文件）也一并复制过来,这些头文件可以在 ceres-solver-XXXX 目录找到：有以下文件：config,eigen,include,internal 特别的是logging.h,这些头其实也可以不必要特意的去找，在你编译的时候会提示你，如果是提示找不到标准c++库 你需要增加Application.mk,内容如下：
	APP_PLATFORM := android-23
	APP_STL := c++_static
	APP_ABI := armeabi-v7a
	
	3、在jni文件里我们需要给他创建一个Android.mk文件内容如下（我的也是复制的，语法我也不是很清楚）：
			
	LOCAL_PATH := $(call my-dir)
	LOCAL_ALLOW_UNDEFINED_SYMBOLS := true
	include $(CLEAR_VARS)
	LOCAL_MODULE := ceres
	LOCAL_SRC_FILES := libceres.a
	include $(PREBUILT_STATIC_LIBRARY)
	include $(CLEAR_VARS)
	LOCAL_C_INCLUDES += /opt/sdk/ndk-bundle/sources/cxx-stl/llvm-libc++
	LOCAL_C_INCLUDES += /opt/sdk/ndk-bundle/sources/cxx-stl/llvm-libc++abi
	LOCAL_C_INCLUDES += /opt/sdk/ndk-bundle/sources/cxx-stl
	#以下都是静态库所需要的头文件,如果编译时有提示找不到一些依赖库，你可能需要思考下是否添加了他们的文件路径
	#比如说这个提示：fatal error: 'Eigen/Core' ...   那么我就将该句添加：
	#LOCAL_C_INCLUDES += $(LOCAL_PATH)/../eigen/eigen-eigen-3/  在该路径下就有Eigen文件夹

	LOCAL_C_INCLUDES += $(LOCAL_PATH)/../
	LOCAL_C_INCLUDES += $(LOCAL_PATH)/../config
	LOCAL_C_INCLUDES += $(LOCAL_PATH)/../include
	LOCAL_C_INCLUDES += $(LOCAL_PATH)/..
	LOCAL_C_INCLUDES += $(LOCAL_PATH)/types.h
	LOCAL_C_INCLUDES += $(LOCAL_PATH)/../internal/ceres/miniglog
	LOCAL_MODULE := myproject	#这个可以自己改，为打包的库文件名字	

	LOCAL_SRC_FILES := test.cpp	#这个是自己的文件，好像需要改成 .cpp 格式，好像跟标准
					#c++库有关，	在使用.cc文件我试过会报错
	LOCAL_STATIC_LIBRARIES = ceres
	LOCAL_LDLIBS += -llog -ldl
	include $(BUILD_SHARED_LIBRARY)


	我的test.cpp内容如下：

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

	如果你的头文件都包含了的话，那么就不会有提示找不到某某文之类的错误，在进行 ndk-build的 这时候 你可能会报这个错误： error: One of CERES_USE_OPENMP,
        CERES_USE_TBB,CERES_USE_CXX11_THREADS or CERES_NO_THREADS must be defined.

	这个错误我是在config.h 这个文件里面新增了一句：
		#define CERES_USE_CXX11_THREADS
	其实config.h  个人感觉这个文件就是为在安卓使用而增加的。也就是配置一些ceres选项。
	
	到此基本上动态库也就可以生成了。如果需要在安卓中使用，还需要使用jni进行调用（这块自己解决）
	
	在继续编译会出现与线程相关的错误，在网上找了很久才发现 原来是文件缺少导致的（只有极个别的版本才会出现这在情况），出现这个问题可以先尝试从 ceres-solver-XXXX/internal/ceres/ 目录下
	查找下是不是没有将文件添加进编译，我的就是把 thread_token_provider.cc 重新添加进去了，就是在 Android.mk 	LOCAL_SRC_FILES 中
	LOCAL_SRC_FILES := $(CERES_SRC_PATH)/array_utils.cc \
                   $(CERES_SRC_PATH)/blas.cc \
                   $(CERES_SRC_PATH)/block_evaluate_preparer.cc \
                   $(CERES_SRC_PATH)/block_jacobian_writer.cc \
              		.
			.
			.
		  $(CERES_SRC_PATH)/thread_token_provider.cc

	把缺失的文件添加上然后在从新编译静态库.


	minilog 的用法：
	VLOG(2) << "******************************************** ";//括号里面的数字意义如下：
		//   2 - Verbose
		//   1 - Debug
		//   0 - Info
		//  -1 - Warning
		//  -2 - Error
		//  -3 - Fatal
	三：使用动态库
		在使用动态库的时候需要避免使用线程，因为在打包静态库的时候，把线程关了，如需开启的话自己可以去研究下。
	如果使用了线程则有可能报Fatal signal 11 (SIGSEGV)	
