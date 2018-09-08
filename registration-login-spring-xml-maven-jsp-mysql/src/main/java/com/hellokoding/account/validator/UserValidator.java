package com.hellokoding.account.validator;

import com.hellokoding.account.model.User;
import com.hellokoding.account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import java.util.regex.Pattern;

@Component
public class UserValidator implements Validator {
    @Autowired
    private UserService userService;
    
	@Autowired
	@Qualifier("emailValidator")
	EmailValidator emailValidator;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;
              
        Pattern UpperCasePatten = Pattern.compile("[A-Z ]");      
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
        if (user.getUsername().length() < 3 || user.getUsername().length() > 32) {
            errors.rejectValue("username", "Size.userForm.username");
        }
        if (userService.findByUsername(user.getUsername()) != null) {
            errors.rejectValue("username", "Duplicate.userForm.username");
        }
        
        if(!emailValidator.valid(user.getEmail())){
			errors.rejectValue("email", "Pattern.userForm.email");
		}

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        if (user.getPassword().length() < 7 || user.getPassword().length() > 32) {
            errors.rejectValue("password", "Size.userForm.password");
        }

        if (!user.getPasswordConfirm().equals(user.getPassword())) {
            errors.rejectValue("password", "Diff.userForm.passwordConfirm");
        }
      
        if (!UpperCasePatten.matcher(user.getPassword()).find()) {
        	errors.rejectValue("password", "UpperCaseCharacter.userForm.password");
        }
       
        if (!digitCasePatten.matcher(user.getPassword()).find()) {
        	errors.rejectValue("password", "DigitCharacter.userForm.password");
        }
        
    }
}
