package cin3.chess.services;

import cin3.chess.domain.FriendRequest;
import cin3.chess.domain.User;
import cin3.chess.repository.FriendRequestRepository;
import cin3.chess.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendRequestService
{
	@Autowired
	private UserRepository users;

	@Autowired
	private FriendRequestRepository friendRequests;

	/**
	 * Get all the friend requests that have been accepted by the user, and return a list of the users that sent those
	 * requests.
	 *
	 * @param receiver The user who is receiving the friend request.
	 * @return A list of users that are friends with the user that is passed in.
	 */
	public List<User> getFriendUserList(User receiver)
	{
		List<FriendRequest> friends = friendRequests.findAllByReceiverAndIsAccepted(receiver, true);
		List<User> userFriends = new ArrayList<>();

		for (FriendRequest friend: friends)
		{
			userFriends.add(users.findByUsername(friend.getSender()));
		}

		return userFriends;
	}
}
