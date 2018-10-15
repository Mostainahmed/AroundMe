package com.aroundme.mostain.Utils.Location.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Angopapo, LDA on 25.09.16.
 */
public class AddressComponent {
    String long_name, short_name;
    List<String> types;

    public AddressComponent(){
        types = new ArrayList<String>();
    }
}
