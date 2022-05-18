package cin3.chess.controller;

import cin3.chess.domain.FriendRequest;
import cin3.chess.form.FriendRequestForm;
import cin3.chess.repository.FriendRequestRepository;
import cin3.chess.repository.UserRepository;
import cin3.chess.services.FriendRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

	@GetMapping("/send")
		List<User> userLists = users.findAll();
		// friends list
		List<FriendRequest> friends = friendRequests.findAllByReceiverAndIsAccepted(currentUser, true);
		List<String> senders = friends.stream().map(FriendRequest::getSender).collect(Collectors.toList());

		userLists.removeIf(user -> {
			return user.getId().equals(currentUser.getId()) || senders.contains(user.getUsername());
		});

		FriendRequestForm form = new FriendRequestForm();
		form.setSender(currentUser.getUsername());

		model.addAttribute("users", userLists);
		model.addAttribute("request", form);

		return "user/send-friend-request";
	}

	@PostMapping("/send")
	public String sendFriendRequestToUser(@Valid @ModelAttribute("request") FriendRequestForm form, BindingResult result) {
		if (result.hasErrors()) {
			return "redirect:";
		}
		System.out.println("sender : " + form.getSender());
		User receiver = users.findByUsername(form.getUsername());

		if (receiver != null) {
			FriendRequest req = new FriendRequest();
			req.setId(form.getId());
			req.setSender(form.getSender());
			req.setReceiver(receiver);
			req.setAccepted(false);

			friendRequests.save(req);
		}
		return "redirect:/";
	}

	@GetMapping("/accept")
	public String acceptFriendRequest(
			@RequestParam Long userId,
			@RequestParam String username
	) {
		Optional<User> currentUser = users.findById(userId);
		if (currentUser.isPresent()) {
			System.out.println(username);
			Optional<FriendRequest> f = friendRequests.findByReceiverAndSenderAndIsAccepted(currentUser.get(), username, false);
			if (f.isPresent()) {
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

	@GetMapping("/decline")
	@Transactional
	public String declineFriendRequest(
			@RequestParam final Long userId,
			@RequestParam final String username
	) {
		Optional<User> currentUser = users.findById(userId);
		currentUser.ifPresent(user -> friendRequests.deleteByReceiverAndSenderAndIsAccepted(user, username, false));

		return "redirect:/";
	}

	@GetMapping("/delete")
	@Transactional
	public String deleteFriend(
			@RequestParam final Long userId,
			@RequestParam final String username
	) {
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
