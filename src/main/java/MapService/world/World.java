package MapService.world;

import MapService.Zone;
import com.sg188.lib.Log;
import com.sg188.real.Char;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public abstract class World {

    public static final byte ARENA = 0;
    public static final byte DeadForest = 1;
    public static final byte DUNGEON = 2;
    public static final byte TERRITORY = 3;
    public static final byte CamThuat = 4;
    public static final byte SONCAP = 5;
    public static final byte DAI_HOI_VO_THUAT = 6;
    public static final byte TRANH_DOAT_LANH_THO = 7;
    public static final byte EVENT = 8;
    public static final byte TRAINING = 9;
    public static final byte DAI_CHIEN_NHAN_GIA_3 = 10;

    public static int number = 0;

    @Getter
    protected int id;
    @Getter
    @Setter
    protected byte type;
    @Getter
    protected int countDown;
    @Getter
    protected boolean isClosed;
    protected boolean initFinished;
    protected ReadWriteLock lock;
    protected ArrayList<Char> members;
    public ArrayList<Zone> zones;
    @Getter
    protected WorldService service;
    @Getter
    protected String name;

    public World() {
        this.name = "World";
        this.members = new ArrayList<>();
        this.zones = new ArrayList<>();
        this.lock = new ReentrantReadWriteLock();
        this.service = new WorldService(this);
        WorldManager.getInstance().addWorld(this);
    }

    public void generateId() {
        this.id = number++;
    }

    public List<Char> getMembers() {
        lock.readLock().lock();
        try {
            return members.stream().distinct().collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    public void addMember(Char _char) {
        lock.writeLock().lock();
        try {
            if(members!=null)
            this.members.add(_char);
            Log.debug(String.format("add %s playername: %s", toString(), _char.Info.name));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeMember(Char _char) {
        lock.writeLock().lock();
        try {
            if(this.members!=null)
            this.members.remove(_char);
            Log.debug(String.format("remove %s playername: %s", toString(), _char.Info.name));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public abstract boolean enterWorld(Zone pre, Zone next);

    public abstract boolean leaveWorld(Zone pre, Zone next);

    public void clearAllMember() {
        lock.writeLock().lock();
        try {
            this.members.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addZone(Zone z) {
        lock.writeLock().lock();
        try {
            zones.add(z);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeZone(Zone z) {
        lock.writeLock().lock();
        try {
            zones.remove(z);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void update() {
        zones.forEach(z -> {
            if (z != null&&!isClosed&& !z.isClosed) {
                z.update();
                z.updatePlayer();
            }
        });
        if (countDown > 0) {
            countDown--;
            if (countDown <= 0) {
                close();
                return;
            }
        }
    }

    public void close() {
        this.isClosed = true;
        clearAllMember();
        this.members = null;
        zones.forEach(z -> z.close());
        this.zones = null;
    }

    public Zone find(int id) {
        for (Zone zone : this.zones) {
            if (zone.map.mapID == id) {
                return zone;
            }
        }
        return null;
    }

    public void setCountdown(int countDown) {
        this.countDown = countDown;
//        service.sendTimeInMap(countDown);
    }

    @Override
    public String toString() {
        return String.format("%s[%d]", this.name, this.id);
    }
}
