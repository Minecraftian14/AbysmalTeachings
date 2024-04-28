package in.mcxiv.abyss.mathematics;

import in.mcxiv.abyss.interfaces.IntToIntFunction;

import java.io.*;
import java.util.Arrays;

public final class MoreMath {

    public static final float EQUALITY_THRESHOLD = 1e-6f;

    private MoreMath() {
    }

    public static float sum(float x, float y) {
        return x + y;
    }

    public static float sub(float x, float y) {
        return x - y;
    }

    public static float mul(float x, float y) {
        return x * y;
    }

    public static float div(float x, float y) {
        return x / y;
    }

    public static float square(float v) {
        return v * v;
    }

    public static float negate(float v) {
        return -v;
    }

    public static int multiplyItems(int[] array) {
        int product = 1;
        for (int i : array) product *= i;
        return product;
    }

    public static int multiplyItems(int length, IntToIntFunction array) {
        int product = 1;
        for (int i = 0; i < length; i++)
            product *= array.applyAsInt(i);
        return product;
    }

    public static int sumItems(int length, IntToIntFunction array) {
        int product = 0;
        for (int i = 0; i < length; i++)
            product += array.applyAsInt(i);
        return product;
    }

    /**
     * Multidimensional Array Address to 1D Array / Memory Location
     */
    public static int collapseAddress(int[] shape, int[] address) {
        int sum = address[address.length - 1];
        int size = shape[address.length - 1];
        for (int i = address.length - 2; i >= 0; i--) {
            sum += size * address[i];
            size *= shape[i];
        }
        return sum;
    }

    public static int[] crossDimensions(int[] first, int[] second) {
        if (first[first.length - 1] != second[0])
            throw new IllegalStateException("Dimensions not compatible for matrix multiplication! Received " + Arrays.toString(first) + " and " + Arrays.toString(first) + ".");
        if (first.length + second.length - 2 < 2)
            throw new IllegalStateException("Dimensions not compatible for matrix multiplication! Try dot product instead.");
        int[] result = new int[first.length + second.length - 2];
        System.arraycopy(first, 0, result, 0, first.length - 1);
        System.arraycopy(second, 1, result, first.length - 1, second.length - 1);
        return result;
    }

    public static boolean functionalArrayEquals(int length, IntToIntFunction array1, IntToIntFunction array2) {
        for (int i = 0; i < length; i++)
            if (array1.get(i) != array2.get(i)) return false;
        return true;
    }

    public static int[] functionalArrayToArray(int length, IntToIntFunction array) {
        int[] result = new int[length];
        for (int i = 0; i < length; i++) result[i] = array.get(i);
        return result;
    }

    public static int[] reversed(int[] array) {
        int[] clone = array.clone();
        for (int i = 0; i < clone.length / 2; i++) {
            int temp = clone[i];
            clone[i] = clone[clone.length - i - 1];
            clone[clone.length - i - 1] = temp;
        }
        return clone;
    }

    public static float random() {
        return (float) Math.random();
    }

    public static int randomInt() {
        return (int) (Integer.MAX_VALUE * Math.random());
    }

    public static int[] removed(int[] array, int index) {
        int[] result = new int[array.length - 1];
        System.arraycopy(array, 0, result, 0, index);
        System.arraycopy(array, index + 1, result, index, array.length - index - 1);
        return result;
    }

    public static boolean equals(float a, float b) {
        return Math.abs(a - b) < EQUALITY_THRESHOLD;
    }

    public static int[] broadcastingMask(int[] secondShape) {
        int[] mask = new int[secondShape.length];
        for (int i = 0; i < secondShape.length; i++)
            if (secondShape[i] > 1) mask[i] = 1;
        return mask;
    }

    public static int[] mul(int[] first, int[] second, int[] result) {
        if (result == null) result = new int[first.length];
        if (result.length != first.length || second.length != first.length)
            throw new IllegalStateException();
        for (int i = 0; i < first.length; i++) result[i] = first[i] * second[i];
        return result;
    }

    public static Object clone(Object object) {
        try {
            var bos = new ByteArrayOutputStream();
            var oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            oos.close();

            var bis = new ByteArrayInputStream(bos.toByteArray());
            var ois = new ObjectInputStream(bis);
            Object clone = ois.readObject();
            ois.close();

            return clone;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}