package ch.agilesolutions.krb.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Endpoint {
	
	@GetMapping(value = "/protected")
	public String getVersion() {


		return String.format("You passed Kerberos SSO authentication");
	}


}
