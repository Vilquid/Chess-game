package cin3.chess.controller;

import cin3.chess.domain.GameRequest;
import cin3.chess.repository.GameRequestRepository;
import cin3.chess.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
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

}
