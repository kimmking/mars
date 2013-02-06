/*
 * Copyright 1999-2011 Alibaba Group.
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
package com.cn.jd.mars.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * 
 * 
 * @author netcomm(baiwenzhi@360buy.com)
 * @date 2013-2-6
 */
public class MarsBeanDefinitionParser implements BeanDefinitionParser {
    private final Class<?> beanClass;
    
    private final boolean required;

    public MarsBeanDefinitionParser(Class<?> beanClass, boolean required) {
        this.beanClass = beanClass;
        this.required = required;
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return parse(element, parserContext, beanClass, required);
    }

    @SuppressWarnings("unchecked")
    private static BeanDefinition parse(Element element, ParserContext parserContext, Class<?> beanClass, boolean required) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setLazyInit(false);
        String id = element.getAttribute("id");
        if ("".equals(id) || id == null)
        {
        	id = Long.toString(System.nanoTime());
        }
        if ((id == null || id.length() == 0) && required) {
        	String generatedBeanName = element.getAttribute("name");
        	if (generatedBeanName == null || generatedBeanName.length() == 0) {
        		generatedBeanName = element.getAttribute("interface");
        	}
            id = generatedBeanName; 
            int counter = 2;
            while(parserContext.getRegistry().containsBeanDefinition(id)) {
                id = generatedBeanName + (counter ++);
            }
        }
        if (id != null && id.length() > 0) {
            if (parserContext.getRegistry().containsBeanDefinition(id))  {
        		throw new IllegalStateException("Duplicate spring bean id " + id);
        	}
            parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
            beanDefinition.getPropertyValues().addPropertyValue("id", id);
        }
        
        NamedNodeMap attributes = element.getAttributes();
        int len = attributes.getLength();
        for (int i = 0; i < len; i++) {
            Node node = attributes.item(i);
            String name = node.getLocalName();
            String value = node.getNodeValue();
            
            if ("ref".equals(name) && parserContext.getRegistry().containsBeanDefinition(value)) {
                /*
            	BeanDefinition refBean = parserContext.getRegistry().getBeanDefinition(value);
                if (! refBean.isSingleton()) {
                    throw new IllegalStateException("The exported service ref " + value + " must be singleton! Please set the " + value + " bean scope to singleton, eg: <bean id=\"" + value+ "\" scope=\"singleton\" ...>");
                }
                */
                Object reference = new RuntimeBeanReference(value);
                beanDefinition.getPropertyValues().addPropertyValue(name, reference);
            }
            else
            {
            	beanDefinition.getPropertyValues()
            		.addPropertyValue(name, value);
            }
        }
        
        return beanDefinition;
    }
    
    public static String camelToSplitName(String camelName, String split) {
	    if (camelName == null || camelName.length() == 0) {
	        return camelName;
	    }
	    StringBuilder buf = null;
	    for (int i = 0; i < camelName.length(); i ++) {
	        char ch = camelName.charAt(i);
	        if (ch >= 'A' && ch <= 'Z') {
	            if (buf == null) {
	                buf = new StringBuilder();
	                if (i > 0) {
	                    buf.append(camelName.substring(0, i));
	                }
	            }
	            if (i > 0) {
	                buf.append(split);
	            }
	            buf.append(Character.toLowerCase(ch));
	        } else if (buf != null) {
	            buf.append(ch);
	        }
	    }
	    return buf == null ? camelName : buf.toString();
	}
}