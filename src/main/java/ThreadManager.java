import com.interra.server.ClientThread;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by pgordon on 05.07.2017.
 */
public class ThreadManager {
    private static ThreadManager instance;

    private final ConcurrentHashMap<Long, ClientThread> clients = new ConcurrentHashMap<>();
    private AtomicLong pid = new AtomicLong();

    class UnregisterHandler{
        private long pid;
        public void unregister(){

        }

    }
    public void registerThread(ClientThread thread){

        clients.put(pid.incrementAndGet(), thread);
    }
    public void deregisterThread(ClientThread thread){
        clients.put(pid.incrementAndGet(), thread);
    }

    private ThreadManager() {
    }

    public static synchronized ThreadManager getInstance() {
        if (instance == null) {
            instance = new ThreadManager();
        }
        return instance;
    }


}
