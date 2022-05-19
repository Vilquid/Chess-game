package cin3.chess.controller;

import cin3.chess.domain.Game;
import cin3.chess.domain.User;
import cin3.chess.repository.*;
import cin3.chess.services.FriendRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController
{
	@Autowired
	private FriendRequestRepository friendRequests;

	@Autowired
	private GameListRepository lastGames;

	@Autowired
	private GameRepository games;

	@Autowired
	private UserRepository users;

	@Autowired
	private FriendRequestService friendService;

	@Autowired
	private GameRequestRepository gameRequests;

	@GetMapping("/")
	public String welcome(@AuthenticationPrincipal User user, final Model model)
	{
		List<User> friends = friendService.getFriendUserList(user);
		List<Game> currentGames = games.findByWhitePlayerOrBlackPlayerAndIsFinish(user, user, false);
		currentGames.removeIf(Game::getFinish);

		List<Game> lastGames = games.findByWhitePlayerOrBlackPlayerAndIsFinish(user, user, true);

		User u = users.findByUsername(user.getUsername());
		u.setPlaying(false);
		u.setLogIn(true);
		users.save(u);

		// pause all the games
		for (Game g: currentGames)
		{
			g.setPause(true);
		}
		games.saveAll(currentGames);

		model.addAttribute("user", user);
		model.addAttribute("friend_requests", friendRequests.findAllByReceiverAndIsAccepted(user, false));
		model.addAttribute("friends", friends);
		model.addAttribute("game_requests", gameRequests.findAllByReceiverAndIsAccepted(user, false));
		model.addAttribute("pending_game_requests", gameRequests.findAllBySenderAndIsAccepted(user, false));
		model.addAttribute("last_games", lastGames);
		model.addAttribute("games", currentGames);
		return "index";
	}

//	@GetMapping("/logout")
//	public String logout(@AuthenticationPrincipal User user, final Model model) // pas utilisée
//	{
//		User u = users.findByUsername(user.getUsername());
//		u.setPlaying(false);
//		u.setLogIn(false);
//		users.save(u);
//		return "redirect:/";
//	}
}
