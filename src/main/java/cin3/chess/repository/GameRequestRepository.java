package cin3.chess.repository;

import cin3.chess.domain.GameRequest;
import cin3.chess.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRequestRepository extends JpaRepository<GameRequest, Long> {
	void deleteBySenderAndIsAccepted(User sender, Boolean isAccepted);
	List<GameRequest> findAllByReceiverAndIsAccepted(User receiver, Boolean isAccepted);
	List<GameRequest> findAllBySenderAndIsAccepted(User sender, Boolean isAccepted);
	Optional<GameRequest> findBySenderAndIsAccepted(User sender, Boolean isAccepted);
	Optional<GameRequest> findBySenderAndReceiverAndIsAccepted(User sender, User receiver, Boolean isAccepted);
}

