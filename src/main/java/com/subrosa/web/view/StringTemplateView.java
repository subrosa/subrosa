package com.subrosa.web.view;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.view.InternalResourceView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

public class StringTemplateView extends InternalResourceView {

    private static final Logger LOG = LoggerFactory.getLogger(StringTemplateView.class);

    @Override
    protected void renderMergedOutputModel(Map model, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        LOG.debug("Resolving view using StringTemplate.");

        Resource templateFile = getApplicationContext().getResource(getUrl());
        if (LOG.isInfoEnabled()) {
            LOG.info("Resolving URL to template file path ({}).", templateFile.getFile().getCanonicalPath());
        }
        StringTemplateGroup group = new StringTemplateGroup("webpages", templateFile.getFile().getParent());
        StringTemplate template = group.getInstanceOf(getBeanName());
        template.setAttributes(model);

        PrintWriter writer = response.getWriter();
        writer.print(template);
        writer.flush();
        writer.close();
    }
}