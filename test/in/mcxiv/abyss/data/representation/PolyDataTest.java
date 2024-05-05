package in.mcxiv.abyss.data.representation;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static in.mcxiv.abyss.data.representation.Array1DPolyData.n;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class PolyDataTest {
    @Test
    void testAdd() {
        PolyData m1 = new Array1DPolyData(2, 3, 5);
        m1.fill(3);
        PolyData m2 = new Array1DPolyData(2, 3, 5);
        AtomicInteger integer = new AtomicInteger();
        m2.fill(integer::incrementAndGet);

        PolyData m3 = m1.add(m2, n());
        assertArrayEquals(new float[]{4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f, 11.0f, 12.0f, 13.0f, 14.0f, 15.0f, 16.0f, 17.0f, 18.0f, 19.0f, 20.0f, 21.0f, 22.0f, 23.0f, 24.0f, 25.0f, 26.0f, 27.0f, 28.0f, 29.0f, 30.0f, 31.0f, 32.0f, 33.0f}, m3.export());
    }

    @Test
    void testCross() {
        PolyData m1 = new Array1DPolyData(3, 2);
        m1.fill(List.of(0, 1, 2, 3, 4, 5).iterator()::next);
        PolyData m2 = new Array1DPolyData(2, 4);
        m2.fill(List.of(0, 1, 2, 3, 4, 5, 6, 7).iterator()::next);
        PolyData m3 = new Array1DPolyData(5, 5);
        m1.cross(m2, m3);
        assertArrayEquals(new float[]{4.0f, 5.0f, 6.0f, 7.0f, 12.0f, 17.0f, 22.0f, 27.0f, 20.0f, 29.0f, 38.0f, 47.0f}, m3.export());
    }

    @Test
    void testConvolveOperation() {
        AtomicInteger integer = new AtomicInteger();

        PolyData m1 = new Array1DPolyData(5, 5);
        m1.fill(integer::incrementAndGet);

        PolyData m2 = new Array1DPolyData(3, 3);
        m2.fill(List.of(1, 1, -1, 1, 1, -1, 1, 1, -1).iterator()::next);

        assertArrayEquals(new float[]{15.0f, 18.0f, 21.0f, 30.0f, 33.0f, 36.0f, 45.0f, 48.0f, 51.0f}, m1.convolveOperation(m2).export());
    }

    @Test
    void testImageConvolveForward() {
        AtomicInteger integer = new AtomicInteger();

        PolyData m1 = new Array1DPolyData(10, 4, 4, 3/*:channels*/);
        m1.fill(integer::getAndIncrement);

        PolyData m2 = new Array1DPolyData(3, 3, 3 /*:channels*/, 8);
        m2.fill(integer::getAndIncrement);

        var m3 = m1.imageConvolveForward(m2);

        System.out.println(m3);
        assertArrayEquals(new float[]{269280, 269712, 270144, 270576, 271008, 271440, 271872, 272304, 316584, 317097, 317610, 318123, 318636, 319149, 319662, 320175, 458496, 459252, 460008, 460764, 461520, 462276, 463032, 463788, 505800, 506637, 507474, 508311, 509148, 509985, 510822, 511659, 1026144, 1027872, 1029600, 1031328, 1033056, 1034784, 1036512, 1038240, 1073448, 1075257, 1077066, 1078875, 1080684, 1082493, 1084302, 1086111, 1215360, 1217412, 1219464, 1221516, 1223568, 1225620, 1227672, 1229724, 1262664, 1264797, 1266930, 1269063, 1271196, 1273329, 1275462, 1277595, 1783008, 1786032, 1789056, 1792080, 1795104, 1798128, 1801152, 1804176, 1830312, 1833417, 1836522, 1839627, 1842732, 1845837, 1848942, 1852047, 1972224, 1975572, 1978920, 1982268, 1985616, 1988964, 1992312, 1995660, 2019528, 2022957, 2026386, 2029815, 2033244, 2036673, 2040102, 2043531, 2539872, 2544192, 2548512, 2552832, 2557152, 2561472, 2565792, 2570112, 2587176, 2591577, 2595978, 2600379, 2604780, 2609181, 2613582, 2617983, 2729088, 2733732, 2738376, 2743020, 2747664, 2752308, 2756952, 2761596, 2776392, 2781117, 2785842, 2790567, 2795292, 2800017, 2804742, 2809467, 3296736, 3302352, 3307968, 3313584, 3319200, 3324816, 3330432, 3336048, 3344040, 3349737, 3355434, 3361131, 3366828, 3372525, 3378222, 3383919, 3485952, 3491892, 3497832, 3503772, 3509712, 3515652, 3521592, 3527532, 3533256, 3539277, 3545298, 3551319, 3557340, 3563361, 3569382, 3575403, 4053600, 4060512, 4067424, 4074336, 4081248, 4088160, 4095072, 4101984, 4100904, 4107897, 4114890, 4121883, 4128876, 4135869, 4142862, 4149855, 4242816, 4250052, 4257288, 4264524, 4271760, 4278996, 4286232, 4293468, 4290120, 4297437, 4304754, 4312071, 4319388, 4326705, 4334022, 4341339, 4810464, 4818672, 4826880, 4835088, 4843296, 4851504, 4859712, 4867920, 4857768, 4866057, 4874346, 4882635, 4890924, 4899213, 4907502, 4915791, 4999680, 5008212, 5016744, 5025276, 5033808, 5042340, 5050872, 5059404, 5046984, 5055597, 5064210, 5072823, 5081436, 5090049, 5098662, 5107275, 5567328, 5576832, 5586336, 5595840, 5605344, 5614848, 5624352, 5633856, 5614632, 5624217, 5633802, 5643387, 5652972, 5662557, 5672142, 5681727, 5756544, 5766372, 5776200, 5786028, 5795856, 5805684, 5815512, 5825340, 5803848, 5813757, 5823666, 5833575, 5843484, 5853393, 5863302, 5873211, 6324192, 6334992, 6345792, 6356592, 6367392, 6378192, 6388992, 6399792, 6371496, 6382377, 6393258, 6404139, 6415020, 6425901, 6436782, 6447663, 6513408, 6524532, 6535656, 6546780, 6557904, 6569028, 6580152, 6591276, 6560712, 6571917, 6583122, 6594327, 6605532, 6616737, 6627942, 6639147, 7081056, 7093152, 7105248, 7117344, 7129440, 7141536, 7153632, 7165728, 7128360, 7140537, 7152714, 7164891, 7177068, 7189245, 7201422, 7213599, 7270272, 7282692, 7295112, 7307532, 7319952, 7332372, 7344792, 7357212, 7317576, 7330077, 7342578, 7355079, 7367580, 7380081, 7392582, 7405083}, m3.export());
    }

    @Test
    void testReduceSumAlong() {
        PolyData m1 = new Array1DPolyData(4, 3, 2);
        AtomicInteger integer = new AtomicInteger();
        m1.fill(integer::getAndIncrement);
        PolyData m2 = new Array1DPolyData(1);
        m1.sumAlong(m1, 0, m2);
        assertArrayEquals(new float[]{36.0f, 40.0f, 44.0f, 48.0f, 52.0f, 56.0f}, m2.export());
        m1.sumAlong(m1, 1, m2);
        assertArrayEquals(new float[]{6.0f, 9.0f, 24.0f, 27.0f, 42.0f, 45.0f, 60.0f, 63.0f}, m2.export());
    }

    @Test
    void testReduce() {
        var m1 = Array1DPolyData.fromArray(
                3, 4, 2, 5,
                5, 6, 3, 4,
                6, 7, 6, 5,
                4, 6, 5, 7,
                4, 5, 6, 3,
                4, 6, 5, 7
        ).reshape(6, 4);
        assertArrayEquals(new float[]{6f, 7f, 6f, 7f}, m1.reduceOperation(m1, n(), 0, Math::max).export());
        assertArrayEquals(new float[]{5f, 6f, 7f, 7f, 6f, 7f}, m1.reduceOperation(m1, n(), 1, Math::max).export());
    }

    @Test
    void testIndex() {
        var m1 = Array1DPolyData.fromArray(
                3, 4, 2, 5,
                5, 6, 3, 4,
                6, 7, 6, 5,
                4, 6, 5, 7,
                4, 5, 6, 3,
                4, 6, 5, 7
        ).reshape(6, 4);
        assertArrayEquals(new float[]{2f, 2f, 2f, 3f}, m1.indexOperation(m1, n(), 0, Math::max).export());
        assertArrayEquals(new float[]{3f, 1f, 1f, 3f, 2f, 3f}, m1.indexOperation(m1, n(), 1, Math::max).export());
    }

    @Test
    void testSlice() {
        var data = new Array1DPolyData(3, 3, 3);
        var ai = new AtomicInteger();
        data.fill(ai::incrementAndGet);
        System.out.println(data);
        System.out.println(data.slice(0));
    }
}