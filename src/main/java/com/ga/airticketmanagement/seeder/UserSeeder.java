package com.ga.airticketmanagement.seeder;

import com.ga.airticketmanagement.model.Role;
import com.ga.airticketmanagement.model.User;
import com.ga.airticketmanagement.model.UserProfile;
import com.ga.airticketmanagement.repository.UserProfileRepository;
import com.ga.airticketmanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserSeeder {

    private static final Logger logger = LoggerFactory.getLogger(UserSeeder.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserProfileRepository userProfileRepository;

    public void seed() {
        logger.info("👤 Seeding users...");

        if (userRepository.count() > 0) {
            logger.info("⏭️  Users already exist (count: {}), skipping user seeding", userRepository.count());
            return;
        }

        List<User> users = new ArrayList<>();

        // Admin user
        users.add(createUser(
                "admin@airticket.com",
                "admin123",
                Role.ADMIN,
                "Admin",
                "Super",
                true,
                "https://i.pravatar.cc/150?img=1"
        ));

        // Manager user
        users.add(createUser(
                "manager@airticket.com",
                "manager123",
                Role.CUSTOMER,
                "Manager",
                "System",
                true,
                "https://i.pravatar.cc/150?img=2"
        ));

        // Regular users
        users.add(createUser(
                "john.doe@example.com",
                "user123",
                Role.CUSTOMER,
                "John",
                "Doe",
                true,
                "https://i.pravatar.cc/150?img=3"
        ));

        users.add(createUser(
                "jane.smith@example.com",
                "user123",
                Role.CUSTOMER,
                "Jane",
                "Smith",
                true,
                "https://i.pravatar.cc/150?img=4"
        ));

        users.add(createUser(
                "mike.johnson@example.com",
                "user123",
                Role.ADMIN,
                "Mike",
                "Johnson",
                false,
                "https://i.pravatar.cc/150?img=5"
        ));

        // Test users
        users.add(createUser(
                "test@example.com",
                "test123",
                Role.ADMIN,
                "Test",
                "User",
                false,
                null
        ));

        // Save all users
        userRepository.saveAll(users);

        logger.info("✅ Created {} users", users.size());
        logger.info("   - admin@airticket.com / admin123 (ADMIN)");
        logger.info("   - manager@airticket.com / manager123 (MANAGER)");
        logger.info("   - john.doe@example.com / user123 (USER)");
        logger.info("   - jane.smith@example.com / user123 (USER)");
        logger.info("   - mike.johnson@example.com / user123 (USER)");
        logger.info("   - test@example.com / test123 (USER)");
    }

    private User createUser(String email, String password, Role role,
                            String firstName, String lastName,
                            boolean verified, String profileImg) {
        User user = new User();
        user.setEmailAddress(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setEmailVerified(verified);

        UserProfile profile = new UserProfile();
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setProfileImg(profileImg);
        profile.setUser(user);
        user.setUserProfile(profile);

        return user;
    }
}