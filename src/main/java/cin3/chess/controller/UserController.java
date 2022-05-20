package cin3.chess.controller;

import cin3.chess.form.UserForm;
import cin3.chess.services.DbUserDetailsService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.logging.Logger;

@Controller
public class UserController
{
	//	private Logger logger = LoggerFactory.getLogger(UserController.class);
	private final Logger logger = (Logger) LoggerFactory.getLogger(UserController.class);
	@Autowired
	private DbUserDetailsService userService;

	@GetMapping("/login")
	public String login()
	{
		return "user/login";
	}

	/**
	 * The function takes a model object as a parameter, adds a user object to the model, and returns the name of the view to
	 * be rendered
	 *
	 * @param model The model is a map of values that is passed to the view.
	 * @return A string that is the name of the view.
	 */
	@GetMapping("/register")
	public String register(Model model)
	{
		model.addAttribute("user", userService.createForm(null));
		return "user/register";
	}

	/**
	 * If the form has errors, return the form with the errors. Otherwise, save the form and redirect to the login page
	 *
	 * @param form The form object that will be used to populate the form.
	 * @param result The BindingResult must come right after the model object that is validated or else Spring fails to
	 * validate the object and throws an exception.
	 * @param model The model is a Map of key-value pairs that will be passed to the view.
	 * @return A redirect to the login page.
	 */
	@PostMapping("/register")
	public String addUser(@Valid @ModelAttribute("user") UserForm form, BindingResult result, Model model)
	{
		if (result.hasErrors())
		{
			model.addAttribute("user", form);
			return "user/register";
		}

		userService.save(form);
		logger.info("successfully created user, need to register it in database");

		return "redirect:/login";
	}
}
