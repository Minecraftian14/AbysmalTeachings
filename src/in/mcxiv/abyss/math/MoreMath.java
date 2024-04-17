package in.mcxiv.abyss.math;

import in.mcxiv.abyss.iface.IntToIntFunction;

import java.util.Arrays;

public final class MoreMath {
    private MoreMath() {
    }

    public static int multiplyElements(int[] array) {
        int product = 1;
        for (int i : array) product *= i;
        return product;
    }

    public static int multiplyElements(int length, IntToIntFunction array) {
        int product = 1;
        for (int i = 0; i < length; i++)
            product *= array.applyAsInt(i);
        return product;
    }

    public static int sumElements(int length, IntToIntFunction array) {
        int product = 0;
        for (int i = 0; i < length; i++)
            product += array.applyAsInt(i);
        return product;
    }

    public static int collapseAddress(int[] shape, int[] address) {
        int sum = address[address.length - 1];
        int size = shape[address.length - 1];
        for (int i = address.length - 2; i >= 0; i--) {
            sum += size * address[i];
            size *= shape[i];
        }
        return sum;
    }

    public static int[] multiplyDimensions(int[] first, int[] second) {
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
}