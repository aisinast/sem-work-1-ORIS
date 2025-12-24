package ru.itis.spotty.listeners;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.itis.spotty.repositories.*;
import ru.itis.spotty.services.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@WebListener
public class InitListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Properties properties = new Properties();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (inputStream == null) {
                throw new RuntimeException("Unable to load application.properties");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UserRepository userRepository = new JdbcUserRepository();
        SessionRepository sessionRepository = new JdbcSessionRepository();

        SecurityService securityService = new SecurityServiceImpl(userRepository, sessionRepository, properties);
        servletContextEvent.getServletContext().setAttribute("securityService", securityService);

        UserService userService = new UserServiceImpl(userRepository);
        servletContextEvent.getServletContext().setAttribute("userService", userService);

        PostRepository postRepository = new JdbcPostRepository();
        PostTagRepository postTagRepository = new JdbcPostTagRepository();
        TagRepository tagRepository = new JdbcTagRepository();
        LikeRepository likeRepository = new JdbcLikeRepository();
        PostService postService = new PostServiceImpl(postRepository, postTagRepository, tagRepository, likeRepository);
        servletContextEvent.getServletContext().setAttribute("postService", postService);

        PlaceRepository placeRepository = new JdbcPlaceRepository();
        PlaceService placeService = new PlaceServiceImpl(placeRepository);
        servletContextEvent.getServletContext().setAttribute("placeService", placeService);

        FollowingRepository followingRepository = new JdbcFollowingRepository();
        FollowingService followingService = new FollowingServiceImpl(followingRepository);
        servletContextEvent.getServletContext().setAttribute("followingService", followingService);
    }
}

