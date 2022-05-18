package cin3.chess.controller;


@Controller
public class IndexController()
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
}
