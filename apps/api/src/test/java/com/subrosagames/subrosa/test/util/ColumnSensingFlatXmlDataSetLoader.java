package com.subrosagames.subrosa.test.util;

import java.io.InputStream;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.core.io.Resource;

import com.github.springtestdbunit.dataset.FlatXmlDataSetLoader;

/**
 * Extends @{link FlatXmlDataSetLoader} to configure column sensing.
 */
public class ColumnSensingFlatXmlDataSetLoader extends FlatXmlDataSetLoader {

    @Override
    protected IDataSet createDataSet(Resource resource) throws Exception {
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        try (InputStream inputStream = resource.getInputStream()) {
            return builder.build(inputStream);
        }
    }
}

