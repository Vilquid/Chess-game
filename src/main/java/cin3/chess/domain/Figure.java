package cin3.chess.domain;


import org.springframework.data.annotation.Id;

import javax.persistence.*;

@Entity
public class Figure
{
	public static final String CODE_PREFIX = "&#";

	public static final int CODE_NUMBER = 9812;

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "figures_seq_gen")
	@SequenceGenerator(name = "figures_seq_gen", sequenceName = "figures_id_seq")
	private Long id;

	@Column
	private Integer code;

	@Column
	private Integer x;

	@Column
	private Integer y;

	@Column
	private String name;

	@Column
	private Integer owner;

	@Column
	private Integer countPlayed = 0;

	@ManyToOne
	private Game game;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Integer getCode()
	{
		return code;
	}

	public void setCode(int code)
	{
		if (code >= 0 && code <= 5)
		{
			this.code = code;
		}
	}

	public Integer getX()
	{
		return x;
	}

	public void setX(int x)
	{
		if (x >= 0 && x <= 7)
		{
			this.x = x;
		}
	}

	public Integer getY()
	{
		return y;
	}

	public void setY(int y)
	{
		if (y >= 0 && y <= 7)
		{
			this.y = y;
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		if (FigureName.stringToFigureName(name) != null)
		{
			this.name = name;
		}
	}

	public int getOwner()
	{
		return owner;
	}

	public void setOwner(int owner)
	{
		this.owner = owner;
	}

	public Game getGame()
	{
		return game;
	}

	public void setGame(Game game)
	{
		this.game = game;
	}

	public Integer getCountPlayed()
	{
		return countPlayed;
	}

	public void updateCountPlayed()
	{
		this.countPlayed = this.countPlayed + 1;
	}

	public String getHtmlCode()
	{
		String htmlCode = CODE_PREFIX + (CODE_NUMBER + code) + ";";

		if (owner == 1)
		{
			htmlCode = CODE_PREFIX + (CODE_NUMBER + code + 6) + ";";
		}

		return htmlCode;
	}

	public String getMoveCode()
	{
		return (char) (x + 65) + Integer.toString(8 - y);
	}
}
