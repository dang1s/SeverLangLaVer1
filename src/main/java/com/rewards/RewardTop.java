package com.rewards;


import com.sg188.real.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RewardTop {
    private int id;
    private int gold;
    private int gold_lock;
    private int sliver;
    private int sliver_lock;
    public List<Item>items;
}
