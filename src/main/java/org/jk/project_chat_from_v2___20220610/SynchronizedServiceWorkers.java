package org.jk.project_chat_from_v2___20220610;

import lombok.RequiredArgsConstructor;
import org.jk.project_chat_from_v2___20220610.commons.TransferObject;

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
    public void broadcast(TransferObject transferObject) {
        lock.readLock().lock();
        serverWorkers.broadcast(transferObject);
        lock.readLock().unlock();
    }

    @Override
    public void broadcastInRoom(Worker worker, TransferObject transferObject) {
        lock.readLock().lock();
        serverWorkers.broadcastInRoom(worker, transferObject);
        lock.readLock().unlock();
    }


    @Override
    public void serverResponse(Worker worker, TransferObject transferObject) {
        serverWorkers.serverResponse(worker, transferObject);
    }


    @Override
    public String getRoomInfo(TransferObject transferObject) {
        lock.readLock().lock();
        String roomInfo = serverWorkers.getRoomInfo(transferObject);
        lock.readLock().unlock();
        return roomInfo;
    }

    @Override
    public void getHistory(Worker worker, TransferObject transferObject) {
        lock.readLock().lock();
        serverWorkers.getHistory(worker, transferObject);
        lock.readLock().unlock();
    }

}
