package org.openkoala.businesslog.impl;

import org.openkoala.businesslog.config.AbstractBusinessLogConfigAdapter;
import org.openkoala.businesslog.common.BusinessLogConfigXmlParser;

/**
 * User: zjzhai
 * Date: 12/4/13
 * Time: 10:36 AM
 */
public class BusinessLogXmlConfigDefaultAdapter extends AbstractBusinessLogConfigAdapter {

    /**
     * 配置文件
     */
    public final static String XML_CONFIG_FILE_NAME = "koala-businesslog-config.xml";

    @Override
    public String getPreTemplate() {
        BusinessLogConfigXmlParser parser = BusinessLogConfigXmlParser.parsing(getClass().getClassLoader().getResource(XML_CONFIG_FILE_NAME).getFile());
        return parser.getPreTemplate();
    }

    @Override
    public AbstractBusinessLogConfigAdapter findConfigByBusinessOperator(String businessOperator) {

        BusinessLogConfigXmlParser parser = BusinessLogConfigXmlParser.parsing(getClass().getClassLoader().getResource(XML_CONFIG_FILE_NAME).getFile());

        setPreTemplate(parser.getPreTemplate());

        setTemplate(parser.getTemplateFrom(businessOperator));

        System.out.println("............");
        System.out.println(parser.getTemplateFrom(businessOperator));

        setQueries(parser.getQueriesFrom(businessOperator));

        return this;
    }


}