package in.mcxiv.cuda;

import in.mcxiv.abyss.data.representation.Array1DPolyData;
import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.interfaces.FloatOperation;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.cublasHandle;

import static jcuda.jcublas.JCublas2.*;
import static jcuda.jcublas.cublasOperation.CUBLAS_OP_N;
import static jcuda.runtime.JCuda.cudaFree;
import static jcuda.runtime.JCuda.cudaMalloc;

public class CudaArrayPolyData extends Array1DPolyData {

    public CudaArrayPolyData(int... shape) {
        super(shape);
    }

    @Override
    public PolyData vectorOperation(PolyData first, PolyData second, PolyData result, FloatOperation cast, FloatOperation reduce, float identity) {
        if (!(first instanceof Array1DPolyData) || !(second instanceof Array1DPolyData) || !(result instanceof Array1DPolyData))
            return super.vectorOperation(first, second, result, cast, reduce, identity);
        if (first.dims() != 2 || second.dims() != 2)
            return super.vectorOperation(first, second, result, cast, reduce, identity);
        if (first.shape(1) != second.shape(0))
            throw new IllegalStateException();

        int m = first.shape(0);
        int n = first.shape(1);
        int c = second.shape(1);

        result.reshape(m, c);

        float[] a_f = ((Array1DPolyData) first).data;
        float[] b_f = ((Array1DPolyData) second).data;
        float[] c_f = ((Array1DPolyData) result).data;
        int a_nn = m * n;
        int b_nn = n * c;
        int c_nn = m * c;

        // Create a CUBLAS handle
        cublasHandle handle = new cublasHandle();
        cublasCreate(handle);

        // Allocate memory on the device
        Pointer d_A = new Pointer();
        Pointer d_B = new Pointer();
        Pointer d_C = new Pointer();
        cudaMalloc(d_A, a_nn * Sizeof.FLOAT);
        cudaMalloc(d_B, b_nn * Sizeof.FLOAT);
        cudaMalloc(d_C, c_nn * Sizeof.FLOAT);

        // Copy the memory from the host to the device
        cublasSetVector(a_nn, Sizeof.FLOAT, Pointer.to(a_f), 1, d_A, 1);
        cublasSetVector(b_nn, Sizeof.FLOAT, Pointer.to(b_f), 1, d_B, 1);
        cublasSetVector(c_nn, Sizeof.FLOAT, Pointer.to(c_f), 1, d_C, 1);

        // Execute sgemm
        Pointer pAlpha = Pointer.to(new float[]{1f});
        Pointer pBeta = Pointer.to(new float[]{0f});
//        cublasSgemm(handle, CUBLAS_OP_N, CUBLAS_OP_N, c, m, n, pAlpha, d_A, m, d_B, n, pBeta, d_C, m);
        cublasSgemm(handle, CUBLAS_OP_N, CUBLAS_OP_N, c, m, n, pAlpha, d_B, c, d_A, n, pBeta, d_C, c);

        // Copy the result from the device to the host
        cublasGetVector(c_nn, Sizeof.FLOAT, d_C, 1, Pointer.to(c_f), 1);

        // Clean up
        cudaFree(d_A);
        cudaFree(d_B);
        cudaFree(d_C);
        cublasDestroy(handle);

        return result;
    }
}
