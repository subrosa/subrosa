package com.subrosa.web.view.mustache;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class MustacheView extends org.springframework.web.servlet.view.mustache.MustacheView {

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {

        //@TODO add js and css resources dynamically

        super.renderMergedTemplateModel(model, request, response);
    }
}
