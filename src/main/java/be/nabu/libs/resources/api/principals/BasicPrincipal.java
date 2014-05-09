package be.nabu.libs.resources.api.principals;

import java.security.Principal;

public interface BasicPrincipal extends Principal {
	public String getPassword();
}
