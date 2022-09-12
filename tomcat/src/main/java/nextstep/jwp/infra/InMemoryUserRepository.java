package nextstep.jwp.infra;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import nextstep.jwp.domain.User;
import nextstep.jwp.domain.UserRepository;
import nextstep.jwp.exception.DuplicateUserException;

public class InMemoryUserRepository implements UserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static AtomicLong index = new AtomicLong(1L);

    static {
        final User user = new User(index.addAndGet(1L), "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static InMemoryUserRepository getInstance() {
        return new InMemoryUserRepository();
    }

    @Override
    public User save(User user) {
        validateDuplicateUser(user.getAccount());
        User newUser = new User(index.addAndGet(1L), user.getAccount(), user.getPassword(), user.getEmail());
        database.put(user.getAccount(), newUser);
        return database.get(user.getAccount());
    }

    public void validateDuplicateUser(final String account){
        if(findByAccount(account).isPresent()){
            throw new DuplicateUserException();
        }
    }


    @Override
    public Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    private InMemoryUserRepository() {
    }
}
