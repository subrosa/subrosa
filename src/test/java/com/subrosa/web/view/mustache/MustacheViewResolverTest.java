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

import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;

/**
 * Test the mustache view resolver.
 */
public class MustacheViewResolverTest {

    private MustacheViewResolver viewResolver;
    private String viewName;

    /**
     * Test set up.
     */
    @Before
    public void setUp() throws Exception {
        viewName = "viewname";

        viewResolver = new MustacheViewResolver();
        ResourceLoader resourceLoader = Mockito.mock(ResourceLoader.class);
        Mockito.doReturn(new FileSystemResource("/does/not/exist")).when(resourceLoader).getResource("viewname");
        viewResolver.setResourceLoader(resourceLoader);
    }

    /**
     * Test not-found view template.
     */
    @Test(expected = FileNotFoundException.class)
    public void testBuildView() throws Exception {
        viewResolver.buildView(viewName);
    }
}
