package squidpony.squidmath;

import squidpony.annotation.Beta;

import java.io.Serializable;
import java.util.SortedSet;

/**
 * A generic method of holding a probability table to determine weighted random
 * outcomes.
 *
 * The weights do not need to add up to any particular value, they will be
 * normalized when choosing a random entry.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 *
 * @param <T> The type of object to be held in the table
 */
@Beta
public class ProbabilityTable<T> implements Serializable {
    private static final long serialVersionUID = -1307656083434154736L;
    private final OrderedMap<T, Integer> table;
    private RNG rng;
    private int total;

    /**
     * Creates a new probability table.
     */
    public ProbabilityTable() {
        this(new StatefulRNG());
    }

    /**
     * Creates a new probability table with the provided source of randomness
     * used.
     *
     * @param rng the source of randomness
     */
    public ProbabilityTable(RNG rng) {
        this.rng = rng;
        table = new OrderedMap<>();
        total = 0;
    }

    /**
     * Returns an object randomly based on assigned weights.
     *
     * Returns null if no elements have been put in the table.
     *
     * @return the chosen object or null
     */
    public T random() {
        if (table.isEmpty()) {
            return null;
        }
        int index = rng.nextInt(total);
        for (T t : table.keySet()) {
            index -= table.get(t);
            if (index < 0) {
                return t;
            }
        }
        return null;//something went wrong, shouldn't have been able to get all the way through without finding an item
    }

    /**
     * Adds the given item to the table.
     *
     * Weight must be greater than 0.
     *
     * @param item the object to be added
     * @param weight the weight to be given to the added object
     */
    public void add(T item, int weight) {
        Integer i = table.get(item);
        if (i == null) {
            i = weight;
        } else {
            i += weight;
        }
        table.put(item, i);
        total += weight;
    }

    /**
     * Returns the weight of the item if the item is in the table. Returns zero
     * of the item is not in the table.
     *
     * @param item the item searched for
     * @return the weight of the item, or zero
     */
    public int weight(T item) {
        Integer i = table.get(item);
        return i == null ? 0 : i;
    }

    /**
     * Provides a set of the items in this table, without reference to their
     * weight.
     *
     * @return a "sorted" set of all items stored, really sorted in insertion order
     */
    public SortedSet<T> items() {
        return table.keySet();
    }
}
