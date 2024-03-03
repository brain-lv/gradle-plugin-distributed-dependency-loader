package lv.brain.gradle.dependency.extension;

import java.util.ArrayList;
import java.util.List;

public class DependencyMapping {
    private final List<String> jars = new ArrayList<>();
    private final List<String> resources = new ArrayList<>();

    public List<String> getJars() {
        return jars;
    }

    public List<String> getResources() {
        return resources;
    }
}
