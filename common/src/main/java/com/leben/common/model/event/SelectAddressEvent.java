package com.leben.common.model.event;

import com.leben.common.model.bean.AddressEntity;

public class SelectAddressEvent {

    private AddressEntity address;

    public SelectAddressEvent(AddressEntity address) {
        this.address = address;
    }

    public AddressEntity getAddress() {
        return address;
    }
}
