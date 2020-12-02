package no.hvl.past.webui.backend.quickRuleEngine;

import no.hvl.past.webui.transfer.quickRuleEngine.domain.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class EventQueue {

    private final List<Event> events = new ArrayList<>();
    private final ReentrantLock mutex = new ReentrantLock(true);
    private final Semaphore empty = new Semaphore(0);

    public void add(Event event) {
        this.mutex.lock();
        this.events.add(event);
        this.empty.release();
        this.mutex.unlock();
    }

    public Event remove() throws InterruptedException {
        this.empty.acquire();
        this.mutex.lock();
        Event event = events.get(0);
        events.remove(0);
        this.mutex.unlock();
        return event;
    }

    public Optional<Event> peekFirst() {
        this.mutex.lock();
        Optional<Event> first = events.isEmpty() ? Optional.empty() : Optional.of(events.get(0));
        this.mutex.unlock();
        return first;
    }

    public Optional<Event> peekLast() {
        this.mutex.lock();
        Optional<Event> last = events.isEmpty() ? Optional.empty() : Optional.of(events.get(events.size() - 1));
        this.mutex.unlock();
        return last;
    }

    public boolean isEmpty() {
        this.mutex.lock();
        boolean isEmpty = events.isEmpty();
        this.mutex.unlock();
        return isEmpty;
    }



}
