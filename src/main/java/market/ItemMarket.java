package market;

import com.sg188.real.Item;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemMarket {
    private long id;
    private String name;
    private int time;
    private int price;
    private Item item;
    private byte status;
}
