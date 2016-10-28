package co.gobd.tracker.config;

/**
 * Created by tonmoy on 11-Apr-16.
 */
public class BackendUrl {
    private BackendUrl() {
    }

    // Tracking server of go fetch
    public static final class ShadowCat {
        public static final String BASE = "http://gofetch.cloudapp.net:1337";
        public static final String PING = "/api/ping";
    }

    // Main backend of go fetch
    public static final class TaskCat {
        //Production
        public static final String BASE = "http://gofetch.cloudapp.net";

        // Path
        public static final String LOGIN = "/token";
        public static final String GET_PROFILE = "/api/Account/Profile";
        public static final String GET_REGISTER = "/api/Account/Registration";
        public static final String GET_ASSIGNED_JOBS = "/api/Account/{userId}/jobs";
        public static final String PATCH_TASK_STATE = "/api/Job/{jobId}/{taskId}";

    }
}
