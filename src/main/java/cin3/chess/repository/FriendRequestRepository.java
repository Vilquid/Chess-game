package cin3.chess.repository;

import cin3.chess.domain.FriendRequest;
import cin3.chess.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long>
{
	// list of friends
	List<FriendRequest> findAllByReceiverAndIsAccepted(User receiver, Boolean isAccepted);

	void deleteByReceiverAndSenderAndIsAccepted(User receiver, String sender, Boolean isAccepted);
	Optional<FriendRequest> findByReceiverAndSenderAndIsAccepted(User receiver, String sender, Boolean isAccepted);
}
