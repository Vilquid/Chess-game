package cin3.chess.domain;

import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
public class Authority implements GrantedAuthority
{
	@javax.persistence.Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_authority", nullable = false)
	private Long idAuthority;

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authority_seq_gen")
	@SequenceGenerator(name = "authority_seq_gen", sequenceName = "authority_id_seq")
	private Long id;

	private String authority;

	public Long getIdAuthority()
	{
		return idAuthority;
	}

	public void setIdAuthority(Long idAuthority)
	{
		this.idAuthority = idAuthority;
	}

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
