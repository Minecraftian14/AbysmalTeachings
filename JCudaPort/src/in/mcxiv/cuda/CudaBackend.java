package in.mcxiv.cuda;

import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.interfaces.FloatOperation;
import in.mcxiv.abyss.mathematics.MiscMath;
import in.mcxiv.abyss.mathematics.backend.Backend;
import in.mcxiv.abyss.utilities.Pool;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.CUfunction;
import jcuda.driver.CUmodule;

import java.util.HashMap;

import static jcuda.driver.JCudaDriver.*;

public class CudaBackend implements Backend {

    public static final Backend FALLBACK;
    public static final Pool<Cuda2ArrayPolyData> POOL;
    private static final HashMap<String, CUfunction> CODES = new HashMap<>();

    static {
        FALLBACK = MiscMath.BACKEND;
        MiscMath.BACKEND = new CudaBackend();

        POOL = new Pool<>(
                Cuda2ArrayPolyData.class,
                Cuda2ArrayPolyData::allocationSize,
                Cuda2ArrayPolyData::new,
                Cuda2ArrayPolyData::reshape
        );
    }

    private CUfunction code(String code) {
        if (CODES.containsKey(code)) return CODES.get(code);
        String ptxFile = JCudaSamplesUtils.preparePtxFileFromCode(code);
        CUmodule module = new CUmodule();
        cuModuleLoad(module, ptxFile);
        CUfunction function = new CUfunction();
        cuModuleGetFunction(function, module, "function");
        CODES.put(code, function);
        return function;
    }

    public boolean test(PolyData... pds) {
        for (PolyData pd : pds)
            if (!(pd instanceof Cuda2ArrayPolyData))
                return true;
        return false;
    }

    @Override
    public PolyData fill(PolyData first, float value) {
        if (test(first)) return FALLBACK.fill(first, value);
        var function = code("""
                extern "C"
                __global__ void function(int n, float *a, float b) {
                    int i = blockIdx.x * blockDim.x + threadIdx.x;
                    if (i < n) {
                        a[i] = b;
                    }
                }
                """);
        var f = (Cuda2ArrayPolyData) first;
        Pointer parameters = Pointer.to(
                Pointer.to(new int[]{f.data.length}),
                Pointer.to(f.deviceData),
                Pointer.to(new float[]{value})
        );
        int blockSizeX = 256;
        int gridSizeX = (int) Math.ceil((double) f.weight / blockSizeX);
        cuLaunchKernel(function,
                gridSizeX, 1, 1,      // Grid dimension
                blockSizeX, 1, 1,   // Block dimension
                0, null,                        // Shared memory size and stream
                parameters, null                        // Kernel and extra parameters
        );
        cuCtxSynchronize();
        return first;
    }

    @Override
    public float[] export(PolyData first) {
        if (test(first)) return FALLBACK.export(first);
        var f = ((Cuda2ArrayPolyData) first);
        cuMemcpyDtoH(Pointer.to(f.data), f.deviceData, f.weight * Sizeof.FLOAT);
        return f.data.clone();
    }

    @Override
    public PolyData scalarOperation(PolyData first, PolyData second, PolyData result, FloatOperation operation) {
        if (test(first, second, result)) return FALLBACK.scalarOperation(first, second, result, operation);
        Cuda2ArrayPolyData f = ((Cuda2ArrayPolyData) first), s = ((Cuda2ArrayPolyData) second), r = ((Cuda2ArrayPolyData) result);
        r.reshape(first.shape());
        var function = code("""
                extern "C"
                __global__ void function(int n, float *a, float *b, float *c) {
                    int x = blockIdx.x * blockDim.x + threadIdx.x;
                    if (x < n) {
                        c[x] = a[x] + b[x];
                    }
                }
                """);
        Pointer parameters = Pointer.to(
                Pointer.to(new int[]{f.data.length}),
                Pointer.to(f.deviceData),
                Pointer.to(s.deviceData),
                Pointer.to(r.deviceData)
        );
        int blockSizeX = 256;
        int gridSizeX = (int) Math.ceil((double) r.weight / blockSizeX);
        cuLaunchKernel(function,
                gridSizeX, 1, 1,      // Grid dimension
                blockSizeX, 1, 1,   // Block dimension
                0, null,                        // Shared memory size and stream
                parameters, null                        // Kernel and extra parameters
        );
        cuCtxSynchronize();
        return result;
    }

    @Override
    public PolyData vectorOperation(PolyData first, PolyData second, PolyData result, FloatOperation cast, FloatOperation reduce, float identity) {
        if (test(first, second, result)) return FALLBACK.vectorOperation(first, second, result, cast, reduce, identity);
        Cuda2ArrayPolyData f = ((Cuda2ArrayPolyData) first), s = ((Cuda2ArrayPolyData) second), r = ((Cuda2ArrayPolyData) result);
        r.reshape(MiscMath.crossDimensions(first.shape(), second.shape()));
        var function = code("""
                extern "C"
                __global__ void function(int m, int n, int c, float *a, float *b, float *c) {
                    int x = blockIdx.x * blockDim.x + threadIdx.x;
                    int i = x / c;
                    int j = x % c;
                    if (i < m && j < c) {
                        float sum = 0;
                        for (int k = 0; k < n; k++) {
                            sum += a[i * n + k] * b[k * c + j];
                        }
                        c[x] = sum;
                    }
                }
                """);
        Pointer parameters = Pointer.to(
                Pointer.to(new int[]{f.shape(0)}),
                Pointer.to(new int[]{f.shape(1)}),
                Pointer.to(new int[]{s.shape(1)}),
                Pointer.to(f.deviceData),
                Pointer.to(s.deviceData),
                Pointer.to(r.deviceData)
        );
        int blockSizeX = 256;
        int gridSizeX = (int) Math.ceil((double) r.weight / blockSizeX);
        cuLaunchKernel(function,
                gridSizeX, 1, 1,      // Grid dimension
                blockSizeX, 1, 1,   // Block dimension
                0, null,                        // Shared memory size and stream
                parameters, null                        // Kernel and extra parameters
        );
        cuCtxSynchronize();
        return result;
    }
}
