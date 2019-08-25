package online.patologia.flatsearcher.repositories;

import online.patologia.flatsearcher.model.Flat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlatRepository extends CrudRepository<Flat,Long> {
    @Query(value = "SELECT * FROM FLAT WHERE LINK LIKE ?1%", nativeQuery = true)
    Flat findByLinkStartsWith(String link);
}
