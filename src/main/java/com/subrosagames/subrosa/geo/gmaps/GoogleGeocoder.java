package com.subrosagames.subrosa.geo.gmaps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderAddressComponent;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.common.collect.Lists;


/**
 * Makes use of the Google's Geocoding API to resolve an address into its component parts.
 *
 * @see <a href="https://developers.google.com/maps/documentation/geocoding/">Google's Geocoding API</a>
 */
@Component
public class GoogleGeocoder {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleGeocoder.class);

    @Autowired
    private String geocodeEndpoint;

    @Autowired
    private String geocodeApiKey;

    /**
     * Geocodes the provided address.
     *
     * @param address address string
     * @return address components
     * @throws IOException if
     */
    public GoogleAddress geocode(String address) throws IOException {
        LOG.debug("Geocoding address <{}>", address);
        GeocoderResult result = getGeocoderResultForAddress(address, geocodeApiKey);

        GoogleAddress gaddr = new GoogleAddress();
        gaddr.setFullAddress(result.getFormattedAddress());
        String number = getAddressComponentAsString(result, "street_number");
        String route = getAddressComponentAsString(result, "route");
        gaddr.setStreetAddress("" + number + " " + route);
        gaddr.setCity(getAddressComponentAsString(result, "locality"));
        gaddr.setState(getAddressComponentAsString(result, "administrative_area_level_1"));
        gaddr.setCountry(getAddressComponentAsString(result, "country"));
        gaddr.setPostalCode(getAddressComponentAsString(result, "postal_code"));

        gaddr.setLatitude(result.getGeometry().getLocation().getLat());
        gaddr.setLongitude(result.getGeometry().getLocation().getLng());

        gaddr.setPartialMatch(result.isPartialMatch());
        gaddr.setLocationType(result.getGeometry().getLocationType());
        return gaddr;
    }

    String getAddressComponentAsString(GeocoderResult result, final String component) {
        ArrayList<GeocoderAddressComponent> addressComponents = Lists.newArrayList(result.getAddressComponents());
        CollectionUtils.filter(
                addressComponents,
                new Predicate<GeocoderAddressComponent>() {
                    @Override
                    public boolean evaluate(GeocoderAddressComponent gac) {
                        return gac.getTypes().contains(component);
                    }
                });
        if (!addressComponents.isEmpty()) {
            return addressComponents.get(0).getShortName();
        }
        return null;
    }

    GeocoderResult getGeocoderResultForAddress(String address, String key) throws IOException {
        final Geocoder googleGeocoder = new Geocoder();
        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(address).setLanguage("en").getGeocoderRequest();
        GeocodeResponse geocoderResponse = googleGeocoder.geocode(geocoderRequest);
        List<GeocoderResult> results = geocoderResponse.getResults();
        return results.get(0);
    }

}


