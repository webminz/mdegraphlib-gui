package no.hvl.past.webui.transfer.api;

import no.hvl.past.webui.transfer.entities.User;

import java.time.LocalDateTime;

public interface UserService {

    User register(String username, String password, String fullName, String avatarUrl, LocalDateTime at);

    User userObjectForName(String credential);

    boolean authenticate(String credentials, String pw);

}
