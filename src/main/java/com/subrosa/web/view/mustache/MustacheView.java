package com.subrosa.web.view.mustache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.sampullara.mustache.Mustache;
import com.sampullara.util.TemplateFunction;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.view.AbstractTemplateView;

import com.subrosa.web.view.il8n.LocaleDetectionFilter;

public class MustacheView extends AbstractTemplateView {
    private static final String CSS_RESOURCE_PATH = "subrosa/static/css/";
    private static final String JS_RESOURCE_PATH = "subrosa/static/js/";

    private Mustache template;
    private MessageSource messageSource;

    //@TODO: add header, footer, and other partials to instance variables?
    private List<String> cssRequirements = new ArrayList<String>();
    private List<String> jsRequirements = new  ArrayList<String>();

    public MustacheView() {
        //@TODO: update these with actual "base" CSS/JSS requirements.
        cssRequirements.add("style.css");
        jsRequirements.add("jquery.min.js");
    }

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, final HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {

        TemplateFunction translate = new TemplateFunction() {
            @Override
            public String apply(String input) {
                Locale locale = (Locale) request.getAttribute(LocaleDetectionFilter.REQUEST_LOCALE_ATTRIBUTE);
                return messageSource.getMessage(input, null, locale);
            }
        };
        model.put("i18n", translate);
        model.put("js", getJsRequirements());
        model.put("css", getCssRequirements());

        response.setContentType(getContentType());
        template.execute(response.getWriter(), model);
    }

    protected String buildRequirementString(List<String> requirementArray) {
        StringBuilder sb = new StringBuilder();
        int end = requirementArray.size() - 1;
        sb.append(StringUtils.join(Collections2.transform(requirementArray.subList(0, end), new Function<String, String>() {
            @Override
            public String apply(String from) {
                return FilenameUtils.removeExtension(from);
            }
        }), ","));
        sb.append(",");
        sb.append(requirementArray.get(end));
        return sb.toString();
    }

    public void addCssRequirement(String requirement) {
        cssRequirements.add(requirement);
    }

    public String getCssRequirements() {
        return CSS_RESOURCE_PATH + buildRequirementString(cssRequirements);
    }
    public void addJsRequirement(String requirement) {
        jsRequirements.add(requirement);
    }

    public String getJsRequirements() {
        return JS_RESOURCE_PATH + buildRequirementString(jsRequirements);
    }

    public void setTemplate(Mustache template) {
        this.template = template;
    }

    public Mustache getTemplate() {
        return template;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
