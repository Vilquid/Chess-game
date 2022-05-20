package cin3.chess.domain;


import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "chess_user")
public class User implements UserDetails
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chess_users_seq_gen")
	@SequenceGenerator(name = "chess_users_seq_gen", sequenceName = "chess_users_id_seq")
	private Long id;

	@Column(unique = true)
	private String username;

	private String password;

	@Column(unique = true)
	private String email;

	@Transient
	private String confirmPassword;

	@Column
	private Boolean isLogIn = false;

	@Column
	private Boolean isPlaying = false;

	@ManyToMany(fetch = FetchType.EAGER)
	private Collection<Authority> authorities;

	public Boolean getLogIn()
	{
		return isLogIn;
	}

	public void setLogIn(Boolean logIn)
	{
		isLogIn = logIn;
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
	public String getUsername()
	{
		return username;
	}

	@Override
	public boolean isAccountNonExpired()
	{
		return true;
	}

	@Override
	public boolean isAccountNonLocked()
	{
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired()
	{
		return true;
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getConfirmPassword()
	{
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword)
	{
		this.confirmPassword = confirmPassword;
	}

	public Collection<Authority> getAuthorities()
	{
		return authorities;
	}

	public void setAuthorities(Collection<Authority> authorities)
	{
		this.authorities = authorities;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public Boolean getPlaying()
	{
		return isPlaying;
	}

	public void setPlaying(Boolean playing)
	{
		isPlaying = playing;
	}
}

