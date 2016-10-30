package com.fererlab;

import org.junit.Assert;
import org.junit.Test;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class ReferenceTest {

    /**
     * WeakReference: associate meta data with another type
     */
    @Test
    public void weakReference() {
        /*
         * Person person            ---------> (PERSON#1)
         * WeakReference<Person> wr ---------> WR
         * Person p                 ---------> (PERSON#1)
         */
        Person person = new Person();
        WeakReference<Person> personWeakReference = new WeakReference<>(person);
        Person p = personWeakReference.get();
        System.out.println("person = " + person + " p = " + p);
        Assert.assertEquals(person, p);
    }

    @Test
    public void weakReferenceNull() {
        /*
         * Person person            ---------> (PERSON#1)
         * WeakReference<Person> wr ---------> WR
         * Person person            ---------> null
         * GC
         * Person p                 ---------> null
         */
        Person person = new Person();
        WeakReference<Person> personWeakReference = new WeakReference<>(person);
        person = null;
        System.gc();
        Person p = personWeakReference.get();
        System.out.println("person = " + person + " p = " + p);
        Assert.assertEquals(person, p);
    }

    /**
     * WeakReference allows us to add meta data to an object,
     * example to one usage might be a class that is marked as final
     * which we cannot extend the class, we can use WeakHashMap for that
     */
    @Test
    public void weakHashMap() {
        // key in a WeakHashMap is a weak reference to an object not a strong reference
        // the value of this key is the object's meta data
        // when there is no more strong references the key released and value goes away
        /*
         *                (1)
         * Person person --> (PERSON#1)
         *                   /
         *         --(2)----/
         *        / (weak reference to object)
         *       /
         * ------------------------------------------------
         * |  key  |  value  |
         * ------------------------------------------------
         *              \
         *               \ (reference to this object's meta data)
         *                \
         *                 -(3)--> MetaData(PERSON#1)
         *
         *  when the 'person' strong reference removed, then the key removed so the value and meta data removed
         */
        Person person = new Person();
        PersonMetaData personMetaDataNew = new PersonMetaData();

        WeakHashMap<Person, PersonMetaData> weakHashMap = new WeakHashMap<>();
        weakHashMap.put(person, personMetaDataNew);
        System.out.println("weakHashMap = " + weakHashMap);

        PersonMetaData personMetaDataGet = weakHashMap.get(person);
        System.out.println("personMetaDataNew = " + personMetaDataNew + " personMetaDataGet = " + personMetaDataGet);

        Assert.assertEquals(personMetaDataNew, personMetaDataGet);

        person = null;
        System.gc();

        System.out.println("weakHashMap = " + weakHashMap);
        System.out.println("personMetaDataNew = " + personMetaDataNew + " personMetaDataGet = " + personMetaDataGet);

        Assert.assertFalse(weakHashMap.containsValue(personMetaDataNew));
        Assert.assertFalse(weakHashMap.containsValue(personMetaDataGet));

    }

    /**
     * Refe renceQueue: when all the strong references cleared,
     * reference object is added to the reference queue
     * Can be used to attach clean up code
     */
    @Test
    public void referenceQueue() {
        Person person = new Person();
        ReferenceQueue<Person> referenceQueue = new ReferenceQueue<>();

        PersonCleaner personCleaner = new PersonCleaner();
        PersonWeakReference personWeakReference = new PersonWeakReference(person, personCleaner, referenceQueue);

        person = null;
        System.gc();

        try {
            // there should be only one reference in the queue which is the PersonWeakReference instance
            Reference reference = referenceQueue.remove(); // remove() method is a Blocking call
            Assert.assertNotNull(reference);
            Assert.assertTrue(reference instanceof PersonWeakReference);

            PersonWeakReference wr = (PersonWeakReference) reference;
            wr.clean();

            // there should not be any other reference in the queue
            Reference nullReference = referenceQueue.poll();
            Assert.assertNull(nullReference);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * SoftReference: can be used for caching
     * It is expected that the SoftReference should behave just like the WeakReference,
     * but the difference is, event if the object does not have any strong references,
     * as long as there is enough memory, garbage collector will not collect the object
     * if there is a SoftReference available.
     */
    @Test
    public void softReference() {

        /*
         * Person person            ---------> (PERSON#1)
         * SoftReference<Person> wr ---------> SR
         * Person person            ---------> null
         * GC
         * Person p                 ---------> (PERSON#1)
         */
        Person person = new Person();
        SoftReference<Person> personSoftReference = new SoftReference<>(person);

        person = null;
        System.gc();

        // Person object did not collected by garbage collector because
        // there is enough memory and a SoftReference to it
        Person p = personSoftReference.get();
        System.out.println("person = " + person + " p = " + p);

        Assert.assertTrue(person == null);
        Assert.assertTrue(p != null);
        System.out.println("p = " + p);

    }

    /**
     * PhantomReference: interaction with the garbage collector,
     * can be used to monitor when the object is collected,
     * may be do some extra work when the object is being collected
     * instead of finalize we can use PhantomReference
     * <p/>
     * when we call get() method of PhantomReference we always get null
     */
    @Test
    public void phantomReference() {
        ReferenceQueue<Person> queue = new ReferenceQueue<>();
        List<FinalizePerson> finalizePersonList = new ArrayList<>();
        List<Person> people = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Person person = new Person();
            people.add(person);
            finalizePersonList.add(new FinalizePerson(person, queue));
        }

        people = null;
        System.gc();

        Reference<? extends Person> referenceFromQueue;
        while ((referenceFromQueue = queue.poll()) != null) {
            Assert.assertTrue(referenceFromQueue instanceof FinalizePerson);
            FinalizePerson finalizePersonFromQueue = (FinalizePerson) referenceFromQueue;
            finalizePersonFromQueue.cleanup();
            referenceFromQueue.clear();
        }

    }


}
