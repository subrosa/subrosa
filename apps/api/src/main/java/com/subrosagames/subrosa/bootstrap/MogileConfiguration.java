package com.subrosagames.subrosa.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.subrosagames.subrosa.domain.file.FileStorer;
import com.subrosagames.subrosa.domain.file.MogileFileStorer;
import fm.last.moji.spring.SpringMojiBean;

/**
 * Mogile FS configuration.
 */
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

    /**
     * Mogile FS file storer.
     *
     * @param springMojiBean spring moji bean
     * @return mogilefs file storer
     */
    @Bean
    @Profile("!unit-test")
    public FileStorer mogileFileStorer(SpringMojiBean springMojiBean) {
        return new MogileFileStorer(springMojiBean);
    }

    /**
     * Spring moji bean.
     *
     * @return spring moji bean
     */
    @Bean
    @Profile("!unit-test")
    public SpringMojiBean springMojiBean() {
        SpringMojiBean mojiBean = new SpringMojiBean();
        mojiBean.setAddressesCsv(getTrackerAddresses());
        mojiBean.setDomain(getDomain());
        return mojiBean;
    }
}
