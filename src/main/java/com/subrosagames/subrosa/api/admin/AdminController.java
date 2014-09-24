package com.subrosagames.subrosa.api.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Exposes administrative functionality.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    /**
     * Dumps the application configuration.
     * @return application configuration dump
     */
    @RequestMapping("/configuration")
    @ResponseBody
    public String configuration() {
        StringBuilder sb = new StringBuilder();
        /*sb.append("Build Properties:\n\n");
        sb.append(buildProperties.getAllAsString()).append("\n\n");
        sb.append("Configuration Properties:\n\n");
        sb.append(configProperties.getAllAsString()).append("\n\n");*/
        PropertiesFormatter systemFormatter = new PropertiesFormatter(System.getProperties());
        sb.append("System Properties:\n\n");
        sb.append(systemFormatter.getAllAsString()).append("\n\n");
        return sb.toString();
    }

}
