package cin3.chess.repository;

import cin3.chess.domain.Move;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoveRepository extends JpaRepository<Move, Long> {
}
