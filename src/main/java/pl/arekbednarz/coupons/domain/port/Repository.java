package pl.arekbednarz.coupons.domain.port;

import java.util.Optional;


public interface Repository<T, ID> {

	Optional<T> findById(ID id);

	T save(T aggregate);
}
