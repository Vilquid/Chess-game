package cin3.chess.controller;

import cin3.chess.domain.GameRequest;
import cin3.chess.domain.User;
import cin3.chess.repository.GameRequestRepository;
import cin3.chess.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

public class GameRequestController
{
	@Autowired
	private UserRepository users;

	@Autowired
	private GameRequestRepository requests;

	/**
	 * If the user exists, create a new GameRequest object, set the sender to the current user, set the receiver to the user
	 * with the given ID, and save the request.
	 *
	 * @param currentUser The currently logged in user.
	 * @param friendId The id of the user you want to send a friend request to.
	 * @return A redirect to the root page.
	 */
	@GetMapping("/send")
	public String sendFriendRequest(@AuthenticationPrincipal User currentUser, @RequestParam Long friendId)
	{
		Optional<User> friend = users.findById(friendId);
		if (friend.isPresent())
		{
			GameRequest req = new GameRequest();
			req.setAccepted(false);
			req.setSender(currentUser);
			req.setReceiver(friend.get());

			requests.save(req);
		}

		return "redirect:/";
	}

	/**
	 * If the user is logged in, and the user has a friend request from the friend with the given ID, and the request hasn't
	 * been accepted yet, then accept the request and redirect to the game initialization page
	 *
	 * @param currentUser The user who is currently logged in.
	 * @param friendId The id of the user who sent the request
	 * @return A redirect to the game init page.
	 */
	@GetMapping("/accept")
	public String acceptGameRequest(@AuthenticationPrincipal User currentUser, @RequestParam Long friendId)
	{
		Optional<User> sender = users.findById(friendId);

		if (sender.isPresent())
		{
			Optional<GameRequest> req = requests.findBySenderAndReceiverAndIsAccepted(sender.get(), currentUser, false);
			if (req.isPresent())
			{
				req.get().setAccepted(true);
				requests.save(req.get());
			}
		}

		return "redirect:/game/init/" + friendId + "/" + currentUser.getId();
	}

	/**
	 * If the sender exists, delete the request.
	 *
	 * @param senderId The id of the user who sent the request.
	 * @return A redirect to the root path.
	 */
	@GetMapping("/decline")
	@Transactional
	public String decline(@RequestParam Long senderId)
	{
		Optional<User> sender = users.findById(senderId);
		sender.ifPresent(user -> requests.deleteBySenderAndIsAccepted(user, false));

		return "redirect:/";
	}

	/**
	 * It deletes a game request from the database
	 *
	 * @param currentUser the user who is currently logged in
	 * @param friendId the id of the user who sent the request
	 * @return A redirect to the root page.
	 */
	@GetMapping("/cancel")
	@Transactional
	public String cancelGameRequest(@AuthenticationPrincipal User currentUser, @RequestParam Long friendId) // pas encore utilisée
	{
		Optional<User> sender = users.findById(friendId);

		if (sender.isPresent())
		{
			Optional<GameRequest> req = requests.findBySenderAndReceiverAndIsAccepted(sender.get(), currentUser, false);
			if (req.isPresent())
			{
				requests.delete(req.get());
			}
		}

		return "redirect:/";
	}

	/**
	 * It deletes a game request from the database
	 *
	 * @param currentUser the user who is currently logged in
	 * @param friendId the id of the user who sent the request
	 * @return A redirect to the root page.
	 */
	@GetMapping("/delete")
	@Transactional
	public String deleteGameRequest(@AuthenticationPrincipal User currentUser, @RequestParam Long friendId) // pas encore utilisée
	{
		Optional<User> sender = users.findById(friendId);

		if (sender.isPresent())
		{
			Optional<GameRequest> req = requests.findBySenderAndReceiverAndIsAccepted(sender.get(), currentUser, true);
			if (req.isPresent())
			{
				requests.delete(req.get());
			}
		}

		return "redirect:/";
	}
}
