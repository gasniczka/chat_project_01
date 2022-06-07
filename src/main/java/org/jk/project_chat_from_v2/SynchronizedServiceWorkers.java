package org.jk.project_chat_from_v2;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


// realizacja kontraktu z synchronizacją
@RequiredArgsConstructor
class SynchronizedServiceWorkers implements ServerWorkers {

    private final ServerWorkers serverWorkers;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void add(Worker worker) {
        lock.writeLock().lock();
        serverWorkers.add(worker);
        lock.writeLock().unlock();
    }

    @Override
    public void remove(Worker worker) {
        lock.writeLock().lock();
        serverWorkers.remove(worker);
        lock.writeLock().unlock();
    }

    @Override
    public void broadcast(String text) {
        lock.readLock().lock();
        serverWorkers.broadcast(text);
        lock.readLock().unlock();
    }

    @Override
    public void broadcastInRoom(Worker worker, String text) {
        lock.readLock().lock();
        serverWorkers.broadcastInRoom(worker, text);
        lock.readLock().unlock();
    }


    @Override
    public void serverResponse(Worker worker, String text) {
        serverWorkers.serverResponse(worker, text);
    }


    @Override
    public String getRoomInfo(String text) {
        lock.readLock().lock();
        String roomInfo = serverWorkers.getRoomInfo(text);
        lock.readLock().unlock();
        return roomInfo;
    }

    @Override
    public void getHistory(Worker worker, String text) {
        lock.readLock().lock();
        serverWorkers.getHistory(worker, text);
        lock.readLock().unlock();
    }

}
