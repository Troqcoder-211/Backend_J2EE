package j2ee.ourteam.services.presence;

import org.junit.jupiter.api.Test;

class SchedulerSyncServiceTest {

    @Test
    void testSyncLastSeen_runsWithoutError() {
        SchedulerSyncService service = new SchedulerSyncService();

        // gọi trực tiếp (không chạy scheduler thật)
        service.syncLastSeen();
    }
}
