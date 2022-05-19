package cin3.chess.repository;

import cin3.chess.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class AuthorityRepository implements JpaRepository<Authority, Long>
{
	public abstract Authority findByAuthority(String name);
}
