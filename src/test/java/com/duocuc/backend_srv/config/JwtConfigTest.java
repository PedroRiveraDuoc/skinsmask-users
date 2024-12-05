package com.duocuc.backend_srv.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=testSecret",
        "jwt.expirationMs=3600000"
})
class JwtConfigTest {

    @Autowired
    private JwtConfig jwtConfig;

    @Test
    void testJwtSecretProperty() {
        // Verificar que el valor de la propiedad jwt.secret se cargó correctamente
        assertThat(jwtConfig.getSecret()).isEqualTo("testSecret");
    }

    @Test
    void testJwtExpirationMsProperty() {
        // Verificar que el valor de la propiedad jwt.expirationMs se cargó correctamente
        assertThat(jwtConfig.getExpirationMs()).isEqualTo(3600000);
    }
}
