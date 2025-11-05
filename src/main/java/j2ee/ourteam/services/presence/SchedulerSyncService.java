package j2ee.ourteam.services.presence;

// import j2ee.ourteam.repositories.UserRepository;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulerSyncService {

    // @Autowired
    // private IPresenceService presenceService;

    // @Autowired
    // private UserRepository userRepository;

    // runs every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void syncLastSeen() {
        // Example: scan keys (note: using KEYS in prod is discouraged; better Redis
        // SCAN)
        // Here we assume you have a way to get active users list
        // If you track last seen timestamps in Redis separately, read them and batch
        // update DB
    }
}