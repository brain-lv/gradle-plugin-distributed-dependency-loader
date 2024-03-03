package lv.brain.gradle.dependency;

import lv.brain.gradle.dependency.extension.Extension;
import lv.brain.gradle.dependency.transform.UnzipTransform;
import org.gradle.api.Project;
import org.gradle.api.Plugin;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.artifacts.ArtifactAttributes;

public class DistributedDependencyLoaderPlugin implements Plugin<Project> {
    public void apply(Project project) {

        project.getExtensions().create("distributedDependencyLoader", Extension.class, project);

        Configuration files = project.getConfigurations().create("distribution", configuration -> {
            configuration.setCanBeResolved(true);
            configuration.setCanBeConsumed(false);
            configuration.attributes(attributes -> {
                attributes.attribute(ArtifactAttributes.ARTIFACT_FORMAT, "directory");
            });
        });

        project.afterEvaluate(project1 -> {
            Configuration targetConfig = project.getConfigurations().getByName("implementation");

            FileCollection unzippedArtifacts = files.getIncoming().artifactView(viewConfig -> {
                viewConfig.getAttributes().attribute(ArtifactAttributes.ARTIFACT_FORMAT, "directory"); // Specify the format you're transforming to
            }).getFiles();

            unzippedArtifacts.getFiles().forEach(file -> {
                ConfigurableFileTree fileTree = project.fileTree(file);
                fileTree.include("**/*.jar"); // Include only Java source filesfileTree.exclude("**/t

                project.getDependencies().add(targetConfig.getName(),fileTree);
            });
        });

        project.getDependencies().registerTransform(UnzipTransform.class, transformSpec -> {
            transformSpec.getFrom().attribute(ArtifactAttributes.ARTIFACT_FORMAT, "zip");
            transformSpec.getTo().attribute(ArtifactAttributes.ARTIFACT_FORMAT, "directory");
        });

        // Set a project variable with the path to the unzipped folder
        String unzippedFolderPath = project.getBuildDir().getAbsolutePath() + "/unzipped";
        project.getExtensions().getExtraProperties().set("unzippedFolderPath", unzippedFolderPath);
    }
}
