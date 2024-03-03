package lv.brain.gradle.dependency.extension;

import org.gradle.api.Action;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Extension {
    private final Project project;
    private final Map<String, DependencyMapping> dependencyMappings = new LinkedHashMap<>();

    public Extension(Project project) {
        this.project = project;
    }

    public void dependencyMappings(Action<? super Map<String, DependencyMapping>> configureAction) {
        configureAction.execute(dependencyMappings);
    }

}