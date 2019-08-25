package online.patologia.flatsearcher.repositories;

import online.patologia.flatsearcher.model.Flat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlatRepository extends CrudRepository<Flat,Long> {
    Flat findByLink(String link);
}
