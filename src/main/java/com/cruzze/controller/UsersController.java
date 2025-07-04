package com.cruzze.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import com.cruzze.entity.Users;
import com.cruzze.service.UsersService;
import com.cruzze.util.JwtUtils;
import com.cruzze.util.ResponseStructure;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersService usersService;
@PostMapping(value = "/createUsers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<ResponseStructure<Users>> createUserFromToken(
        @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
        @RequestPart("token") String bearerToken) {

    try {
        String token = bearerToken.replace("Bearer ", "");
        Map<String, Object> claims = JwtUtils.verifyAndExtractPayload(token);

        Users user = new Users();
        user.setClerkUserId((String) claims.get("sub"));

        // Email is now optional
        String email = (String) claims.get("email");
        if (email != null && !email.isBlank()) {
            user.setEmail(email);
        }

        String firstName = (String) claims.get("first_name");
        if (firstName != null && !firstName.isBlank()) {
            user.setFirstName(firstName);
        }

        String lastName = (String) claims.get("last_name");
        if (lastName != null && !lastName.isBlank()) {
            user.setLastName(lastName);
        }

        String phoneNumber = (String) claims.get("phone_number");
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            user.setPhoneNumber(phoneNumber);
        }

        // ✅ Set profile image if available
        if (profileImage != null && !profileImage.isEmpty()) {
            user.setProfileImage(profileImage.getBytes());
        }

        // ✅ Handle optional public metadata
        Map<String, Object> publicMetadata = (Map<String, Object>) claims.get("public_metadata");
        if (publicMetadata != null) {
            String dob = (String) publicMetadata.get("dateOfBirth");
            if (dob != null && !dob.isBlank()) {
                user.setDateOfBirth(LocalDate.parse(dob));
            }

            String gender = (String) publicMetadata.get("gender");
            if (gender != null && !gender.isBlank()) {
                user.setGender(Users.Gender.valueOf(gender.toUpperCase()));
            }

            String emergencyName = (String) publicMetadata.get("userEmergencyContactName");
            if (emergencyName != null && !emergencyName.isBlank()) {
                user.setUserEmergencyContactName(emergencyName);
            }

            String emergencyNumber = (String) publicMetadata.get("userEmergencyContactNumber");
            if (emergencyNumber != null && !emergencyNumber.isBlank()) {
                user.setUserEmergencyContactNumber(emergencyNumber);
            }
        }

        ResponseStructure<Users> response = usersService.createUser(user);
        return ResponseEntity.status(response.getStatus()).body(response);

    } catch (Exception e) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token", e);
    }
}


    @GetMapping("/getUserByClerkUserId/{clerkUserId}")
    public ResponseEntity<ResponseStructure<Users>> getUserByClerkId(@PathVariable String clerkUserId) {
        ResponseStructure<Users> response = usersService.getUserByClerkId(clerkUserId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    
@PutMapping(value = "/updateProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<ResponseStructure<Users>> updateUser(
        @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
        @RequestPart(value = "userEmergencyContactName", required = false) String emergencyName,
        @RequestPart(value = "userEmergencyContactNumber", required = false) String emergencyNumber,
        @RequestPart(value = "dateOfBirth", required = false) String dateOfBirth
) {
    // ✅ Securely extract clerkUserId from the JWT
    String clerkUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    Users updatedUser = usersService.updateUser(clerkUserId, profileImage, emergencyName, emergencyNumber, dateOfBirth);

    ResponseStructure<Users> response = new ResponseStructure<>();
    response.setStatus(HttpStatus.OK.value());
    response.setMessage("User profile updated successfully");
    response.setData(updatedUser);
    return ResponseEntity.ok(response);
}


    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

}
