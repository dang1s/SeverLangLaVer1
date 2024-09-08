package MapService.world;

import MapService.Map;
import MapService.Zone;
import MapService.zones.TrainingZone;
import com.sg188.lib.Log;
import com.sg188.real.Char;

import java.util.ArrayList;
import java.util.List;

public class Training extends World {
    public static byte number = 0;
    public static List<Training> trainings = new ArrayList<>();

    public static void addTraining(Training dungeon) {
        synchronized (trainings) {
            trainings.add(dungeon);
        }
    }

    public static void removeTraining(Training dungeon) {
        synchronized (trainings) {
            trainings.remove(dungeon);
        }
    }
    public static Training findTrainingById(int id) {
        synchronized (trainings) {
            for (Training training : trainings) {
                if (training.id == id) {
                    return training;
                }
            }
        }
        return null;
    }

    public Training(int level, int buff) {
        setType(World.TRAINING);
        this.name = "Training";
        this.id = number++;
        this.countDown = 18000;
        TrainingZone trainingZone = new TrainingZone(new Map(84), 0);
        trainingZone.setWorld(this);
        trainingZone.level = level;
        trainingZone.buff = buff;
        trainingZone.createMob();
        addZone(trainingZone);
        initFinished=true;
    }
    public void join(Char p) {
        zones.get(0).addChar(p);
        p.service.sendTimeInMap(getCountDown() * 10, false);
    }

    public void close() {
        if (this.isClosed) {
            return;
        }
        try {
            List<Char> members = getMembers();
            for (Char _char : members) {
                try {
                    if (_char.isClean) {
                        continue;
                    }
                    _char.idKhuLuyenTap=-1;
                    Map.maps[_char.Info.mapReSpawm].addChar(_char);
                    _char.service.serverMessage("Bạn đã hết thời gian trong khu luyện tập, vui lòng quay lại vào ngày mai");
                    _char.removeWorld(World.TRAINING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {

        } finally {
            Training.removeTraining(this);
            super.close();
        }

    }

    @Override
    public boolean enterWorld(Zone pre, Zone next) {
        return pre.map.mapID!=84&&next.map.mapID ==84;
    }

    @Override
    public boolean leaveWorld(Zone pre, Zone next) {
        return pre.map.mapID==84&&next.map.mapID !=84;
    }
}
