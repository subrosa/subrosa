package com.subrosagames.subrosa.geo.gmaps;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.google.code.geocoder.model.GeocoderAddressComponent;
import com.google.code.geocoder.model.GeocoderGeometry;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.LatLng;
import com.google.common.collect.Lists;

/**
 * Mocks out the API call for tests involving geocoding.
 */
public class MockGoogleGeocoder extends GoogleGeocoder {

    private Map<String, GeocoderResult> responses;

    /**
     * Construct with a map of address inputs to geocoding responses.
     *
     * @param responses map of address inputs to geocoding responses
     */
    public MockGoogleGeocoder(Map<String, GeocoderResult> responses) {
        this.responses = responses;
    }

    @Override
    GeocoderResult getGeocoderResultForAddress(String address, String key) throws IOException {
        return responses.get(address);
    }

    /**
     * Create a geocoding result from the provided address components.
     *
     * @param components address components
     * @return geocoding result
     */
    public static GeocoderResult resultForAddressComponents(Map<Component, String> components) {
        GeocoderResult result = new GeocoderResult();
        result.setAddressComponents(Lists.<GeocoderAddressComponent>newArrayList());
        result.setGeometry(new GeocoderGeometry());
        result.getGeometry().setLocation(new LatLng());
        for (Map.Entry<Component, String> componentEntry : components.entrySet()) {
            componentEntry.getKey().registerValueWithResult(componentEntry.getValue(), result);
        }
        return result;
    }

    /**
     * Enumeration of address component types.
     */
    public static enum Component {
        FULL_ADDRESS
                {
                    @Override
                    public void registerValueWithResult(String value, GeocoderResult result) {
                        result.setFormattedAddress(value);
                    }
                },
        NUMBER
                {
                    @Override
                    public void registerValueWithResult(String value, GeocoderResult result) {
                        result.getAddressComponents().add(newAddressComponent(value, Lists.newArrayList("street_number")));
                    }
                },
        ROUTE
                {
                    @Override
                    public void registerValueWithResult(String value, GeocoderResult result) {
                        result.getAddressComponents().add(newAddressComponent(value, Lists.newArrayList("route")));
                    }
                },
        CITY
                {
                    @Override
                    public void registerValueWithResult(String value, GeocoderResult result) {
                        result.getAddressComponents().add(newAddressComponent(value, Lists.newArrayList("locality", "political")));
                    }
                },
        STATE
                {
                    @Override
                    public void registerValueWithResult(String value, GeocoderResult result) {
                        result.getAddressComponents().add(newAddressComponent(value, Lists.newArrayList("administrative_area_level_1", "political")));
                    }
                },
        COUNTRY
                {
                    @Override
                    public void registerValueWithResult(String value, GeocoderResult result) {
                        result.getAddressComponents().add(newAddressComponent(value, Lists.newArrayList("country", "political")));
                    }
                },
        POSTAL_CODE
                {
                    @Override
                    public void registerValueWithResult(String value, GeocoderResult result) {
                        result.getAddressComponents().add(newAddressComponent(value, Lists.newArrayList("postal_code")));
                    }
                },
        LATITUDE
                {
                    @Override
                    public void registerValueWithResult(String value, GeocoderResult result) {
                        result.getGeometry().getLocation().setLat(new BigDecimal(value));
                    }
                },
        LONGITUDE
                {
                    @Override
                    public void registerValueWithResult(String value, GeocoderResult result) {
                        result.getGeometry().getLocation().setLng(new BigDecimal(value));
                    }
                };

        private static GeocoderAddressComponent newAddressComponent(String value, List<String> types) {
            GeocoderAddressComponent component = new GeocoderAddressComponent();
            component.setShortName(value);
            component.setTypes(types);
            return component;
        }

        /**
         * Registers the component in the provided geocoding result.
         *
         * @param value  address component value
         * @param result geocoding result
         */
        public abstract void registerValueWithResult(String value, GeocoderResult result);
    }

}
