package in.mcxiv.abyss.data.representation;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static in.mcxiv.abyss.data.representation.Array1DPolyData.n;
import static in.mcxiv.abyss.data.representation.PolyData.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class PolyDataTest {
    @Test
    void testAdd() {
        PolyData m1 = new Array1DPolyData(2, 3, 5);
        m1.fill(3);
        PolyData m2 = new Array1DPolyData(2, 3, 5);
        AtomicInteger integer = new AtomicInteger();
        m2.fill(integer::incrementAndGet);
        PolyData m3 = new Array1DPolyData(5, 5);
        add(m1, m2, m3);
        assertArrayEquals(new float[]{4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f, 11.0f, 12.0f, 13.0f, 14.0f, 15.0f, 16.0f, 17.0f, 18.0f, 19.0f, 20.0f, 21.0f, 22.0f, 23.0f, 24.0f, 25.0f, 26.0f, 27.0f, 28.0f, 29.0f, 30.0f, 31.0f, 32.0f, 33.0f}, m3.export());
    }

    @Test
    void testCross() {
        PolyData m1 = new Array1DPolyData(3, 2);
        m1.fill(List.of(0, 1, 2, 3, 4, 5).iterator()::next);
        PolyData m2 = new Array1DPolyData(2, 4);
        m2.fill(List.of(0, 1, 2, 3, 4, 5, 6, 7).iterator()::next);
        PolyData m3 = new Array1DPolyData(5, 5);
        PolyData.cross(m1, m2, m3);
        assertArrayEquals(new float[]{4.0f, 5.0f, 6.0f, 7.0f, 12.0f, 17.0f, 22.0f, 27.0f, 20.0f, 29.0f, 38.0f, 47.0f}, m3.export());
    }

    @Test
    void testConvolveOperation() {
        AtomicInteger integer = new AtomicInteger();

        PolyData m1 = new Array1DPolyData(5, 5);
        m1.fill(integer::incrementAndGet);

        PolyData m2 = new Array1DPolyData(3, 3);
        m2.fill(List.of(1, 1, -1, 1, 1, -1, 1, 1, -1).iterator()::next);

        assertArrayEquals(new float[]{15.0f, 18.0f, 21.0f, 30.0f, 33.0f, 36.0f, 45.0f, 48.0f, 51.0f}, convolveOperation(m1, m2, n()).export());
    }

    @Test
    void testImageConvolveForward() {
        AtomicInteger integer = new AtomicInteger();

        PolyData m1 = new Array1DPolyData(6, 5, 4, 3);
        m1.fill(integer::getAndIncrement);

        PolyData m2 = new Array1DPolyData(3, 3, 3, 4);
        m2.fill(integer::getAndIncrement);

        var m3 = imageConvolveForward(m1, m2, n());

        assertArrayEquals(new float[]{186480f, 186912f, 187344f, 187776f, 219852f, 220365f, 220878f, 221391f, 319968f, 320724f, 321480f, 322236f, 353340f, 354177f, 355014f, 355851f, 453456f, 454536f, 455616f, 456696f, 486828f, 487989f, 489150f, 490311f, 853920f, 855972f, 858024f, 860076f, 887292f, 889425f, 891558f, 893691f, 987408f, 989784f, 992160f, 994536f, 1020780f, 1023237f, 1025694f, 1028151f, 1120896f, 1123596f, 1126296f, 1128996f, 1154268f, 1157049f, 1159830f, 1162611f, 1521360f, 1525032f, 1528704f, 1532376f, 1554732f, 1558485f, 1562238f, 1565991f, 1654848f, 1658844f, 1662840f, 1666836f, 1688220f, 1692297f, 1696374f, 1700451f, 1788336f, 1792656f, 1796976f, 1801296f, 1821708f, 1826109f, 1830510f, 1834911f, 2188800f, 2194092f, 2199384f, 2204676f, 2222172f, 2227545f, 2232918f, 2238291f, 2322288f, 2327904f, 2333520f, 2339136f, 2355660f, 2361357f, 2367054f, 2372751f, 2455776f, 2461716f, 2467656f, 2473596f, 2489148f, 2495169f, 2501190f, 2507211f, 2856240f, 2863152f, 2870064f, 2876976f, 2889612f, 2896605f, 2903598f, 2910591f, 2989728f, 2996964f, 3004200f, 3011436f, 3023100f, 3030417f, 3037734f, 3045051f, 3123216f, 3130776f, 3138336f, 3145896f, 3156588f, 3164229f, 3171870f, 3179511f, 3523680f, 3532212f, 3540744f, 3549276f, 3557052f, 3565665f, 3574278f, 3582891f, 3657168f, 3666024f, 3674880f, 3683736f, 3690540f, 3699477f, 3708414f, 3717351f, 3790656f, 3799836f, 3809016f, 3818196f, 3824028f, 3833289f, 3842550f, 3851811f}, m3.export());
    }

    @Test
    void testReduceSumAlong() {
        PolyData m1 = new Array1DPolyData(4, 3, 2);
        AtomicInteger integer = new AtomicInteger();
        m1.fill(integer::getAndIncrement);
        PolyData m2 = new Array1DPolyData(1);
        PolyData.sumAlong(m1, 0, m2);
        assertArrayEquals(new float[]{36.0f, 40.0f, 44.0f, 48.0f, 52.0f, 56.0f}, m2.export());
        PolyData.sumAlong(m1, 1, m2);
        assertArrayEquals(new float[]{6.0f, 9.0f, 24.0f, 27.0f, 42.0f, 45.0f, 60.0f, 63.0f}, m2.export());
    }

    @Test
    void testSlice() {
        var data = new Array1DPolyData(3, 3, 3);
        var ai = new AtomicInteger();
        data.fill(ai::incrementAndGet);
        System.out.println(data);
        System.out.println(PolyData.slice(data, 0));
    }
}