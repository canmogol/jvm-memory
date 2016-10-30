package com.fererlab;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class PersonWeakReference extends WeakReference<Person> {

    private PersonCleaner personCleaner;

    public PersonWeakReference(Person person, PersonCleaner personCleaner, ReferenceQueue<? super Person> referenceQueue) {
        super(person, referenceQueue);
        this.personCleaner = personCleaner;
    }

    public void clean(){
        personCleaner.clean();
    }
}
