import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * Class for a simple hash map.
 * @author Linda Tang lt2899
 * @version 1.0 November 19, 2022
 */
public class MyHashMap<K extends Comparable<K>, V> implements MyMap<K, V> {
    // Helpful list of primes available at:
    // https://www2.cs.arizona.edu/icon/oddsends/primes.htm
    private static final int[] primes = new int[] {
            101, 211, 431, 863, 1733, 3467, 6947, 13901, 27803, 55609, 111227,
            222461 };
    private static final double MAX_LOAD_FACTOR = 0.75;
    private Entry<K, V>[] table;
    private int primeIndex, numEntries;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        table = new Entry[primes[primeIndex]];
    }

    /**
     * Returns the number of buckets in this MyHashMap.
     * @return the number of buckets in this MyHashMap
     */
    public int getTableSize() {
        return table.length;
    }

    /**
     * Returns the number of key-value mappings in this map.
     * @return the number of key-value mappings in this map
     */
    @Override
    public int size() {
        return numEntries;
    }

    /**
     * Returns true if this map contains no key-value mappings.
     * @return true if this map contains no key-value mappings
     */
    @Override
    public boolean isEmpty() {
        return numEntries == 0;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     * @param  key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if this
     *         map contains no mapping for the key
     */
    @Override
    public V get(K key) {
        int tableIndex = key.hashCode() % getTableSize();
        Entry<K,V> ptr = table[tableIndex];
        while(ptr != null && !ptr.key.equals(key)) {
            ptr = ptr.next;
        }
        if(ptr == null) {
            return null;
        } else {
            return ptr.value;
        }
    }

    /**
     * Associates the specified value with the specified key in this map. If the
     * map previously contained a mapping for the key, the old value is replaced
     * by the specified value.
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no
     *         mapping for key
     */
    @Override
    public V put(K key, V value) {
        //find the hashcode of the key
        int tableIndex = key.hashCode() % getTableSize();
        Entry<K,V> ptr = table[tableIndex];
        //check if key is already in map
        if(get(key) != null) {
            //if key is in map, update value and return old value
            V oldVal = get(key);
            while(!ptr.key.equals(key)) {
                ptr = ptr.next;
            }
            ptr.value = value;
            return oldVal;
        } else {
            //if key is not in map, insert pair at head of list
            Entry<K,V> newEntry = new Entry<>(key, value);
            newEntry.next = ptr;
            table[tableIndex] = newEntry;
            numEntries++;
        }
        //check if rehash is needed (load factor is exceeded and table isn't maxed)
        if(getLoadFactor() > MAX_LOAD_FACTOR && getTableSize() < primes[primes.length-1]){
            rehash();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void rehash() {
        // TODO - see spec
        primeIndex++;
        Entry<K,V>[] newTable = new Entry[primes[primeIndex]];
        for(int i = 0; i < table.length; i++) {
            while(table[i] != null) {
                Entry<K,V> newEntry = new Entry<>(table[i].key, table[i].value);
                int newTableIndex = table[i].key.hashCode() % newTable.length;
                Entry<K,V> ptr = newTable[newTableIndex];
                newEntry.next = ptr;
                newTable[newTableIndex] = newEntry;
                table[i] = table[i].next;
            }
        }
        table = newTable;
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     * @param key the key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no
     *         mapping for key
     */
    @Override
    public V remove(K key) {
        //check if key is in the map
        if(get(key) == null) {
            return null;
        }
        int tableIndex = key.hashCode() % getTableSize();
        Entry<K,V> curr = table[tableIndex];
        //check if key is at the head
        if(curr.key.equals(key)) {
            table[tableIndex] = curr.next;
        } else {
            Entry<K,V> prev = table[tableIndex];
            curr = prev.next;
            while(curr != null && !curr.key.equals(key)){
                curr = curr.next;
                prev = prev.next;
            }
            if(curr.next != null) {
                prev.next = curr.next;
            } else {
                prev.next = null;
            }
        }
        numEntries--;
        return curr.value;
    }

    /**
     * Returns the load factor of this MyHashMap, defined as the number of
     * entries / table size.
     * @return the load factor of this MyHashMap
     */
    public double getLoadFactor() {
        return (double)numEntries / primes[primeIndex];
    }

    /**
     * Returns the maximum length of a chain in this MyHashMap. This value
     * provides information about how well the hash function is working. With a
     * max load factor of 0.75, we would like to see a max chain length close
     * to 1.
     * @return the maximum length of a chain in this MyHashMap
     */
    public int computeMaxChainLength() {
        int maxChainLength = 0;
        for (Entry<K, V> chain : table) {
            if (chain != null) {
                int currentChainLength = 0;
                Entry<K, V> chainPtr = chain;
                while (chainPtr != null) {
                    currentChainLength++;
                    chainPtr = chainPtr.next;
                }
                if (currentChainLength > maxChainLength) {
                    maxChainLength = currentChainLength;
                }
            }
        }
        return maxChainLength;
    }

    /**
     * Returns a string representation of this MyHashMap for tables with up
     * to and including 1000 entries.
     * @return a string representation of this MyHashMap
     */
    public String toString() {
        if (numEntries > 1000) {
            return "HashMap too large to represent as a string.";
        }
        if (numEntries == 0) {
            return "HashMap is empty.";
        }
        int maxIndex;
        for (maxIndex = table.length - 1; maxIndex >= 0; maxIndex--) {
            if (table[maxIndex] != null) {
                break;
            }
        }
        int maxIndexWidth = String.valueOf(maxIndex).length();
        StringBuilder builder = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        for (int i = 0; i < table.length; i++) {
            Entry<K, V> chain = table[i];
            if (chain != null) {
                int indexWidth = String.valueOf(i).length();
                builder.append(" ".repeat(maxIndexWidth - indexWidth));
                builder.append(i);
                builder.append(": ");
                while (chain != null) {
                    builder.append(chain);
                    if (chain.next != null) {
                        builder.append(" -> ");
                    }
                    chain = chain.next;
                }
                builder.append(newLine);
            }
        }
        return builder.toString();
    }

    /**
     * Returns an iterator over the Entries in this MyHashMap in the order
     * in which they appear in the table.
     * @return an iterator over the Entries in this MyHashMap
     */
    public Iterator<Entry<K, V>> iterator() {
        return new MapItr();
    }

    private class MapItr implements Iterator<Entry<K, V>> {
        private Entry<K, V> current;
        private int index;

        MapItr() {
            advanceToNextEntry();
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Entry<K, V> next() {
            Entry<K, V> e = current;
            if (current.next == null) {
                index++;
                advanceToNextEntry();
            } else {
                current = current.next;
            }
            return e;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void advanceToNextEntry() {
            while (index < table.length && table[index] == null) {
                index++;
            }
            current = index < table.length ? table[index] : null;
        }
    }

    public static void main(String[] args) {
        MyHashMap<String, Integer> map = new MyHashMap<>();
        int upperLimit = 100;
        int expectedSum = 0;
        for (int i = 1; i <= upperLimit; i++) {
            map.put(String.valueOf(i), i);
            expectedSum += i;
        }
        System.out.println("Size            : " + map.size());
        System.out.println("Table size      : " + map.getTableSize());
        System.out.println("Load factor     : " + map.getLoadFactor());
        System.out.println("Max chain length: " + map.computeMaxChainLength());
        System.out.println();
        System.out.println("Expected sum: " + expectedSum);
        System.out.println(map);

        int receivedSum = 0;
        for (int i = 1; i <= upperLimit; i++) {
            receivedSum += map.get(String.valueOf(i));
        }
        System.out.println("Received sum: " + receivedSum);

        expectedSum = 0;
        for (int i = 1; i <= upperLimit; i++) {
            int newValue = upperLimit - i + 1;
            map.put(String.valueOf(i), newValue);
            expectedSum += newValue;
        }
        System.out.println("Size            : " + map.size());
        System.out.println("Table size      : " + map.getTableSize());
        System.out.println("Load factor     : " + map.getLoadFactor());
        System.out.println("Max chain length: " + map.computeMaxChainLength());
        System.out.println();
        System.out.println("Expected sum: " + expectedSum);

        receivedSum = 0;
        for (int i = 1; i <= upperLimit; i++) {
            receivedSum += map.get(String.valueOf(i));
        }
        System.out.println("Received sum: " + receivedSum);

        receivedSum = 0;
        Iterator<Entry<String, Integer>> iter = map.iterator();
        while (iter.hasNext()) {
            receivedSum += iter.next().value;
        }
        System.out.println("Received sum: " + receivedSum);

        receivedSum = 0;
        for (int i = 1; i <= upperLimit; i++) {
            receivedSum += map.remove(String.valueOf(i));
        }
        System.out.println("Received sum: " + receivedSum);
        System.out.println("Size            : " + map.size());
        System.out.println("Table size      : " + map.getTableSize());
        System.out.println("Load factor     : " + map.getLoadFactor());
        System.out.println("Max chain length: " + map.computeMaxChainLength());
        System.out.println();
        System.out.println("Expected sum: " + expectedSum);
    }
}
