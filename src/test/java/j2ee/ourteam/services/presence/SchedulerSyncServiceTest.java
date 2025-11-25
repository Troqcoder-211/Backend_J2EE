package j2ee.ourteam.services.presence;

import j2ee.ourteam.BaseTest;
import org.junit.jupiter.api.Test;

class SchedulerSyncServiceTest extends BaseTest {

    @Test
    void testSyncLastSeen_runsWithoutError() {
        SchedulerSyncService service = new SchedulerSyncService();

        // gọi trực tiếp (không chạy scheduler thật)
        service.syncLastSeen();
    }
}
