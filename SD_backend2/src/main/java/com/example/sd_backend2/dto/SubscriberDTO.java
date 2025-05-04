package com.example.sd_backend2.dto;

public class SubscriberDTO {
    private Long subscriberId;
    private String subscriberName;


    public SubscriberDTO(Long subscriberId, String subscriberName) {
        this.subscriberId = subscriberId;
        this.subscriberName = subscriberName;
    }

    public Long getSubscriberId() {
        return subscriberId;
    }

    public String getSubscriberName() {
        return subscriberName;
    }


}
