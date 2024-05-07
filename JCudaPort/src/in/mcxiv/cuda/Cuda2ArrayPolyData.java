package in.mcxiv.cuda;

import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.interfaces.FloatOperation;
import in.mcxiv.abyss.mathematics.MiscMath;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static jcuda.driver.JCudaDriver.*;

public class Cuda2ArrayPolyData implements PolyData {

    private static final CUdevice CU_DEVICE;
    private static final CUcontext CU_CONTEXT;
    private static final ArrayList<CUdeviceptr> ALLOCATIONS = new ArrayList<>();

    static {

        JCudaDriver.setExceptionsEnabled(true);

        cuInit(0);

        cuDeviceGet(CU_DEVICE = new CUdevice(), 0);
        byte[] deviceName = new byte[1024];
        cuDeviceGetName(deviceName, deviceName.length, CU_DEVICE);
        System.out.println("CUDA Device Name: " + new String(deviceName).trim());

        long[] totalMemory = {0};
        cuDeviceTotalMem(totalMemory, CU_DEVICE);
        System.out.println("Total Memory: " + totalMemory[0] + " bytes");

        int[] multiprocessors = {0};
        cuDeviceGetAttribute(multiprocessors, CUdevice_attribute.CU_DEVICE_ATTRIBUTE_MULTIPROCESSOR_COUNT, CU_DEVICE);
        System.out.println("Number of Multiprocessors: " + multiprocessors[0]);

        cuCtxCreate(CU_CONTEXT = new CUcontext(), 0, CU_DEVICE);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            int size = ALLOCATIONS.size();
            ALLOCATIONS.forEach(JCudaDriver::cuMemFree);
            System.out.println("Cleaned " + size + " allocations.");
        }));

    }

    public static void main(String[] args) {
        System.out.println("End");
    }

    public int[] shape;
    public long weight;
    public float[] data;

    public CUdeviceptr deviceData;

    public Cuda2ArrayPolyData(int... shape) {
        this.shape = shape.clone();
        this.weight = MiscMath.multiplyItems(shape);
        this.data = new float[(int) weight];

        addToGPU();
    }

    private void addToGPU() {
        if (deviceData != null) {
            ALLOCATIONS.remove(deviceData);
            cuMemFree(deviceData);
        }

        deviceData = new CUdeviceptr();
        cuMemAlloc(deviceData, weight * Sizeof.FLOAT);
        cuMemcpyHtoD(deviceData, Pointer.to(data), weight * Sizeof.FLOAT);
        ALLOCATIONS.add(deviceData);
    }

    @Override
    public int dims() {
        return shape.length;
    }

    @Override
    public int shape(int dim) {
        return shape[dim];
    }

    @Override
    public PolyData reshape(int... shape) {
        this.shape = shape.clone();
        this.weight = MiscMath.multiplyItems(shape);
        this.data = Arrays.copyOf(this.data, (int) weight);
        addToGPU();
        return this;
    }

    @Override
    public float get(int... address) {
        int index = MiscMath.collapseAddress(shape, address);
        float[] buffer = new float[1];
        cuMemcpyDtoH(Pointer.to(buffer), deviceData.withByteOffset(index * Sizeof.FLOAT), Sizeof.FLOAT);
        return buffer[0];
    }

    @Override
    public void set(float value, int... address) {
        int index = MiscMath.collapseAddress(shape, address);
        float[] buffer = {value};
        cuMemcpyHtoD(deviceData.withByteOffset(index * Sizeof.FLOAT), Pointer.to(buffer), Sizeof.FLOAT);
    }

    @Override
    public String toString() {
        cuMemcpyDtoH(Pointer.to(data), deviceData, weight * Sizeof.FLOAT);
        return stringify();
    }

    public int allocationSize() {
        return data.length;
    }

    @Override
    public PolyData clone() {
        throw new UnsupportedOperationException();
    }
}