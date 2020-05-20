package unknow.common.data;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

import unknow.common.data.kvstore.StringIndex;
import unknow.common.data.kvstore.StringIndex.Creator;

@Mojo(name = "kvstore-create", threadSafe = true)
public class StoreBuilder extends AbstractMojo {
	@Parameter(required = true, property = "input")
	private FileSet input;

	@Parameter(required = true, property = "output")
	private File output;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		FileSetManager manager = new FileSetManager();
		try (Creator creator = new StringIndex.Creator(output)) {
			String[] f = manager.getIncludedFiles(input);
			Properties p = new Properties();
			for (int i = 0; i < f.length; i++) {
				p.clear();
				try (FileInputStream in = new FileInputStream(f[i])) {
					p.load(in);
				}
				for (Entry e : p.entrySet())
					creator.add((String) e.getKey(), (String) e.getValue());
			}
		} catch (Exception e) {
			throw new MojoExecutionException("", e);
		}
	}
}
