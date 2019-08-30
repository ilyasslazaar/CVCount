package io.novelis.filtragecv.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Filtragecv.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private String uploadDir;

    private String tessDataDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getTessDataDir() {
        return tessDataDir;
    }

    public void setTessDataDir(String tessDataDir) {
        this.tessDataDir = tessDataDir;
    }
}
