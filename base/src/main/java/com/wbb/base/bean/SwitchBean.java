package com.wbb.base.bean;

public class SwitchBean {
    private Boolean is_comment_show;
    private Boolean is_can_comment;
    private String policy_url;
    private String service_url;
    private Boolean recharge;

    public Boolean getRecharge() {
        return recharge;
    }

    public void setRecharge(Boolean recharge) {
        this.recharge = recharge;
    }

    public Boolean getIs_comment_show() {
        return is_comment_show;
    }

    public void setIs_comment_show(Boolean is_comment_show) {
        this.is_comment_show = is_comment_show;
    }

    public Boolean getIs_can_comment() {
        return is_can_comment;
    }

    public void setIs_can_comment(Boolean is_can_comment) {
        this.is_can_comment = is_can_comment;
    }

    public String getPolicy_url() {
        return policy_url;
    }

    public void setPolicy_url(String policy_url) {
        this.policy_url = policy_url;
    }

    public String getService_url() {
        return service_url;
    }

    public void setService_url(String service_url) {
        this.service_url = service_url;
    }
}
