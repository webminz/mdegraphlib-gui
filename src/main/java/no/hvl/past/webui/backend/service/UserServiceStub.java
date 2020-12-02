package no.hvl.past.webui.backend.service;

import no.hvl.past.webui.transfer.api.UserService;
import no.hvl.past.webui.transfer.entities.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceStub implements UserService {

    private static final String PATRICK_PW = "tagachi";
    public static User SYSTEM_USER = new User(0L, "system", "system", null);
    static User PATRICK = new User(1L, "past@hvl.no", "Patrick Stünkel", null);

    private final Map<String, User> db = new HashMap<>();
    private final Map<String, String> pwDB = new HashMap<>();
    private long idGenerator = 2L;


    // TODO error codes in the user object


    @PostConstruct
    public void setUp() {
        this.db.put(PATRICK.getUsername(), PATRICK);
        this.pwDB.put(PATRICK.getUsername(), PATRICK_PW);
    }

    @Override
    public User register(String username, String password, String fullName, String avatarUrl, LocalDateTime at) {
        if (this.db.containsKey(username)) {
            return null;
        }
        User newUser = new User(idGenerator++, username, fullName, avatarUrl);
        // TODO notify the repo service as well
        this.pwDB.put(username, password);
        return newUser;
    }

    @Override
    public User userObjectForName(String username) {
        if (this.db.containsKey(username)) {
            return this.db.get(username);
        }
        return null;
    }

    @Override
    public boolean authenticate(String credentials, String pw) {
        if (this.pwDB.containsKey(credentials)) {
            return this.pwDB.get(credentials).equals(pw);
        }
        return false;
    }
}
