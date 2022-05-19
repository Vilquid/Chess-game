package cin3.chess.domain;

import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
public class Authority implements GrantedAuthority
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authority_seq_gen")
	@SequenceGenerator(name = "authority_seq_gen", sequenceName = "authority_id_seq")
	private Long id;

	private String authority;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	@Override
	public String getAuthority()
	{
		return authority;
	}

	public void setAuthority(String authority)
	{
		this.authority = authority;
	}
}
