package lv.brain.gradle.dependency.transform;

import org.gradle.api.artifacts.transform.TransformAction;
import org.gradle.api.artifacts.transform.TransformOutputs;
import org.gradle.api.artifacts.transform.TransformParameters;
import org.gradle.api.artifacts.transform.CacheableTransform;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Provider;
import org.gradle.api.artifacts.transform.InputArtifact;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;

import java.io.File;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@CacheableTransform
public abstract class UnzipTransform implements TransformAction<TransformParameters.None> {

    @PathSensitive(PathSensitivity.NAME_ONLY)
    @InputArtifact
    public abstract Provider<FileSystemLocation> getInputArtifact();

    @Override
    public void transform(TransformOutputs outputs) {
        File inputZip = getInputArtifact().get().getAsFile();
        System.out.println(inputZip);
        File outputDir = outputs.dir(inputZip.getName().replace(".zip",""));

        System.out.println(outputDir);
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(inputZip))) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                File entryOutput = new File(outputDir, entry.getName());
                if (entry.isDirectory()) {
                    entryOutput.mkdirs();
                } else {
                    entryOutput.getParentFile().mkdirs();
                    try (FileOutputStream out = new FileOutputStream(entryOutput)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zipIn.read(buffer)) > 0) {
                            out.write(buffer, 0, length);
                        }
                    }
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to unzip file", e);
        }
    }
}
