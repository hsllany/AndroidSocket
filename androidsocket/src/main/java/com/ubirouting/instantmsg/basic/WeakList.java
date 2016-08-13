package com.ubirouting.instantmsg.basic;

import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Yang Tao on 16/6/20.
 */
public class WeakList<T> implements List<T> {

    private List<WeakReference<T>> wkrList = new LinkedList<>();

    @Override
    public void add(int location, T object) {
        wkrList.add(location, new WeakReference<T>(object));
    }

    @Override
    public boolean add(T object) {
        return wkrList.add(new WeakReference<T>(object));
    }

    @Override
    public boolean addAll(int location, Collection<? extends T> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        wkrList.clear();
    }

    @Override
    public boolean contains(Object object) {

        Iterator<WeakReference<T>> itr = wkrList.iterator();

        while (itr.hasNext()) {
            WeakReference<T> item = itr.next();

            T activity = item.get();

            if (activity == null) {
                itr.remove();
                continue;
            } else {
                if (activity == object) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T get(int location) {
        WeakReference<T> item = wkrList.get(location);
        T activity = item.get();
        if (activity != null)
            return activity;
        return null;
    }

    @Override
    public int indexOf(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<T> listIterator() {
        return null;
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int location) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int location) {
        WeakReference<T> wkr = wkrList.remove(location);
        return wkr.get();
    }

    @Override
    public boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T set(int location, T object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return wkrList.size();
    }

    @NonNull
    @Override
    public List<T> subList(int start, int end) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public <T> T[] toArray(T[] array) {
        throw new UnsupportedOperationException();
    }
}
