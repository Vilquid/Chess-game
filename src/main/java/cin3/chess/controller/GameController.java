package cin3.chess.controller;

import cin3.chess.domain.*;
import cin3.chess.form.PromoteForm;
import cin3.chess.repository.*;
import cin3.chess.services.ChessGameService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
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

	@Autowired
	private FigureRepository figures;

	@Autowired
	private UserRepository users;

	private Logger logger = LoggerFactory.getLogger(GameController.class);

	@GetMapping("/init/{whiteUserId}/{blackUserId}")
	public String init(
			@PathVariable Long whiteUserId,
			@PathVariable Long blackUserId
	) {
		Optional<User> white = users.findById(whiteUserId);
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

	@GetMapping("/play/{id}")
	public String play(
			final Model model,
			@PathVariable final Long id,
			@AuthenticationPrincipal User currentUser
	) {
		Optional<Game> game = games.findById(id);
		if (game.isPresent()) {
			if (game.get().getWhitePlayer().getPlaying() && game.get().getBlackPlayer().getPlaying()) {
				game.get().setPause(false);
			}

			model.addAttribute("game", game.get());
			model.addAttribute("user_index", (game.get().getBlackPlayer().getUsername().equals(currentUser.getUsername())) ? 1 : 0);
			model.addAttribute("user", currentUser);
			model.addAttribute("error_msg", "");
			model.addAttribute("time", gameService.getTimeElapsed(game.get().getGameTime()));
			model.addAttribute("time_move", gameService.getTimeElapsed(game.get().getTimeCurrentPlayer()));

			logger.info("Bool echec " + gameService.checkEchec(game.get()));
			if (gameService.checkEchec(game.get())) {
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
		logger.info("game {} not found for route /play/{}", id, id);
		return INDEX_REDIRECTION;
	}

	@GetMapping("/promote/{gameId}/{promoteId}")
	public String promote(final Model model,
						  @PathVariable final Long gameId,
						  @PathVariable final Long promoteId
	) {
		Optional<Game> game = games.findById(gameId);
		if (game.isPresent()) {
			Optional<Figure> fig = figures.findById(promoteId);
			if (fig.isPresent()) {
				model.addAttribute("game", game.get());
				model.addAttribute("error_msg", "");
				model.addAttribute("figure", fig.get());
				model.addAttribute("time", gameService.getTimeElapsed(game.get().getGameTime()));
				model.addAttribute("time_move", gameService.getTimeElapsed(game.get().getTimeCurrentPlayer()));
				return "game-promote";
			}
		}
		logger.info("game {} not found for route /promote/{}/{}", gameId, gameId, promoteId);
		return INDEX_REDIRECTION;
	}

	@PostMapping("/promote")
	public String promoteForm(PromoteForm form, BindingResult result) {
		if (result.hasErrors()) {
			logger.info("error promote form");
		}

		logger.info("you decided to promote {} to a {}", form.getId(), form.getName());

		Optional<Figure> figure = figures.findById(form.getId());

		if (figure.isPresent()) {
			if (Game.FIGURES_PROMOTION.contains(form.getName())) {
				figure.get().setName(form.getName());
				figure.get().setCode(FigureName.stringToFigureName(form.getName()).ordinal());
				figures.save(figure.get());
			}

			return GAME_REDIRECTION + figure.get().getGame().getId();
		}

		return "game-promote";
	}


	@GetMapping("/endgame/{gameId}/{winner}/{looser}")
	public String EndGame(@PathVariable final Long gameId,
						  @PathVariable final String winner,
						  @PathVariable final String looser
	) {
		Optional<Game> game = games.findById(gameId);
		if (game.isPresent()) {
			game.get().setFinish(true);
			game.get().setPause(true);
			if (game.get().getBlackPlayer().getUsername().equals(winner)) {
				game.get().setWinner(PlayerName.BLACK);
			} else if (game.get().getWhitePlayer().getUsername().equals(winner)) {
				game.get().setWinner(PlayerName.WHITE);
			}
			games.save(game.get());
		}

		if (gamesList.findByGameId(gameId) == null) {
			GameList gameList = new GameList();
			gameList.setWinner(winner);
			gameList.setLooser(looser);
			gameList.setGameId(gameId);
			gamesList.save(gameList);
		}

		return GAME_REDIRECTION + gameId;
	}


	@GetMapping("/passant/{gameId}/{pawnId}/{x}/{y}")
	public String priseEnPassant(
			@PathVariable final Long gameId,
			@PathVariable final Long pawnId,
			@PathVariable final Integer x,
			@PathVariable final Integer y,
			@AuthenticationPrincipal User currentUser
	) {
		Optional<Game> game = games.findById(gameId);
		if (game.isPresent()) {
			// change the coordinate of the moved pawn to the new position
			Figure f = figures.getOne(pawnId);
			if (f.getOwner() == game.get().getCurrentPlayer() && game.get().getCurrentUser().getUsername().equals(currentUser.getUsername())) {

				int dy = Arrays.asList(-1, 1).get(f.getOwner());
				// y offset
				int py = f.getY() + dy;
				if (Math.abs(x - f.getX()) == 1 && y == py) { // the move is in diagonal
					if (gameService.checkEnPassant(game.get(), f, x, y)) {
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

	@GetMapping("/move/{gameId}/{pawnId}/{x}/{y}")
	public String moveOnVoidCell(final Model model,
								 @PathVariable final Long gameId,
								 @PathVariable final Long pawnId,
								 @PathVariable final Integer x,
								 @PathVariable final Integer y,
								 @AuthenticationPrincipal User currentUser
	) {
		Optional<Game> game = games.findById(gameId);
		if (game.isPresent()) {
			// change the coordinate of the moved pawn to the new position
			Figure f = figures.getOne(pawnId);
			// the player is able to move is own pawns only
			if (f.getOwner() == game.get().getCurrentPlayer() && game.get().getCurrentUser().getUsername().equals(currentUser.getUsername())) {
				// check the movement
				if (gameService.checkAny(game.get(), f, x, y)) {
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
					if (gameService.enablePromotePawn(f)) {
						return "redirect:/game/promote/" + game.get().getId() + "/" + f.getId();
					}
				} else if (f.getName().equals("pawn")) {
					return "redirect:/game/passant/" + game.get().getId() + "/" + f.getId() + "/" + x + "/" + y;
				}
			} else {
				//TODO throw exception and inform the view
				logger.info("You can't move a pawn that doesn't belong to you !");
			}

			model.addAttribute("game", game.get());
			return GAME_REDIRECTION + game.get().getId();
		}
		logger.info("game {} not found for route /move/{}/...", gameId, gameId);
		return INDEX_REDIRECTION;
	}

	@GetMapping("/move/{gameId}/{pawnId1}/{pawnId2}")
	public String moveOnAnyPawn(final Model model,
								@PathVariable final Long gameId,
								@PathVariable final Long pawnId1,
								@PathVariable final Long pawnId2,
								@AuthenticationPrincipal User currentUser
	) {
		Optional<Game> game = games.findById(gameId);
		if (game.isPresent()) {
			// change the coordinate of the moved pawn to the new position
			Figure f = figures.getOne(pawnId1);
			Figure f2 = figures.getOne(pawnId2);

			// the player is able to move is own pawns only
			if (f.getOwner() == game.get().getCurrentPlayer() && f.getOwner() != f2.getOwner() && game.get().getCurrentUser().getUsername().equals(currentUser.getUsername())) {
				// check the movement
				if (gameService.checkAny(game.get(), f, f2.getX(), f2.getY())) {
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
					if (gameService.checkEchec(game.get())) {
						game.get().setEchec(1);
					} else {
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
					if (gameService.enablePromotePawn(f)) {
						return "redirect:/game/promote/" + game.get().getId() + "/" + f.getId();
					}
				}
			} else {
				//TODO throw exception and inform the view
				logger.info("You can't move a pawn that doesn't belong to you !");
			}

			model.addAttribute("game", game.get());
			return GAME_REDIRECTION + game.get().getId();
		}
		logger.info("game {} not found for route moveOnAnyPawn", gameId);
		return INDEX_REDIRECTION;
	}
}
