package com.fererlab;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

public class FinalizePerson extends PhantomReference<Person> {

    /**
     * Creates a new phantom reference that refers to the given object and
     * is registered with the given queue.
     * <p/>
     * <p> It is possible to create a phantom reference with a <tt>null</tt>
     * queue, but such a reference is completely useless: Its <tt>get</tt>
     * method will always return null and, since it does not have a queue, it
     * will never be enqueued.
     *
     * @param person         the object the new phantom reference will refer to
     * @param referenceQueue the queue with which the reference is to be registered,
     */
    public FinalizePerson(Person person, ReferenceQueue<? super Person> referenceQueue) {
        super(person, referenceQueue);
    }

    public void cleanup() {
        System.out.println("finalizing person class");
    }

}
