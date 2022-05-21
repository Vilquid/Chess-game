package cin3.chess.controller;

import cin3.chess.domain.*;
import cin3.chess.form.PromoteForm;
import cin3.chess.repository.*;
import cin3.chess.services.ChessGameService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

@Controller
@RequestMapping("/game")
public class GameController
{
	/**
	 * default game redirection.
	 */
	private static final String GAME_REDIRECTION = "redirect:/game/play/";
	/**
	 * default redirection.
	 */
	private static final String INDEX_REDIRECTION = "redirect:/";

	@Autowired
	private ChessGameService gameService;

	@Autowired
	private GameRepository games;

	@Autowired
	private GameListRepository gamesList;

	@Autowired
	private MoveRepository moves;

//	@Autowired
	private FigureRepository figures;

	@Autowired
	private UserRepository users;

//	private Logger logger = LoggerFactory.getLogger(GameController.class);
	private Logger logger = (Logger) LoggerFactory.getLogger(GameController.class);

	/**
	 * It creates a new game, generates the grid, saves the figures and redirects to the game page
	 *
	 * @param whiteUserId the id of the white player
	 * @param blackUserId the id of the user who will play with the black figures
	 * @return The game is being returned.
	 */
	@GetMapping("/init/{whiteUserId}/{blackUserId}")
	public String init(@PathVariable Long whiteUserId, @PathVariable Long blackUserId)
	{
		Optional<cin3.chess.domain.User> white = users.findById(whiteUserId);
		Optional<User> black = users.findById(blackUserId);

		if (white.isPresent() && black.isPresent()) {
			if (white.get().getLogIn() && black.get().getLogIn()) {
				black.get().setPlaying(true);
				users.save(black.get());

				// clean up
				//TODO clean according to one game not delete all the table
				games.deleteAll();
				figures.deleteAll();
				moves.deleteAll();
				// create a game
				Game g = new Game();
				// randomize which player start
				Random rand = new Random();
				int randomValue = rand.nextInt() % 2;
				g.setCurrentPlayer(randomValue);
				// add the players
				g.setBlackPlayer(black.get());
				g.setWhitePlayer(white.get());
				g.setEchec(0);
				g.setPause(false);
				// initialize the times
				g.setGameTime();
				g.setTimeCurrentPlayer(System.currentTimeMillis());

				games.save(g);

				// generate the grid
				gameService.generateGrid(g);

				// save generated figures
				figures.saveAll(g.getGrid());
				gameService.findKing(g);

				games.save(g);
				logger.info("figures saved from game/");

				return GAME_REDIRECTION + g.getId();
			}
		}

		return INDEX_REDIRECTION;
	}

	/**
	 * It's the controller for the game-play page
	 *
	 * @param model the model object that will be passed to the view
	 * @param id the id of the game
	 * @param currentUser the user currently logged in
	 * @return A String
	 */
	@GetMapping("/play/{id}")
	public String play(final Model model, @PathVariable final Long id, @AuthenticationPrincipal User currentUser)
	{
		Optional<Game> game = games.findById(id);
		if (game.isPresent())
		{
			if (game.get().getWhitePlayer().getPlaying() && game.get().getBlackPlayer().getPlaying())
			{
				game.get().setPause(false);
			}

			model.addAttribute("game", game.get());
			model.addAttribute("user_index", (game.get().getBlackPlayer().getUsername().equals(currentUser.getUsername())) ? 1 : 0);
			model.addAttribute("user", currentUser);
			model.addAttribute("error_msg", "");
			model.addAttribute("time", gameService.getTimeElapsed(game.get().getGameTime()));
			model.addAttribute("time_move", gameService.getTimeElapsed(game.get().getTimeCurrentPlayer()));

			logger.info("Bool echec " + gameService.checkEchec(game.get()));

			if (gameService.checkEchec(game.get()))
			{
				game.get().setEchec(1);
			} else {
				game.get().setEchec(0);
			}

			logger.info("Bool mate " + gameService.checkMate(game.get()));
			model.addAttribute("mate", gameService.checkMate(game.get()));

			if (gamesList.findByGameId(id) != null)
				model.addAttribute("gameList", gamesList.findByGameId(id));

			currentUser.setPlaying(true);
			users.save(currentUser);

			return "game-play";
		}

		logger.info("game " + id + " not found for route /play/" + id);

		return INDEX_REDIRECTION;
	}


	/**
	 * It returns the game-promote.html page, which is the page that allows the user to choose which piece to promote to
	 *
	 * @param model The model object that will be used to render the view.
	 * @param gameId the id of the game
	 * @param promoteId the id of the pawn that is being promoted
	 * @return A String
	 */
	@GetMapping("/promote/{gameId}/{promoteId}")
	public String promote(final Model model, @PathVariable final Long gameId, @PathVariable final Long promoteId)
	{
		Optional<Game> game = games.findById(gameId);

		if (game.isPresent())
		{
			Optional<Figure> fig = figures.findById(promoteId);

			if (fig.isPresent())
			{
				model.addAttribute("game", game.get());
				model.addAttribute("error_msg", "");
				model.addAttribute("figure", fig.get());
				model.addAttribute("time", gameService.getTimeElapsed(game.get().getGameTime()));
				model.addAttribute("time_move", gameService.getTimeElapsed(game.get().getTimeCurrentPlayer()));
				return "game-promote";
			}
		}

		logger.info("game " + gameId + " not found for route /promote/" + gameId + "/" + promoteId);

		return INDEX_REDIRECTION;
	}

