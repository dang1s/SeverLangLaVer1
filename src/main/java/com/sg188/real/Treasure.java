package com.sg188.real;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class Treasure {

    private int id;
    private int quantity;
    private int index;
    private double rate;
}
