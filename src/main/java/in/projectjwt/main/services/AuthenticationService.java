package in.projectjwt.main.services;

import in.projectjwt.main.dtos.LoginUserDto;
import in.projectjwt.main.dtos.RegisterUserDto;
import in.projectjwt.main.entities.User;
import in.projectjwt.main.exceptions.InvalidCredentialsException;
import in.projectjwt.main.exceptions.UserNotFoundException;
import in.projectjwt.main.repositories.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
	
	private final UserRepository userRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
        UserRepository userRepository,
        AuthenticationManager authenticationManager,
        PasswordEncoder passwordEncoder) 
    {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<Map<String, String>> register(User user) {
    	Map<String, String> response = new HashMap<>();
    	if (userRepository.existsByEmail(user.getEmail())) {
    		response.put("message", "User already created with the same email ID.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    	//user.setPassword(user.getPassword());
    	user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt())); // Hashing password
        userRepository.save(user);
        response.put("message", "User registered successfully.");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    

    public User authenticate(LoginUserDto input) {
    	try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            // Throw a custom exception or return a response
            throw new InvalidCredentialsException("Invalid email or password.");
        }

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + input.getEmail()));
    }
    public User findById(Long userId) {
		// TODO Auto-generated method stub
		return userRepository.findById(userId).orElse(null);
	}
}
        
        
        
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        input.getEmail(),
//                        input.getPassword()
//                )
//        );
//
//        return userRepository.findByEmail(input.getEmail())
//                .orElseThrow();
//    }
    


