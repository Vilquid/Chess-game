package cin3.chess.controller;

import cin3.chess.domain.FriendRequest;
import cin3.chess.domain.User;
import cin3.chess.form.FriendRequestForm;
import cin3.chess.repository.FriendRequestRepository;
import cin3.chess.repository.UserRepository;
import cin3.chess.services.FriendRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/friends")
public class FriendRequestController
{
	@Autowired
	private FriendRequestService service;

	@Autowired
	private UserRepository users;

	@Autowired
	private FriendRequestRepository friendRequests;

	/**
	 * It gets the friend requests of the user and adds them to the model
	 *
	 * @param user the user that is currently logged in
	 * @param model the model object that will be used to render the view.
	 * @return A list of friend requests
	 * @implNote none used
	 */
//	@GetMapping("/requests")
//	public String getFriends(@AuthenticationPrincipal User user, Model model)
//	{
//		List<FriendRequest> friendRequests = service.getFriendRequests(user);
//		model.addAttribute("friendRequests", friendRequests);
//		return "friends";
//	}

	/**
	 * It gets a list of all users, removes the current user and all users that are already friends, and then passes the
	 * remaining users to the view
	 *
	 * @param currentUser The currently logged in user.
	 * @param model This is the model that will be passed to the view.
	 * @return A list of users that are not the current user and are not already friends with the current user.
	 */
	@GetMapping("/send")
	public String sendFriendRequest(@AuthenticationPrincipal User currentUser, Model model)
	{
//		List<User> userLists = users.findAll();
		List<cin3.chess.domain.User> userLists = users.findAll();
		// friends list
		List<FriendRequest> friends = friendRequests.findAllByReceiverAndIsAccepted(currentUser, true);
//		List<String> senders = friends.stream().map(FriendRequest::getSender).collect(Collectors.toList());
		List<String> senders = friends.stream().map(FriendRequest::getSender).toList();

		userLists.removeIf(user -> user.getId().equals(currentUser.getId()) || senders.contains(user.getUsername()));

		FriendRequestForm form = new FriendRequestForm();
		form.setSender(currentUser.getUsername());

		model.addAttribute("users", userLists);
		model.addAttribute("request", form);

		return "user/send-friend-request";
	}

	/**
	 * It takes a form object, validates it, and if it's valid, it creates a new friend request object and saves it to the
	 * database
	 *
	 * @param form The form object that will be used to store the data from the form.
	 * @param result The BindingResult object that holds the result of the validation and binding and contains errors that may
	 * have occurred.
	 * @return A redirect to the root path.
	 */
	@PostMapping("/send")
	public String sendFriendRequestToUser(@Valid @ModelAttribute("request") FriendRequestForm form, BindingResult result)
	{
		if (result.hasErrors())
		{
			return "redirect:";
		}
		System.out.println("sender : " + form.getSender());
		User receiver = users.findByUsername(form.getUsername());

		if (receiver != null)
		{
			FriendRequest req = new FriendRequest();
			req.setId(form.getId());
			req.setSender(form.getSender());
			req.setReceiver(receiver);
			req.setAccepted(false);

			friendRequests.save(req);
		}
		return "redirect:/";
	}

	/**
	 * It takes in a userId and a username, finds the user with the given userId, finds the friend request with the given
	 * username and the current user, and sets the friend request to accepted
	 *
	 * @param userId the id of the current user
	 * @param username the username of the person who sent the friend request
	 * @return A redirect to the home page.
	 */
	@GetMapping("/accept")
	public String acceptFriendRequest(@RequestParam Long userId, @RequestParam String username)
	{
		Optional<User> currentUser = users.findById(userId);
		if (currentUser.isPresent())
		{
			System.out.println(username);
			Optional<FriendRequest> f = friendRequests.findByReceiverAndSenderAndIsAccepted(currentUser.get(), username, false);
			if (f.isPresent())
			{
				User friend = users.findByUsername(username);
				f.get().setAccepted(true);
				// create the back request
				FriendRequest back = new FriendRequest();
				back.setAccepted(true);
				back.setSender(currentUser.get().getUsername());
				back.setReceiver(friend);

				friendRequests.save(back);
				friendRequests.save(f.get());
			}
		}

		return "redirect:/";
	}

	/**
	 * It deletes a friend request from the database
	 *
	 * @param userId The id of the user who is currently logged in.
	 * @param username The username of the user who sent the friend request
	 * @return A redirect to the home page.
	 */
	@GetMapping("/decline")
	@Transactional
	public String declineFriendRequest(@RequestParam final Long userId, @RequestParam final String username)
	{
		Optional<User> currentUser = users.findById(userId);
		currentUser.ifPresent(user -> friendRequests.deleteByReceiverAndSenderAndIsAccepted(user, username, false));

		return "redirect:/";
	}

	/**
	 * It deletes the friendship between the current user and the user with the username passed in as a parameter
	 *
	 * @param userId The id of the current user
	 * @param username The username of the friend you want to delete
	 * @return A redirect to the root page.
	 */
	@GetMapping("/delete")
	@Transactional
	public String deleteFriend(@RequestParam final Long userId, @RequestParam final String username)
	{
		System.out.println(userId);
		System.out.println(username);

		Optional<User> currentUser = users.findById(userId);
		if(currentUser.isPresent()) {
			friendRequests.deleteByReceiverAndSenderAndIsAccepted(currentUser.get(), username, true);

			User friend = users.findByUsername(username);
			friendRequests.deleteByReceiverAndSenderAndIsAccepted(friend, currentUser.get().getUsername(), true);
		}

		return "redirect:/";
	}
}
