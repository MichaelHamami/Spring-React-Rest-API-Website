package acs.rest.utils;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class ValidEmail {
	
	public  boolean isEmailVaild(String email) {
		String emailRegex = "^[\\w-\\.+]*[\\w-\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

		Pattern pat = Pattern.compile(emailRegex);
		if (email == null)
			return false;
		return pat.matcher(email).matches();
	}

}
