package cin3.chess.repository;

import cin3.chess.domain.Game;
import cin3.chess.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long>
{
	List<Game> findByWhitePlayerOrBlackPlayerAndIsFinish(User white, User black, Boolean isFinish);
}
