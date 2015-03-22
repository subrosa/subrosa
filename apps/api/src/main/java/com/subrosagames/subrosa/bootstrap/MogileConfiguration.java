package com.subrosagames.subrosa.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import fm.last.moji.spring.SpringMojiBean;

@Component
@ConfigurationProperties("moji")
public class MogileConfiguration {

    private String trackerAddresses;
    private String domain;

    public String getTrackerAddresses() {
        return trackerAddresses;
    }

    public void setTrackerAddresses(String trackerAddresses) {
        this.trackerAddresses = trackerAddresses;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Bean
    public SpringMojiBean springMojiBean() {
        SpringMojiBean mojiBean = new SpringMojiBean();
        mojiBean.setAddressesCsv(getTrackerAddresses());
        mojiBean.setDomain(getDomain());
        return mojiBean;
    }
}
