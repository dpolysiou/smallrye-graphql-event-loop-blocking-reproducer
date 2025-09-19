package org.acme;

import jakarta.annotation.PostConstruct;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.stream.JsonParser;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@GraphQLApi
public class GraphQLResource {

    private JsonObject largeObject;

    @PostConstruct
    void init() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream =
                     classLoader.getResourceAsStream("employees-10-level_100MB.json.zip");
             ZipInputStream zipInputStream =
                     new ZipInputStream(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8)) {
            ZipEntry entry = zipInputStream.getNextEntry();
            Objects.requireNonNull(entry, "Zip file is empty");
            JsonParser parser = Json.createParser(zipInputStream);
            parser.next();
            largeObject = parser.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Query
    public @NonNull JsonObject getLargeObject() {
        return largeObject;
    }
}