	/**
	 * It takes a promote form, checks if it has errors, if not, it finds the figure with the id in the form, checks if it
	 * exists, if it does, it checks if the name in the form is a valid promotion, if it is, it sets the name and code of the
	 * figure to the name and code of the promotion, and saves the figure
	 *
	 * @param form the object that will be filled with the data from the form
	 * @param result The BindingResult object is used to store the validation errors.
	 * @return A redirect to the game page.
	 */
	@PostMapping("/promote")
	public String promoteForm(PromoteForm form, BindingResult result)
	{
		if (result.hasErrors())
		{
			logger.info("error promote form");
		}

		logger.info("you decided to promote " + form.getId() + " to a " +  form.getName());

		Optional<Figure> figure = figures.findById(form.getId());

		if (figure.isPresent())
		{
			if (Game.FIGURES_PROMOTION.contains(form.getName()))
			{
				figure.get().setName(form.getName());
				figure.get().setCode(FigureName.stringToFigureName(form.getName()).ordinal());
				figures.save(figure.get());
			}

			return GAME_REDIRECTION + figure.get().getGame().getId();
		}

		return "game-promote";
	}


	/**
	 * It takes the gameId, the winner and the looser as parameters, and then it sets the game as finished, and sets the
	 * winner
	 * @param gameId the id of the game
	 * @param winner the username of the winner
	 * @param looser the player who lost the game
	 * @return A string
	 */
	@GetMapping("/endgame/{gameId}/{winner}/{looser}")
	public String EndGame(@PathVariable final Long gameId, @PathVariable final String winner, @PathVariable final String looser)
	{
		Optional<Game> game = games.findById(gameId);
		if (game.isPresent())
		{
			game.get().setFinish(true);
			game.get().setPause(true);
			if (game.get().getBlackPlayer().getUsername().equals(winner))
			{
				game.get().setWinner(PlayerName.BLACK);
			} else if (game.get().getWhitePlayer().getUsername().equals(winner))
			{
				game.get().setWinner(PlayerName.WHITE);
			}
			games.save(game.get());
		}

		if (gamesList.findByGameId(gameId) == null)
		{
			GameList gameList = new GameList();
			gameList.setWinner(winner);
			gameList.setLooser(looser);
			gameList.setGameId(gameId);
			gamesList.save(gameList);
		}

		return GAME_REDIRECTION + gameId;
	}


	/**
	 * It checks if the move is valid, if it is, it moves the pawn and deletes the pawn that was taken en passant
	 *
	 * @param gameId the id of the game
	 * @param pawnId the id of the pawn that is going to be moved
	 * @param x the x coordinate of the destination
	 * @param y the y coordinate of the pawn
	 * @param currentUser the user who is currently logged in
	 * @return A string
	 */
	@GetMapping("/passant/{gameId}/{pawnId}/{x}/{y}")
	public String priseEnPassant(@PathVariable final Long gameId, @PathVariable final Long pawnId, @PathVariable final Integer x, @PathVariable final Integer y, @AuthenticationPrincipal User currentUser)
	{
		Optional<Game> game = games.findById(gameId);
		if (game.isPresent())
		{
			// change the coordinate of the moved pawn to the new position
			Figure f = figures.getOne(pawnId);
			if (f.getOwner() == game.get().getCurrentPlayer() && game.get().getCurrentUser().getUsername().equals(currentUser.getUsername()))
			{

				int dy = Arrays.asList(-1, 1).get(f.getOwner());
				// y offset
				int py = f.getY() + dy;
				if (Math.abs(x - f.getX()) == 1 && y == py)
				{ // the move is in diagonal
					if (gameService.checkEnPassant(game.get(), f, x, y))
					{
						Figure f2 = figures.getOne((game.get().getCurrentPlayer() == 0 ? game.get().getFigureAt(x, y + 1).getId() : game.get().getFigureAt(x, y - 1).getId()));
						figures.delete(f2);
						Move m = new Move();
						m.setPositionStart(f.getMoveCode());

						f.setX(x);
						f.setY(y);
						f.updateCountPlayed();

						figures.save(f);
						logger.info("figure moved");

						// save the move
						m.setPositionEnd(f.getMoveCode());
						m.setPlayer(game.get().getCurrentPlayer());

						moves.save(m);

						// change player
						Game g = game.get();
						g.getGrid().remove(f2);
						g.changePlayer();
						games.save(g);
					}
				}
			}
			return GAME_REDIRECTION + game.get().getId();
		}
		return INDEX_REDIRECTION;
	}


