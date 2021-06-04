package fileoperations.sorts;

import java.util.PriorityQueue;

/**
 * The type Min heap.
 */
public class MinHeap {
    final private PriorityQueue<String> minHeap;
    final private Object lock = new Object();

    /**
     * Instantiates a new Min heap.
     *
     * @param size the size
     */
    public MinHeap(final int size) {
        this.minHeap = new PriorityQueue<>(size);
    }

    /**
     * Add element.
     *
     * @param element the element
     */
    public void addElement(final String element) {
            synchronized (lock) {
                minHeap.add(element);
            }
    }

    /**
     * Remove element string.
     *
     * @return the string
     */
    public String removeElement() {
        if (minHeap.size() == 0) {
            throw new ArrayIndexOutOfBoundsException();
        } else {
            synchronized (lock) {
                return minHeap.remove();
            }
        }
    }

    /**
     * Is empty boolean.
     *
     * @return the boolean
     */
    public boolean isEmpty() {
        synchronized (lock) {
            return minHeap.isEmpty();
        }
    }

    /**
     * Size int.
     *
     * @return the int
     */
    public int size() {
        synchronized (lock) {
            return minHeap.size();
        }
    }
}
