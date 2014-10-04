package com.subrosagames.subrosa.api.admin;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.subrosagames.subrosa.mgmt.config.ExposablePropertyPlaceholderConfigurer;

/**
 * Exposes administrative functionality.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ExposablePropertyPlaceholderConfigurer placeholderConfigurer;

    /**
     * Dumps the application configuration.
     *
     * @return application configuration dump
     */
    @RequestMapping("/configuration")
    @ResponseBody
    public String configuration() {
        StringBuilder sb = new StringBuilder();
        Map<String, String> resolvedProperties = placeholderConfigurer.getResolvedProperties();

        PropertiesFormatter systemFormatter = new PropertiesFormatter(System.getProperties());
        sb.append("System Properties:\n\n");
        sb.append(systemFormatter.getAllAsString()).append("\n\n");
        sb.append("Configuration Properties:\n\n");
        for (Map.Entry<String, String> entry : resolvedProperties.entrySet()) {
            sb.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
        }
        sb.append("\n\n");

        return sb.toString();
    }

}

