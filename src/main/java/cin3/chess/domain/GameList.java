package cin3.chess.domain;

import org.springframework.data.annotation.Id;

import javax.persistence.*;

@Entity
public class GameList
{
	@javax.persistence.Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_game_list", nullable = false)
	private Long idGameList;

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "games_list_seq_gen")
	@SequenceGenerator(name = "games_list_seq_gen", sequenceName = "games_list_id_seq")
	private Long id;

	@Column
	private String winner;

	@Column
	private String looser;

	@Column
	private Long gameId;

	public Long getIdGameList() {
		return idGameList;
	}

	public void setIdGameList(Long idGameList) {
		this.idGameList = idGameList;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getWinner()
	{
		return winner;
	}

	public void setWinner(String winner)
	{
		this.winner = winner;
	}

	public String getLooser()
	{
		return looser;
	}

	public void setLooser(String looser)
	{
		this.looser = looser;
	}

	public Long getGameId()
	{
		return gameId;
	}

	public void setGameId(Long gameId)
	{
		this.gameId = gameId;
	}
}
