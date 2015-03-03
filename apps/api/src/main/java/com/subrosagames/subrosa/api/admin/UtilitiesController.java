package com.subrosagames.subrosa.api.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.subrosagames.subrosa.geo.gmaps.GoogleGeocoder;

/**
 * Supplies administrative utility methods.
 */
@Controller
@RequestMapping("/admin/utility")
public class UtilitiesController {

    @Autowired
    private GoogleGeocoder googleGeocoder;

    /**
     * Runs given address through Google's geocoding service.
     *
     * @param address input address
     * @return geocoded address info
     * @throws Exception if something goes wrong
     */
    @RequestMapping("/geocode")
    @ResponseBody
    public String geocode(@RequestParam("address") String address) throws Exception {
        return googleGeocoder.geocode(address).toString();
    }

}
