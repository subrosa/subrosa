package com.subrosagames.subrosa.task;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.subrosagames.subrosa.domain.account.AccountRepository;
import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.geo.gmaps.GoogleAddress;
import com.subrosagames.subrosa.geo.gmaps.GoogleGeocoder;

/**
 * Periodic task that will find addresses in need of geocoding and fill out their details.
 */
@Component
public class AddressGeocodingTask {

    private static final Logger LOG = LoggerFactory.getLogger(AddressGeocodingTask.class);

    @Autowired
    private GoogleGeocoder googleGeocoder;

    @Autowired
    private AccountRepository accountRepository;

    /**
     * Execute address geocoding task.
     * @throws IOException if geocoding API call goes awry
     */
    public void execute() throws IOException {
        LOG.debug("Executing address geocoding task");
        Map<String, Object> conditions = new HashMap<String, Object>(1) {
            {
                put("countryUnset", true);
            }
        };
        List<Address> addresses = accountRepository.addressesWhere(conditions);
        for (Address address : addresses) {
            if (StringUtils.isEmpty(address.getUserProvided())) {
                continue;
            }
            LOG.debug("Geocoding user-provided address <{}> for address id {}", address.getUserProvided(), address.getId());
            GoogleAddress googleAddress = googleGeocoder.geocode(address.getUserProvided());
            LOG.debug("Found address {}", googleAddress.toString());
            address.setFullAddress(googleAddress.getFullAddress());
            address.setStreetAddress(googleAddress.getStreetAddress());
            address.setCity(googleAddress.getCity());
            address.setState(googleAddress.getState());
            address.setCountry(googleAddress.getCountry());
            address.setPostalCode(googleAddress.getPostalCode());
            accountRepository.update(address);
        }
    }

    public void setGoogleGeocoder(GoogleGeocoder googleGeocoder) {
        this.googleGeocoder = googleGeocoder;
    }
}
