package com.data;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Example to represent objects linked to the SUT
 *
 * @author ffrik
 */
@Getter
@AllArgsConstructor
public class ObjectExample {

    @Setter
    private String param1;

    @Setter
    private int param2;

}