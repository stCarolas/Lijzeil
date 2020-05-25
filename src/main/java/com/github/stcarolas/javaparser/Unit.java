package com.github.stcarolas.javaparser;

@Named("Unit")
public class Unit implements Function<String, Try<CompilationUnit>> {

    public Try<CompilationUnit> apply(String path) {
        return Try(() -> new FileInputStream(new File(URI.create(path)))).map(content -> StaticJavaParser.parse(content));
    }
}
