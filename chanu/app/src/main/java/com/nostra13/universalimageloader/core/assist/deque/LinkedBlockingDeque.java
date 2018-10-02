/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

package com.nostra13.universalimageloader.core.assist.deque;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An optionally-bounded {@linkplain BlockingDeque blocking deque} based on
 * linked nodes.
 *
 * <p> The optional capacity bound constructor argument serves as a
 * way to prevent excessive expansion. The capacity, if unspecified,
 * is equal to {@link Integer#MAX_VALUE}.  Linked nodes are
 * dynamically created upon each insertion unless this would bring the
 * deque above capacity.
 *
 * <p>Most operations run in constant time (ignoring time spent
 * blocking).  Exceptions include {@link #remove(Object) remove},
 * {@link #removeFirstOccurrence removeFirstOccurrence}, {@link
 * #removeLastOccurrence removeLastOccurrence}, {@link #contains
 * contains}, {@link #iterator iterator.remove()}, and the bulk
 * operations, all of which run in linear time.
 *
 * <p>This class and its iterator implement all of the
 * <em>optional</em> methods of the {@link Collection} and {@link
 * Iterator} interfaces.
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @since 1.6
 * @author  Doug Lea
 * @param <E> the type of elements held in this collection
 */
public class LinkedBlockingDeque<E>
    extends AbstractQueue<E>
    implements BlockingDeque<E>,  java.io.Serializable {

    /*
     * Implemented as a simple doubly-linked list protected by a
     * single lock and using conditions to manage blocking.
     *
     * To implement weakly consistent iterators, it appears we need to
     * keep all Nodes GC-reachable from a predecessor dequeued Node.
     * That would cause two problems:
     * - allow a rogue Iterator to cause unbounded memory retention
     * - cause cross-generational linking of old Nodes to new Nodes if
     *   a Node was tenured while live, which generational GCs have a
     *   hard time dealing with, causing repeated major collections.
     * However, only non-deleted Nodes need to be reachable from
     * dequeued Nodes, and reachability does not necessarily have to
     * be of the kind understood by the GC.  We use the trick of
     * linking a Node that has just been dequeued to itself.  Such a
     * self-link implicitly means to jump to "first" (for next links)
     * or "last" (for prev links).
     */

    /*
     * We have "diamond" multiple interface/abstract class inheritance
     * here, and that introduces ambiguities. Often we want the
     * BlockingDeque javadoc combined with the AbstractQueue
     * implementation, so a lot of method specs are duplicated here.
     */

    private static final long serialVersionUID = -387911632671998426L;

    /** Doubly-linked list node class */
    static final class Node<E> {
        /**
         * The item, or null if this node has been removed.
         */
        E item;

        /**
         * One of:
         * - the real predecessor Node
         * - this Node, meaning the predecessor is tail
         * - null, meaning there is no predecessor
         */
        Node<E> prev;

        /**
         * One of:
         * - the real successor Node
         * - this Node, meaning the successor is head
         * - null, meaning there is no successor
         */
        Node<E> next;

        Node(E x) {
            item = x;
        }
    }

    /**
     * Pointer to first node.
     * Invariant: (first == null && last == null) ||
     *            (first.prev == null && first.item != null)
     */
    transient Node<E> first;

    /**
     * Pointer to last node.
     * Invariant: (first == null && last == null) ||
     *            (last.next == null && last.item != null)
     */
    transient Node<E> last;

    /** Number of items in the deque */
    private transient int count;

    /** Maximum number of items in the deque */
    private final int capacity;

    /** Main lock guarding all access */
    final ReentrantLock lock = new ReentrantLock();

    /** Condition for waiting takes */
    private final Condition notEmpty = lock.newCondition();

    /** Condition for waiting puts */
    private final Condition notFull = lock.newCondition();

    /**
     * Creates a {@code LinkedBlockingDeque} with a capacity of
     * {@link Integer#MAX_VALUE}.
     */
    public LinkedBlockingDeque() {
        this(Integer.MAX_VALUE);
    }

    /**
     * Creates a {@code LinkedBlockingDeque} with the given (fixed) capacity.
     *
     * @param capacity the capacity of this deque
     * @throws IllegalArgumentException if {@code capacity} is less than 1
     */
    public LinkedBlockingDeque(int capacity) {
        if (capacity <= 0) {throw new IllegalArgumentException();}
        this.capacity = capacity;
    }

    /**
     * Creates a {@code LinkedBlockingDeque} with a capacity of
     * {@link Integer#MAX_VALUE}, initially containing the elements of
     * the given collection, added in traversal order of the
     * collection's iterator.
     *
     * @param c the collection of elements to initially contain
     * @throws NullPointerException if the specified collection or any
     *         of its elements are null
     */
    public LinkedBlockingDeque(Collection<? extends E> c) {
        this(Integer.MAX_VALUE);
        final ReentrantLock lock = this.lock;
        lock.lock(); /*// Never contended, but necessary for visibility*/
        try {
            for (E e : c) {
                if (e == null)
                    {throw new NullPointerException();}
                if (!linkLast(new Node<E>(e)))
                    {throw new IllegalStateException("Deque full");}
            }
        } finally {
            lock.unlock();
        }
    }


    /*// Basic linking and unlinking operations, called only while holding lock*/

    /**
     * Links node as first element, or returns false if full.
     */
    private boolean linkFirst(Node<E> node) {
        com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.linkFirst(Node)",this,node);try{/*// assert lock.isHeldByCurrentThread();*/
        if (count >= capacity)
            {{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.linkFirst(Node)",this);return false;}}
        Node<E> f = first;
        node.next = f;
        first = node;
        if (last == null)
            {last = node;}
        else
            {f.prev = node;}
        ++count;
        notEmpty.signal();
        {com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.linkFirst(Node)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.linkFirst(Node)",this,throwable);throw throwable;}
    }

    /**
     * Links node as last element, or returns false if full.
     */
    private boolean linkLast(Node<E> node) {
        com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.linkLast(Node)",this,node);try{/*// assert lock.isHeldByCurrentThread();*/
        if (count >= capacity)
            {{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.linkLast(Node)",this);return false;}}
        Node<E> l = last;
        node.prev = l;
        last = node;
        if (first == null)
            {first = node;}
        else
            {l.next = node;}
        ++count;
        notEmpty.signal();
        {com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.linkLast(Node)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.linkLast(Node)",this,throwable);throw throwable;}
    }

    /**
     * Removes and returns first element, or null if empty.
     */
    private E unlinkFirst() {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.unlinkFirst()",this);try{/*// assert lock.isHeldByCurrentThread();*/
        Node<E> f = first;
        if (f == null)
            {{com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.unlinkFirst()",this);return null;}}
        Node<E> n = f.next;
        E item = f.item;
        f.item = null;
        f.next = f; /*// help GC*/
        first = n;
        if (n == null)
            {last = null;}
        else
            {n.prev = null;}
        --count;
        notFull.signal();
        {com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.unlinkFirst()",this);return item;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.unlinkFirst()",this,throwable);throw throwable;}
    }

    /**
     * Removes and returns last element, or null if empty.
     */
    private E unlinkLast() {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.unlinkLast()",this);try{/*// assert lock.isHeldByCurrentThread();*/
        Node<E> l = last;
        if (l == null)
            {{com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.unlinkLast()",this);return null;}}
        Node<E> p = l.prev;
        E item = l.item;
        l.item = null;
        l.prev = l; /*// help GC*/
        last = p;
        if (p == null)
            {first = null;}
        else
            {p.next = null;}
        --count;
        notFull.signal();
        {com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.unlinkLast()",this);return item;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.unlinkLast()",this,throwable);throw throwable;}
    }

    /**
     * Unlinks x.
     */
    void unlink(Node<E> x) {
        com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.unlink(Node)",this,x);try{/*// assert lock.isHeldByCurrentThread();*/
        Node<E> p = x.prev;
        Node<E> n = x.next;
        if (p == null) {
            unlinkFirst();
        } else if (n == null) {
            unlinkLast();
        } else {
            p.next = n;
            n.prev = p;
            x.item = null;
            /*// Don't mess with x's links.  They may still be in use by*/
            /*// an iterator.*/
            --count;
            notFull.signal();
        }com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.unlink(Node)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.unlink(Node)",this,throwable);throw throwable;}
    }

    /*// BlockingDeque methods*/

    /**
     * @throws IllegalStateException {@inheritDoc}
     * @throws NullPointerException  {@inheritDoc}
     */
    public void addFirst(E e) {
        com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.addFirst(E)",this,e);try{com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.addFirst(E)",this);if (!offerFirst(e))
            {throw new IllegalStateException("Deque full");}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.addFirst(E)",this,throwable);throw throwable;}
    }

    /**
     * @throws IllegalStateException {@inheritDoc}
     * @throws NullPointerException  {@inheritDoc}
     */
    public void addLast(E e) {
        com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.addLast(E)",this,e);try{com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.addLast(E)",this);if (!offerLast(e))
            {throw new IllegalStateException("Deque full");}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.addLast(E)",this,throwable);throw throwable;}
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    public boolean offerFirst(E e) {
        com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offerFirst(E)",this,e);try{if (e == null) {throw new NullPointerException();}
        Node<E> node = new Node<E>(e);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            {com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offerFirst(E)",this);return linkFirst(node);}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offerFirst(E)",this,throwable);throw throwable;}
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    public boolean offerLast(E e) {
        com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offerLast(E)",this,e);try{if (e == null) {throw new NullPointerException();}
        Node<E> node = new Node<E>(e);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            {com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offerLast(E)",this);return linkLast(node);}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offerLast(E)",this,throwable);throw throwable;}
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @throws InterruptedException {@inheritDoc}
     */
    public void putFirst(E e) throws InterruptedException {
        com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.putFirst(E)",this,e);try{if (e == null) {throw new NullPointerException();}
        Node<E> node = new Node<E>(e);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            while (!linkFirst(node))
                {notFull.await();}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.putFirst(E)",this,throwable);throw throwable;}
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @throws InterruptedException {@inheritDoc}
     */
    public void putLast(E e) throws InterruptedException {
        com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.putLast(E)",this,e);try{if (e == null) {throw new NullPointerException();}
        Node<E> node = new Node<E>(e);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            while (!linkLast(node))
                {notFull.await();}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.putLast(E)",this,throwable);throw throwable;}
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @throws InterruptedException {@inheritDoc}
     */
    public boolean offerFirst(E e, long timeout, TimeUnit unit)
        throws InterruptedException {
        com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offerFirst(E,long,java.util.concurrent.TimeUnit)",this,e,timeout,unit);try{if (e == null) {throw new NullPointerException();}
        Node<E> node = new Node<E>(e);
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (!linkFirst(node)) {
                if (nanos <= 0)
                    {{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offerFirst(E,long,java.util.concurrent.TimeUnit)",this);return false;}}
                nanos = notFull.awaitNanos(nanos);
            }
            {com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offerFirst(E,long,java.util.concurrent.TimeUnit)",this);return true;}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offerFirst(E,long,java.util.concurrent.TimeUnit)",this,throwable);throw throwable;}
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @throws InterruptedException {@inheritDoc}
     */
    public boolean offerLast(E e, long timeout, TimeUnit unit)
        throws InterruptedException {
        com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offerLast(E,long,java.util.concurrent.TimeUnit)",this,e,timeout,unit);try{if (e == null) {throw new NullPointerException();}
        Node<E> node = new Node<E>(e);
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (!linkLast(node)) {
                if (nanos <= 0)
                    {{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offerLast(E,long,java.util.concurrent.TimeUnit)",this);return false;}}
                nanos = notFull.awaitNanos(nanos);
            }
            {com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offerLast(E,long,java.util.concurrent.TimeUnit)",this);return true;}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offerLast(E,long,java.util.concurrent.TimeUnit)",this,throwable);throw throwable;}
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public E removeFirst() {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.removeFirst()",this);try{E x = pollFirst();
        if (x == null) {throw new NoSuchElementException();}
        {com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.removeFirst()",this);return x;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.removeFirst()",this,throwable);throw throwable;}
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public E removeLast() {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.removeLast()",this);try{E x = pollLast();
        if (x == null) {throw new NoSuchElementException();}
        {com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.removeLast()",this);return x;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.removeLast()",this,throwable);throw throwable;}
    }

    public E pollFirst() {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.pollFirst()",this);try{final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            {com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.pollFirst()",this);return unlinkFirst();}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.pollFirst()",this,throwable);throw throwable;}
    }

    public E pollLast() {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.pollLast()",this);try{final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            {com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.pollLast()",this);return unlinkLast();}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.pollLast()",this,throwable);throw throwable;}
    }

    public E takeFirst() throws InterruptedException {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.takeFirst()",this);try{final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            E x;
            while ( (x = unlinkFirst()) == null)
                {notEmpty.await();}
            {com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.takeFirst()",this);return x;}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.takeFirst()",this,throwable);throw throwable;}
    }

    public E takeLast() throws InterruptedException {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.takeLast()",this);try{final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            E x;
            while ( (x = unlinkLast()) == null)
                {notEmpty.await();}
            {com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.takeLast()",this);return x;}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.takeLast()",this,throwable);throw throwable;}
    }

    public E pollFirst(long timeout, TimeUnit unit)
        throws InterruptedException {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.pollFirst(long,java.util.concurrent.TimeUnit)",this,timeout,unit);try{long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            E x;
            while ( (x = unlinkFirst()) == null) {
                if (nanos <= 0)
                    {{com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.pollFirst(long,java.util.concurrent.TimeUnit)",this);return null;}}
                nanos = notEmpty.awaitNanos(nanos);
            }
            {com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.pollFirst(long,java.util.concurrent.TimeUnit)",this);return x;}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.pollFirst(long,java.util.concurrent.TimeUnit)",this,throwable);throw throwable;}
    }

    public E pollLast(long timeout, TimeUnit unit)
        throws InterruptedException {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.pollLast(long,java.util.concurrent.TimeUnit)",this,timeout,unit);try{long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            E x;
            while ( (x = unlinkLast()) == null) {
                if (nanos <= 0)
                    {{com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.pollLast(long,java.util.concurrent.TimeUnit)",this);return null;}}
                nanos = notEmpty.awaitNanos(nanos);
            }
            {com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.pollLast(long,java.util.concurrent.TimeUnit)",this);return x;}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.pollLast(long,java.util.concurrent.TimeUnit)",this,throwable);throw throwable;}
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public E getFirst() {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.getFirst()",this);try{E x = peekFirst();
        if (x == null) {throw new NoSuchElementException();}
        {com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.getFirst()",this);return x;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.getFirst()",this,throwable);throw throwable;}
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public E getLast() {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.getLast()",this);try{E x = peekLast();
        if (x == null) {throw new NoSuchElementException();}
        {com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.getLast()",this);return x;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.getLast()",this,throwable);throw throwable;}
    }

    public E peekFirst() {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.peekFirst()",this);try{final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            {com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.peekFirst()",this);return (first == null) ? null : first.item;}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.peekFirst()",this,throwable);throw throwable;}
    }

    public E peekLast() {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.peekLast()",this);try{final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            {com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.peekLast()",this);return (last == null) ? null : last.item;}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.peekLast()",this,throwable);throw throwable;}
    }

    public boolean removeFirstOccurrence(Object o) {
        com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.removeFirstOccurrence(java.lang.Object)",this,o);try{if (o == null) {{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.removeFirstOccurrence(java.lang.Object)",this);return false;}}
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            for (Node<E> p = first; p != null; p = p.next) {
                if (o.equals(p.item)) {
                    unlink(p);
                    {com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.removeFirstOccurrence(java.lang.Object)",this);return true;}
                }
            }
            {com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.removeFirstOccurrence(java.lang.Object)",this);return false;}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.removeFirstOccurrence(java.lang.Object)",this,throwable);throw throwable;}
    }

    public boolean removeLastOccurrence(Object o) {
        com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.removeLastOccurrence(java.lang.Object)",this,o);try{if (o == null) {{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.removeLastOccurrence(java.lang.Object)",this);return false;}}
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            for (Node<E> p = last; p != null; p = p.prev) {
                if (o.equals(p.item)) {
                    unlink(p);
                    {com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.removeLastOccurrence(java.lang.Object)",this);return true;}
                }
            }
            {com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.removeLastOccurrence(java.lang.Object)",this);return false;}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.removeLastOccurrence(java.lang.Object)",this,throwable);throw throwable;}
    }

    /*// BlockingQueue methods*/

    /**
     * Inserts the specified element at the end of this deque unless it would
     * violate capacity restrictions.  When using a capacity-restricted deque,
     * it is generally preferable to use method {@link #offer offer}.
     *
     * <p>This method is equivalent to {@link #addLast}.
     *
     * @throws IllegalStateException if the element cannot be added at this
     *         time due to capacity restrictions
     * @throws NullPointerException if the specified element is null
     */
    public boolean add(E e) {
        com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.add(E)",this,e);try{addLast(e);
        {com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.add(E)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.add(E)",this,throwable);throw throwable;}
    }

    /**
     * @throws NullPointerException if the specified element is null
     */
    public boolean offer(E e) {
        com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offer(E)",this,e);try{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offer(E)",this);return offerLast(e);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offer(E)",this,throwable);throw throwable;}
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @throws InterruptedException {@inheritDoc}
     */
    public void put(E e) throws InterruptedException {
        com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.put(E)",this,e);try{putLast(e);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.put(E)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.put(E)",this,throwable);throw throwable;}
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @throws InterruptedException {@inheritDoc}
     */
    public boolean offer(E e, long timeout, TimeUnit unit)
        throws InterruptedException {
        com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offer(E,long,java.util.concurrent.TimeUnit)",this,e,timeout,unit);try{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offer(E,long,java.util.concurrent.TimeUnit)",this);return offerLast(e, timeout, unit);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.offer(E,long,java.util.concurrent.TimeUnit)",this,throwable);throw throwable;}
    }

    /**
     * Retrieves and removes the head of the queue represented by this deque.
     * This method differs from {@link #poll poll} only in that it throws an
     * exception if this deque is empty.
     *
     * <p>This method is equivalent to {@link #removeFirst() removeFirst}.
     *
     * @return the head of the queue represented by this deque
     * @throws NoSuchElementException if this deque is empty
     */
    public E remove() {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.remove()",this);try{com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.remove()",this);return removeFirst();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.remove()",this,throwable);throw throwable;}
    }

    public E poll() {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.poll()",this);try{com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.poll()",this);return pollFirst();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.poll()",this,throwable);throw throwable;}
    }

    public E take() throws InterruptedException {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.take()",this);try{com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.take()",this);return takeFirst();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.take()",this,throwable);throw throwable;}
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.poll(long,java.util.concurrent.TimeUnit)",this,timeout,unit);try{com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.poll(long,java.util.concurrent.TimeUnit)",this);return pollFirst(timeout, unit);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.poll(long,java.util.concurrent.TimeUnit)",this,throwable);throw throwable;}
    }

    /**
     * Retrieves, but does not remove, the head of the queue represented by
     * this deque.  This method differs from {@link #peek peek} only in that
     * it throws an exception if this deque is empty.
     *
     * <p>This method is equivalent to {@link #getFirst() getFirst}.
     *
     * @return the head of the queue represented by this deque
     * @throws NoSuchElementException if this deque is empty
     */
    public E element() {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.element()",this);try{com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.element()",this);return getFirst();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.element()",this,throwable);throw throwable;}
    }

    public E peek() {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.peek()",this);try{com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.peek()",this);return peekFirst();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.peek()",this,throwable);throw throwable;}
    }

    /**
     * Returns the number of additional elements that this deque can ideally
     * (in the absence of memory or resource constraints) accept without
     * blocking. This is always equal to the initial capacity of this deque
     * less the current {@code size} of this deque.
     *
     * <p>Note that you <em>cannot</em> always tell if an attempt to insert
     * an element will succeed by inspecting {@code remainingCapacity}
     * because it may be the case that another thread is about to
     * insert or remove an element.
     */
    public int remainingCapacity() {
        com.mijack.Xlog.logMethodEnter("int com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.remainingCapacity()",this);try{final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            {com.mijack.Xlog.logMethodExit("int com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.remainingCapacity()",this);return capacity - count;}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.remainingCapacity()",this,throwable);throw throwable;}
    }

    /**
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     */
    public int drainTo(Collection<? super E> c) {
        com.mijack.Xlog.logMethodEnter("int com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.drainTo(java.util.Collection)",this,c);try{com.mijack.Xlog.logMethodExit("int com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.drainTo(java.util.Collection)",this);return drainTo(c, Integer.MAX_VALUE);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.drainTo(java.util.Collection)",this,throwable);throw throwable;}
    }

    /**
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     */
    public int drainTo(Collection<? super E> c, int maxElements) {
        com.mijack.Xlog.logMethodEnter("int com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.drainTo(java.util.Collection,int)",this,c,maxElements);try{if (c == null)
            {throw new NullPointerException();}
        if (c == this)
            {throw new IllegalArgumentException();}
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int n = Math.min(maxElements, count);
            for (int i = 0; i < n; i++) {
                c.add(first.item);   /*// In this order, in case add() throws.*/
                unlinkFirst();
            }
            {com.mijack.Xlog.logMethodExit("int com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.drainTo(java.util.Collection,int)",this);return n;}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.drainTo(java.util.Collection,int)",this,throwable);throw throwable;}
    }

    /*// Stack methods*/

    /**
     * @throws IllegalStateException {@inheritDoc}
     * @throws NullPointerException  {@inheritDoc}
     */
    public void push(E e) {
        com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.push(E)",this,e);try{addFirst(e);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.push(E)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.push(E)",this,throwable);throw throwable;}
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public E pop() {
        com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.pop()",this);try{com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.pop()",this);return removeFirst();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.pop()",this,throwable);throw throwable;}
    }

    /*// Collection methods*/

    /**
     * Removes the first occurrence of the specified element from this deque.
     * If the deque does not contain the element, it is unchanged.
     * More formally, removes the first element {@code e} such that
     * {@code o.equals(e)} (if such an element exists).
     * Returns {@code true} if this deque contained the specified element
     * (or equivalently, if this deque changed as a result of the call).
     *
     * <p>This method is equivalent to
     * {@link #removeFirstOccurrence(Object) removeFirstOccurrence}.
     *
     * @param o element to be removed from this deque, if present
     * @return {@code true} if this deque changed as a result of the call
     */
    public boolean remove(Object o) {
        com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.remove(java.lang.Object)",this,o);try{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.remove(java.lang.Object)",this);return removeFirstOccurrence(o);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.remove(java.lang.Object)",this,throwable);throw throwable;}
    }

    /**
     * Returns the number of elements in this deque.
     *
     * @return the number of elements in this deque
     */
    public int size() {
        com.mijack.Xlog.logMethodEnter("int com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.size()",this);try{final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            {com.mijack.Xlog.logMethodExit("int com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.size()",this);return count;}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.size()",this,throwable);throw throwable;}
    }

    /**
     * Returns {@code true} if this deque contains the specified element.
     * More formally, returns {@code true} if and only if this deque contains
     * at least one element {@code e} such that {@code o.equals(e)}.
     *
     * @param o object to be checked for containment in this deque
     * @return {@code true} if this deque contains the specified element
     */
    public boolean contains(Object o) {
        com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.contains(java.lang.Object)",this,o);try{if (o == null) {{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.contains(java.lang.Object)",this);return false;}}
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            for (Node<E> p = first; p != null; p = p.next)
                {if (o.equals(p.item))
                    {{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.contains(java.lang.Object)",this);return true;}}}
            {com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.contains(java.lang.Object)",this);return false;}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.contains(java.lang.Object)",this,throwable);throw throwable;}
    }

    /*
     * TODO: Add support for more efficient bulk operations.
     *
     * We don't want to acquire the lock for every iteration, but we
     * also want other threads a chance to interact with the
     * collection, especially when count is close to capacity.
     */

/*//     /***/
/*//      * Adds all of the elements in the specified collection to this*/
/*//      * queue.  Attempts to addAll of a queue to itself result in*/
/*//      * {@code IllegalArgumentException}. Further, the behavior of*/
/*//      * this operation is undefined if the specified collection is*/
/*//      * modified while the operation is in progress.*/
/*//      **/
/*//      * @param c collection containing elements to be added to this queue*/
/*//      * @return {@code true} if this queue changed as a result of the call*/
/*//      * @throws ClassCastException            {@inheritDoc}*/
/*//      * @throws NullPointerException          {@inheritDoc}*/
/*//      * @throws IllegalArgumentException      {@inheritDoc}*/
/*//      * @throws IllegalStateException         {@inheritDoc}*/
/*//      * @see #add(Object)*/
/*//      */
/*//     public boolean addAll(Collection<? extends E> c) {*/
/*//         if (c == null)*/
/*//             throw new NullPointerException();*/
/*//         if (c == this)*/
/*//             throw new IllegalArgumentException();*/
/*//         final ReentrantLock lock = this.lock;*/
/*//         lock.lock();*/
/*//         try {*/
/*//             boolean modified = false;*/
/*//             for (E e : c)*/
/*//                 if (linkLast(e))*/
/*//                     modified = true;*/
/*//             return modified;*/
/*//         } finally {*/
/*//             lock.unlock();*/
/*//         }*/
/*//     }*/

    /**
     * Returns an array containing all of the elements in this deque, in
     * proper sequence (from first to last element).
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this deque.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this deque
     */
    public Object[] toArray() {
        com.mijack.Xlog.logMethodEnter("[java.lang.Object com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.toArray()",this);try{final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] a = new Object[count];
            int k = 0;
            for (Node<E> p = first; p != null; p = p.next)
                {a[k++] = p.item;}
            {com.mijack.Xlog.logMethodExit("[java.lang.Object com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.toArray()",this);return a;}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[java.lang.Object com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.toArray()",this,throwable);throw throwable;}
    }

    /**
     * Returns an array containing all of the elements in this deque, in
     * proper sequence; the runtime type of the returned array is that of
     * the specified array.  If the deque fits in the specified array, it
     * is returned therein.  Otherwise, a new array is allocated with the
     * runtime type of the specified array and the size of this deque.
     *
     * <p>If this deque fits in the specified array with room to spare
     * (i.e., the array has more elements than this deque), the element in
     * the array immediately following the end of the deque is set to
     * {@code null}.
     *
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     *
     * <p>Suppose {@code x} is a deque known to contain only strings.
     * The following code can be used to dump the deque into a newly
     * allocated array of {@code String}:
     *
     * <pre>
     *     String[] y = x.toArray(new String[0]);</pre>
     *
     * Note that {@code toArray(new Object[0])} is identical in function to
     * {@code toArray()}.
     *
     * @param a the array into which the elements of the deque are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose
     * @return an array containing all of the elements in this deque
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this deque
     * @throws NullPointerException if the specified array is null
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        com.mijack.Xlog.logMethodEnter("[java.lang.Object com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.toArray([java.lang.Object)",this,a);try{final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (a.length < count)
                {a = (T[])java.lang.reflect.Array.newInstance
                    (a.getClass().getComponentType(), count);}

            int k = 0;
            for (Node<E> p = first; p != null; p = p.next)
                {a[k++] = (T)p.item;}
            if (a.length > k)
                {a[k] = null;}
            {com.mijack.Xlog.logMethodExit("[java.lang.Object com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.toArray([java.lang.Object)",this);return a;}
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[java.lang.Object com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.toArray([java.lang.Object)",this,throwable);throw throwable;}
    }

    public String toString() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.toString()",this);try{final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Node<E> p = first;
            if (p == null)
                {{com.mijack.Xlog.logMethodExit("java.lang.String com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.toString()",this);return "[]";}}

            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (;;) {
                E e = p.item;
                sb.append(e == this ? "(this Collection)" : e);
                p = p.next;
                if (p == null)
                    {{com.mijack.Xlog.logMethodExit("java.lang.String com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.toString()",this);return sb.append(']').toString();}}
                sb.append(',').append(' ');
            }
        } finally {
            lock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.toString()",this,throwable);throw throwable;}
    }

    /**
     * Atomically removes all of the elements from this deque.
     * The deque will be empty after this call returns.
     */
    public void clear() {
        com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.clear()",this);try{final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            for (Node<E> f = first; f != null; ) {
                f.item = null;
                Node<E> n = f.next;
                f.prev = null;
                f.next = null;
                f = n;
            }
            first = last = null;
            count = 0;
            notFull.signalAll();
        } finally {
            lock.unlock();
        }com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.clear()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.clear()",this,throwable);throw throwable;}
    }

    /**
     * Returns an iterator over the elements in this deque in proper sequence.
     * The elements will be returned in order from first (head) to last (tail).
     *
     * <p>The returned iterator is a "weakly consistent" iterator that
     * will never throw {@link java.util.ConcurrentModificationException
     * ConcurrentModificationException}, and guarantees to traverse
     * elements as they existed upon construction of the iterator, and
     * may (but is not guaranteed to) reflect any modifications
     * subsequent to construction.
     *
     * @return an iterator over the elements in this deque in proper sequence
     */
    public Iterator<E> iterator() {
        com.mijack.Xlog.logMethodEnter("java.util.Iterator com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.iterator()",this);try{com.mijack.Xlog.logMethodExit("java.util.Iterator com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.iterator()",this);return new Itr();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.Iterator com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.iterator()",this,throwable);throw throwable;}
    }

    /**
     * Returns an iterator over the elements in this deque in reverse
     * sequential order.  The elements will be returned in order from
     * last (tail) to first (head).
     *
     * <p>The returned iterator is a "weakly consistent" iterator that
     * will never throw {@link java.util.ConcurrentModificationException
     * ConcurrentModificationException}, and guarantees to traverse
     * elements as they existed upon construction of the iterator, and
     * may (but is not guaranteed to) reflect any modifications
     * subsequent to construction.
     *
     * @return an iterator over the elements in this deque in reverse order
     */
    public Iterator<E> descendingIterator() {
        com.mijack.Xlog.logMethodEnter("java.util.Iterator com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.descendingIterator()",this);try{com.mijack.Xlog.logMethodExit("java.util.Iterator com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.descendingIterator()",this);return new DescendingItr();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.Iterator com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.descendingIterator()",this,throwable);throw throwable;}
    }

    /**
     * Base class for Iterators for LinkedBlockingDeque
     */
    private abstract class AbstractItr implements Iterator<E> {
        /**
         * The next node to return in next()
         */
         Node<E> next;

        /**
         * nextItem holds on to item fields because once we claim that
         * an element exists in hasNext(), we must return item read
         * under lock (in advance()) even if it was in the process of
         * being removed when hasNext() was called.
         */
        E nextItem;

        /**
         * Node returned by most recent call to next. Needed by remove.
         * Reset to null if this element is deleted by a call to remove.
         */
        private Node<E> lastRet;

        abstract Node<E> firstNode();
        abstract Node<E> nextNode(Node<E> n);

        AbstractItr() {
            /*// set to initial position*/
            final ReentrantLock lock = LinkedBlockingDeque.this.lock;
            lock.lock();
            try {
                next = firstNode();
                nextItem = (next == null) ? null : next.item;
            } finally {
                lock.unlock();
            }
        }

        /**
         * Returns the successor node of the given non-null, but
         * possibly previously deleted, node.
         */
        private Node<E> succ(Node<E> n) {
            com.mijack.Xlog.logMethodEnter("Node com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$AbstractItr.succ(Node)",this,n);try{/*// Chains of deleted nodes ending in null or self-links*/
            /*// are possible if multiple interior nodes are removed.*/
            for (;;) {
                Node<E> s = nextNode(n);
                if (s == null)
                    {{com.mijack.Xlog.logMethodExit("Node com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$AbstractItr.succ(Node)",this);return null;}}
                else if (s.item != null)
                    {{com.mijack.Xlog.logMethodExit("Node com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$AbstractItr.succ(Node)",this);return s;}}
                else if (s == n)
                    {{com.mijack.Xlog.logMethodExit("Node com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$AbstractItr.succ(Node)",this);return firstNode();}}
                else
                    {n = s;}
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("Node com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$AbstractItr.succ(Node)",this,throwable);throw throwable;}
        }

        /**
         * Advances next.
         */
        void advance() {
            com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$AbstractItr.advance()",this);try{final ReentrantLock lock = LinkedBlockingDeque.this.lock;
            lock.lock();
            try {
                /*// assert next != null;*/
                next = succ(next);
                nextItem = (next == null) ? null : next.item;
            } finally {
                lock.unlock();
            }com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$AbstractItr.advance()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$AbstractItr.advance()",this,throwable);throw throwable;}
        }

        public boolean hasNext() {
            com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$AbstractItr.hasNext()",this);try{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$AbstractItr.hasNext()",this);return next != null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$AbstractItr.hasNext()",this,throwable);throw throwable;}
        }

        public E next() {
            com.mijack.Xlog.logMethodEnter("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$AbstractItr.next()",this);try{if (next == null)
                {throw new NoSuchElementException();}
            lastRet = next;
            E x = nextItem;
            advance();
            {com.mijack.Xlog.logMethodExit("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$AbstractItr.next()",this);return x;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("E com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$AbstractItr.next()",this,throwable);throw throwable;}
        }

        public void remove() {
            com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$AbstractItr.remove()",this);try{Node<E> n = lastRet;
            if (n == null)
                {throw new IllegalStateException();}
            lastRet = null;
            final ReentrantLock lock = LinkedBlockingDeque.this.lock;
            lock.lock();
            try {
                if (n.item != null)
                    {unlink(n);}
            } finally {
                lock.unlock();
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$AbstractItr.remove()",this,throwable);throw throwable;}
        }
    }

    /** Forward iterator */
    private class Itr extends AbstractItr {
        Node<E> firstNode() { com.mijack.Xlog.logMethodEnter("Node com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$Itr.firstNode()",this);try{com.mijack.Xlog.logMethodExit("Node com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$Itr.firstNode()",this);return first;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("Node com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$Itr.firstNode()",this,throwable);throw throwable;} }
        Node<E> nextNode(Node<E> n) { com.mijack.Xlog.logMethodEnter("Node com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$Itr.nextNode(Node)",this,n);try{com.mijack.Xlog.logMethodExit("Node com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$Itr.nextNode(Node)",this);return n.next;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("Node com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$Itr.nextNode(Node)",this,throwable);throw throwable;} }
    }

    /** Descending iterator */
    private class DescendingItr extends AbstractItr {
        Node<E> firstNode() { com.mijack.Xlog.logMethodEnter("Node com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$DescendingItr.firstNode()",this);try{com.mijack.Xlog.logMethodExit("Node com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$DescendingItr.firstNode()",this);return last;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("Node com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$DescendingItr.firstNode()",this,throwable);throw throwable;} }
        Node<E> nextNode(Node<E> n) { com.mijack.Xlog.logMethodEnter("Node com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$DescendingItr.nextNode(Node)",this,n);try{com.mijack.Xlog.logMethodExit("Node com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$DescendingItr.nextNode(Node)",this);return n.prev;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("Node com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>$DescendingItr.nextNode(Node)",this,throwable);throw throwable;} }
    }

    /**
     * Save the state of this deque to a stream (that is, serialize it).
     *
     * @serialData The capacity (int), followed by elements (each an
     * {@code Object}) in the proper order, followed by a null
     * @param s the stream
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {
        com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.writeObject(java.io.ObjectOutputStream)",this,s);try{final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            /*// Write out capacity and any hidden stuff*/
            s.defaultWriteObject();
            /*// Write out all elements in the proper order.*/
            for (Node<E> p = first; p != null; p = p.next)
                {s.writeObject(p.item);}
            /*// Use trailing null as sentinel*/
            s.writeObject(null);
        } finally {
            lock.unlock();
        }com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.writeObject(java.io.ObjectOutputStream)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.writeObject(java.io.ObjectOutputStream)",this,throwable);throw throwable;}
    }

    /**
     * Reconstitute this deque from a stream (that is,
     * deserialize it).
     * @param s the stream
     */
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.readObject(java.io.ObjectInputStream)",this,s);try{s.defaultReadObject();
        count = 0;
        first = null;
        last = null;
        /*// Read in all elements and place in queue*/
        for (;;) {
            @SuppressWarnings("unchecked")
            E item = (E)s.readObject();
            if (item == null)
                {break;}
            add(item);
        }com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.readObject(java.io.ObjectInputStream)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.assist.deque.LinkedBlockingDeque<E>.readObject(java.io.ObjectInputStream)",this,throwable);throw throwable;}
    }

}
