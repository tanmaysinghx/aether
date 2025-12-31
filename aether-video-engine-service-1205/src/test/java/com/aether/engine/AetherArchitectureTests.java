package com.aether.engine;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class AetherArchitectureTests {

    ApplicationModules modules = ApplicationModules.of(AetherVideoEngineApplication.class);

    @Test
    void verifyModularity() {
        modules.verify();
    }

    @Test
    void writeDocumentationSnippets() {
        new Documenter(modules)
                .writeDocumentation();
    }
}
