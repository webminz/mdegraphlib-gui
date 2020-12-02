package no.hvl.past.webui.backend.repositories;

import no.hvl.past.webui.backend.entities.AbstractEntity;

import java.util.*;

public abstract class AbstractRepository<E extends AbstractEntity> {

    private final Map<Long, E> db = new HashMap<>();

    private long counter = 0;

    private Long nextId() {
        Long result = counter;
        counter++;
        return result;
    }

    public void save(E e) {
        if (e.getId() == null) {
            e.setId(nextId());
        }
        this.db.put(e.getId(), e);
    }

    public void saveAll(Collection<E> es) {
        for (E e : es) {
            this.save(e);
        }
    }

    public void delete(E e) {
        if (e.getId() != null) {
            this.db.remove(e);
            e.setId(null);
        }
    }

    public List<E> findAll() {
        return new ArrayList<>(this.db.values());
    }

    public E findById(Long id) {
        return this.db.get(id);
    }

    public long count() {
        return this.db.entrySet().size();
    }


}
