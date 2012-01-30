package com.mindalliance.channels.playbook.support.util;

import java.util.*;

public class CountedSet {

    private List<Basket> list = new ArrayList<Basket>();
    private Hashtable<String, Basket> hash = new Hashtable<String, Basket>();
    private HashSet set = new HashSet();
    private boolean sorted = false;
    private int size = 0;

    private class Basket {
        Object object;
        int count;

        public Basket(Object object, int count) {
            this.object = object;
            this.count = count;
        }

        public boolean equals(Object obj) {
            return object.equals(((Basket) obj).object);
        }

        public int hashCode() {
            return object.hashCode();
        }
    }

    private Comparator<Basket> countComparator = new Comparator<Basket>() {
        public int compare(Basket basket1, Basket basket2) {
            return basket2.count - basket1.count;
		}
	};

    public CountedSet() {}

    public int size() {
        return size;
    }

    public Object get(int i) {

        if (!sorted) {
            sortByCount();
        }

        return (list.get(i)).object;
    }

    /**
     * get frequency of ith object
     */
    public int getCount(int i) {
        if (!sorted) {
            sortByCount();
        }

        return ((Basket) list.get(i)).count;
    }

    /**
     * get sum of all counts
     */
    public int getSumCount() {
        int sum = 0;
        for (int i = 0, n = size(); i < n; i++) {
            sum += getCount(i);
        }

        return sum;
    }

    /**
     * adds an object to the set
     */
    public void add(Object object) {
        add(object, 1);
    }

    public void addAll(Collection coll) {
        for (Object item : coll) {
            add(item);
        }
    }

    /**
     * adds an object to the set with an initial count, or if object already
     * in counted set then count is incremented by 1.
     */
    public void add(Object object, int count) {

        Basket basket = hash.get(object.toString());
        if (basket != null) {
            basket.count += 1;
        } else {
            basket = new Basket(object, count);
            hash.put(object.toString(), basket);
            size += 1;
            sorted = false;
        }
    }

    /**
     * The countedSets are equal if their inner sets are equal
     */
    public boolean equals(Object obj) {
        return set.equals(((CountedSet) obj).set);
    }

    public List toList() {
        List list = new ArrayList();
        for (int i=0; i < this.size(); i++) {
            list.add(this.get(i));
        }
        return list;
    }

    /**
     * sort by descending count
     */
    private void sortByCount() {

        list.clear();
        for (Basket basket : hash.values()) {
            list.add(basket);
        }

        Collections.sort(list, countComparator);
        sorted = true;
    }

}