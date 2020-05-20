package unknow.common.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import unknow.common.data.store.DataCodec;
import unknow.common.data.store.DataStoreZipWO;

@Mojo(name = "package", threadSafe = true)
public class Package extends AbstractMojo {
	@Parameter(required = true, property = "input")
	private Path input;

	@Parameter(required = true, property = "output")
	private Path output;

	public void setInput(File f) {
		this.input = Paths.get(f.toURI());
	}

	public void setOutput(File f) {
		this.output = Paths.get(f.toURI());
	}

	private static final DataCodec<Path> FILE_CODEC = new DataCodec<Path>() {

		@Override
		public void write(OutputStream out, Path t) throws IOException {
			Files.copy(t, out);
		}

		@Override
		public Path read(InputStream in) throws IOException {
			return null;
		}
	};

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			DataStoreZipWO store = new DataStoreZipWO(output);
			Files.walkFileTree(input, new Visitor() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					store.put(input.relativize(file).toString(), file, FILE_CODEC);
					return FileVisitResult.CONTINUE;
				}
			});
			store.close();
		} catch (Exception e) {
			throw new MojoExecutionException("", e);
		}
	}

	private static class Visitor implements FileVisitor<Path> {
		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;
		}
	}
}
