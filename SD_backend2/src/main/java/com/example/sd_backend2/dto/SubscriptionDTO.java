package com.example.sd_backend2.dto;

public class SubscriptionDTO {
    private Long subscribedToId;
    private String subscribedToName;

    public SubscriptionDTO() {
    }

    public SubscriptionDTO(Long subscribedToId, String subscribedToName) {
        this.subscribedToId = subscribedToId;
        this.subscribedToName = subscribedToName;
    }

    public Long getSubscribedToId() {
        return subscribedToId;
    }

    public void setSubscribedToId(Long subscribedToId) {
        this.subscribedToId = subscribedToId;
    }

    public String getSubscribedToName() {
        return subscribedToName;
    }

    public void setSubscribedToName(String subscribedToName) {
        this.subscribedToName = subscribedToName;
    }
}
