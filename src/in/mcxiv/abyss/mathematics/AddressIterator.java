package in.mcxiv.abyss.mathematics;

import java.util.Iterator;

public class AddressIterator implements Iterator<int[]> {

    private int[] address;
    private int[] shape;
    private boolean dirty = false;

    public AddressIterator(int[] shape) {
        this.address = new int[shape.length];
        this.shape = shape;
    }

    @Override
    public int[] next() {
        dirty = true;
        return address;
    }

    @Override
    public boolean hasNext() {
        if (address[0] == shape[0]) return false;
        if (dirty) {
            address[address.length - 1]++;
            for (int i = address.length - 1; i >= 1; i--) {
                if (address[i] >= shape[i]) {
                    address[i] = 0;
                    address[i - 1]++;
                } else break;
            }
            if (address[0] == shape[0]) return false;
        }
        return true;
    }
}
