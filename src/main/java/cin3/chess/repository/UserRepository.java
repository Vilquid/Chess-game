package cin3.chess.repository;

import cin3.chess.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>
{
	public User findByUsername(String username);
	public User findByEmail(String email);
}
