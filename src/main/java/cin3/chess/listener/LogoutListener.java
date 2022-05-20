package cin3.chess.listener;

import cin3.chess.domain.User;
import cin3.chess.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class LogoutListener implements ApplicationListener<LogoutSuccessEvent>
{
	@Autowired
	private UserRepository users;

	/**
	 * When a user logs out, set their logIn field to false
	 *
	 * @param event The event that was fired.
	 */
	@Override
	public void onApplicationEvent(LogoutSuccessEvent event)
	{
		String login = event.getAuthentication().getName();

		User u = users.findByUsername(login);
		if (u != null)
		{
			u.setLogIn(false);
			users.save(u);
		}
	}
}
