package in.mcxiv.abyss.math;

import in.mcxiv.abyss.utilities.AddressIterator;
import in.mcxiv.abyss.mathematics.MiscMath;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AddressIteratorTest {

    @Test
    void test() {
        AddressIterator iterator = new AddressIterator(new int[]{2, 3, 4});
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{0, 0, 0}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{0, 0, 1}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{0, 0, 2}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{0, 0, 3}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{0, 1, 0}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{0, 1, 1}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{0, 1, 2}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{0, 1, 3}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{0, 2, 0}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{0, 2, 1}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{0, 2, 2}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{0, 2, 3}, iterator.next());

        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{1, 0, 0}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{1, 0, 1}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{1, 0, 2}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{1, 0, 3}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{1, 1, 0}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{1, 1, 1}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{1, 1, 2}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{1, 1, 3}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{1, 2, 0}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{1, 2, 1}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{1, 2, 2}, iterator.next());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new int[]{1, 2, 3}, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    @Disabled
    void print() {
        AddressIterator iterator = new AddressIterator(new int[]{2, 3, 5});
        while (iterator.hasNext())
            System.out.println(Arrays.toString(iterator.next()) + "\t" + MiscMath.collapseAddress(new int[]{2, 3, 5}, iterator.next()));
    }
}