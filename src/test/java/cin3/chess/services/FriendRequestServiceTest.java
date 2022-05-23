package cin3.chess.services;

import cin3.chess.domain.FriendRequest;
import cin3.chess.domain.User;
import cin3.chess.repository.FriendRequestRepository;
import cin3.chess.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FriendRequestServiceTest
{
	private FriendRequestRepository friendRequests;

	@Test
	public void testGetFriendUserList()
	{
		User receiver = new User();
		List<FriendRequest> friends = friendRequests.findAllByReceiverAndIsAccepted(receiver, true);
		List<User> userFriends = new ArrayList<>();

		assertThat(friends.contains(userFriends)).isNotNull();
	}

	@Test
	public void testGetFriendRequests()
	{
//		User sender = new User();
//		List<FriendRequest> friends = friendRequests.findAllBySenderAndIsAccepted(sender, true);
//		List<User> userSender = new ArrayList<>();
//
//		assertThat(friendRequests.contains(userSender)).isNotNull();
	}
}
