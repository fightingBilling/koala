package org.openkoala.opencis.authentication;

import org.apache.http.protocol.HttpContext;

import java.net.URL;
import java.util.Map;

/**
 * User: zjzhai
 * Date: 12/27/13
 * Time: 5:12 PM
 */
public interface CISAuthentication {

    boolean authentication();

    CISAuthentication setAppURL(URL url);

    /**
     * 暂时依赖HttpContext
     *
     * @return
     */
    HttpContext getContext();

}