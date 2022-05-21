package cin3.chess.repository;

import cin3.chess.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public abstract class AuthorityRepository implements JpaRepository<Authority, Long>
{
	public abstract Authority findByAuthority(String name);
}
