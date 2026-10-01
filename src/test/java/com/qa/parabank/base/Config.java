package com.qa.parabank.base;

/** Settings read from the environment, with defaults that match docker-compose.yml. */
public final class Config {

    public static final String BASE_URL = withTrailingSlash(
            System.getenv().getOrDefault("BASE_URL", "http://localhost:8081/parabank/"));

    public static final boolean HEADLESS =
            !"false".equalsIgnoreCase(System.getenv().getOrDefault("HEADLESS", "true"));

    /** Empty for a local browser, or the address of a Selenium Grid. */
    public static final String REMOTE_URL = System.getenv().getOrDefault("SELENIUM_REMOTE_URL", "");

    private Config() {
    }

    private static String withTrailingSlash(String url) {
        return url.endsWith("/") ? url : url + "/";
    }
}
