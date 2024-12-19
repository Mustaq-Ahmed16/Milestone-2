package in.projectjwt.main.controllers;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;




import in.projectjwt.main.dtos.LoginUserDto;
import in.projectjwt.main.dtos.ResetPasswordRequestdto;
import in.projectjwt.main.dtos.OtpVerificationRequestdto;
import in.projectjwt.main.entities.User;
import in.projectjwt.main.exceptions.InvalidOTPException;

import in.projectjwt.main.repositories.UserRepository;
import in.projectjwt.main.services.AuthenticationService;
import in.projectjwt.main.services.JwtService;
import in.projectjwt.main.services.PasswordResetService;
import jakarta.servlet.http.HttpSession;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
	
	private final JwtService jwtService;
    
    private final AuthenticationService authenticationService;
    @Autowired
    private PasswordResetService passwordResetService;
    
    @Autowired
    private UserRepository userRepo;
  

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
    	Map<String, Object> response = new HashMap<>();

        // Call the service to handle the user signup
        ResponseEntity<Map<String, String>> signupResponse = authenticationService.register(user);

        // If signup response contains a message about the user already existing
        if (signupResponse.getStatusCode() == HttpStatus.CONFLICT) {
        	response.put("success", "false");
            response.put("message", signupResponse.getBody().get("message"));
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // HTTP 409 Conflict
        }
     // If user is successfully registered
        response.put("success", "true");
        response.put("message", signupResponse.getBody().get("message"));
        
        // Add the user details
        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("fullName", user.getFullName());
        userDetails.put("email", user.getEmail());
        response.put("user", userDetails);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticate(@RequestBody LoginUserDto loginUserDto,HttpSession session) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        
     // Store user info in session
        session.setAttribute("userId", authenticatedUser.getId());
        session.setAttribute("email", authenticatedUser.getEmail());
        session.setAttribute("fullName", authenticatedUser.getFullName());
        
        String jwtToken = jwtService.generateToken(authenticatedUser);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login successful");
        response.put("token", jwtToken);  // Provide token for further use
        response.put("user", authenticatedUser);  // Include user details if needed


       // LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime()).setFullname(authenticatedUser.getFullName()).setEmail(authenticatedUser.getEmail());
        

        return ResponseEntity.ok(response);
    }
 // Verify JWT token and get user details
    @GetMapping("/me")
    public ResponseEntity<User> getLoggedInUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            User user = userRepo.findById(userId).orElse(null);
            if (user != null) {
                return ResponseEntity.ok(user);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
    	try {
    		passwordResetService.sendResetLink(request.getEmail());
    		return ResponseEntity.ok("Password reset email sent successfully");
    	}
    	catch (InvalidOTPException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
        
    }
    
    @PostMapping("/otp-verification")
    public ResponseEntity<?> verifyOTP(@RequestBody OtpVerificationRequestdto request) {
        try {
        	passwordResetService.verifyOTP(request.getEmail(), request.getOtp()); // Implement this method in your UserService
            return ResponseEntity.ok("OTP verified successfully");
        } catch (InvalidOTPException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("OTP verification failed: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestdto request) {
        try {
        	passwordResetService.resetPassword(request);
            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

}
