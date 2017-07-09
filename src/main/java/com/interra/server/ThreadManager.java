package com.interra.server;

import javax.naming.LimitExceededException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by pgordon on 05.07.2017.
 */
public class ThreadManager {
    private static ThreadManager instance;
    private static final int MAX_CLIENTS = 2;

    private ThreadManager() {
    }

    private final ConcurrentHashMap<Long, ClientThread> clients = new ConcurrentHashMap<>();
    private AtomicLong pid = new AtomicLong();


    public void registerThread(ClientThread thread) throws LimitExceededException {
        if (clients.size() >= MAX_CLIENTS) {
            throw new LimitExceededException();
        }
        long id = pid.incrementAndGet();
        clients.put(id, thread);
        thread.setUnregisterHandler(new UnregisterHandler(id));

    }


    public static synchronized ThreadManager getInstance() {
        if (instance == null) {
            instance = new ThreadManager();
        }
        return instance;
    }

    class UnregisterHandler {
        private long pid;

        public UnregisterHandler(long pid) {
            this.pid = pid;
        }

        public void deregister() {
            clients.remove(pid);
        }
    }


}
