package cin3.chess.listener;

import cin3.chess.domain.User;
import cin3.chess.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class LoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent>
{
	@Autowired
	private UserRepository users;

	@Override
	public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event)
	{
		UserDetails user = (UserDetails) event.getAuthentication().getPrincipal();
		User u = users.findByUsername(user.getUsername());
		if (u != null)
		{
			u.setLogIn(true);
			users.save(u);
			System.out.println("new log in true");
		}
	}
}
