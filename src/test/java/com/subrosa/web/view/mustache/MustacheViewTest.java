/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.subrosa.web.view.mustache;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sampullara.mustache.Mustache;

import static org.junit.Assert.assertEquals;

/**
 * Test the mustache view.
 */
public class MustacheViewTest {

    private Mustache template;
    private HttpServletResponse response;
    private PrintWriter mockWriter;
    private MustacheView view;

    /**
     * Set up for tests.
     */
    @Before
    public void setUp() throws Exception {

        template = Mockito.mock(Mustache.class);
        response = Mockito.mock(HttpServletResponse.class);
        mockWriter = Mockito.mock(PrintWriter.class);

        view = new MustacheView();
        view.setTemplate(template);
    }

    /**
     * Test template render.
     */
    @Test
    public void testRenderMergedTemplateModel() throws Exception {
        final Map<String, Object> model = new HashMap<String, Object>();
        Mockito.doReturn(mockWriter).when(response).getWriter();

        view.renderMergedTemplateModel(model, null, response);

        Mockito.verify(mockWriter).flush();

        assertEquals(template, view.getTemplate());
    }
}
