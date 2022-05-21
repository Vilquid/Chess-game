package cin3.chess.domain;

import org.springframework.data.annotation.Id;

import javax.persistence.*;

@Entity
public class GameRequest
{
	@javax.persistence.Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_game_request", nullable = false)
	private Long idGameRequest;

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_request_seq_gen")
	@SequenceGenerator(name = "game_request_seq_gen", sequenceName = "game_request_id_seq")
	private Long id;

	@OneToOne
	private User receiver;

	@OneToOne
	private User sender;

	@Column
	private Boolean isAccepted;

	public Long getIdGameRequest() {
		return idGameRequest;
	}

	public void setIdGameRequest(Long idGameRequest) {
		this.idGameRequest = idGameRequest;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public User getReceiver()
	{
		return receiver;
	}

	public void setReceiver(User receiver)
	{
		this.receiver = receiver;
	}

	public User getSender()
	{
		return sender;
	}

	public void setSender(User sender)
	{
		this.sender = sender;
	}

	public Boolean getAccepted()
	{
		return isAccepted;
	}

	public void setAccepted(Boolean accepted)
	{
		isAccepted = accepted;
	}
}