	/**
	 * It moves a pawn on the board
	 *
	 * @param model the model object that will be used to render the view
	 * @param gameId the id of the game
	 * @param pawnId the id of the pawn to move
	 * @param x the x coordinate of the cell where the pawn is moved
	 * @param y the y coordinate of the pawn to move
	 * @param currentUser the user who is currently logged in
	 * @return A string that is the name of the view to be rendered.
	 */
	@GetMapping("/move/{gameId}/{pawnId}/{x}/{y}")
	public String moveOnVoidCell(final Model model, @PathVariable final Long gameId, @PathVariable final Long pawnId, @PathVariable final Integer x, @PathVariable final Integer y, @AuthenticationPrincipal User currentUser)
	{
		Optional<Game> game = games.findById(gameId);
		if (game.isPresent())
		{
			// change the coordinate of the moved pawn to the new position
			Figure f = figures.getOne(pawnId);
			// the player is able to move is own pawns only
			if (f.getOwner() == game.get().getCurrentPlayer() && game.get().getCurrentUser().getUsername().equals(currentUser.getUsername()))
			{
				// check the movement
				if (gameService.checkAny(game.get(), f, x, y))
				{
					Move m = new Move();
					m.setPositionStart(f.getMoveCode());

					f.setX(x);
					f.setY(y);
					f.updateCountPlayed();

					figures.save(f);
					logger.info("figure moved");

					// save the move
					m.setPositionEnd(f.getMoveCode());
					m.setPlayer(game.get().getCurrentPlayer());
					m.setTime(gameService.getTimeElapsed(game.get().getTimeCurrentPlayer()));
					m.setGame(game.get());

					moves.save(m);

					// change player
					Game g = game.get();
					g.changePlayer();
					g.setTimeCurrentPlayer(System.currentTimeMillis());
					g.getMoves().add(m);
					games.save(g);

					// pawn promotion
					if (gameService.enablePromotePawn(f))
					{
						return "redirect:/game/promote/" + game.get().getId() + "/" + f.getId();
					}
				} else if (f.getName().equals("pawn"))
				{
					return "redirect:/game/passant/" + game.get().getId() + "/" + f.getId() + "/" + x + "/" + y;
				}
			} else
			{
				//TODO throw exception and inform the view
				logger.info("You can't move a pawn that doesn't belong to you !");
			}

			model.addAttribute("game", game.get());
			return GAME_REDIRECTION + game.get().getId();
		}
		logger.info("game " + gameId + " not found for route /move/" + gameId + "/...");
		return INDEX_REDIRECTION;
	}

	/**
	 * A controller that handles the move of a pawn on another pawn.
	 *
	 * @param model the model object that will be passed to the view
	 * @param gameId the id of the game
	 * @param pawnId1 the id of the pawn that is moved
	 * @param pawnId2 the id of the pawn that is being moved to
	 * @param currentUser the user who is currently logged in
	 * @return A string
	 */
	@GetMapping("/move/{gameId}/{pawnId1}/{pawnId2}")
	public String moveOnAnyPawn(final Model model, @PathVariable final Long gameId, @PathVariable final Long pawnId1, @PathVariable final Long pawnId2, @AuthenticationPrincipal User currentUser)
	{
		Optional<Game> game = games.findById(gameId);
		if (game.isPresent())
		{
			// change the coordinate of the moved pawn to the new position
			Figure f = figures.getOne(pawnId1);
			Figure f2 = figures.getOne(pawnId2);

			// the player is able to move is own pawns only
			if (f.getOwner() == game.get().getCurrentPlayer() && f.getOwner() != f2.getOwner() && game.get().getCurrentUser().getUsername().equals(currentUser.getUsername()))
			{
				// check the movement
				if (gameService.checkAny(game.get(), f, f2.getX(), f2.getY()))
				{
					Move m = new Move();
					m.setPositionStart(f.getMoveCode());

					f.setX(f2.getX());
					f.setY(f2.getY());
					f.updateCountPlayed();

					figures.save(f);
					logger.info("figure moved");

					figures.delete(f2);
					logger.info("figure f2 deleted");

					// save the move
					m.setPositionEnd(f.getMoveCode());
					m.setPlayer(game.get().getCurrentPlayer());
					m.setTime(gameService.getTimeElapsed(game.get().getTimeCurrentPlayer()));
					m.setGame(game.get());

					moves.save(m);
					logger.info("Bool echec " + gameService.checkEchec(game.get()));
					if (gameService.checkEchec(game.get()))
					{
						game.get().setEchec(1);
					} else
					{
						game.get().setEchec(0);
					}
					// change player
					Game g = game.get();
					g.changePlayer();
					g.setTimeCurrentPlayer(System.currentTimeMillis());
					g.getMoves().add(m);

					// delete figure f2
					g.getGrid().remove(f2);

					games.save(g);

					// pawn promotion
					if (gameService.enablePromotePawn(f))
					{
						return "redirect:/game/promote/" + game.get().getId() + "/" + f.getId();
					}
				}
			} else
			{
				//TODO throw exception and inform the view
				logger.info("You can't move a pawn that doesn't belong to you !");
			}

			model.addAttribute("game", game.get());
			return GAME_REDIRECTION + game.get().getId();
		}
		logger.info("game " + gameId + " not found for route moveOnAnyPawn");
		return INDEX_REDIRECTION;
	}
}
