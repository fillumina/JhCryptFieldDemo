package com.fillumina.demo.jhcryptfield.config;

import com.fillumina.demo.jhcryptfield.security.EncryptionHelper.EncryptionSecret;
import javax.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Jh Crypt Field Demo.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    public static EncryptionSecret FIELD_ENCRYPTION_SECRET;

    private final FieldEncryptionSecret customerAddressSecret = new FieldEncryptionSecret();

    @PostConstruct
    public void initStaticInstance() {
        FIELD_ENCRYPTION_SECRET = customerAddressSecret;
    }

    public FieldEncryptionSecret getFieldEncryptionSecret() {
        return customerAddressSecret;
    }

    public static class FieldEncryptionSecret implements EncryptionSecret {

        private String password;
        private String salt;

        @Override
        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String getSalt() {
            return salt;
        }

        public void setSalt(String salt) {
            this.salt = salt;
        }
    }
}
