package cin3.chess.repository;

import cin3.chess.domain.GameList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameListRepository extends JpaRepository<GameList, Long>
{
	public GameList findByGameId(Long gameId);
	List<GameList> findByWinnerOrLooser(String winner, String looser);
}
